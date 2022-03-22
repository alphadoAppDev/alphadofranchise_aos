package kr.co.alphadopetshop.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.roundToInt

class FitXyImageView(context: Context, attrs : AttributeSet?, defStyle : Int) : AppCompatImageView(context, attrs, defStyle) {
    private var dimension : Float = 0.0f
    private var initial = true

    constructor(context: Context, attrs : AttributeSet) : this(context, attrs, 0){}
    constructor(context: Context) : this(context, null, 0){}

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        super.setLayoutParams(params)
    }


    override fun setImageDrawable(drawable: Drawable?) {
        setLayoutParams(drawable)
        super.setImageDrawable(drawable)
    }

    override fun setImageResource(resId: Int) {
        setLayoutParams(drawable)
        super.setImageResource(resId)
    }

    override fun setImageURI(uri: Uri?) {
        setLayoutParams(drawable)
        super.setImageURI(uri)
    }

    private fun setLayoutParams(drawable: Drawable?){
        if(drawable == null) return
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        if(initial) {
            dimension = intrinsicWidth.toFloat() / intrinsicHeight.toFloat()
            initial = false
            Log.d("test","dimension : $dimension")
        }
        val deviceSize = CommonUtils.getDeviceSize(context)
        val param = this.layoutParams
        param.width = deviceSize.width
        param.height = (deviceSize.width.toFloat() /dimension).roundToInt()
        this.layoutParams = param
    }
}