package com.example.androidstudio.game.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Card
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.game.GuessCardFragment
import kotlin.properties.Delegates

class GameView(context: Context?) : View(context), View.OnTouchListener{

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
    private val cardCircleVotePaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    private val cardTextVotePaint = Paint().apply {
        color = Color.CYAN
        textSize = 9f * resources.displayMetrics.density
    }
    private val chatBoxTeam1Paint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.chat_game_background_team1)
        style = Paint.Style.FILL
    }
    private val chatBoxTeam2Paint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.chat_game_background_team2)
        style = Paint.Style.FILL
    }
    private val chatBoxBorderPaint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.chat_game_border)
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * resources.displayMetrics.density
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
                cards.add(
                    Card(
                        gameLobby.words[i * 4 + j],
                        roundRectCard,
                        i * 4 + j,
                        gameLobby.words[i * 4 + j].turned
                    )
                )
                // set the color of the card
                if (userGame.userId == gameLobby.captainIndex1
                    || userGame.userId == gameLobby.captainIndex2
                    || gameLobby.words[i * 4 + j].turned
                ) {
//                val cardGreen = BitmapFactory.decodeStream(context.assets.open("greenBackCard.jpg"))
//                roundRectPaint.shader = BitmapShader(cardGreen, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                    when (gameLobby.words[i * 4 + j].color) {
                        "green" -> roundRectPaint.color = Color.GREEN
                        "gray" -> roundRectPaint.color = Color.GRAY
                        "black" -> roundRectPaint.color = Color.BLACK
                        "red" -> roundRectPaint.color = Color.RED
                    }
                } else {
                    roundRectPaint.color = Color.LTGRAY
                }
                // draw the card
                canvas?.drawRoundRect(
                    roundRectCard,
                    20f * resources.displayMetrics.density,
                    15f * resources.displayMetrics.density,
                    roundRectPaint
                )
                // if the card is not turned
                if (!gameLobby.words[i * 4 + j].turned) {
                    // set coordinates for the text word
                    var textBound = Rect()
                    cardTextPaint.getTextBounds(
                        gameLobby.words[i * 4 + j].text,
                        0,
                        gameLobby.words[i * 4 + j].text.length,
                        textBound
                    )
                    var textX = roundRectCard.centerX() - textBound.exactCenterX()
                    var textY = roundRectCard.centerY() - textBound.exactCenterY()
                    // draw the text
                    canvas?.drawText(gameLobby.words[i * 4 + j].text, textX, textY, cardTextPaint)

                    // if the turn phase is not "captain choosing the clue"
                    if (gameLobby.turnPhase != 0) {
                        // find the number of votes for this card
                        var numberVoteCard = 0
                        for (member in gameLobby.members) {
                            if (member.vote == i * 4 + j) {
                                numberVoteCard += 1
                            }
                        }
                        // if the number of votes is greater than 0 show the votes
                        if (numberVoteCard != 0) {
                            // draw the circle that contain the number of votes
                            canvas?.drawCircle(
                                roundRectCard.right - padding,
                                roundRectCard.top + padding,
                                10f * resources.displayMetrics.density,
                                cardCircleVotePaint
                            )
                            // set coordinate for votes word
                            textBound = Rect()
                            cardTextVotePaint.getTextBounds(
                                numberVoteCard.toString(),
                                0,
                                numberVoteCard.toString().length,
                                textBound
                            )
                            textX = roundRectCard.right - padding - textBound.exactCenterX()
                            textY = roundRectCard.top + padding - textBound.exactCenterY()
                            // draw the text votes
                            canvas?.drawText(
                                numberVoteCard.toString(),
                                textX,
                                textY,
                                cardTextVotePaint
                            )
                        }
                        // if my vote is on this card show it with a symbol
                        if (userGame.vote == i * 4 + j) {
                            // draw the symbol
                            canvas?.drawCircle(
                                roundRectCard.left + padding,
                                roundRectCard.top + padding,
                                10f * resources.displayMetrics.density,
                                cardCircleVotePaint
                            )
                        }
                    }
                    // if im the captain show the direction of the card
                    if (userGame.userId == gameLobby.captainIndex1 || userGame.userId == gameLobby.captainIndex2) {
                        // set coordinates for the direction word
                        textBound = Rect()
                        cardTextPaint.getTextBounds(
                            gameLobby.words[i * 4 + j].direction,
                            0,
                            gameLobby.words[i * 4 + j].direction.length,
                            textBound
                        )
                        textX = roundRectCard.left + padding
                        textY = roundRectCard.bottom - padding
                        // draw the direction word
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
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        cards = mutableListOf()
        val coordinates = setKeyPoints()
        drawCards(coordinates, canvas)
        val chatBox = RectF()
        chatBox.left = endGridx
        chatBox.right = endRightPartX
        chatBox.top = topGridY
        chatBox.bottom = bottomGridY
        if (userGame.team == resources.getString(R.string.team1)) {
            canvas?.drawRoundRect(
                chatBox,
                10f * resources.displayMetrics.density,
                10f * resources.displayMetrics.density,
                chatBoxTeam1Paint
            )
        } else {
            canvas?.drawRoundRect(
                chatBox,
                10f * resources.displayMetrics.density,
                10f * resources.displayMetrics.density,
                chatBoxTeam2Paint
            )
        }
        canvas?.drawRoundRect(
            chatBox,
            10f * resources.displayMetrics.density,
            10f * resources.displayMetrics.density,
            chatBoxBorderPaint
        )
//        canvas?.drawLine(endGridx, bottomGridY, endGridx, topGridY, linePaint)
//        canvas?.drawLine(endGridx, topGridY, endRightPartX, topGridY, linePaint)
//        canvas?.drawLine(endGridx, bottomGridY, endRightPartX, bottomGridY, linePaint)
//        canvas?.drawLine(endRightPartX, bottomGridY, endRightPartX, topGridY, linePaint)
//        canvas?.drawLine(endGridx, turnChatY, endRightPartX, turnChatY, chatBoxTeam1BorderPaint)
//        canvas?.drawLine(endGridx, upY, endRightPartX, upY, chatBoxTeam1BorderPaint)
//        canvas?.drawLine(endGridx, chatTextY, endRightPartX, chatTextY, chatBoxTeam1BorderPaint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (userGame.userId != gameLobby.captainIndex1
            && userGame.userId != gameLobby.captainIndex2
            && gameLobby.turn == userGame.team
            && gameLobby.turnPhase >= 1
        ) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    for (card in cards) {
                        if (
                            event.x >= card.squareCoordinates.left
                            && event.x <= card.squareCoordinates.right
                            && event.y >= card.squareCoordinates.top
                            && event.y <= card.squareCoordinates.bottom
                            && !card.turned
                        ) {
                            val guessCardFragment = GuessCardFragment(card, userGame, gameLobby)
                            guessCardFragment.show(
                                activity.supportFragmentManager,
                                "GameView->GuessCardView"
                            )
                            Log.i(Config.GAME_VIEW_TAG, card.word.toString())
//                            break
                        }
                    }
                }
            }
        }
        return true
    }
}