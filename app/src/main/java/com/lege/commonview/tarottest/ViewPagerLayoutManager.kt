package com.lege.commonview.tarottest

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import android.util.Log


/**
 * https://www.jianshu.com/p/93d402aa83a0
 * Description:
 * Created by loctek on 2020/11/27.
 */
class ViewPagerLayoutManager : androidx.recyclerview.widget.LinearLayoutManager {
    constructor(context: Context?, orientation: Int) : super(context, orientation, false) {
        init()
    }

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
        init()
    }

    companion object {
        const val TAG = "ViewPagerLayoutManager"
    }

    private var mPagerSnapHelper: androidx.recyclerview.widget.PagerSnapHelper? = null
    private var mOnViewPagerListener: OnViewPagerListener? = null
    private var mRecyclerView: androidx.recyclerview.widget.RecyclerView? = null

    //位移，用来判断移动方向
    private var mDrift = 0

    private fun init() {
        mPagerSnapHelper = androidx.recyclerview.widget.PagerSnapHelper()
    }

    override fun onAttachedToWindow(view: androidx.recyclerview.widget.RecyclerView?) {
        //RecyclerView.setLayoutManager会调用到这个方法，所以不需要在外部recyclerview.attachxxx了
        super.onAttachedToWindow(view)
        mPagerSnapHelper?.attachToRecyclerView(view)
        this.mRecyclerView = view
    }

    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state
     */
    override fun onScrollStateChanged(state: Int) {
        when (state) {
            androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE -> {
                //中间的View
                val viewCenter = mPagerSnapHelper!!.findSnapView(this)
                if (viewCenter != null) {
                    val positionIdle = getPosition(viewCenter)
                    if (mOnViewPagerListener != null ) {
                        mOnViewPagerListener!!.onPageSelected(positionIdle, positionIdle == itemCount - 1)
                    }
                }
            }
            androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING -> {
            }
            androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING -> {
            }
            else -> {
            }
        }
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: androidx.recyclerview.widget.RecyclerView.State?): Int {
        mDrift = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: Recycler?,
        state: androidx.recyclerview.widget.RecyclerView.State?
    ): Int {
        mDrift = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }


    /**
     * if return >= 0 snap is exist
     * if return < 0 snap is not exist
     *
     * @return
     */
    fun findSnapPosition(): Int {
        val viewIdle = mPagerSnapHelper!!.findSnapView(this)
        return viewIdle?.let { getPosition(it) } ?: -1
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    fun setOnViewPagerListener(listener: OnViewPagerListener?) {
        mOnViewPagerListener = listener
    }


    interface OnViewPagerListener {

        /**
         * 选中的监听以及判断是否滑动到底部
         *
         * @param position
         * @param isBottom
         */
        fun onPageSelected(position: Int, isBottom: Boolean)

    }
}