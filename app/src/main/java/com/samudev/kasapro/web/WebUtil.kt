package com.samudev.kasapro.web

import android.util.Log
import com.samudev.kasapro.model.Device
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*


class WebUtil {

    class KasaServerException(errorMessage: String): Exception()  // Something went wrong with server call

    companion object {
        private val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
        private const val TPLINK_BASIC_ADDRESS = "https://eu-wap.tplinkcloud.com"

        fun getToken(uuid: UUID, email: String, password: String): String? {
            val bodyJson = JSONObject()
            val innerBody = JSONObject()
            bodyJson.put("method", "login")
            innerBody.put("appType", "Kasa_Android")
            innerBody.put("cloudUserName", email)
            innerBody.put("cloudPassword", password)
            innerBody.put("terminalUUID", uuid.toString())
            bodyJson.put("params", innerBody)

            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, bodyJson.toString())
            val request = Request.Builder()
                    .url(TPLINK_BASIC_ADDRESS)
                    .post(requestBody)
                    .build()

            val response: Response
            try {
                response = OkHttpClient().newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
                throw KasaServerException("Failed to execute http request")
            }

            val body = response.body()  // body is never null on a non null response. https://github.com/square/okhttp/issues/2883
            var token = ""
            try {
                // TODO: extract alias also
                token = JSONObject(body!!.string()).getJSONObject("result").getString("token")
            } catch (e: Exception) {
                e.printStackTrace()
                throw KasaServerException("Failed to parse json payload")
            }
            return token
        }

        fun getDeviceId(token: String): List<String>? {
            val jsonBody = JSONObject()
            jsonBody.put("method", "getDeviceList")
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonBody.toString())
            val requestUrl = HttpUrl.parse(TPLINK_BASIC_ADDRESS)?.newBuilder()?.addQueryParameter("token", token)?.build()  ?: return null
            val request = Request.Builder()
                    .url(requestUrl)
                    .post(requestBody)
                    .build()

            val response: Response
            try {
                response = OkHttpClient().newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
                throw KasaServerException("Failed to execute http request")
            }

            var deviceId = ""
            var deviceName = ""
            try {
                val resultList = JSONObject(response.body()?.string())  // body is never null on a non null response. https://github.com/square/okhttp/issues/2883
                        .getJSONObject("result")
                        .getJSONArray("deviceList")
                        .getJSONObject(0)
                        ?: return null
                deviceId = resultList.getString("deviceId")
                deviceName = resultList.getString("alias")
            } catch (e: Exception) {
                e.printStackTrace()
                throw KasaServerException("Failed to parse json payload")
            }
            return Arrays.asList(deviceId, deviceName)
        }

        /**
         * Should adjust light state as specified in 'device', and return 'device' if successful
         * or 'null' if failed to execute
         */
        fun adjustLight(device: Device): Device? {

            val jsonBody = JSONObject()
            val jsonBodyParams = JSONObject()
            val jsonParamsRequestData = JSONObject()
            val jsonRequestDataLightningService = JSONObject()
            val jsonLightningState = JSONObject()

            jsonLightningState.put("on_off", if (device.lightOn) 1 else 0)
            if (device.brightness > 0) jsonLightningState.put("brightness", device.brightness)  // negative brightness implies not to set it
            jsonRequestDataLightningService.put("transition_light_state", jsonLightningState)
            jsonParamsRequestData.put("smartlife.iot.smartbulb.lightingservice", jsonRequestDataLightningService)
            jsonBodyParams.put("deviceId", device.id)
            jsonBodyParams.put("requestData", jsonParamsRequestData.toString())  // API expects a json in string format
            jsonBody.put("method", "passthrough")
            jsonBody.put("params", jsonBodyParams)

            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonBody.toString())
            val requestUrl = HttpUrl.parse(TPLINK_BASIC_ADDRESS)?.newBuilder()?.addQueryParameter("token", device.token)?.build() ?: return null
            val request = Request.Builder()
                    .url(requestUrl)
                    .post(requestBody)
                    .build()

            val response: Response
            try {
                response = OkHttpClient().newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

            var errorCode = -1

            try {
                errorCode = JSONObject(response.body()?.string())
                        .getInt("error_code")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return if (errorCode == 0) device else null // 0: no error
        }


    }
}