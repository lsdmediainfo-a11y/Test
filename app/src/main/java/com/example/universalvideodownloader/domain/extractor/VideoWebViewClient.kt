package com.example.universalvideodownloader.domain.extractor

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.UUID

class VideoWebViewClient(
    private val onNetworkEventCaptured: (CapturedNetworkEvent) -> Unit
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        request?.let {
            val url = it.url.toString()
            val method = it.method
            
            if (url.contains(".m3u8") || url.contains(".mp4") || url.contains(".webm") || 
                url.contains(".mpd") || url.contains(".ts") || url.contains(".m4s") || 
                url.contains(".m4v") || url.contains(".mkv") || url.contains(".aac") || url.contains(".m4a")) {
                
                val event = CapturedNetworkEvent(
                    id = UUID.randomUUID().toString(),
                    url = url,
                    method = method,
                    pageUrl = view?.url,
                    frameUrl = null,
                    requestHeaders = it.requestHeaders ?: emptyMap(),
                    cookie = null, 
                    source = CaptureSource.WEBVIEW_INTERCEPT
                )
                onNetworkEventCaptured(event)
            }
        }
        return null 
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        if (url != null && (url.contains(".m3u8") || url.contains(".mp4") || url.contains(".webm") || 
            url.contains(".mpd") || url.contains(".ts") || url.contains(".m4s"))) {
             val event = CapturedNetworkEvent(
                id = UUID.randomUUID().toString(),
                url = url,
                method = "GET",
                pageUrl = view?.url,
                frameUrl = null,
                requestHeaders = emptyMap(),
                cookie = null,
                source = CaptureSource.LOAD_RESOURCE
            )
            onNetworkEventCaptured(event)
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        
        val js = """
            (function() {
                if (window.UVD_INJECTED) return;
                window.UVD_INJECTED = true;

                function reportMedia(url, source, extraParams = {}) {
                    console.log("UVD_MEDIA_FOUND: [" + source + "] " + url, extraParams);
                }

                // 1. Fetch Observer
                const originalFetch = window.fetch;
                window.fetch = async function() {
                    let requestUrl = arguments[0];
                    if (typeof requestUrl === 'string' && requestUrl.match(/\.(m3u8|mp4|webm|mpd|ts|m4s|m4v|mkv|aac|m4a)/i)) {
                        reportMedia(requestUrl, 'FETCH');
                    }
                    return originalFetch.apply(this, arguments);
                };

                // 2. XHR Observer
                const originalXHR = window.XMLHttpRequest.prototype.open;
                window.XMLHttpRequest.prototype.open = function(method, url) {
                    if (typeof url === 'string' && url.match(/\.(m3u8|mp4|webm|mpd|ts|m4s|m4v|mkv|aac|m4a)/i)) {
                        reportMedia(url, 'XHR');
                    }
                    return originalXHR.apply(this, arguments);
                };

                // 3. Performance API Observer
                if (window.PerformanceObserver) {
                    const observer = new PerformanceObserver((list) => {
                        for (const entry of list.getEntries()) {
                            if (entry.name.match(/\.(m3u8|mp4|webm|mpd|ts|m4s|m4v|mkv|aac|m4a)/i)) {
                                reportMedia(entry.name, 'PERFORMANCE_RESOURCE');
                            }
                        }
                    });
                    observer.observe({entryTypes: ['resource']});
                }

                // 4. Video Playback Session Events (Bölüm 6)
                function bindVideoEvents(video) {
                    if(video.uvd_bound) return;
                    video.uvd_bound = true;

                    const events = ['play', 'playing', 'loadedmetadata', 'durationchange'];
                    events.forEach(evt => {
                        video.addEventListener(evt, () => {
                            reportMedia(video.currentSrc || video.src, 'VIDEO_PLAYBACK_EVENT', { eventType: evt, duration: video.duration });
                        });
                    });
                }

                // 5. Mutation Observer (video, source elementleri ve currentSrc değişimi için)
                const mutObserver = new MutationObserver((mutations) => {
                    mutations.forEach((mutation) => {
                        mutation.addedNodes.forEach((node) => {
                            if (node.tagName === 'VIDEO') {
                                bindVideoEvents(node);
                                if(node.src) reportMedia(node.src, 'VIDEO_ELEMENT');
                            }
                            if (node.tagName === 'SOURCE' && node.src) {
                                reportMedia(node.src, 'SOURCE_ELEMENT');
                            }
                        });
                        
                        // src attribute değişikliklerini izle
                        if(mutation.type === 'attributes' && (mutation.attributeName === 'src' || mutation.attributeName === 'currentSrc')) {
                            if (mutation.target.tagName === 'VIDEO' || mutation.target.tagName === 'SOURCE') {
                                reportMedia(mutation.target.src || mutation.target.currentSrc, 'SRC_CHANGED');
                            }
                        }
                    });
                });
                
                // Sayfadaki mevcut videoları bağla
                document.querySelectorAll('video').forEach(bindVideoEvents);

                mutObserver.observe(document.documentElement, { 
                    childList: true, 
                    subtree: true,
                    attributes: true,
                    attributeFilter: ['src', 'currentSrc']
                });

            })();
        """.trimIndent()
        view?.evaluateJavascript(js, null)
    }
}
