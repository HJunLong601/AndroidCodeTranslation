package com.hjl.translatecode

interface ICodeStringFilter {


    companion object {
        val outputLog = false
    }

    fun filter(string: String): Boolean

    fun log(msg: String) {
        if (!outputLog) return
        println(msg)
    }


}