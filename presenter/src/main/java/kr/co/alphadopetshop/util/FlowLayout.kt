package kr.co.alphadopetshop.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class FlowLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var line_height = 0

    class LayoutParams
    /**
     * @param horizontal_spacing Pixels between items, horizontally 우측 마진
     * @param vertical_spacing   Pixels between items, vertically 아래쪽 마진
     */(val horizontal_spacing: Int, val vertical_spacing: Int) :
        ViewGroup.LayoutParams(0, 0)



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        assert(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        val count = childCount
        var line_height = 0
        var xpos = paddingLeft
        var ypos = paddingTop
        val childHeightMeasureSpec: Int
        childHeightMeasureSpec =
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
            } else {
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            }
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child.getVisibility() !== GONE) {
                val lp = child.getLayoutParams() as LayoutParams
                child.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                    childHeightMeasureSpec
                )
                val childw: Int = child.getMeasuredWidth()
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing)
                if (xpos + childw > width) {
                    xpos = paddingLeft
                    ypos += line_height
                }
                xpos += childw + lp.horizontal_spacing
            }
        }
        this.line_height = line_height
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams? {
        return LayoutParams(1, 1) // default of 1px spacing
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams? {
        return LayoutParams(1, 1)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return if (p is LayoutParams) {
            true
        } else false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val width = r - l
        var xpos = paddingLeft
        var ypos = paddingTop
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child.getVisibility() !== GONE) {
                val childw: Int = child.getMeasuredWidth()
                val childh: Int = child.getMeasuredHeight()
                val lp = child.getLayoutParams() as LayoutParams
                if (xpos + childw > width) {
                    xpos = paddingLeft
                    ypos += line_height
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh)
                xpos += childw + lp.horizontal_spacing
            }
        }
    }
}