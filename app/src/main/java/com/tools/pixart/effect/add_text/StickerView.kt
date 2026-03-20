package com.background.remover.bg.eraser.photo.editor.presentation.editing_ui.add_text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.tools.pixart.R

abstract class StickerView : FrameLayout {
    private var iv_border: BorderView? = null
    private var iv_scale: ImageView? = null
    private var iv_delete: ImageView? = null
    private var iv_flip: ImageView? = null

    // For scalling
    private var this_orgX = -1f
    private var this_orgY = -1f
    private var scale_orgX = -1f
    private var scale_orgY = -1f
    private var scale_orgWidth = -1.0
    private var scale_orgHeight = -1.0

    // For rotating
    private var rotate_orgX = -1f
    private var rotate_orgY = -1f
    private var rotate_newX = -1f
    private var rotate_newY = -1f

    // For moving
    private var move_orgX = -1f
    private var move_orgY = -1f
    private var centerX = 0.0
    private var centerY = 0.0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        iv_border = BorderView(context)
        iv_scale = ImageView(context)
        iv_delete = ImageView(context)
        iv_flip = ImageView(context)

        iv_scale?.setImageResource(com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp)
        iv_scale?.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)

        iv_delete?.setImageResource(R.drawable.ic_delete)
        iv_delete?.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)

        iv_flip?.setImageResource(R.drawable.ic_done)
        iv_flip?.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)

        this.tag = "DraggableViewGroup"
        iv_border?.tag = "iv_border"
        iv_scale?.tag = "iv_scale"
        iv_delete?.tag = "iv_delete"
        iv_flip?.tag = "iv_flip"
        val margin = convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext()) / 2
        val size = convertDpToPixel(SELF_SIZE_DP.toFloat(), getContext())
        val this_params = LayoutParams(
            size,
            size
        )
        this_params.gravity = Gravity.CENTER
        val iv_main_params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        iv_main_params.setMargins(margin, margin, margin, margin)
        val iv_border_params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        iv_border_params.setMargins(margin, margin, margin, margin)
        val iv_scale_params = LayoutParams(
            convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext()),
            convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext())
        )
        iv_scale_params.gravity = Gravity.BOTTOM or Gravity.RIGHT
        val iv_delete_params = LayoutParams(
            convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext()),
            convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext())
        )
        iv_delete_params.gravity = Gravity.TOP or Gravity.RIGHT
        val iv_flip_params = LayoutParams(
            convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext()),
            convertDpToPixel(BUTTON_SIZE_DP.toFloat(), getContext())
        )
        iv_flip_params.gravity = Gravity.TOP or Gravity.LEFT
        this.layoutParams = this_params
        this.addView(mainView, iv_main_params)
        this.addView(iv_border, iv_border_params)
        this.addView(iv_scale, iv_scale_params)
        this.addView(iv_delete, iv_delete_params)
        this.addView(iv_flip, iv_flip_params)
        setOnTouchListener(mTouchListener)
        iv_scale?.setOnTouchListener(mTouchListener)
        iv_delete?.setOnClickListener {
            if (this@StickerView.parent != null) {
                val myCanvas = this@StickerView.parent as ViewGroup
                myCanvas.removeView(this@StickerView)
            }
        }
        iv_flip?.setOnClickListener {
            Log.v(TAG, "flip the view")
            setControlsVisibility(false)

        }
    }

    val isFlip: Boolean
        get() = mainView.rotationY == -180f
    protected abstract val mainView: View
    private val mTouchListener = OnTouchListener { view, event ->
        if (view.tag == "DraggableViewGroup") {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.v(TAG, "sticker view action down")
                    move_orgX = event.rawX
                    move_orgY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.v(TAG, "sticker view action move")
                    val offsetX = event.rawX - move_orgX
                    val offsetY = event.rawY - move_orgY
                    this@StickerView.x = this@StickerView.x + offsetX
                    this@StickerView.y = this@StickerView.y + offsetY
                    move_orgX = event.rawX
                    move_orgY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    setControlItemsHidden(false)
                    Log.v(TAG, "sticker view action up")
                }
            }
        } else if (view.tag == "iv_scale") {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.v(TAG, "iv_scale action down")
                    this_orgX = this@StickerView.x
                    this_orgY = this@StickerView.y
                    scale_orgX = event.rawX
                    scale_orgY = event.rawY
                    scale_orgWidth = this@StickerView.layoutParams.width.toDouble()
                    scale_orgHeight = this@StickerView.layoutParams.height.toDouble()
                    rotate_orgX = event.rawX
                    rotate_orgY = event.rawY
                    centerX = (this@StickerView.x +
                            (this@StickerView.parent as View).x + this@StickerView.width.toFloat() / 2).toDouble()


                    //double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
                    var result = 0
                    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                    if (resourceId > 0) {
                        result = resources.getDimensionPixelSize(resourceId)
                    }
                    val statusBarHeight = result.toDouble()
                    centerY = this@StickerView.y +
                            (this@StickerView.parent as View).y +
                            statusBarHeight + this@StickerView.height.toFloat() / 2
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.v(TAG, "iv_scale action move")
                    rotate_newX = event.rawX
                    rotate_newY = event.rawY
                    val angle_diff = Math.abs(Math.atan2((event.rawY - scale_orgY).toDouble(), (event.rawX - scale_orgX).toDouble())
                            - Math.atan2(scale_orgY - centerY, scale_orgX - centerX)) * 180 / Math.PI
                    Log.v(TAG, "angle_diff: $angle_diff")
                    val length1 = getLength(centerX, centerY, scale_orgX.toDouble(), scale_orgY.toDouble())
                    val length2 = getLength(centerX, centerY, event.rawX.toDouble(), event.rawY.toDouble())
                    val size = convertDpToPixel(SELF_SIZE_DP.toFloat(), context)
                    if (length2 > length1
                        && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)) {
                        //scale up
                        val offsetX = Math.abs(event.rawX - scale_orgX).toDouble()
                        val offsetY = Math.abs(event.rawY - scale_orgY).toDouble()
                        var offset = Math.max(offsetX, offsetY)
                        offset = Math.round(offset).toDouble()
                        this@StickerView.layoutParams.width += offset.toInt()
                        this@StickerView.layoutParams.height += offset.toInt()
                        onScaling(true)
                        //DraggableViewGroup.this.setX((float) (getX() - offset / 2));
                        //DraggableViewGroup.this.setY((float) (getY() - offset / 2));
                    } else if (length2 < length1 && (angle_diff < 25 || Math.abs(angle_diff - 180) < 25)
                        && this@StickerView.layoutParams.width > size / 2 && this@StickerView.layoutParams.height > size / 2) {
                        //scale down
                        val offsetX = Math.abs(event.rawX - scale_orgX).toDouble()
                        val offsetY = Math.abs(event.rawY - scale_orgY).toDouble()
                        var offset = Math.max(offsetX, offsetY)
                        offset = Math.round(offset).toDouble()
                        this@StickerView.layoutParams.width -= offset.toInt()
                        this@StickerView.layoutParams.height -= offset.toInt()
                        onScaling(false)
                    }

                    //rotate
                    val angle = Math.atan2(event.rawY - centerY, event.rawX - centerX) * 180 / Math.PI
                    Log.v(TAG, "log angle: $angle")

                    //setRotation((float) angle - 45);
                    rotation = angle.toFloat() - 45
                    Log.v(TAG, "getRotation(): $rotation")
                    onRotating()
                    rotate_orgX = rotate_newX
                    rotate_orgY = rotate_newY
                    scale_orgX = event.rawX
                    scale_orgY = event.rawY
                    postInvalidate()
                    requestLayout()
                }
                MotionEvent.ACTION_UP ->                         //  setControlItemsHidden(false);
                    Log.v(TAG, "iv_scale action up")
            }
        }
        true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun getLength(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return Math.sqrt(Math.pow(y2 - y1, 2.0) + Math.pow(x2 - x1, 2.0))
    }

    private fun getRelativePos(absX: Float, absY: Float): FloatArray {
        Log.v("ken", "getRelativePos getX:" + (this.parent as View).x)
        Log.v("ken", "getRelativePos getY:" + (this.parent as View).y)
        val pos = floatArrayOf(
            absX - (this.parent as View).x,
            absY - (this.parent as View).y
        )
        Log.v(TAG, "getRelativePos absY:$absY")
        Log.v(TAG, "getRelativePos relativeY:" + pos[1])
        return pos
    }

    fun setControlItemsHidden(isHidden: Boolean) {
        if (isHidden) {
            iv_border?.visibility = INVISIBLE
            iv_scale?.visibility = INVISIBLE
            iv_delete?.visibility = INVISIBLE
            iv_flip?.visibility = INVISIBLE
        } else {
            iv_border?.visibility = VISIBLE
            iv_scale?.visibility = VISIBLE
            iv_delete?.visibility = VISIBLE
            iv_flip?.visibility = VISIBLE
        }
    }

    protected val imageViewFlip: View?
        protected get() = iv_flip

    protected fun onScaling(scaleUp: Boolean) {}
    protected fun onRotating() {}
    private inner class BorderView : View {
        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
        constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            // Draw sticker border
            val params = this.layoutParams as LayoutParams
            Log.v(TAG, "params.leftMargin: " + params.leftMargin)
            val border = Rect()
            border.left = this.left - params.leftMargin
            border.top = this.top - params.topMargin
            border.right = this.right - params.rightMargin
            border.bottom = this.bottom - params.bottomMargin
            val borderPaint = Paint()
            borderPaint.strokeWidth = 6f
            borderPaint.color = context.getColor(R.color.colorPrimaryDark)
            borderPaint.style = Paint.Style.STROKE
            canvas.drawRect(border, borderPaint)
        }
    }

    fun setControlsVisibility(isVisible: Boolean) {
        if (!isVisible) {
            iv_border?.visibility = GONE
            iv_delete?.visibility = GONE
            iv_flip?.visibility = GONE
            iv_scale?.visibility = GONE
        } else {
            iv_border?.visibility = VISIBLE
            iv_delete?.visibility = VISIBLE
            iv_flip?.visibility = VISIBLE
            iv_scale?.visibility = VISIBLE
        }
    }

    companion object {
        const val TAG = "ffnet"
        private const val BUTTON_SIZE_DP = 30
        private const val SELF_SIZE_DP = 100
        private fun convertDpToPixel(dp: Float, context: Context): Int {
            val resources = context.resources
            val metrics = resources.displayMetrics
            val px = dp * (metrics.densityDpi / 160f)
            return px.toInt()
        }
    }
}