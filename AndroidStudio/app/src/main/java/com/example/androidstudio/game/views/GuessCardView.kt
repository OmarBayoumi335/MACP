package com.example.androidstudio.game.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withMatrix
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Card
import com.example.androidstudio.classes.utils.Config
import kotlin.math.roundToInt
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
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL
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
        style = Paint.Style.FILL
    }

    private var divisionX by Delegates.notNull<Float>()
    private var centerY by Delegates.notNull<Float>()
    private var leftCenterX by Delegates.notNull<Float>()
    private var compassDiameter by Delegates.notNull<Float>()

    lateinit var card: Card
    private val padding = 7f * resources.displayMetrics.density
    private lateinit var compass: Bitmap
    private var lastAcceleration = FloatArray(3)
    private var lastMagnetic = FloatArray(3)
    private var orientation = FloatArray(3)
    private var compassRotationMatrix = FloatArray(9)
    private var yaw = 0f

    private fun setKeyPoints(canvas: Canvas?) {
        divisionX = width.toFloat() * 0.50f
        centerY = height.toFloat() * 0.50f
        leftCenterX = divisionX * 0.50f
        compassDiameter = if (divisionX < height) {
            divisionX * 0.70f
        } else {
            height * 0.70f
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setKeyPoints(canvas)


        // I will delete these lines
        canvas?.drawLine(divisionX, 0f, divisionX, height.toFloat(), linePaint)
        canvas?.drawLine(0f, centerY, divisionX, centerY, linePaint)
        canvas?.drawLine(leftCenterX, 0f, leftCenterX, height.toFloat(), linePaint)

//        val compass = BitmapFactory.decodeStream(context.assets.open("compass.png"))
//        circlePaint.shader = BitmapShader(compass, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
//        canvas?.drawCircle(leftCenterX, centerY, compassRadius, circlePaint)

        compass = ResourcesCompat.getDrawable(resources, R.drawable.ic_compass1, null)?.toBitmap(compassDiameter.toInt(), compassDiameter.toInt())!!
        val rotation = Matrix()
        //rotation.postRotate(-yaw*180/Math.PI.toFloat(), leftCenterX, centerY)
        rotation.postRotate(Math.toDegrees(yaw.toDouble()).toFloat(), leftCenterX, centerY)
        canvas?.withMatrix(rotation) {
            drawBitmap(compass, leftCenterX - compassDiameter/2f, centerY - compassDiameter/2f, null)
        }
        val colors = intArrayOf(
            Color.DKGRAY,
            Color.LTGRAY,
            Color.GRAY
        )
        val vertices = floatArrayOf(
            0f, 0f,
            100f, 0f,
            50f, 100f
        )
        val t = Matrix()
        t.setTranslate(leftCenterX - 50f, centerY - 100 - compassDiameter/2 - padding*2)
        canvas?.withMatrix(t){
            canvas.drawVertices(
                Canvas.VertexMode.TRIANGLES,
                vertices.size,
                vertices,
                0,
                null,
                0,
                colors,
                0,
                null,
                0,
                0,
                trianglePaint
            )
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
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
        Log.i(Config.GAME_VIEW_TAG, "Not yet implemented")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        Log.i(Config.GAME_VIEW_TAG, "Not yet implemented")
    }
}