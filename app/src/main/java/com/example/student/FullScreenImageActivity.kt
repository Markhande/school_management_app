package com.example.student

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.schoolerp.R
import kotlin.math.max
import kotlin.math.min

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetector
    private lateinit var mImageView: ImageView

    private var mPosX = 0f
    private var mPosY = 0f
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f

    private var mScaleFactor = 1f
    private val minScale = 1f
    private val maxScale = 5f // Maximum zoom factor
    private var isZoomedIn = false // Track zoom state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_full_screen_image)

        // Set padding for system bars (status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mImageView = findViewById(R.id.full_screen_image)

        // Initialize ScaleGestureDetector with a listener for scaling
        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Get scale factor from detector and apply limits
                val scaleFactor = detector.scaleFactor
                mScaleFactor = (mScaleFactor * scaleFactor).coerceIn(minScale, maxScale)
                mImageView.scaleX = mScaleFactor
                mImageView.scaleY = mScaleFactor
                return true
            }
        })

        // Initialize GestureDetector for detecting double tap
        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                e?.let {
                    if (isZoomedIn) {
                        // Reset to initial scale (1x zoom)
                        mScaleFactor = 1f
                        isZoomedIn = false
                    } else {
                        // Zoom in by 50% (1.5x)
                        mScaleFactor = (mScaleFactor * 1.5f).coerceIn(minScale, maxScale)
                        isZoomedIn = true
                    }

                    // Apply the scale to the image view
                    mImageView.scaleX = mScaleFactor
                    mImageView.scaleY = mScaleFactor

                    // Adjust the position to keep the zoom centered on the touch point
                    val centerX = it.x
                    val centerY = it.y

                    // Calculate scale difference and adjust the image position
                    val scaleDifference = mScaleFactor / (mScaleFactor / 1.5f)
                    mPosX -= (centerX - mPosX) * (scaleDifference - 1)
                    mPosY -= (centerY - mPosY) * (scaleDifference - 1)

                    mImageView.translationX = mPosX
                    mImageView.translationY = mPosY
                }
                return true
            }
        })

        if (intent.getStringExtra("image_url") != null) {
            Glide.with(this)
                .load(intent.getStringExtra("image_url"))
                .placeholder(R.drawable.imagenotfound)
                .into(mImageView)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pass the event to both GestureDetector and ScaleGestureDetector
        mGestureDetector.onTouchEvent(event) // Handle double-tap
        mScaleGestureDetector.onTouchEvent(event) // Handle scale gestures

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastTouchX = event.x
                mLastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - mLastTouchX
                val dy = event.y - mLastTouchY

                mPosX += dx
                mPosY += dy

                // Constrain image movement within screen bounds
                val imageWidth = mImageView.width * mScaleFactor
                val imageHeight = mImageView.height * mScaleFactor

                // Get screen dimensions
                val screenWidth = resources.displayMetrics.widthPixels
                val screenHeight = resources.displayMetrics.heightPixels

                // Constrain the image movement to stay inside the screen bounds
                mPosX = mPosX.coerceIn(-imageWidth + screenWidth, 0f)
                mPosY = mPosY.coerceIn(-imageHeight + screenHeight, 0f)

                mImageView.translationX = mPosX
                mImageView.translationY = mPosY

                mLastTouchX = event.x
                mLastTouchY = event.y
            }
        }
        return true
    }
}
