package com.monday.learncanvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.Nullable
import androidx.core.view.GestureDetectorCompat
import com.bytedance.coloring.ext.dpToPx

/**
 * ClassName: Doodle
 * Description:
 * author 彭赞
 * since 2020-04-06  17:37
 * version 1.0
 */
class DoodleView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val tagName = this.javaClass.simpleName


    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f.dpToPx
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private var path: Path = Path()

    private val pathList = mutableListOf<Path>()


    private lateinit var rect: Rect
    private val rectWidth = 100.dpToPx
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f.dpToPx
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private lateinit var pointer: Point
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f.dpToPx
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.RED
    }

    private var currentScale = 1f


    private val detector = GestureDetectorCompat(context,
        object : GestureDetector.SimpleOnGestureListener() {

            private var trackingPointerId = 0
            private var lastX: Float = 0f
            private var lastY: Float = 0f

            private fun resetLastXY(eventX: Float, eventY: Float) {
                //x轴
                lastX = resizeScaledX(eventX)

                //y轴
                lastY = resizeScaledY(eventY)

//                lastY = (eventY) * currentScale
//                lastY = (eventY) / currentScale
//                lastX = (eventX)
//                lastY = (eventY)
            }

            private fun resizeScaledX(eventX: Float): Float {
                //x轴
//                return when {
//                    eventX > scalePointX -> {
//                        //x-
//                        (eventX) / currentScale
//
//                    }
//                    eventX == scalePointX -> {
//                        eventX
//                    }
//                    else -> {
//                        //x+
//                        (eventX) * currentScale
//
//                    }
//                }
                return eventX / currentScale
            }

            private fun resizeScaledY(eventY: Float): Float {
                //y轴
//                return when {
//                    eventY > scalePointY -> {
//                        //Y+
//                        (eventY) * currentScale
//
//                    }
//                    eventY == scalePointY -> {
//                        eventY
//                    }
//                    else -> {
//                        //Y-
//                        ( eventY) / currentScale
//                    }
//                }
                return eventY / currentScale
            }

            override fun onDown(event: MotionEvent): Boolean {
                initDrawingPath()
                trackingPointerId = event.getPointerId(event.actionIndex)
                resetLastXY(event.x, event.y)
                path.moveTo(lastX, lastY)
                invalidate()
                return true
            }

            override fun onScroll(
                firstDownEvent: MotionEvent,
                currentEvent: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                //两个手指不绘制
                if (currentEvent.pointerCount > 1) return false
                //只绘制第一个手指
                val index: Int = currentEvent.findPointerIndex(trackingPointerId)
                if (index == -1) return true

//                path.quadTo(
//                    lastX,
//                    lastY,
//                    (lastX + (scalePointX + currentEvent.getX(index)) / currentScale) / 2,
//                    (lastY + (scalePointY + currentEvent.getY(index)) / currentScale) / 2
//                )
                //x轴
                val lineToX = resizeScaledX(currentEvent.getX((index)))

                //y轴
                val lineToY = resizeScaledY(currentEvent.getY((index)))
                path.lineTo(
                    lineToX,
                    lineToY
                )
//                path.lineTo(
//                    currentEvent.getX(index),
//                    currentEvent.getY(index)
//                )
                //重新绘制
                bufferCanvas.drawPath(path, paint)
                invalidate()
                resetLastXY(currentEvent.getX(index), currentEvent.getY(index))
//                lastX = currentEvent.getX(index) / currentScale
//                lastY = currentEvent.getY(index) / currentScale
                return true
            }
        })


    private var scalePointX = 0f
    private var scalePointY = 0f
    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.OnScaleGestureListener {

            private var initialCurrentScale: Float = 0f

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                initialCurrentScale = currentScale
                scalePointX = detector.focusX
                scalePointY = detector.focusY
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {

                scalePointX = detector.focusX
                scalePointY = detector.focusY
                currentScale = initialCurrentScale * detector.scaleFactor
                currentScale = currentScale.coerceAtLeast(1f)
                loge("lll onScale ==> currentScale = $currentScale")
                invalidate()
                return false
            }

        })

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private lateinit var bufferBitmap: Bitmap
    private lateinit var bufferCanvas: Canvas

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bufferCanvas = Canvas(bufferBitmap)
        rect = Rect(
            (width - rectWidth) / 2,
            (height - rectWidth) / 2,
            (width + rectWidth) / 2,
            (height + rectWidth) / 2
        )
        pointer = Point(width / 2, height / 2)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = true
        if (event.pointerCount > 1) {
            result = scaleDetector.onTouchEvent(event)
        } else {
            if (!scaleDetector.isInProgress && event.pointerCount == 1) {
                result = detector.onTouchEvent(event)
            }
        }
        return result
    }

    private fun initDrawingPath() {
        path = Path()
        pathList.add(Path(path))
    }


    override fun onDraw(canvas: Canvas) {
        canvas.save()
//        canvas.scale(currentScale, currentScale, 0f, 0f)
        canvas.scale(currentScale, currentScale, scalePointX, scalePointY)
//        canvas.translate((width - width * currentScale) / 2, (height - height * currentScale) / 2)
        canvas.drawRect(rect, rectPaint)
        canvas.drawPoint(20f.dpToPx, 20f.dpToPx, pointerPaint)
        canvas.drawBitmap(bufferBitmap, 0f, 0f, null)
        canvas.drawPoint(scalePointX, scalePointY, pointerPaint)
        canvas.restore()

    }

    private fun loge(log: String) {
        Log.e(tagName, log)
    }
}