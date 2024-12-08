package com.hjl.translatecode.processor

import com.hjl.translatecode.TranslateConfig

interface IProcessor {

    fun process(config: TranslateConfig)

}