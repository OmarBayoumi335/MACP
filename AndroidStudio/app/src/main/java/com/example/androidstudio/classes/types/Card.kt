package com.example.androidstudio.classes.types

import android.graphics.RectF

data class Card(var word: Word,
                var squareCoordinates: RectF
                ) {

    constructor(): this(Word(), RectF())

    override fun toString(): String {
        return "Card(word=$word, squareCoordinates=$squareCoordinates)"
    }
}