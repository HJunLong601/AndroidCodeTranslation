package com.hjl.translatecode.preset

abstract class IPresetKeyParser(val path: String) {

    /**
     * 解析预设的中文 - key 映射表
     */
    abstract fun parseKey(map: MutableMap<String, String>)

}