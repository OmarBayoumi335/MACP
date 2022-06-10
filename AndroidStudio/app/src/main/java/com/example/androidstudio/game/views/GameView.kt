package com.example.androidstudio.game.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Card
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.game.ChooseDirectionFragment
import com.example.androidstudio.game.GuessCardFragment
import com.example.androidstudio.home.profile.ProfileFragment
import kotlin.properties.Delegates

class GameView: View, View.OnTouchListener {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setOnTouchListener(this)
    }

    // painter
    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val roundRectPaint = Paint().apply {
        strokeWidth = 5f
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val cardTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 14f * resources.displayMetrics.density
        isFakeBoldText = true
    }
    private val cardDirectionPaint = Paint().apply {
        color = Color.WHITE
        textSize = 12f * resources.displayMetrics.density
        isFakeBoldText = true
    }

    // border
    private var bottomGridY by Delegates.notNull<Float>()
    private var topGridY by Delegates.notNull<Float>()
    private var startGridX by Delegates.notNull<Float>()
    private var endGridx by Delegates.notNull<Float>()

    // center
    private var centerGridX by Delegates.notNull<Float>()
    private var centerGridY by Delegates.notNull<Float>()

    // center of quadrants
    private var leftX by Delegates.notNull<Float>()
    private var rightX by Delegates.notNull<Float>()
    private var upY by Delegates.notNull<Float>()
    private var downY by Delegates.notNull<Float>()

    // right part
    private var endRightPartX by Delegates.notNull<Float>()
    private var turnChatY by Delegates.notNull<Float>()
    private var chatTextY by Delegates.notNull<Float>()

    // bottom part
    private var bottomPartY by Delegates.notNull<Float>()
    private var directionNumberY by Delegates.notNull<Float>()

    private val padding = 7f * resources.displayMetrics.density
    lateinit var userGame: UserGame
    lateinit var gameLobby: GameLobby
    private var cards = mutableListOf<Card>()
    lateinit var activity: FragmentActivity

    private fun setKeyPoints(): MutableList<MutableList<Float>> {
        // border
        bottomGridY = height.toFloat() * (83f/100f)
        topGridY = height.toFloat() * (2f/100f)
        startGridX = width.toFloat() * (2f/100f)
        endGridx = width.toFloat() * (65f/100f)

        // center
        centerGridX = (startGridX + endGridx)/2f
        centerGridY = (bottomGridY + topGridY)/2f

        // center of quadrants
        leftX = (startGridX + centerGridX)/2f
        rightX = (centerGridX + endGridx)/2f
        upY = (topGridY + centerGridY)/2f
        downY = (centerGridY + bottomGridY)/2f

        // right part
        endRightPartX = width.toFloat() * (98f/100f)
        turnChatY = (topGridY + upY)/2f
        chatTextY = downY*(2f/3f) + bottomGridY*(1f/3f)

        // bottom part
        bottomPartY = height.toFloat() * (98f/100f)
        directionNumberY = endGridx*(2f/3f) + endRightPartX*(1f/3f)

        val xCoordinates = mutableListOf(startGridX, leftX, centerGridX, rightX, endGridx)
        val yCoordinates = mutableListOf(topGridY, upY, centerGridY, downY, bottomGridY)
        return mutableListOf(xCoordinates, yCoordinates)
    }

    private fun drawCards(coordinates: MutableList<MutableList<Float>>, canvas: Canvas?){
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                // set coordinates for the card
                val roundRectCard = RectF()
                roundRectCard.left = coordinates[0][i] + padding
                roundRectCard.top = coordinates[1][j] + padding
                roundRectCard.right = coordinates[0][i + 1] - padding
                roundRectCard.bottom = coordinates[1][j + 1] - padding
                cards.add(Card(gameLobby.words[i*4 + j], roundRectCard))
                // set the color of the card
                if (userGame.userId == gameLobby.captainIndex1 || userGame.userId == gameLobby.captainIndex2) {
//                val cardGreen = BitmapFactory.decodeStream(context.assets.open("greenBackCard.jpg"))
//                roundRectPaint.shader = BitmapShader(cardGreen, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                    when (gameLobby.words[i*4 + j].color) {
                        "green" -> roundRectPaint.color = Color.GREEN
                        "gray" -> roundRectPaint.color = Color.GRAY
                        "black" -> roundRectPaint.color = Color.BLACK
                        "red" -> roundRectPaint.color = Color.RED
                    }
                } else {
                   roundRectPaint.color = Color.LTGRAY
                }
                // draw the card
                canvas?.drawRoundRect(roundRectCard,
                    20f* resources.displayMetrics.density,
                    15f*resources.displayMetrics.density,
                    roundRectPaint
                )
                // set coordinates for the text word
                var textBound = Rect()
                cardTextPaint.getTextBounds(
                    gameLobby.words[i*4 + j].text,
                    0,
                    gameLobby.words[i*4 + j].text.length,
                    textBound
                )
                var textX = roundRectCard.centerX() - textBound.exactCenterX()
                var textY = roundRectCard.centerY() - textBound.exactCenterY()
                // draw the text
                canvas?.drawText(gameLobby.words[i*4 + j].text, textX, textY, cardTextPaint)
                // set coordinates for the direction word

                if (userGame.userId == gameLobby.captainIndex1 || userGame.userId == gameLobby.captainIndex2) {
                    textBound = Rect()
                    cardTextPaint.getTextBounds(
                        gameLobby.words[i * 4 + j].direction,
                        0,
                        gameLobby.words[i * 4 + j].direction.length,
                        textBound
                    )
                    textX = roundRectCard.left + padding
                    textY = roundRectCard.bottom - padding
                    // draw the text
                    canvas?.drawText(
                        gameLobby.words[i * 4 + j].direction,
                        textX,
                        textY,
                        cardDirectionPaint
                    )
                }
            }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // background turn
        if (gameLobby.turn == resources.getString(R.string.team2)) {
            canvas?.drawColor(ContextCompat.getColor(context, R.color.background_game_red))
        } else {
            canvas?.drawColor(ContextCompat.getColor(context, R.color.background_game_green))
        }
        val coordinates = setKeyPoints()
        drawCards(coordinates, canvas)



        // I will comment the grid lines. They are only a support
        // border
        canvas?.drawLine(startGridX, bottomGridY, endGridx, bottomGridY, linePaint)
        canvas?.drawLine(startGridX, topGridY, endGridx, topGridY, linePaint)
        canvas?.drawLine(startGridX, bottomGridY, startGridX, topGridY, linePaint)
        canvas?.drawLine(endGridx, bottomGridY, endGridx, topGridY, linePaint)
        // center
        canvas?.drawLine(startGridX, centerGridY, endGridx, centerGridY, linePaint)
        canvas?.drawLine(centerGridX, bottomGridY, centerGridX, topGridY, linePaint)
        // center of quadrants
        canvas?.drawLine(startGridX, upY, endGridx, upY, linePaint)
        canvas?.drawLine(startGridX, downY, endGridx, downY, linePaint)
        canvas?.drawLine(leftX, bottomGridY, leftX, topGridY, linePaint)
        canvas?.drawLine(rightX, bottomGridY, rightX, topGridY, linePaint)
        // right part
        canvas?.drawLine(endGridx, topGridY, endRightPartX, topGridY, linePaint)
        canvas?.drawLine(endGridx, bottomGridY, endRightPartX, bottomGridY, linePaint)
        canvas?.drawLine(endRightPartX, bottomGridY, endRightPartX, topGridY, linePaint)
        canvas?.drawLine(endGridx, turnChatY, endRightPartX, turnChatY, linePaint)
        canvas?.drawLine(endGridx, upY, endRightPartX, upY, linePaint)
        canvas?.drawLine(endGridx, chatTextY, endRightPartX, chatTextY, linePaint)
        // bottom part
