package com.cricfy

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import org.json.JSONArray

@CloudstreamPlugin
class CricfyPlugin: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(CricfyLiveTV())
    }
}

class CricfyLiveTV : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/itsyourap/cricfy-kodi-plugin/main"
    override var name = "Cricfy Live TV"
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "hi"
    override val hasMainPage = true

    private val channelsDataUrl = "$mainUrl/resources/data/channels.json"

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val homeSections = mutableListOf<HomePageList>()
        try {
            val response = app.get(channelsDataUrl).text
            val items = JSONArray(response)
            val liveCricket = mutableListOf<SearchResponse>()
            val sportsChannels = mutableListOf<SearchResponse>()

            for (i in 0 until items.length()) {
                val obj = items.getJSONObject(i)
                val channelName = obj.optString("name", "Live Match")
                val streamLink = obj.optString("url")
                val logo = obj.optString("logo", "https://i.imgur.com/8QG9gXp.png")
                val category = obj.optString("category", "Sports")

                val card = LiveSearchResponse(
                    name = channelName,
                    url = streamLink,
                    apiName = this@CricfyLiveTV.name,
                    type = TvType.Live,
                    posterUrl = logo
                )

                if (category.contains("Cricket", ignoreCase = true) || category.contains("Live", ignoreCase = true)) {
                    liveCricket.add(card)
                } else {
                    sportsChannels.add(card)
                }
            }
            if (liveCricket.isNotEmpty()) homeSections.add(HomePageList("🏏 Live Matches", liveCricket))
            if (sportsChannels.isNotEmpty()) homeSections.add(HomePageList("📺 Sports Channels", sportsChannels))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return HomePageResponse(homeSections)
    }

    override suspend fun load(url: String): LoadResponse {
        return LiveStreamLoadResponse(
            name = "Live Stream",
            url = url,
            apiName = this.name,
            dataUrl = url
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val streamHeaders = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
            "Referer" to "https://cricfy.com/"
        )
        callback.invoke(
            ExtractorLink(
                source = this.name,
                name = "Direct Live Stream (HD)",
                url = data,
                referer = streamHeaders["Referer"] ?: "",
                quality = Qualities.P1080.value,
                isM3u8 = data.contains(".m3u8"),
                headers = streamHeaders
            )
        )
        return true
    }
}
