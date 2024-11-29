package com.hjl.translatecode.preset

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class JsonKeyParser(path: String) : IPresetKeyParser(path) {
    override fun parseKey(map: MutableMap<String, String>) {

        val jsonObject = Gson().fromJson(File(path).readText(), JsonObject::class.java)
        jsonObject.keySet().forEach {
            map[jsonObject.get(it).asString] = it
        }

    }
}

fun main() {

    val json = """
        {
           "test":"测试",
            "test2":"测试2"
        }
    """.trimIndent()

    val jsonObject = Gson().fromJson(json, JsonObject::class.java)
    val map = HashMap<String, String>()
    jsonObject.keySet().forEach {
        map[jsonObject.get(it).asString] = it
    }
    println(map)


}