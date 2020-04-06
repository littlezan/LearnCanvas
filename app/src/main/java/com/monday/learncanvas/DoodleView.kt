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

    private var currentScale = 1f


    private val detector = GestureDetectorCompat(context,
        object : GestureDetector.SimpleOnGestureListener() {

            private var trackingPointerId = 0
            private var lastX: Float = 0f
            private var lastY: Float = 0f

            override fun onDown(event: MotionEvent): Boolean {
                initDrawingPath()
                trackingPointerId = event.getPointerId(event.actionIndex)
                lastX = event.x / currentScale
                lastY = event.y / currentScale
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

                path.quadTo(
                    lastX,
                    lastY,
                    (lastX + currentEvent.getX(index) / currentScale) / 2,
                    (lastY + currentEvent.getY(index) / currentScale) / 2
                )
                //重新绘制
                bufferCanvas.drawPath(path, paint)
                invalidate()
                lastX = currentEvent.getX(index) / currentScale
                lastY = currentEvent.getY(index) / currentScale
                return true
            }
        })


    private var scalePointX = 0f
    private var scalePointY = 0f
    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.OnScaleGestureListener {

            private var bigScale = 1.5f
            private var smallScale = 1f
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
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = scaleDetector.onTouchEvent(event)
        if (!scaleDetector.isInProgress && event.pointerCount == 1) {
            result = detector.onTouchEvent(event)
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
//        if (scaleDetector.isInProgress) {

            canvas.scale(currentScale, currentScale, scalePointX, scalePointY)
//        }
//        canvas.translate((width - width * currentScale) / 2, (height - height * currentScale) / 2)
        canvas.drawRect(rect, rectPaint)
        canvas.drawBitmap(bufferBitmap, 0f, 0f, null)
        canvas.restore()

    }

    private fun loge(log: String) {
        Log.e(tagName, log)
    }
}