package com.example.schoolerp.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.marginTop

class CircularProgressViewLeave @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val backgroundPaint = Paint().apply {
        color = 0xFFE0E0E0.toInt() // Light gray
        style = Paint.Style.STROKE
        strokeWidth = 25f
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        color = 0xFF42A5F5.toInt() // Blue
        style = Paint.Style.STROKE
        strokeWidth = 25f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = 0xFF000000.toInt() // Black text color
        textSize = 50f
        isAntiAlias = true
        isFakeBoldText =true
        textAlign = Paint.Align.CENTER
    }

    private val textLeave = Paint().apply {
        color = 0xFF000000.toInt() // Black text color
        textSize = 30f
        isAntiAlias = true
        isFakeBoldText =true
        textAlign = Paint.Align.CENTER
    }



    private var progress: Float = 0f // Progress value (0 to 100)

    private var text: String = "" // Text to display in the center

    private val textTopMargin = 24f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - 20f // Adjust for stroke width

        // Draw the background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Draw the progress circle (sweep angle is based on progress)
        val sweepAngle = (progress / 100f) * 360f
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            90f, // Start angle (top of the circle)
            sweepAngle, // Sweep angle
            false, // Do not use the center
            progressPaint
        )

        // Draw the text in the middle of the circle
        canvas.drawText(text, centerX, centerY + (textPaint.textSize / 3), textPaint)

        // Draw the "Leave" label below the percentage
        canvas.drawText("Leave", centerX, centerY + textLeave.textSize+textTopMargin, textLeave)
    }

    // Method to update progress
    fun setProgress(progress: Float) {
        this.progress = progress
        text = "${progress.toInt()}%"
        // Update the text to show the current progress
        invalidate() // Redraw the view
    }
    fun setProgressWithAnimation(targetProgress: Float, duration: Long = 1000) {
        val animator = ValueAnimator.ofFloat(progress, targetProgress)
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { animation ->
            progress = animation.animatedValue as Float
            text = "${progress.toInt()}%"
            invalidate()
        }

        animator.start()
    }



}
