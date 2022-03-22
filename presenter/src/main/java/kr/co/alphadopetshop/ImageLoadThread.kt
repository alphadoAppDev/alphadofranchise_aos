package kr.co.alphadopetshop

import android.os.Bundle
import android.os.Handler
import android.os.Message
import kr.co.alphadopetshop.util.CommonUtils

class ImageLoadThread(
    private val mHandler : Handler,
    private val url : String?
) : Thread(){
    override fun run() {
        super.run()

        val bundle = Bundle()
        bundle.putString("url",url)
        val msg = Message()
        msg.data = bundle

        mHandler.sendMessage(msg)
    }
}