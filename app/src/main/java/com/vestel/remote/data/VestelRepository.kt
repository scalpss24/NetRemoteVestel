package com.vestel.remote.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class TvInfo(
    val friendlyName: String,
    val tvVersion: String,
    val ip: String,
    val isOnline: Boolean = true
)

sealed class CommandResult {
    data class Success(val code: Int, val httpStatus: Int) : CommandResult()
    data class Error(val code: Int, val message: String) : CommandResult()
}

class VestelRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    private val xmlMediaType = "text/xml; charset=utf-8".toMediaType()

    /**
     * Отправляет команду клавиши на телевизор Vestel.
     * Эндпоинт: http://<IP>:56789/apps/SmartCenter
     * Метод: POST
     * Тело: <?xml version="1.0" ?><remote><key code="XXXX"/></remote>
     */
    suspend fun sendKey(ip: String, code: Int): CommandResult = withContext(Dispatchers.IO) {
        val url = "http://$ip:56789/apps/SmartCenter"
        val xmlPayload = "<?xml version=\"1.0\" ?><remote><key code=\"$code\"/></remote>"
        val requestBody = xmlPayload.toRequestBody(xmlMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "text/xml")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 201 || response.code == 200) {
                    CommandResult.Success(code, response.code)
                } else {
                    CommandResult.Error(code, "HTTP ${response.code}: ${response.message}")
                }
            }
        } catch (e: IOException) {
            CommandResult.Error(code, e.localizedMessage ?: "Сетевая ошибка при отправке команды")
        } catch (e: Exception) {
            CommandResult.Error(code, e.localizedMessage ?: "Неизвестная ошибка")
        }
    }

    /**
     * Проверяет доступность телевизора и получает информацию из dd.xml
     * Эндпоинт: http://<IP>:56790/dd.xml
     */
    suspend fun fetchDeviceInfo(ip: String): TvInfo? = withContext(Dispatchers.IO) {
        val url = "http://$ip:56790/dd.xml"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    val friendlyName = extractTag(body, "friendlyName") ?: "Vestel Smart TV"
                    val tvVersion = extractTag(body, "tv_version") ?: "MB130_VESTEL"
                    TvInfo(friendlyName = friendlyName, tvVersion = tvVersion, ip = ip, isOnline = true)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            // Если dd.xml недоступен, но порт 56789 может отвечать
            null
        }
    }

    private fun extractTag(xml: String, tag: String): String? {
        val pattern = Pattern.compile("<$tag>(.*?)</$tag>", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(xml)
        return if (matcher.find()) matcher.group(1)?.trim() else null
    }
}
