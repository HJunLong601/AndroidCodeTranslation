package com.hjl.translatecode

data class TranslateConfig(
        val presetKeyMap: MutableMap<String, String>, // 预设的 中文 - key 映射表
        val keyMap: MutableMap<String, String>, // 中文 - key 映射表
        val modulePath: String,     // 模块路径
        val stringTemplateAction: (xmlKey: String?) -> String,
        val outputLog: Boolean = false, // 一些过滤掉的字符串日志输出
        val scanOnly: Boolean = false, // 只扫描 文件 不做替换和string的key生成，用于简单过一遍模块，减少不必要的api调用
        var importArray: Array<String> = emptyArray()
)