package com.kian.mahmoudi.vegang.data.config

import android.util.Log
import com.kian.mahmoudi.vegang.dto.ProfileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

object GeoLocator {

    private const val TAG = "GeoLocator"
    private const val BATCH_URL = "http://ip-api.com/batch"
    private const val BATCH_SIZE = 100

    private val client = OkHttpClient()

    data class Location(val countryCode: String, val country: String, val city: String)

    suspend fun locateServers(addresses: List<String>): List<Location?> {
        val results = MutableList<Location?>(addresses.size) { null }
        if (addresses.isEmpty()) return results

        withContext(Dispatchers.IO) {
            val unique = addresses.filter { it.isNotBlank() }.distinct()

            unique.chunked(BATCH_SIZE).forEach { chunk ->
                val payload = JSONArray()
                chunk.forEach { addr ->
                    payload.put(
                        JSONObject()
                            .put("query", addr)
                            .put("fields", "status,countryCode,country,city")
                    )
                }
                try {
                    val request = Request.Builder()
                        .url(BATCH_URL)
                        .post(payload.toString().toRequestBody("application/json".toMediaType()))
                        .build()

                    client.newCall(request).execute().use { response ->
                        val body = response.body?.string() ?: return@use
                        if (response.isSuccessful) {
                            val array = JSONArray(body)
                            for (i in 0 until array.length()) {
                                val obj = array.optJSONObject(i)
                                if (obj != null && obj.optString("status") == "success") {
                                    val loc = Location(
                                        countryCode = obj.optString("countryCode", "??"),
                                        country = obj.optString("country", "Unknown"),
                                        city = obj.optString("city", "")
                                    )
                                    val addr = chunk[i]
                                    addresses.forEachIndexed { idx, a -> if (a == addr) results[idx] = loc }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Batch lookup failed", e)
                }
            }
        }
        return results
    }

    fun flagEmoji(countryCode: String): String {
        val cc = countryCode.uppercase(Locale.ROOT)
        if (cc.length != 2 || !cc.all { it in 'A'..'Z' }) return ""
        val base = 0x1F1E6
        return cc.map { code ->
            val cp = base + (code - 'A')
            String(Character.toChars(cp))
        }.joinToString("")
    }

    fun buildLocationRemarks(
        configs: List<ProfileItem>,
        locations: List<Location?>
    ): List<ProfileItem> {
        val counters = mutableMapOf<String, Int>()

        return configs.mapIndexed { index, config ->
            val loc = locations.getOrNull(index) ?: return@mapIndexed config

            val code = loc.countryCode.ifBlank { "??" }
            val num = (counters[code] ?: 0) + 1
            counters[code] = num

            val flag = flagEmoji(code)
            val city = if (loc.city.isNotBlank()) " - ${loc.city}" else ""
            config.copy(remarks = "$flag $code-$num  ${loc.country}$city")
        }
    }
}