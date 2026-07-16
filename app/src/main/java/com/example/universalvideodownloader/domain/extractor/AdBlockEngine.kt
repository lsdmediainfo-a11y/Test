package com.example.universalvideodownloader.domain.extractor

import android.net.Uri

class AdBlockEngine {
    // Normalde bunlar assets/filters/ altındaki dosyalardan yüklenir
    private val adDomains = setOf("adserver.com", "doubleclick.net", "ads.example.com")
    private val popupDomains = setOf("popup.net", "redirect-tracker.com")
    private val allowedMediaDomains = setOf("cdn.video.com", "trusted-player.net")
    
    // WebChromeClient / WebViewClient seviyesi filtreleme (Katman 3)
    fun shouldBlockResource(url: String): Boolean {
        val uri = Uri.parse(url)
        val host = uri.host ?: return false
        
        if (allowedMediaDomains.any { host.contains(it) }) return false
        if (adDomains.any { host.contains(it) }) return true
        
        // VAST veya VPAID reklam kancaları
        if (url.contains("vast") || url.contains("vpaid") || url.contains("ad_type=")) return true
        
        return false
    }
    
    // Popup Engelleme (Katman 1 ve 2)
    fun shouldBlockPopup(url: String, isUserGesture: Boolean): Boolean {
        if (!isUserGesture) return true // Kullanıcı tıklaması olmadan açılanlar engellenir
        
        val uri = Uri.parse(url)
        val host = uri.host ?: return false
        if (popupDomains.any { host.contains(it) }) return true
        
        return false
    }
}
