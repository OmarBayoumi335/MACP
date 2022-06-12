package com.example.androidstudio.classes.types

import android.graphics.RectF

data class Card(var word: Word,
                var squareCoordinates: RectF,
                var index: Int,
                var turned: Boolean) {

    constructor(): this(Word(), RectF(), 0, false)

    override fun toString(): String {
        return "Card(word=$word, squareCoordinates=$squareCoordinates, index=$index, turned=$turned)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

        if (word != other.word) return false
        if (squareCoordinates != other.squareCoordinates) return false
        if (index != other.index) return false
        if (turned != other.turned) return false

        return true
    }

    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + squareCoordinates.hashCode()
        result = 31 * result + index
        result = 31 * result + turned.hashCode()
        return result
    }

}