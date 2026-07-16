package com.example.universalvideodownloader.domain.extractor

import com.example.universalvideodownloader.domain.extractor.resolvers.DirectFileResolver
import com.example.universalvideodownloader.ui.browser.capture.CapturedNetworkEvent
import com.example.universalvideodownloader.ui.browser.capture.PlaybackCaptureSession
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ResolverPipelineTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var resolver: DirectFileResolver

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val contentVerifier = ContentVerifier()
        resolver = DirectFileResolver(contentVerifier)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `DirectFileResolver resolves valid MP4 url`() = runBlocking {
        // Sahte (Mock) MP4 format imzası ve sunucu yanıtı oluşturulur
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "video/mp4")
                // MP4 dosyaları ftyp izini taşır, ContentVerifier bunu okur.
                .setBody("00000000ftypisom00000000") 
        )

        val baseUrl = mockWebServer.url("/video.mp4").toString()
        
        val event = CapturedNetworkEvent(
            id = "test_event_1",
            url = baseUrl,
            type = "media",
            source = "network_intercept"
        )
        
        val session = PlaybackCaptureSession("session_test", "http://example.com")
        
        val context = ResolveContext(event, session)
        
        val result = resolver.resolve(context)
        
        assertTrue("Result resolved olmalı", result is ResolverOutcome.Resolved)
        val candidate = (result as ResolverOutcome.Resolved).candidate
        assertTrue("Media türü DIRECT_VIDEO olmalı", candidate.mediaType == MediaType.DIRECT_VIDEO)
        assertTrue("Uzantı mp4 olmalı", candidate.extension == "mp4")
    }
}
