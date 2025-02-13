package com.example.schoolerp.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.helpers.MethodLibrary
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.R
import com.example.schoolerp.util.ImageUtil
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.IOException

class JobLetterPrintAdapter(

    private val context: Context,
    private val jobLetterList: List<AllEmployee>
) : PrintDocumentAdapter() {

    private lateinit var printAttributes: PrintAttributes

    override fun onLayout(

        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        printAttributes = newAttributes ?: PrintAttributes.Builder().build()
        val info = PrintDocumentInfo.Builder("Offer_letter_print.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()
        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(

        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback
    ) {
        val pdfDocument = PdfDocument()

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val contentPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            isAntiAlias = true
        }

        val sectionTitlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
            isFakeBoldText = true
        }

        val imagePaint = Paint()

        CoroutineScope(Dispatchers.Default).launch {
            jobLetterList.forEachIndexed { index, employee ->
                val pageInfo = PdfDocument.PageInfo.Builder(600, 800, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                var yPosition = 100f

                val logoBitmap = loadBitmapFromGlide(
                    ImageUtil.getFullImageUrl("institute_logos", employee.institute_logo ?: "")
                )
                logoBitmap?.let { bitmap ->
                    // Define the size and position of the circle
                    val centerX = 180f // X-coordinate of the center of the circle
                    val centerY = 65f  // Y-coordinate of the center of the circle
                    val radius = 30f   // Radius of the circle

                    // Create a circular path
                    val circularPath = Path().apply {
                        addCircle(centerX, centerY, radius, Path.Direction.CW)
                    }

                    // Save the canvas state
                    canvas.save()

                    // Clip the canvas to the circular path
                    canvas.clipPath(circularPath)

                    // Define the rectangle where the bitmap will be drawn
                    val destRect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

                    // Draw the bitmap within the rectangle
                    canvas.drawBitmap(bitmap, null, destRect, imagePaint)

                    // Restore the canvas state
                    canvas.restore()
                }

                val density = context.resources.displayMetrics.density
                val offsetInPixels = 5 * density

                val schoolName = employee.school_name ?: "School Name Not Available"
                titlePaint.textSize = 24f // Adjust the text size for the school name
                canvas.drawText(schoolName, canvas.width / 2f, 50f + offsetInPixels, titlePaint)

                val schoolAddress = MethodLibrary().getDataString("institute_address", context)
                titlePaint.textSize = 16f // Adjust the text size for the school name
                canvas.drawText(schoolAddress, canvas.width / 2f, 70f + offsetInPixels, titlePaint)

                titlePaint.textSize = 30f // Adjust the text size for the "Salary Slip" title
                canvas.drawText("Offer Letter", canvas.width / 2f, 140f, titlePaint)



                yPosition = yPosition + 10f


                yPosition += 80f
                canvas.drawText("Name : ${employee.employee_name}", 50f, yPosition, contentPaint)
                canvas.drawText("Date of Joining : ${employee.date_of_joining}", 50f, yPosition + 30f, contentPaint)
                canvas.drawText("Position : ${employee.employee_role}", 50f, yPosition + 60f, contentPaint)
                canvas.drawText("Salary : ${employee.monthly_salary}", 50f, yPosition + 90f, contentPaint)
                canvas.drawText("Mobile : ${employee.number}", 50f, yPosition + 120f, contentPaint)
                canvas.drawText("National ID : ${employee.national_id}", 50f, yPosition + 150f, contentPaint)
                canvas.drawText("Education : ${employee.education}", 50f, yPosition + 180f, contentPaint)
                canvas.drawText("Gender : ${employee.gender}", 50f, yPosition + 210f, contentPaint)
                canvas.drawText("Blood Group : ${employee.blood_group}", 50f, yPosition + 240f, contentPaint)
                canvas.drawText("Experience : ${employee.experience}", 50f, yPosition + 270f, contentPaint)
                canvas.drawText("Date of Birth : ${employee.date_of_birth}", 50f, yPosition + 300f, contentPaint)
                canvas.drawText("Home Address : ${employee.home_address}", 50f, yPosition + 330f, contentPaint)
                canvas.drawText("Email : ${employee.email}", 50f, yPosition + 360f, contentPaint)

                val employeeImageBitmap = loadBitmapFromGlide(
                    ImageUtil.getFullImageUrl("employee", employee.picture ?: "")
                )

                employeeImageBitmap?.let {
                    if (!it.isRecycled) {
                        val imageWidth = 130 // Desired image width
                        val imageHeight = 150 // Desired image height

// Define the rectangle for the image in the right corner
                        val rightMargin = 50f // Margin from the right edge of the canvas
                        val topMargin = 180f // Margin from the top edge of the canvas
                        val left = canvas.width - imageWidth - rightMargin
                        val top = topMargin
                        val right = canvas.width - rightMargin
                        val bottom = top + imageHeight

                        val imageRect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        canvas.drawBitmap(it, null, imageRect, imagePaint)
                    } else {
                        Log.e("JobLetterPrintAdapter", "Attempted to use a recycled bitmap for ${employee.employee_name}")
                    }
                }

                // Acceptance Section
                val acceptanceStartY = yPosition + 450f
                canvas.drawText("Acceptance", 50f, acceptanceStartY, sectionTitlePaint)
                canvas.drawText(
                    "I, ${employee.employee_name}, hereby accept the offer of employment from ${employee.school_name}.",
                    50f,
                    acceptanceStartY + 30f,
                    contentPaint
                )
                canvas.drawText("Signature  : ______________________________", 50f, acceptanceStartY + 60f, contentPaint)
                canvas.drawText("Date  :      ______________________________", 50f, acceptanceStartY + 90f, contentPaint)

                pdfDocument.finishPage(page)
            }

            try {
                destination?.fileDescriptor?.let {
                    pdfDocument.writeTo(FileOutputStream(it))
                }
                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            } catch (e: IOException) {
                e.printStackTrace()
                callback.onWriteFailed(e.message)
            } finally {
                pdfDocument.close()
            }
        }
    }

    private suspend fun loadBitmapFromGlide(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val futureTarget = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .placeholder(R.drawable.account_avatar_profile)
                    .error(R.drawable.account_avatar_profile)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)

                val bitmap = futureTarget.get()
                Glide.with(context).clear(futureTarget) // Clear target after use
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
