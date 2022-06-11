package com.example.androidstudio.classes.types

data class Clue(var text: String,
                var number: Int,
                var directions: MutableList<String>) {

    constructor(): this("", 0, mutableListOf())
    override fun toString(): String {
        return "$text-$number-$directions"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Clue

        if (text != other.text) return false
        if (number != other.number) return false
        if (directions != other.directions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + number
        result = 31 * result + directions.hashCode()
        return result
    }
}