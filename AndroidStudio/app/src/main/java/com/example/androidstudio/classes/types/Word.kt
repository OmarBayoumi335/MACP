package com.example.androidstudio.classes.types

data class Word (var text: String,
                 var color: String,
                 var direction: String) {

    constructor(): this("", "", "")

    override fun toString(): String {
        return "${text}_${color}_${direction}"
    }
}