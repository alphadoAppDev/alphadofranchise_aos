package kr.co.alphadopetshop.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

class StickyScrollView : NestedScrollView, ViewTreeObserver.OnGlobalLayoutListener{
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    var isHeaderTwo = false

    var header: View? = null
        set(value) {
            field = value
            field?.let {
                it.setOnClickListener { _ ->
                    //클릭 시, 헤더뷰가 최상단으로 오게 스크롤 이동
                    this.smoothScrollTo(scrollX, it.top)
                    callStickListener()
                }

            }
        }

    var secondHeader: View? = null
        set(value) {
            field = value
            field?.let {
                it.setOnClickListener { _ ->
                    //클릭 시, 헤더뷰가 최상단으로 오게 스크롤 이동
                    this.smoothScrollTo(scrollX, it.top)
                    callSecondStickyListener()
                }
            }
        }


    var stickListener: (View) -> Unit = {}
    var freeListener: (View) -> Unit = {}

    var secondStickListener: (View) -> Unit = {}
    var secondFreeListener: (View) -> Unit = {}

    private var mIsHeaderSticky = false
    private var mIsSecondHeaderSticky = false

    private var mHeaderInitPosition = 0f
    private var mSecondHeaderInitPosition = 0f



    override fun onGlobalLayout() {
        mHeaderInitPosition = header?.top?.toFloat() ?: 0f
        mSecondHeaderInitPosition = secondHeader?.top?.toFloat() ?: 0f
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolly = t

        try{
            if (scrolly > mHeaderInitPosition) {
                stickHeader(scrolly - mHeaderInitPosition)
            } else {
                freeHeader()
            }
        }catch (e:Exception){e.printStackTrace()}


        try{
            val secondHeaderY = scrolly + header!!.height
            if(secondHeaderY > mSecondHeaderInitPosition){
                stickSecondHeader(scrolly + header!!.height - mSecondHeaderInitPosition)
            } else {
                freeSecondHeader()
            }
        }catch (e: Exception) {e.printStackTrace()}


    }

    private fun stickHeader(position: Float) {
        header?.translationY = position
        if(!isHeaderTwo)
            callStickListener()
    }

    private fun stickSecondHeader(position: Float) {
        secondHeader?.translationY = position
        callSecondStickyListener()
    }


    //sticky ON
    private fun callStickListener() {
        if (!mIsHeaderSticky) {
            stickListener(header ?: return)
            mIsHeaderSticky = true
        }
    }

    private fun callSecondStickyListener(){
        if(!mIsSecondHeaderSticky) {
            secondStickListener(secondHeader ?: return)
            mIsSecondHeaderSticky = true
        }
    }

    private fun freeHeader() {
        header?.translationY = 0f
        if(!isHeaderTwo) callFreeListener()
    }

    private fun freeSecondHeader(){
        secondHeader?.translationY = 0f
        callSecondStickyFreeListener()
    }



    //sticky OFF
    private fun callFreeListener() {
        if (mIsHeaderSticky) {
            freeListener(header ?: return)
            mIsHeaderSticky = false
        }
    }

    private fun callSecondStickyFreeListener(){
        if(mIsSecondHeaderSticky) {
            secondFreeListener(secondHeader ?: return)
            mIsSecondHeaderSticky = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

}