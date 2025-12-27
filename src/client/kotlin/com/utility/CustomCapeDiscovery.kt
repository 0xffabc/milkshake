package com.client.github.utility

import java.net.URL

import java.util.regex.Pattern
import java.util.regex.Matcher
import java.util.Base64

object CustomCapeDiscovery {
    const val CAPE_REGEX = """\"raw_url\":\"(https://gist\.githubusercontent\.com/\w+/\w+/raw/\w+/\w+\.cape\.txt)"""

    fun getCapeUrl(playerName: String): String {
        val gistsUrl = "https://api.github.com/users/${playerName}/gists"

        val text = URL(gistsUrl).readText()
        val pattern = Pattern.compile(CAPE_REGEX, Pattern.MULTILINE)
        val matcher = pattern.matcher(text)

        if (matcher.find()) {
            return matcher.group(1)
        }

        return ""
    }

    fun fetchCapeDataUrl(playerName: String): String {
        val capeUrl = getCapeUrl(playerName)

        println("[!] Fetching cape bytes from $capeUrl")

        return URL(capeUrl).readText()
    }

    fun fetchCapeBytes(playerName: String): ByteArray {
        val capeUrl = fetchCapeDataUrl(playerName)

        val base64 = capeUrl.substringAfterLast("base64,")!!
        val decoded = Base64.getDecoder().decode(base64)

        return decoded
    }
}
