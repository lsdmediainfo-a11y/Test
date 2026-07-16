package com.example.universalvideodownloader.domain.extractor.parsers

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

data class DashRepresentation(
    val id: String?,
    val bandwidth: Int?,
    val width: Int?,
    val height: Int?,
    val codecs: String?,
    val mimeType: String?
)

class DashParser {
    fun parseMpd(xmlContent: String): List<DashRepresentation> {
        val representations = mutableListOf<DashRepresentation>()
        
        if (xmlContent.contains("ContentProtection")) {
             throw UnsupportedOperationException("DRM korumalı DASH yayınları (ContentProtection) desteklenmemektedir.")
        }
        
        try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(StringReader(xmlContent))
            
            var eventType = parser.eventType
            var currentMimeType: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name == "AdaptationSet") {
                            currentMimeType = parser.getAttributeValue(null, "mimeType")
                        } else if (name == "Representation") {
                            val id = parser.getAttributeValue(null, "id")
                            val bandwidth = parser.getAttributeValue(null, "bandwidth")?.toIntOrNull()
                            val width = parser.getAttributeValue(null, "width")?.toIntOrNull()
                            val height = parser.getAttributeValue(null, "height")?.toIntOrNull()
                            val codecs = parser.getAttributeValue(null, "codecs")
                            val mimeType = parser.getAttributeValue(null, "mimeType") ?: currentMimeType
                            
                            representations.add(
                                DashRepresentation(id, bandwidth, width, height, codecs, mimeType)
                            )
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return representations
    }
}
