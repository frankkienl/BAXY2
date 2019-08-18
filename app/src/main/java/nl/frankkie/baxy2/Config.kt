package nl.frankkie.baxy2

import android.content.Context
import java.io.IOException
import java.util.*

/**
 * Get the API key
 * This is in a file that's not under source-control
 *
 * @param context application context to access the assets
 * @return apiKey if found, null otherwise
 */
fun getApiKey(context: Context): String? {
    val assetManager = context.assets
    try {
        val assets = assetManager.list("")
        for (asset in assets!!) {
            if (asset == "apikey.txt") { //if exists
                val assetInputStream = assetManager.open(asset)
                val scanner = Scanner(assetInputStream)
                return scanner.next()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }

    return null
}