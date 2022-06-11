package com.example.androidstudio.classes.types

import android.graphics.RectF

data class Card(var word: Word,
                var squareCoordinates: RectF,
                var index: Int) {

    constructor(): this(Word(), RectF(), 0)

    override fun toString(): String {
        return "Card(word=$word, squareCoordinates=$squareCoordinates, index=$index)"
    }

}