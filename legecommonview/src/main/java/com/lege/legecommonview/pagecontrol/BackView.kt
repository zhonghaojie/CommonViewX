package com.lege.legecommonview.pagecontrol

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.lege.legecommonview.R

/**
 * Description:
 * Created by loctek on 2020/12/1.
 */
class BackView : View {


    private var color :Int = Color.BLACK
    private var maxDragLength = 40
    private val paint = Paint()
    var currentDragLength = 0
        set(value) {
            if(value > maxDragLength){
                field = maxDragLength
            }else{
                field = value
            }
            invalidate()
        }
    private val path = Path()
    constructor(context: Context?) : this(context, null, -1)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context?.let {ctx->
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.BackView)
            typedArray?.let{
                maxDragLength = it.getDimensionPixelSize(R.styleable.BackView_max_drag_length,50)
                currentDragLength = it.getDimensionPixelSize(R.styleable.BackView_current_drag_length,0)
                color = it.getColor(R.styleable.BackView_background_color,Color.BLACK)
                val strokeWidth  = it.getDimensionPixelSize(R.styleable.BackView_stroke_width,20)
                it.recycle()
                paint.color = color
                paint.style  = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth.toFloat()
                paint.strokeCap = Paint.Cap.ROUND
                paint.strokeJoin = Paint.Join.ROUND

            }
        }
//        setLayerType(LAYER_TYPE_NONE,paint)
    }

    fun onExpand(){
        val animator = ObjectAnimator.ofInt(this,"currentDragLength",0,maxDragLength)
        animator.duration = 200
        animator.start()
    }
    fun onClose(){
        val animator = ObjectAnimator.ofInt(this,"currentDragLength",currentDragLength,0)
        animator.duration = 200
        animator.start()
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height  = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = maxDragLength+15
        setMeasuredDimension(width,height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        path.reset()
        path.moveTo(paint.strokeWidth/2f,paint.strokeWidth/2f)
        path.lineTo( currentDragLength.toFloat()+paint.strokeWidth/2f,measuredHeight/2f)
        path.lineTo(paint.strokeWidth/2f,measuredHeight.toFloat()-paint.strokeWidth/2f)
        canvas?.drawPath(path,paint)
    }

}