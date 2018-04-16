package com.samudev.kasapro.web

import com.samudev.kasapro.control.ControlPresenter
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
                token = JSONObject(body!!.string()).getJSONObject("result").getString("token")
            } catch (e: Exception) {
                e.printStackTrace()
                throw KasaServerException("Failed to parse json payload")
            }
            return token
        }

        fun getDeviceId(token: String): String? {
            val jsonBody = JSONObject()
            jsonBody.put("method", "getDeviceList")
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonBody.toString())
            val requestUrl = HttpUrl.parse(TPLINK_BASIC_ADDRESS)?.newBuilder()?.addQueryParameter("token", token)?.build()  ?: return ""
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
            try {
                deviceId = JSONObject(response.body()!!.string())  // body is never null on a non null response. https://github.com/square/okhttp/issues/2883
                        .getJSONObject("result")
                        .getJSONArray("deviceList")
                        .getJSONObject(0)
                        .getString("deviceId")
            } catch (e: Exception) {
                e.printStackTrace()
                throw KasaServerException("Failed to parse json payload")
            }
            return deviceId
        }

        //TODO: should be possible to adjust brightness without light being on -> two return values?
        fun adjustLight(lightOn: Boolean, brightnessLevel: Int, token: String, deviceId: String): Int {
            var brightness = brightnessLevel
            if (brightnessLevel > 100) {
                brightness = 100
            } else if (brightnessLevel < 0) {
                brightness = 0
            }
            val jsonBody = JSONObject()
            val jsonBodyParams = JSONObject()
            val jsonParamsRequestData = JSONObject()
            val jsonRequestDataLightningService = JSONObject()
            val jsonLightningState = JSONObject()

            jsonLightningState.put("on_off", if (lightOn) 1 else 0)
            jsonLightningState.put("brightness", brightness)
            jsonRequestDataLightningService.put("transition_light_state", jsonLightningState)
            jsonParamsRequestData.put("smartlife.iot.smartbulb.lightingservice", jsonRequestDataLightningService)
            jsonBodyParams.put("deviceId", deviceId)
            jsonBodyParams.put("requestData", jsonParamsRequestData.toString())  // API expects a json in string format
            jsonBody.put("method", "passthrough")
            jsonBody.put("params", jsonBodyParams)

            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonBody.toString())
            val requestUrl = HttpUrl.parse(TPLINK_BASIC_ADDRESS)?.newBuilder()?.addQueryParameter("token", token)?.build() ?: return -1
            val request = Request.Builder()
                    .url(requestUrl)
                    .post(requestBody)
                    .build()

            val response: Response
            try {
                response = OkHttpClient().newCall(request).execute()
            } catch (e: IOException) {
                e.printStackTrace()
                return -1
            }

            var errorCode = -1

            try {
                errorCode = JSONObject(response.body()!!.string())
                        .getInt("error_code")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return if (errorCode == 0) brightness else -1 // no error
        }


    }
}