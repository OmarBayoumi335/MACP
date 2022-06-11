package com.example.androidstudio.classes.types

data class Word (var text: String,
                 var color: String,
                 var direction: String) {

    constructor(): this("", "", "")

    override fun toString(): String {
        return "${text}_${color}_${direction}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word

        if (text != other.text) return false
        if (color != other.color) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + direction.hashCode()
        return result
    }
}