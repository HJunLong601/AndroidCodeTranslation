package com.hjl.translatecode.processor

import com.hjl.translatecode.TransCodeUtils
import com.hjl.translatecode.TranslateConfig
import java.io.File
import java.io.FileWriter

class CodeFileProcessor(val filterFileName: List<String>) : IProcessor {
    override fun process(config: TranslateConfig) {
        println("CodeFileProcessor start ")

        val dir = File("${config.modulePath}/src/main/java")

        TransCodeUtils.traverseFolder(dir) {
            val isJava = it.name.endsWith("java")
            println("check code file ${it.absolutePath} is java :$isJava")

            if (it.name in filterFileName) {
                println("skip file ${it.absolutePath}")
                return@traverseFolder
            }

            if (!isJava && !it.name.endsWith("kt")) {
                println("skip file ${it.absolutePath}")
                return@traverseFolder
            }


            val data = it.readText()

            val result = TransCodeUtils.transOriginJavaCode(data, config.keyMap, isJava)

            FileWriter(it).apply {
                write(result)
                close()
            }
        }
    }
}