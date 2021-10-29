package com.lege.commonview.tarottest

import android.graphics.PointF
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs


/**
 * Description:
 * Created by loctek on 2020/12/7.
 */
class PokerLayoutManager @JvmOverloads constructor(
    private val orientation: Int = HORIZONTAL,
    private val reverseLayout: Boolean = false,
    private val offset: Int = 0,
    private val changeDrawingOrder: Boolean = false
) : androidx.recyclerview.widget.RecyclerView.LayoutManager(), androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider {

    companion object {
        const val HORIZONTAL = androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
        const val VERTICAL = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL

        const val FILL_START_TO_END = 1
        const val FILL_END_TO_START = -1
    }

    //将要scrollTo的Position
    private var mPendingScrollPosition: Int = androidx.recyclerview.widget.RecyclerView.NO_POSITION

    //当前要填充view的索引
    private var mCurrentPosition: Int = 0

    //填充view的方向
    private var mFillDirection: Int = FILL_END_TO_START

    //填充view的坐标，横向就是X，纵向就是Y
    private var mFillAnchorXY: Int = 0

    //要回收的view集合
    private val mOutChildren = hashSetOf<View>()

    //每次fill view后就记录下开始child和结束child的position
    private var mStartPosition: Int = androidx.recyclerview.widget.RecyclerView.NO_POSITION
    private var mEndPosition: Int = androidx.recyclerview.widget.RecyclerView.NO_POSITION

    //记录当次滚动的距离
    private var mLastScrollDelta: Int = 0

    private var mFixOffset: Int = 0

    override fun generateDefaultLayoutParams(): androidx.recyclerview.widget.RecyclerView.LayoutParams {
        return androidx.recyclerview.widget.RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    /**
     * 布局初始化的方法
     * 键盘弹出或收起会重新回调这个方法
     * scrollToPosition也会，smoothScrollToPosition不会
     */
    override fun onLayoutChildren(
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        logDebug("onLayoutChildren")

        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }

        //不支持预测动画，可以直接return
        if (state.isPreLayout) return

        //计算填充view的方向
        //正序就是start->end，reverseLayout就是end->start
        mFillDirection = if (reverseLayout) FILL_END_TO_START else FILL_START_TO_END

        when {
            isScrollToCase() -> {//scrollToPosition的layoutChildren
                mCurrentPosition = mPendingScrollPosition
                calcLayoutDirectionByScrollToPosition()
            }
            isKeyBoardCase() -> {//软键盘弹出收起的layoutChildren
                mCurrentPosition = getBeginFillPosition()
                mFixOffset = getFixOffset()
            }
            else -> {//正常的layoutChildren
                mCurrentPosition = 0
            }
        }

        //轻量级的将view移除屏幕，还是会存在于缓存中
        detachAndScrapAttachedViews(recycler)
        //开始填充view
        fillLayout(recycler, state)
    }

    //scrollToPosition->item填充的方向
    // 正序mPendingScrollPosition>mEndPosition就是FILL_START_TO_END，正序mPendingScrollPosition<mStartPosition就是FILL_START_TO_END
    //逆序mPendingScrollPosition>mStartPosition就是FILL_START_TO_END，正序mPendingScrollPosition<mEndPosition就是FILL_END_TO_START
    private fun calcLayoutDirectionByScrollToPosition() {
        if (reverseLayout) {
            if (mPendingScrollPosition >= mStartPosition) {
                mFillDirection = FILL_START_TO_END
            }
            if (mPendingScrollPosition <= mEndPosition) {
                mFillDirection = FILL_END_TO_START
            }
        } else {
            if (mPendingScrollPosition >= mEndPosition) {
                mFillDirection = FILL_END_TO_START
            }
            if (mPendingScrollPosition <= mStartPosition) {
                mFillDirection = FILL_START_TO_END
            }
        }
    }

    private fun isScrollToCase(): Boolean {
        return mPendingScrollPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION
    }

    private fun isKeyBoardCase(): Boolean {
        return mLastScrollDelta != 0 ||
                (mStartPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION
                        && mEndPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION)
    }

    override fun onLayoutCompleted(state: androidx.recyclerview.widget.RecyclerView.State) {
        logDebug("onLayoutCompleted")
        mPendingScrollPosition = androidx.recyclerview.widget.RecyclerView.NO_POSITION
    }

    override fun canScrollHorizontally() = orientation == HORIZONTAL

    override fun canScrollVertically() = orientation == VERTICAL

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        if (orientation == VERTICAL) return 0

        return scrollBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        if (orientation == HORIZONTAL) return 0

        return scrollBy(dy, recycler, state)
    }

    //delta > 0 向右或者下滑，反之则反
    private fun scrollBy(
        delta: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        if (childCount == 0 || delta == 0) {
            return 0
        }

        val consume = fillScroll(delta, recycler, state)
        offsetChildren(-consume)
        recycleChildren(delta, recycler)

        mLastScrollDelta = consume

        return consume
    }

    override fun scrollToPosition(position: Int) {
        if (childCount == 0 || position < 0 || position > itemCount - 1) return

        if (mStartPosition == androidx.recyclerview.widget.RecyclerView.NO_POSITION
            || mEndPosition == androidx.recyclerview.widget.RecyclerView.NO_POSITION
        ) return

        if (mPendingScrollPosition in mStartPosition..mEndPosition)
            return

        mPendingScrollPosition = position
        requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller =
            androidx.recyclerview.widget.LinearSmoothScroller(recyclerView.context)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }


    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = getPosition(getChildAt(0)!!)
        val direction = if (targetPosition < firstChildPos != reverseLayout) -1 else 1
        return if (orientation == HORIZONTAL) {
            PointF(direction.toFloat(), 0f)
        } else {
            PointF(0f, direction.toFloat())
        }
    }
    //---- 自定义方法开始
    private fun fill(
        available: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        var remainingSpace = abs(available)
        logDebug("remainingSpace = $remainingSpace   ")
        while (remainingSpace > 0 && hasMore(state)) {

            val child = nextView(recycler)

            if (mFillDirection == FILL_START_TO_END) {
                addView(child)
            } else {
                addView(child, 0)
            }

            measureChildWithMargins(child, 0, 0)

            logDebug("")
            layoutChunk(child)

            remainingSpace -= getItemSpace(child) / 2
        }

        if (!state.isMeasuring) {
            calcStartEndPosition()
        }

//        logChildren(recycler)
        logChildrenPosition(recycler)

        return available
    }

    /**
     * 摆放子view
     */
    private fun layoutChunk(
        child: View
    ) {
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        if (orientation == HORIZONTAL) {
            if (mFillDirection == FILL_END_TO_START) {
                right = mFillAnchorXY
                left = right - getItemWidth(child)
            } else {
                left = mFillAnchorXY
                right = left + getItemWidth(child)
            }
            top = paddingTop
            bottom = top + getItemHeight(child) - paddingBottom
        } else {
            if (mFillDirection == FILL_END_TO_START) {
                bottom = mFillAnchorXY
                top = bottom - getItemHeight(child)
            } else {
                top = mFillAnchorXY
                bottom = top + getItemHeight(child)
            }
            left = paddingLeft
            right = left + getItemWidth(child)
        }

        layoutDecoratedWithMargins(child, left, top, right, bottom)

        if (mFillDirection == FILL_END_TO_START) {
            mFillAnchorXY -= (getItemSpace(child) / 2 + offset)
        } else {
            mFillAnchorXY += (getItemSpace(child) / 2 + offset)
        }


    }

    private fun fillLayout(
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        //计算填充view的初始锚点
        mFillAnchorXY = calcAnchorCoordinate()

        fill(getTotalSpace(), recycler, state)

        //fix软键盘弹出时rv已经滚动了的偏移量
        fixScrollOffset(recycler, state)
    }

    private fun fixScrollOffset(
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        if (mFixOffset != 0) {
            scrollBy(-mFixOffset, recycler, state)
            mFixOffset = 0
        }
    }

    /**
     * 计算开始填充view的锚点
     */
    private fun calcAnchorCoordinate() = if (orientation == HORIZONTAL) {
        if (mFillDirection == FILL_END_TO_START) {
            width - paddingRight
        } else {
            paddingLeft
        }
    } else {
        if (mFillDirection == FILL_END_TO_START) {
            height - paddingBottom
        } else {
            paddingTop
        }
    }

    private fun fillScroll(
        delta: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        return if (delta > 0) {
            mFillDirection = FILL_START_TO_END
            fillEnd(delta, recycler, state)
        } else {
            mFillDirection = FILL_END_TO_START
            fillStart(delta, recycler, state)
        }
    }

    //delta < 0
    private fun fillStart(
        delta: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        //如果startView结束的边减去`加上`移动的距离还是没出现在屏幕内
        //那么就可以继续滚动，不填充view
        val startView = getStartView()
        val startViewDecoratedEnd = getDecoratedEnd(startView)
        if (startViewDecoratedEnd - delta < getStart()) {
            return delta
        }

        //如果 startPosition == 0 且startPosition的开始的边加上移动的距离
        //大于等于Recyclerview的最小宽度或高度，就返回修正过后的移动距离
        val startViewDecoratedStart = getDecoratedStart(startView)
        val startPosition = getPosition(startView)
        //已经拖动到了最左边或者顶部
        if (startPosition == getFirstPosition(state) && startViewDecoratedStart - delta >= getStart()) {
            return startViewDecoratedStart - getStart()
        }

        resetCurrentPosition(startPosition)

        mFillAnchorXY = startViewDecoratedStart + getItemSpace(startView) / 2

        return fill(delta, recycler, state)
    }

    //delta > 0
    private fun fillEnd(
        delta: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler,
        state: androidx.recyclerview.widget.RecyclerView.State
    ): Int {
        //如果endView的开始的边`减去`移动的距离还是没出现在屏幕内
        //那么就可以继续滚动，不填充view
        val endView = getEndView()
        val endViewDecoratedStart = getDecoratedStart(endView)
        if (endViewDecoratedStart - delta > getEnd()) {
            return delta
        }

        //如果 endPosition == itemCount - 1 且endView的结束的边减去移动的距离
        //小于等于Recyclerview的最大宽度或高度，就返回修正过后的移动距离
        val endViewDecoratedEnd = getDecoratedEnd(endView)
        val endPosition = getPosition(endView)
        if (endPosition == getLastPosition(state) && endViewDecoratedEnd - delta <= getEnd()) {
            return endViewDecoratedEnd - getEnd()
        }

        resetCurrentPosition(endPosition)

        mFillAnchorXY = endViewDecoratedEnd - getItemSpace(endView) / 2

        return fill(delta, recycler, state)
    }


    /**
     * fillStart == -1
     * fillEnd == 1
     * 重新计算当前要填充view的position
     */
    private fun resetCurrentPosition(position: Int) {
        mCurrentPosition = if (reverseLayout) {
            position - mFillDirection
        } else {
            position + mFillDirection
        }
    }

    /**
     * 获取itemCount第一个view的position
     */
    private fun getFirstPosition(state: androidx.recyclerview.widget.RecyclerView.State) =
        if (reverseLayout) state.itemCount - 1 else 0

    /**
     * 获取itemCount最后一个view的position
     */
    private fun getLastPosition(state: androidx.recyclerview.widget.RecyclerView.State) =
        if (reverseLayout) 0 else state.itemCount - 1

    /**
     * 回收超出屏幕的view
     */
    private fun recycleChildren(
        consume: Int,
        recycler: androidx.recyclerview.widget.RecyclerView.Recycler
    ) {
        if (childCount == 0 || consume == 0) return

        if (consume > 0) {
            recycleStart()
        } else {
            recycleEnd()
        }

        recycleOutChildren(recycler)
    }

    private fun recycleStart() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)!!
            val end = getRecycleStartEdge(child)
            if (end > getStart()) break

