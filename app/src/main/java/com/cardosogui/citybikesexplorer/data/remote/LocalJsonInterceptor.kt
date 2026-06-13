package com.cardosogui.citybikesexplorer.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Answers every request from a bundled JSON asset instead of the network,
 * mapping the last path segment to a file in assets/ ("stations" -> stations.json).
 * Remove this interceptor from the OkHttpClient once the real API is available.
 */
class LocalJsonInterceptor @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val assetName = "${request.url.pathSegments.last()}.json"

        return try {
            val json = context.assets.open(assetName).bufferedReader().use { it.readText() }
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(json.toResponseBody(JSON_MEDIA_TYPE))
                .build()
        } catch (e: FileNotFoundException) {
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("Not Found")
                .body("""{"error":"no local asset named $assetName"}""".toResponseBody(JSON_MEDIA_TYPE))
                .build()
        }
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}
