package com.chengm.cslayout

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.util.Property
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * author : ChenWJ
 * date : 2019/12/7
 * description :
 */
class ScaleViewGesture(ctx: Context) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener, View.OnAttachStateChangeListener {

    override fun onViewDetachedFromWindow(v: View?) {

    }

    override fun onViewAttachedToWindow(v: View?) {

    }

    val isNVersion by lazy { Build.VERSION.SDK_INT == Build.VERSION_CODES.N }

    lateinit var mView: View
    fun bindToView(touchView: View?, scaleView: View?): ScaleViewGesture? {
        if (touchView == null || scaleView == null)
            return null
        touchView.setOnTouchListener(this@ScaleViewGesture)
        touchView.addOnAttachStateChangeListener(this)
        mView = scaleView
        return this
    }

    private val gesture = GestureDetector(ctx, this)
    var onClick: (View) -> Unit = {}
    var scale = 0.93f
    var XD = 0f
    var YD = 0f

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
//        Log.i("onTouch:", "action:${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                XD = event.x
                YD = event.y
                toScale()
            }
            MotionEvent.ACTION_UP -> {
                toRecoverClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                toRecover()
            }
        }
        return gesture.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    // 恢复
    private fun toRecover() {
        if (!animReview.isRunning) {
            animReview.removeAllListeners()
            animReview.start()
        }
    }

    private val lis = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            onClick(mView)
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }

    //恢复
    private fun toRecoverClick() {
        if (isNVersion) {
            onClick(mView)
            return
        }
        if (animScale.isRunning) {
            animScale.cancel()
        }
        if (!animReview.isRunning) {
            if (animReview.listeners == null || !animReview.listeners.contains(lis)) {
                animReview.addListener(lis)
            }
            animReview.start()
        }
    }

    //缩放
    private fun toScale() {
        if (isNVersion)
            return
        if (!animScale.isRunning)
            animScale.start()
    }

    private val animScale by lazy {
        val animator = ObjectAnimator.ofFloat(mView, propertyScale, 1f, scale)
        animator.duration = 120
        animator
    }

    private val animReview by lazy {
        val animator = ObjectAnimator.ofFloat(mView, propertyScale, scale, 1f)
        animator.duration = 120
        animator
    }

    private var propertyScale: Property<View, Float> = object : Property<View, Float>(Float::class.java, "scale") {
        override fun set(v: View, value: Float) {
            v.scaleX = value
            v.scaleY = value
        }

        override fun get(v: View): Float {
            return v.scaleX
        }
    }

    fun setCustomScale(scale: Float): ScaleViewGesture {
        this.scale = scale
        return this
    }

    fun setCustomScale(scaleProperty: Property<View, Float>): ScaleViewGesture {
        propertyScale = scaleProperty
        return this
    }

}