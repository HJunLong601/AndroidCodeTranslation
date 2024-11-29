package com.hjl.translatecode.preset

import org.xml.sax.InputSource
import java.io.FileInputStream
import javax.xml.parsers.SAXParserFactory

class XMLKeyParser(path: String) : IPresetKeyParser(path) {
    override fun parseKey(map: MutableMap<String, String>) {
        val handler = StringXMLHandler()
        try {
            val factory = SAXParserFactory.newInstance()
            val saxParser = factory.newSAXParser()
            saxParser.parse(InputSource(FileInputStream(path)), handler)

            map.putAll(handler.map)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}