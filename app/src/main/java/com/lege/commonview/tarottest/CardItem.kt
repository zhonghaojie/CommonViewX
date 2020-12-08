package com.lege.commonview.tarottest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.lege.android.base.util.BitmapUtil
import com.lege.commonview.R

/**
 * Description:
 * Created by loctek on 2020/11/27.
 */
class CardItem : View {

    private var originalHeight = -1
    private var camera: Camera? = null
    private var moveY: Int = 0

    //dy 正的往上滑，负的往下滑
    fun scroll(dy: Int) {
        if(visibility == View.VISIBLE){
//            moveY += dy
//            val percentY = dy / measuredHeight.toFloat()
            rotateX += -dy
        }

    }

    var rotateX: Int = 70
        set(value) {
            field = when {
                value > 70 -> {
                    moveY = 0
                    0
                }
                value < -70 -> {
                    moveY = 0
                    -70
                }
                else -> {
                    value
                }
            }
            if (originalHeight != -1) {
                viewHeight =
                    (originalHeight * Math.cos(Math.PI * 2 * (field.toDouble() / 360))).toInt()
            }
            Log.i("ZXCVB", "rotateX  $rotateX  viewHeight  $viewHeight")
            requestLayout()
//            invalidate()
        }
    var rotateY: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var rotateZ: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    private var animateDuration = 200
    var resource: Int = -1
        set(value) {
            field = value
            bmp = BitmapFactory.decodeResource(resources, field)
            requestLayout()
        }
    private var bmp: Bitmap? = null
    private val paint = Paint()
    private var viewHeight = 200
    private var viewWidth = 200

    constructor(context: Context?) : this(context, null, -1)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context?.let {
            val typedArray = it.obtainStyledAttributes(attrs, R.styleable.CardItem)
            typedArray?.let {
                resource = it.getResourceId(R.styleable.CardItem_src, -1)
                rotateX = it.getDimensionPixelSize(R.styleable.CardItem_rotateX, 0)
                rotateY = it.getDimensionPixelSize(R.styleable.CardItem_rotateY, 0)
                rotateZ = it.getDimensionPixelSize(R.styleable.CardItem_rotateZ, 0)
                viewHeight = it.getDimensionPixelSize(R.styleable.CardItem_view_height, 200)
                Log.d("ZXCVB", "viewHeight = $viewHeight")
                viewWidth = it.getDimensionPixelSize(R.styleable.CardItem_view_width, 200)
                animateDuration = it.getInt(R.styleable.CardItem_animateDuration, 200)
                it.recycle()
            }
        }
        if (resource != -1) {
            bmp = BitmapFactory.decodeResource(resources, resource)
        }
        camera = Camera()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(viewWidth, viewHeight)
        if (originalHeight == -1) {
            originalHeight = viewHeight
        }
        Log.d("ZXCVB", "onMeasure  viewHeight  $viewHeight")
        bmp = BitmapUtil.zoomImg(bmp, viewWidth, originalHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
//            it.drawRect(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat(), Paint().apply {
//                style = Paint.Style.STROKE
//                color = Color.RED
//                strokeWidth = 5f
//            })
            it.save()
            camera?.save()// 保存 Camera 的状态
            camera?.rotate(
                rotateX.toFloat(),
                rotateY.toFloat(),
                rotateZ.toFloat()
            )// 旋转 Camera 的三维空间
            it.translate(measuredWidth / 2f, 0f)// 旋转之后把投影移动回来
            camera?.applyToCanvas(it)// 把旋转投影到 Canvas
            it.translate(-measuredWidth / 2f, 0f)// 旋转之前把绘制内容移动到轴心（原点）
            camera?.restore()// 恢复 Camera 的状态
            it.drawBitmap(bmp, 0f, 0f, paint)
            canvas.restore()
        }


    }
}