//        canvas?.drawLine(startGridX, bottomPartY, endRightPartX, bottomPartY, linePaint)
//        canvas?.drawLine(startGridX, bottomGridY, startGridX, bottomPartY, linePaint)
//        canvas?.drawLine(endRightPartX, bottomGridY, endRightPartX, bottomPartY, linePaint)
//        canvas?.drawLine(rightX, bottomGridY, rightX, bottomPartY, linePaint)
//        canvas?.drawLine(centerGridX, bottomGridY, centerGridX, bottomPartY, linePaint)
//        canvas?.drawLine(directionNumberY, bottomGridY, directionNumberY, bottomPartY, linePaint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (userGame.userId != gameLobby.captainIndex1 && userGame.userId != gameLobby.captainIndex2) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (card in cards) {
                        if (
                            event.x >= card.squareCoordinates.left
                            && event.x <= card.squareCoordinates.right
                            && event.y >= card.squareCoordinates.top
                            && event.y <= card.squareCoordinates.bottom
                        ) {
                            val guessCardFragment = GuessCardFragment(card)
                            guessCardFragment.show(
                                activity.supportFragmentManager,
                                "GameView->GuessCardView"
                            )
                            Log.i(Config.GAME_VIEW_TAG, card.word.toString())
                        }
                    }
                }
            }
        }
        return true
    }
}