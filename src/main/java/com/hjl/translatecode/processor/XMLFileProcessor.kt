package com.hjl.translatecode.processor

import com.hjl.translatecode.TransCodeUtils
import com.hjl.translatecode.TranslateConfig
import java.io.File
import java.io.FileWriter

class XMLFileProcessor : IProcessor {
    override fun process(config: TranslateConfig) {
        println("XMLFileProcessor start process")

        val xmlDir = File("${config.modulePath}/src/main/res/layout")

        if (xmlDir.exists()) {
            xmlDir.listFiles()!!.filter { it.name.endsWith("xml") }.forEach {
                println("check xml file ${it.absolutePath}")
                val data = it.readText()
                val result = TransCodeUtils.translateOriginXML(data, config.keyMap)

                FileWriter(it).apply {
                    write(result)
                    close()
                }

            }
        }

    }
}