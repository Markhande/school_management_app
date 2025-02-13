package com.example.schoolerp.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import com.bumptech.glide.Glide
import com.example.schoolerp.DataClasses.SalarySlipData
import com.example.schoolerp.R
import com.example.schoolerp.util.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class SalarySlipPrintAdapter(
    private val context: Context,
    private val salarySlipData: SalarySlipData?
) : PrintDocumentAdapter() {

    private val titlePaint = Paint().apply {
        isAntiAlias = true
        textSize = 20f
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private val headerPaint = Paint().apply {
        isAntiAlias = true
        textSize = 16f
        textAlign = Paint.Align.LEFT
        style = Paint.Style.FILL
    }

    private val contentPaint = Paint().apply {
        isAntiAlias = true
        textSize = 14f
        textAlign = Paint.Align.LEFT
    }

    private val grayPaint = Paint().apply {
        color = 0xFFEEEEEE.toInt() // Light gray color
        style = Paint.Style.FILL
    }

    private val blueBorderPaint = Paint().apply {
        color = 0xFF1242C5.toInt() // Blue color
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val whiteBorderPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt() // White color
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback,
        metadata: Bundle?
    ) {
        callback.onLayoutFinished(
            PrintDocumentInfo.Builder("salary_slip.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build(),
            true
        )
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback
    ) {
        val pdfDocument = PdfDocument()

        // Define page size
        val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Start a coroutine for loading the image asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            salarySlipData?.let {
                // Load and draw the school logo on the left
                val schoolLogoBitmap = loadBitmapFromGlide(
                    ImageUtil.getFullImageUrl(
                        "institute_logos",
                        it.institute_logo.toString()
                    )
                )

                withContext(Dispatchers.Main) {
                    if (schoolLogoBitmap != null) {
                        // Create a circular bitmap
                        val size = Math.min(schoolLogoBitmap.width, schoolLogoBitmap.height)
                        val circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

                        val canvasCircular = Canvas(circularBitmap)
                        val paint = Paint().apply {
                            isAntiAlias = true
                            shader = BitmapShader(
                                schoolLogoBitmap,
                                Shader.TileMode.CLAMP,
                                Shader.TileMode.CLAMP
                            )
                        }

                        // Draw the circular bitmap
                        val radius = size / 2f
                        canvasCircular.drawCircle(radius, radius, radius, paint)

                        // Define where the circular bitmap will be drawn
                        val circleLeft = 160f
                        val circleTop = 40f
                        val circleSize = 50f // Adjust as per your needs
                        val destRect = RectF(circleLeft, circleTop, circleLeft + circleSize, circleTop + circleSize)

                        // Draw the circular bitmap on the main canvas
                        canvas.drawBitmap(circularBitmap, null, destRect, null)
                    }

                    val density = context.resources.displayMetrics.density
                    val offsetInPixels = 5 * density

                    val schoolName = it.school_name ?: "School Name Not Available"
                    titlePaint.textSize = 24f // Adjust the text size for the school name
                    canvas.drawText(schoolName, canvas.width / 2f, 50f + offsetInPixels, titlePaint)

                    val schoolAddress = it.address ?: ""
                    titlePaint.textSize = 16f // Adjust the text size for the school name
                    canvas.drawText(schoolAddress, canvas.width / 2f, 70f + offsetInPixels, titlePaint)

                    titlePaint.textSize = 30f // Adjust the text size for the "Salary Slip" title
                    canvas.drawText("Salary Slip", canvas.width / 2f, 140f, titlePaint)

                    drawSalarySlipContent(canvas, it)

                    pdfDocument.finishPage(page)

                    try {
                        FileOutputStream(destination.fileDescriptor).use { outputStream ->
                            pdfDocument.writeTo(outputStream)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        pdfDocument.close()
                    }

                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                }
            }
        }
    }

    private fun drawSalarySlipContent(canvas: Canvas, data: SalarySlipData) {
        drawKeyValuePair(
            canvas,
            "Employee Name",
            data.employee_name,
            200f,
            headerPaint,
            contentPaint,
            true
        )
        drawKeyValuePair(
            canvas,
            "Designation",
            data.employee_role,
            250f,
            headerPaint,
            contentPaint,
            false
        )
        drawKeyValuePair(
            canvas,
            "Date of Payment",
            data.date_of_pay,
            300f,
            headerPaint,
            contentPaint,
            true
        )

        drawKeyValuePair(
            canvas,
            "Salary Month",
            data.salary_month,
            450f,
            headerPaint,
            contentPaint,
            false
        )

        drawKeyValuePair(canvas, "Deduction", data.deduction, 500f, headerPaint, contentPaint, true)
        drawKeyValuePair(canvas, "Bonus", data.bouns, 350f, headerPaint, contentPaint, false)
        drawKeyValuePair(canvas, "Net Paid", data.net_paid, 400f, headerPaint, contentPaint, true)



        // Draw a blue border around the content section
        val contentRect = Rect(50, 160, canvas.width - 50, 525)
        canvas.drawRect(contentRect, blueBorderPaint)

        // Draw a white border inside the blue border
        val innerRect = Rect(
            contentRect.left + 5,
            contentRect.top + 5,
            contentRect.right - 5,
            contentRect.bottom - 5
        )
        canvas.drawRect(innerRect, whiteBorderPaint)
    }

    private suspend fun loadBitmapFromGlide(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val futureTarget = Glide.with(context) // Replace `context` with the proper Context
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                futureTarget.get() // Retrieve the Bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun drawKeyValuePair(
        canvas: Canvas,
        key: String,
        value: String?,
        yPosition: Float,
        keyPaint: Paint,
        valuePaint: Paint,
        isGrayBackground: Boolean
    ) {
        // Draw alternating background color for each key-value pair
        val rowHeight = 40f
        val xStart = 50f

        // Draw gray background for alternating rows
        if (isGrayBackground) {
            canvas.drawRect(
                xStart,
                yPosition - rowHeight + 10,
                canvas.width - xStart,
                yPosition + 10,
                grayPaint
            )
        }

        // Draw the key-value pair
        canvas.drawText("$key:", xStart + 20f, yPosition, keyPaint)
        canvas.drawText(value ?: "N/A", xStart + 200f, yPosition, valuePaint)
    }
}