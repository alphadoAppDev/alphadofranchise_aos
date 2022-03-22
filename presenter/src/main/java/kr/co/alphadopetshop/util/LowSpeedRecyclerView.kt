package kr.co.alphadopetshop.util

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class LowSpeedRecyclerView(context: Context, attributeSet: AttributeSet, defStyleAttr : Int) : RecyclerView(context, attributeSet, defStyleAttr) {




    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val topVelocityY = (Math.min(abs(velocityY), 500) * Math.signum(
            velocityY.toDouble()
        )).toInt()
        return super.fling(velocityX, topVelocityY)
    }
}