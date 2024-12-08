package com.hjl.translatecode.processor

import com.hjl.translatecode.TransCodeUtils
import com.hjl.translatecode.TranslateConfig
import com.hjl.translatecode.translate.ITranslateAPI
import com.hjl.translatecode.translate.RandomCharTranslateImpl

class InitProcessor : IProcessor {
    override fun process(config: TranslateConfig) {

        println("InitProcessor start ")
        // 翻译实现 实例
        ITranslateAPI.instance = RandomCharTranslateImpl()
//        ITranslateAPI.instance = BaiduTranslateImpl.instance!!

        TransCodeUtils.outputLog = config.outputLog
        TransCodeUtils.scanOnly = config.scanOnly


    }
}