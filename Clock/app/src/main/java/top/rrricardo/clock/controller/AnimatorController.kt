package top.rrricardo.clock.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import top.rrricardo.clock.model.ClockViewModel

class AnimatorController(private val clockViewModel: ClockViewModel) {

    private var valueAnimator : ValueAnimator? = null

    fun start(start: Int, end: Int) {
       if (valueAnimator == null) {
           val animator = ValueAnimator.ofInt(start, end)

           animator.interpolator = LinearInterpolator()

           animator.addUpdateListener {
               clockViewModel.second = it.animatedValue as Int
           }

           animator.addListener(object : AnimatorListenerAdapter() {
               override fun onAnimationEnd(animation: Animator) {
                   super.onAnimationEnd(animation)
                   Log.i("Clock", "Animator stopped.")
                   clockViewModel.restore()
               }
           })

           valueAnimator = animator
       } else {
           valueAnimator?.setIntValues(start, end)
       }

        valueAnimator?.duration = (end - start) * 1000L
        valueAnimator?.start()
    }
}