package com.hjl.translatecode.filter

class NoteCodeFilter : ICodeStringFilter {
    override fun filter(string: String): Boolean {

        if (string.contains("tools") || string.contains("//") ||
                string.contains("/*") || string.contains("@")) {
            log("ignore string in NoteCodeFilter with $string")
            return true
        }

        return false
    }
}