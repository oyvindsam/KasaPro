package com.samudev.kasapro.util

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*


class WebUtil {

    companion object {
        private val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
        private const val TPLINK_BASIC_ADDRESS = "https://eu-wap.tplinkcloud.com"

        fun getToken(uuid: UUID, email: String, password: String): String {
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
                return ""
            }

            val body = response.body()  // body is never null on a non null response. https://github.com/square/okhttp/issues/2883
            var token = ""
            try {
                token = JSONObject(body!!.string()).getJSONObject("result").getString("token")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return token
        }


    }
}