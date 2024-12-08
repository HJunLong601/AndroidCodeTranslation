package com.hjl.translatecode.processor

import com.hjl.translatecode.TransCodeUtils
import com.hjl.translatecode.TranslateConfig
import com.hjl.translatecode.translate.ITranslateAPI

class GenerateEnglishProcessor : IProcessor {
    override fun process(config: TranslateConfig) {
        println("GenerateEnglishProcessor start ")

        //        // 生成英文
        val englishStringBuilder = StringBuilder()

        config.keyMap.keys.forEach {
            val translateEN = ITranslateAPI.instance.translateEN(it)
            val englishItem = TransCodeUtils.generateXMLItem(config.keyMap[it]!!, translateEN)
            englishStringBuilder.append(englishItem).append("\n")
        }

        println("English item :\n $englishStringBuilder")
    }
}