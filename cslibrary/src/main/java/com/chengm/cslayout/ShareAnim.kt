@file:Suppress("NOTHING_TO_INLINE")

package com.chengm.cslayout

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.view.View
import android.view.animation.Interpolator
import com.chengm.cslayout.anim.CSViewAnim
import com.chengm.cslayout.anim.ViewAnim
import com.chengm.cslayout.keyparms.CSKeyParm
import com.chengm.cslayout.keyparms.KeyParm

inline fun createKeyParm(key: String, v: View): KeyParm {
    return when (v) {
        is CSInterface -> {
            val csHelper = v.csHelper()
            val csParms = CSParms()
            csParms.mClipL = csHelper.mClipL
            csParms.mClipT = csHelper.mClipT
            csParms.mClipR = csHelper.mClipR
            csParms.mClipB = csHelper.mClipB

            csParms.mCornerLeftTop = csHelper.mCornerLeftTop
            csParms.mCornerTopRight = csHelper.mCornerTopRight
            csParms.mCornerRightBottom = csHelper.mCornerRightBottom
            csParms.mCornerBottomLeft = csHelper.mCornerBottomLeft
            CSKeyParm(key, v.rectInWindow(), csParms)
        }
        else -> {
            KeyParm(key, v.rectInWindow())
        }
    }
}

inline fun createIntentDef(intent: Intent, vararg keyViews: Pair<String, View>): Intent {
    return createIntent(intent, keyViews.map { createKeyParm(it.first, it.second) })
}

inline fun createIntentDef(intent: Intent, csInterface: CSInterface, vararg keyViews: Pair<String, View>): Intent {
    val parm = createKeyParm("csLayout", csInterface as View)
    return createIntent(intent, keyViews.map { createKeyParm(it.first, it.second) }.plus(parm))
}

inline fun createIntent(intent: Intent, vararg keyParm: KeyParm): Intent {
    return createIntent(intent, keyParm.asList())
}

inline fun createIntent(intent: Intent, keyParms: List<KeyParm>): Intent {
    keyParms.forEach { intent.putExtra(it.key, it) }
    return intent
}

inline fun createAnimator(isStart: Boolean, intent: Intent, key: String, v: View,
                          duration: Long = 600,
                          interpolator: Interpolator = OffsetInterpolator()
): Animator {
    val anim: Animator = when (v) {
        is CSInterface -> CSViewAnim(isStart, v, intent.getParcelableExtra(key), CSKeyParm(key, v.rectInWindow(), CSParms()))
        else -> ViewAnim(isStart, v, intent.getParcelableExtra(key), KeyParm(key, v.rectInWindow()))
    }
    anim.duration = duration
    anim.interpolator = interpolator
    return anim
}

inline fun createAnimator(isStart: Boolean, intent: Intent, key: String, v: View): Animator {
    val anim: Animator = when (v) {
        is CSInterface -> CSViewAnim(isStart, v, intent.getParcelableExtra(key), CSKeyParm(key, v.rectInWindow(), CSParms()))
        else -> ViewAnim(isStart, v, intent.getParcelableExtra(key), KeyParm(key, v.rectInWindow()))
    }
    anim.duration = 600
    anim.interpolator = OffsetInterpolator()
    return anim
}

inline fun View.rectInWindow(): Rect {
    val rect = Rect()
    val intArray = IntArray(2)
    getLocationInWindow(intArray)
    intArray[0]
    rect.set(intArray[0], intArray[1], intArray[0] + width, intArray[1] + height)
    return rect
}

inline fun shareAnim(isStart: Boolean, intent: Intent, vararg anims: Pair<String, View>, crossinline onAnimEnd: () -> Unit = {}): Animator {
    return shareAnim(anims.map { createAnimator(isStart, intent, it.first, it.second) }, onAnimEnd)
}

inline fun shareAnim(vararg anims: Animator, crossinline onAnimEnd: () -> Unit = {}): Animator {
    return shareAnim(anims.asList(), onAnimEnd)
}

inline fun shareAnim(anims: List<Animator>, crossinline onAnimEnd: () -> Unit = {}): Animator {
    val set = AnimatorSet()
    set.playTogether(anims)
    set.addListener(object : SimpleAnimLis() {
        override fun onAnimationEnd(animation: Animator?) {
            onAnimEnd()
        }
    })
    set.start()
    return set
}

inline fun Activity.startShareAnim(csInterface: CSInterface, vararg anims: Animator, crossinline onAnimEnd: () -> Unit = {}): Animator {
    val animator = createAnimator(true, intent, "csLayout", csInterface as View)
    val list = anims.toMutableList()
    list.add(animator)
    return shareAnim(list, onAnimEnd)
}

inline fun Activity.finishShareAnim(csInterface: CSInterface, vararg anims: Animator, crossinline onAnimEnd: () -> Unit = {}): Animator {
    val animator = createAnimator(false, intent, "csLayout", csInterface as View)
    val list = anims.toMutableList()
    list.add(animator)
    return shareAnim(list, onAnimEnd)
}
