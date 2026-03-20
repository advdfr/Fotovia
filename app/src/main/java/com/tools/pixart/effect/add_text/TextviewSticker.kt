package com.background.remover.bg.eraser.photo.editor.presentation.editing_ui.add_text


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

class TextviewSticker : StickerView {
    private var tv_main: AutoResizeText? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {}

    //        tv_main.setTextSize(22);
    override val mainView: View
        get() {
            if (tv_main != null) return tv_main!!
            tv_main = AutoResizeText(context)
            //        tv_main.setTextSize(22);
            tv_main!!.setTextColor(Color.WHITE)
            tv_main!!.gravity = Gravity.CENTER
            tv_main!!.textSize = 330f
            tv_main!!.setShadowLayer(4f, 0f, 0f, Color.BLACK)
            tv_main!!.maxLines = 1
            val params = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER
            tv_main!!.layoutParams = params
            return tv_main!!
        }

    fun setFont(text: Typeface?) {
        if (tv_main != null) tv_main!!.typeface = text
    }

    //    public void setCol(int a, int b,int c){
    //        if(tv_main!=null)
    //            tv_main.setTextColor(Color.rgb(a,b,c));
    //    }
    fun setCol(a: Int) {
        if (a != 0) {
            if (tv_main != null) tv_main!!.setTextColor(a)
        } else {
            if (tv_main != null) tv_main!!.setTextColor(Color.WHITE)
        }
    }

    var text: String?
        get() = if (tv_main != null) tv_main!!.text.toString() else null
        set(text) {
            if (tv_main != null) tv_main!!.text = text
        }

//    override fun onScaling(scaleUp: Boolean) {
//        super.onScaling(scaleUp)
//    }

    companion object {
        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }
    }
}