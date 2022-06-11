package com.example.androidstudio.game.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withMatrix
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Card
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.game.GuessCardFragment
import org.json.JSONObject
import kotlin.properties.Delegates


class GuessCardView: View, View.OnTouchListener, SensorEventListener2 {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setOnTouchListener(this)
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME
        )
    }

    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val circlePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private val trianglePaint = Paint().apply {
        color = Color.RED
    }
    private val titleBackgroundPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }
    private val titleTextPaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 40f * resources.displayMetrics.density
        isFakeBoldText = true
    }
    private val descriptionTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 25f * resources.displayMetrics.density
    }
    private val buttonBackgroundPaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }
    private val buttonBorderPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 4f * resources.displayMetrics.density
    }
    private val buttonTextPaint = Paint().apply {
        color = Color.LTGRAY
        textSize = 28f * resources.displayMetrics.density
    }

    private var divisionX by Delegates.notNull<Float>()
    private var centerY by Delegates.notNull<Float>()
    private var leftCenterX by Delegates.notNull<Float>()
    private var compassDiameter by Delegates.notNull<Float>()
    private var centerRightX by Delegates.notNull<Float>()
    private var oneQuarterRightX by Delegates.notNull<Float>()
    private var threeQuarterRightX by Delegates.notNull<Float>()

    lateinit var card: Card
    lateinit var guessCardFragment: GuessCardFragment
    lateinit var requireContext: Context
    lateinit var userGame: UserGame
    lateinit var gameLobby: GameLobby
    private val padding = 7f * resources.displayMetrics.density
    private lateinit var compass: Bitmap
    private var lastAcceleration = FloatArray(3)
    private var lastMagnetic = FloatArray(3)
    private var orientation = FloatArray(3)
    private var compassRotationMatrix = FloatArray(9)
    private var yaw = (Math.PI/4f).toFloat()
    private var startVibration = false
    private var vibrationOn = 0L
    private var vibrationOff = 0L
    private var cancelButtonRect = RectF()
    private var voteButtonRect = RectF()

    private fun setKeyPoints(canvas: Canvas?) {
        divisionX = width.toFloat() * 0.50f
        centerY = height.toFloat() * 0.50f
        leftCenterX = divisionX * 0.50f
        compassDiameter = if (divisionX < height) {
            divisionX * 0.70f
        } else {
            height * 0.70f
        }
        centerRightX = (width.toFloat() + divisionX) * 0.50f
        threeQuarterRightX = (width.toFloat() + centerRightX) * 0.50f
        oneQuarterRightX = (divisionX + centerRightX) * 0.50f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        vibrationOff = System.currentTimeMillis()
        setKeyPoints(canvas)

//        val compass = BitmapFactory.decodeStream(context.assets.open("compass.png"))
//        circlePaint.shader = BitmapShader(compass, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
//        canvas?.drawCircle(leftCenterX, centerY, compassRadius, circlePaint)

        // compass image
        compass = ResourcesCompat.getDrawable(resources, R.drawable.ic_compass1, null)?.toBitmap(compassDiameter.toInt(), compassDiameter.toInt())!!
        val rotation = Matrix()

        var roundYaw: Int = if (yaw < -Math.PI + 0.05 || yaw > Math.PI - 0.05) {
            -90
        } else {
            (90 - Math.toDegrees(yaw.toDouble())).toInt()
        }
        roundYaw = (roundYaw/5) * 5
        if (!startVibration && roundYaw == 90) {
            roundYaw = 50
            startVibration = true
        }

        // vibration
//        Log.i(Config.GAME_VIEW_TAG, roundYaw.toString())
        if (startVibration) {
            if ((card.word.direction == resources.getString(R.string.north) && roundYaw == 0)
                || (card.word.direction == resources.getString(R.string.west) && roundYaw == 90)
                || (card.word.direction == resources.getString(R.string.east) && roundYaw == -90)
                || (card.word.direction == resources.getString(R.string.south) && roundYaw == 180)) {
                if ((vibrationOff - vibrationOn) >= 250L) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        vibrationOn = System.currentTimeMillis()
                        val vibratorManager =
                            context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        val vib = vibratorManager.defaultVibrator
                        vib.vibrate(
                            VibrationEffect.createOneShot(
                                200,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        val vib = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
                        vib.vibrate(200)
                    }
                }
            }
        }

        // rotate compass
        rotation.postRotate(roundYaw.toFloat(), leftCenterX, centerY)
        canvas?.withMatrix(rotation) {
            drawBitmap(compass, leftCenterX - compassDiameter/2f, centerY - compassDiameter/2f, null)
        }

        // triangle indicator
        val path = Path()
        path.moveTo(-1f, 0f)
        path.lineTo(1f, 0f)
        path.lineTo(0f,  1f)
        path.lineTo(-1f, 0f)
        path.close()
        val s = Matrix()
        s.setScale(leftCenterX * (1f/10f), (height - centerY - compassDiameter/2f) * 0.5f)
        s.postTranslate(leftCenterX, 1.5f*centerY - 0.5f*compassDiameter/2f - 0.5f*height)
        canvas?.withMatrix(s){
            drawPath(path, trianglePaint)
        }

        // title
        var textBound = Rect()
        titleTextPaint.getTextBounds(
            card.word.text,
            0,
            card.word.text.length,
            textBound
        )
        canvas?.drawRoundRect(
            centerRightX - textBound.exactCenterX() - padding*2,
            centerY/2f + textBound.top - padding*2,
            centerRightX + textBound.exactCenterX() + padding*2,
            centerY/2f + textBound.bottom + padding*2,
            10f * resources.displayMetrics.density,
            10f * resources.displayMetrics.density,
            titleBackgroundPaint
        )
        var textX = centerRightX - textBound.exactCenterX()
        var textY = centerY/2f
        // draw the text
        canvas?.drawText(
            card.word.text,
            textX,
            textY,
            titleTextPaint
        )

        // text of the request part 1
        textBound = Rect()
        descriptionTextPaint.getTextBounds(
            resources.getString(R.string.vote_text1),
            0,
            resources.getString(R.string.vote_text1).length,
            textBound
        )
        textX = centerRightX - textBound.exactCenterX()
        textY = centerY + textBound.exactCenterY() - 3f * resources.displayMetrics.density
        // draw the text
        canvas?.drawText(
            resources.getString(R.string.vote_text1),
            textX,
            textY,
            descriptionTextPaint
        )

        // text of the request part 2
        textBound = Rect()
        descriptionTextPaint.getTextBounds(
            resources.getString(R.string.vote_text2),
            0,
            resources.getString(R.string.vote_text2).length,
            textBound
        )
        textX = centerRightX - textBound.exactCenterX()
        textY = centerY - textBound.top + 3f * resources.displayMetrics.density
        // draw the text
        canvas?.drawText(
            resources.getString(R.string.vote_text2),
            textX,
            textY,
            descriptionTextPaint
        )

        // cancel button
        textBound = Rect()
        buttonTextPaint.getTextBounds(
            resources.getString(R.string.lobby_cancel),
            0,
            resources.getString(R.string.lobby_cancel).length,
            textBound
        )
        cancelButtonRect.left = oneQuarterRightX - textBound.exactCenterX() - padding*2
        cancelButtonRect.top = centerY * (3f/2f) + textBound.top - padding*2
        cancelButtonRect.right = oneQuarterRightX + textBound.exactCenterX() + padding*2
        cancelButtonRect.bottom = centerY * (3f/2f) + textBound.bottom + padding*2
        canvas?.drawRoundRect(
            oneQuarterRightX - textBound.exactCenterX() - padding*2,
            centerY * (3f/2f) + textBound.top - padding*2,
            oneQuarterRightX + textBound.exactCenterX() + padding*2,
            centerY * (3f/2f) + textBound.bottom + padding*2,
            10f * resources.displayMetrics.density,
            10f * resources.displayMetrics.density,
            buttonBackgroundPaint
        )
        canvas?.drawRoundRect(
            oneQuarterRightX - textBound.exactCenterX() - padding*2,
            centerY * (3f/2f) + textBound.top - padding*2,
            oneQuarterRightX + textBound.exactCenterX() + padding*2,
            centerY * (3f/2f) + textBound.bottom + padding*2,
            10f * resources.displayMetrics.density,
            10f * resources.displayMetrics.density,
            buttonBorderPaint
        )
        textX = oneQuarterRightX - textBound.exactCenterX()
        textY = centerY * (3f/2f)
        // draw the text
        canvas?.drawText(
            resources.getString(R.string.lobby_cancel),
            textX,
            textY,
            buttonTextPaint
        )

        // vote button
        val textBound1 = Rect()
        buttonTextPaint.getTextBounds(
            resources.getString(R.string.vote),
            0,
            resources.getString(R.string.vote).length,
            textBound1
        )
        voteButtonRect.left = threeQuarterRightX - textBound.exactCenterX() - padding*2
        voteButtonRect.top = centerY * (3f/2f) + textBound.top - padding*2
        voteButtonRect.right = threeQuarterRightX + textBound.exactCenterX() + padding*2
        voteButtonRect.bottom = centerY * (3f/2f) + textBound.bottom + padding*2
        canvas?.drawRoundRect(
            threeQuarterRightX - textBound.exactCenterX() - padding*2,
            centerY * (3f/2f) + textBound.top - padding*2,
            threeQuarterRightX + textBound.exactCenterX() + padding*2,
            centerY * (3f/2f) + textBound.bottom + padding*2,
            10f * resources.displayMetrics.density,
            10f * resources.displayMetrics.density,
            buttonBackgroundPaint
        )
        canvas?.drawRoundRect(
            threeQuarterRightX - textBound.exactCenterX() - padding*2,
            centerY * (3f/2f) + textBound.top - padding*2,
            threeQuarterRightX + textBound.exactCenterX() + padding*2,
            centerY * (3f/2f) + textBound.bottom + padding*2,
            10f * resources.displayMetrics.density,
            10f * resources.displayMetrics.density,
            buttonBorderPaint
        )
        textX = threeQuarterRightX - textBound1.exactCenterX()
        textY = centerY * (3f/2f)
        // draw the text
        canvas?.drawText(
            resources.getString(R.string.vote),
            textX,
            textY,
            buttonTextPaint
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN -> {
                if (event.x >= cancelButtonRect.left
                    && event.x <= cancelButtonRect.right
                    && event.y >= cancelButtonRect.top
                    && event.y <= cancelButtonRect.bottom
                ) {
                    guessCardFragment.dismiss()
                }
                if (event.x >= voteButtonRect.left
                    && event.x <= voteButtonRect.right
                    && event.y >= voteButtonRect.top
                    && event.y <= voteButtonRect.bottom
                ) {
                    guessCardFragment.dismiss()
                    val serverHandler = ServerHandler(requireContext)
                    serverHandler.apiCall(
                        Config.POST,
                        Config.POST_VOTE,
                        userId = userGame.userId,
                        gameLobbyId = gameLobby.lobbyId,
                        voteIndex = card.index.toString()
                    )
                }
            }
        }
        return true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            lastAcceleration = event.values.clone()
        }
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            lastMagnetic = event.values.clone()
        }
        SensorManager.getRotationMatrix(
            compassRotationMatrix,
            null,
            lastAcceleration,
            lastMagnetic
        )

        SensorManager.getOrientation(compassRotationMatrix, orientation)
        yaw = -orientation[0]
        invalidate()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.i(Config.ERROR_TAG, "Not yet implemented")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        Log.i(Config.ERROR_TAG, "Not yet implemented")
    }
}