package com.example.androidstudio.classes.types

data class Clue(var text: String,
                var number: Int,
                var directions: MutableList<String>) {

    constructor(): this("", 0, mutableListOf())
    override fun toString(): String {
        return "$text-$number-$directions"
    }
}