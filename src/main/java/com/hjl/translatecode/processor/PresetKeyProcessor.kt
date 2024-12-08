package com.hjl.translatecode.processor

import com.hjl.translatecode.TranslateConfig
import com.hjl.translatecode.preset.IPresetKeyParser

/**
 * 解析预设的 中文 - key 映射表
 */
class PresetKeyProcessor(private val keyParsers: Array<IPresetKeyParser>) : IProcessor {
    override fun process(config: TranslateConfig) {
        println("PresetKeyProcessor start ")

        keyParsers.forEach {
            it.parseKey(config.presetKeyMap)
        }

        println("preset map : ${config.presetKeyMap.size}")

        config.keyMap.putAll(config.presetKeyMap)
    }
}