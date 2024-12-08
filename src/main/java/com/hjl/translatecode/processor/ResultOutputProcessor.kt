package com.hjl.translatecode.processor

import com.hjl.translatecode.TransCodeUtils
import com.hjl.translatecode.TranslateConfig
import java.io.File
import java.io.FileWriter

class ResultOutputProcessor : IProcessor {
    override fun process(config: TranslateConfig) {
        println("ResultOutputProcessor start ")

        println("====================  findXMLKey ${config.keyMap.size} =======================")

        val resultXMLSb = StringBuilder()

        // 过滤掉已经存在的key

        config.presetKeyMap.keys.forEach {
            config.keyMap.remove(it)
        }

        println("after filter preset key  ${config.keyMap.size}")

        config.keyMap.keys.forEach {

            if (config.presetKeyMap.containsKey(it)) return@forEach

            val generateXMLItem = TransCodeUtils.generateXMLItem(config.keyMap[it]!!, it)
            resultXMLSb.append(generateXMLItem).append("\n")
        }

        // 输出生成的string item 并复制到粘贴板
        // 结果输出到模块目录
        println(resultXMLSb)
        TransCodeUtils.copyToClipboard(resultXMLSb.toString())
        File("${config.modulePath}/appendedTranslateResult.txt").apply {
            println("write result:${this.absolutePath}")
            createNewFile()
            FileWriter(this).apply {
                write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<resources>\n")
                write(resultXMLSb.toString())
                write("\n" +
                        "</resources>")
                close()
            }
        }

        println("=============================================")


        println("scan result : ${TransCodeUtils.scanList.size}")
        println("scan result : ${TransCodeUtils.scanList}")

        println("==============================================")
    }
}