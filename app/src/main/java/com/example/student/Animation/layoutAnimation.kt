package com.example.student.Animation

import android.animation.ValueAnimator
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity

class LayoutAnimation {
    private var isExpanded: Boolean = false
    fun animationLinearLayout(
        DefaultisExpanded : Boolean,
        linearLayout: LinearLayout,
        defaultName: String,
        endName: String,
        startLayoutHeight: Int,  // Initial height (collapsed state, 0)
        targetLayoutHeight: Int, // Target expanded height
        timeDuration: Long,
        textView: TextView  // TextView to update based on the expanded/collapsed state
    ) {
        isExpanded = !DefaultisExpanded
        val startHeight: Int
        val targetHeight: Int

        if (isExpanded) {
            startHeight = linearLayout.height
            targetHeight = 0
            linearLayout.gravity = Gravity.TOP // Set gravity to TOP for collapse (from bottom to top)
        } else {
            startHeight = 0
            targetHeight = targetLayoutHeight // Expand to the target height
            linearLayout.gravity = Gravity.BOTTOM // Set gravity to BOTTOM for expand (top to bottom expansion)
        }

        textView.text = if (isExpanded) {endName
        } else {
            defaultName
        }

        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        animator.duration = timeDuration

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = linearLayout.layoutParams
            params.height = value
            linearLayout.layoutParams = params
        }
        animator.start()
        isExpanded = !isExpanded
    }
}
