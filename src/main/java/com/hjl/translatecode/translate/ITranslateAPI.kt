package com.hjl.translatecode.translate

interface ITranslateAPI {


    companion object {
        val instance: ITranslateAPI by lazy {
            BaiduTranslateImpl.instance!!
        }
    }

    fun translateCN(input: String): String

    fun translateEN(input: String): String

    fun translate(input: String?, type: String): String

}