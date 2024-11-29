package com.hjl.translatecode.preset

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class StringXMLHandler : DefaultHandler() {
    // string中文 - key值
    var map = LinkedHashMap<String, String>()

    @Volatile
    var key: String? = null

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        // Handle start of an element

//        System.out.println("startElement uri" + uri);
//        System.out.println("startElement localName" + localName);
//        System.out.println("startElement qName" + qName);
        key = attributes.getValue("name")
        //        System.out.println("Name value: " + nameValue);
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        // Handle end of an element
//        System.out.println("endElement uri" + uri);
//        System.out.println("endElement localName" + localName);
//        System.out.println("endElement qName" + qName);
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        // Handle characters within an element
        if (key == null || key!!.isEmpty()) return
        val data = String(ch, start, length)
        if (data.trim { it <= ' ' }.isEmpty()) return
        //        System.out.println("key:" + key);
//        System.out.println("characters:" + data);
//        System.out.println("start:" + start);
//        System.out.println("length:" + length);
        map[data] = key!!
    }
}
