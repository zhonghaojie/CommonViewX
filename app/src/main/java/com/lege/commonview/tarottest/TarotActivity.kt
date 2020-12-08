package com.lege.commonview.tarottest

import android.graphics.Color
import android.icu.util.UniversalTimeScale.MAX_SCALE
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.lege.commonview.R
import kotlinx.android.synthetic.main.activity_tarot.*
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.Utils
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Description:
 * Created by loctek on 2020/11/27.
 */
class TarotActivity : AppCompatActivity(), SwipeBackActivityBase,
    ViewPagerLayoutManager.OnViewPagerListener {
    companion object {
        const val WEEK_START = 1
        const val WEEK_END = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mActionBar = supportActionBar
        mActionBar?.hide()
        val decorView = window.decorView
        var systemUiVisibility = decorView.systemUiVisibility
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                //                 | View.SYSTEM_UI_FLAG_FULLSCREEN       // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        systemUiVisibility = systemUiVisibility or flags
        decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_tarot)

        mHelper = SwipeBackActivityHelper(this)

        mHelper?.onActivityCreate()
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
        swipeBackLayout.addSwipeListener(object : SwipeBackLayout.SwipeListener {
            override fun onScrollStateChange(state: Int, scrollPercent: Float) {
                Log.d("DDDDDDD", "onScrollStateChange   state  $state")
                if (state == 1) {
                    back.onExpand()
                } else if (state == 2) {
                    back.onClose()
                }
            }

            override fun onEdgeTouch(edgeFlag: Int) {
                Log.d("DDDDDDD", "onEdgeTouch   $edgeFlag")
            }

            override fun onScrollOverThreshold() {
                Log.d("DDDDDDD", "onScrollOverThreshold")
            }

        })
        initView()
        Log.i("日期", "${getWeekStartOrEnd(WEEK_START)}   ${getWeekStartOrEnd(WEEK_END)}")
    }

    private fun getWeekStartOrEnd(type: Int): String {
        val d = Date()
        val cal = Calendar.getInstance()
        var i: Int
        cal.time = d
        i = cal[Calendar.DAY_OF_WEEK]
        if (type == WEEK_START) {
            if (i == 1) {
                i = 8
            }
            val distance = (i - 1) - 1//前面的-1：把周一设置为1
            cal[Calendar.DATE] = cal[Calendar.DATE] - distance
            return "${cal[Calendar.MONTH] + 1}月${cal[Calendar.DAY_OF_MONTH]}日"
        } else {
            if (i == 1) {
                i = 8
            }
            val distance = 7 - (i - 1) //-1：把周一设置为1
            cal[Calendar.DATE] = cal[Calendar.DATE] + distance
            return "${cal[Calendar.MONTH] + 1}月${cal[Calendar.DAY_OF_MONTH]}日"
        }
    }

    private var resourceList = arrayListOf(
        R.drawable.pic_shu,
        R.drawable.pic_niu,
        R.drawable.pic_hu,
        R.drawable.pic_tu,
        R.drawable.pic_long,
        R.drawable.pic_she,
        R.drawable.pic_ma,
        R.drawable.pic_yang,
        R.drawable.pic_hou,
        R.drawable.pic_ji,
        R.drawable.pic_gou,
        R.drawable.pic_zhu
    )

    private fun initView() {
        inflater = LayoutInflater.from(this)
        adapter = object : RecyclerView.Adapter<MyViewHolder>() {
            private var selected:Int = 0
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                return MyViewHolder(inflater.inflate(R.layout.item_card2, parent, false))
            }

            override fun getItemCount(): Int {
                return resourceList.size
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                val iv = holder.itemView?.findViewById<ImageView>(R.id.iv)
                iv?.let{
                    val params = it.layoutParams as LinearLayout.LayoutParams
                    if(selected == position){
                        params.topMargin = resources.getDimensionPixelSize(R.dimen.y40)
                    }else{
                        params.topMargin = 0
                    }
                    it.layoutParams = params
                }

                holder.itemView?.setOnClickListener {
                    selected = position
                    notifyDataSetChanged()
                }
//                (holder.itemView).findViewById<ImageView>(R.id.card)
//                    .setImageResource(resourceList[position])
            }

        }
        val lm = PokerLayoutManager(offset = 0)
//        lm.setOnViewPagerListener(this)
        recyclerview.layoutManager = lm
        recyclerview.adapter = adapter
//        recyclerview.postDelayed({
//            recyclerview.addItemDecoration(CardItemDecoration())
//            midLineY = (recyclerview.top + recyclerview.bottom) / 2
//        }, 200)
//        recyclerview.addOnScrollListener(onScrollListener)
    }

    private var midLineY = -1
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val childCount = recyclerView?.childCount ?: 0

            (0 until childCount).forEach {
                recyclerView?.let { r ->
                    val child = r.getChildAt(it)
                    val lp = child.layoutParams
                    //顶部空间
                    val top = child.top
                    //底部空间
                    val bottom = r.bottom - child.bottom

                    val percent = if (top < 0 || bottom < 0) {
                        0f
                    } else {
                        //当top 和 bottom一样时，percent = 1 ； 当top < 0 时，压缩到最小尺寸
                        Math.min(top, bottom) * 1f / Math.max(top, bottom)
                    }
                    val scaleFactor = 0.85f + Math.abs(percent) * (1f - 0.85f)
                    if (it == 0) {
                        Log.d(
                            "Wumingtag",
                            "percent   $percent   scaleFactor = $scaleFactor;位置：$it  top   $top"
                        )
                    }

                    child.layoutParams = lp
                    child.scaleY = scaleFactor
                    child.scaleX = scaleFactor
                }


            }
        }

    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }

    private lateinit var inflater: LayoutInflater
    private lateinit var adapter: RecyclerView.Adapter<MyViewHolder>
    private var lastSelected = 0
    private var mHelper: SwipeBackActivityHelper? = null
    override fun onPageSelected(position: Int, isBottom: Boolean) {
        if (lastSelected == position) {
            return
        }
        lastSelected = position
        Log.d("ASASASAS", "onPageSelected  $position   isBottom   $isBottom")
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper!!.swipeBackLayout
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout!!.scrollToFinishActivity()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper?.onPostCreate()

    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout!!.setEnableGesture(enable)
    }

}