//            logDebug("recycleStart -- ${getPosition(child)}")
            mOutChildren.add(child)
        }
    }

    private fun recycleEnd() {
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)!!
            val start = getRecycleEndEdge(child)
            if (start < getEnd()) break

//            logDebug("recycleEnd -- ${getPosition(child)}")
            mOutChildren.add(child)
        }
    }

    private fun getRecycleStartEdge(child: View) = if (orientation == HORIZONTAL) {
        getDecoratedRight(child)
    } else {
        getDecoratedBottom(child)
    }

    private fun getRecycleEndEdge(child: View) = if (orientation == HORIZONTAL) {
        getDecoratedLeft(child)
    } else {
        getDecoratedTop(child)
    }

    private fun recycleOutChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler) {
        for (view in mOutChildren) {
            removeAndRecycleView(view, recycler)
        }
        mOutChildren.clear()
    }
//---- 自定义方法结束

//---- 模仿创建OrientationHelper帮助类开始

    private fun hasMore(state: androidx.recyclerview.widget.RecyclerView.State): Boolean {
        return mCurrentPosition >= 0 && mCurrentPosition < state.itemCount
    }

    private fun getTotalSpace(): Int {
        return if (orientation == HORIZONTAL) {
            width - paddingLeft - paddingRight
        } else {
            height - paddingTop - paddingBottom
        }
    }

    private fun calcStartEndPosition() {
        if (childCount == 0) return
        mStartPosition = getPosition(getStartView())
        mEndPosition = getPosition(getEndView())
    }

    /**
     * 移动所有子view
     */
    private fun offsetChildren(amount: Int) {
        if (orientation == HORIZONTAL) {
            offsetChildrenHorizontal(amount)
        } else {
            offsetChildrenVertical(amount)
        }
    }

    /**
     * 获取下一个待填充的view
     */
    private fun nextView(recycler: androidx.recyclerview.widget.RecyclerView.Recycler): View {
        val view = recycler.getViewForPosition(mCurrentPosition)

        if (reverseLayout) {
            mCurrentPosition -= mFillDirection
        } else {
            mCurrentPosition += mFillDirection
        }

        return view
    }

    private fun getItemWidth(child: View): Int {
        val params = child.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
        return getDecoratedMeasuredWidth(child) + params.leftMargin + params.rightMargin
    }

    private fun getItemHeight(child: View): Int {
        val params = child.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
        return getDecoratedMeasuredHeight(child) + params.topMargin + params.bottomMargin
    }

    private fun getItemSpace(child: View) = if (orientation == HORIZONTAL) {
        getItemWidth(child)
    } else {
        getItemHeight(child)
    }

    private fun getStart(): Int {
        return if (orientation == HORIZONTAL) {
            paddingLeft
        } else {
            paddingTop
        }
    }

    private fun getEnd(): Int {
        return if (orientation == HORIZONTAL) {
            width - paddingRight
        } else {
            height - paddingBottom
        }
    }

    private fun getDecoratedStart(child: View): Int {
        return if (orientation == HORIZONTAL) {
            getDecoratedLeft(child)
        } else {
            getDecoratedTop(child)
        }
    }

    private fun getDecoratedEnd(child: View): Int {
        return if (orientation == HORIZONTAL) {
            getDecoratedRight(child)
        } else {
            getDecoratedBottom(child)
        }
    }

    private fun getStartView() = getChildAt(0)!!

    private fun getChildStartPosition() = getPosition(getStartView())

    private fun getEndView() = getChildAt(childCount - 1)!!

    private fun getChildEndPosition() = getPosition(getEndView())

    private fun getBeginFillPosition() = if (reverseLayout) {
        getChildEndPosition()
    } else {
        getChildStartPosition()
    }

    private fun getFixOffset(): Int {
        if (childCount == 0) return 0
        return if (reverseLayout)
            getDecoratedEnd(getEndView()) - getEnd()
        else
            getDecoratedStart(getStartView())
    }

//---- 模仿创建OrientationHelper帮助类结束

    private fun logDebug(msg: String) {
        Log.d("StackLayoutManager", msg)
    }

    private fun logChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler) {
        logDebug("childCount = $childCount -- scrapSize = ${recycler.scrapList.size}")
    }

    private fun logChildrenPosition(recycler: androidx.recyclerview.widget.RecyclerView.Recycler) {
        val builder = StringBuilder()
        for (i in 0 until childCount) {
            val child = getChildAt(i)!!
            builder.append(getPosition(child))
            builder.append(",")
        }
        logDebug("child position == $builder")
    }

    private fun logOutChildren() {
        val builder = StringBuilder()
        for (view in mOutChildren) {
            builder.append(getPosition(view))
            builder.append(",")
        }
        logDebug("out children == ${builder.toString()}")
    }

    override fun onAttachedToWindow(view: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToWindow(view)

        //改变children绘制顺序
        if (changeDrawingOrder) {
            view.setChildDrawingOrderCallback { childCount, i ->
                childCount - 1 - i
            }
        }
    }

}