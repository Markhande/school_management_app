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
import com.example.helpers.MethodLibrary
import com.example.schoolerp.DataClasses.AllEmployee
import com.example.schoolerp.DataClasses.SalarySlipData
import com.example.schoolerp.util.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class SearchEmpPrintAdapter (
    private val context: Context,
    private val EmpData: AllEmployee?
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
            PrintDocumentInfo.Builder("Employees_slip.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(2)
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
        val pageWidth = 600
        val pageHeight = 800
        val margin = 50f
        val rowHeight = 50f
        val contentStartY = 200f
        val maxContentHeight = pageHeight - margin * 2
        var yPosition = contentStartY

        var pageIndex = 0
        var page = pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
        )
        var canvas = page.canvas

        CoroutineScope(Dispatchers.IO).launch {
            EmpData?.let {
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
                        val circularBitmap =
                            Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

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
                        val destRect = RectF(
                            circleLeft,
                            circleTop,
                            circleLeft + circleSize,
                            circleTop + circleSize
                        )

                        // Draw the circular bitmap on the main canvas
                        canvas.drawBitmap(circularBitmap, null, destRect, null)
                    }

                    val density = context.resources.displayMetrics.density
                    val offsetInPixels = 5 * density

                    val schoolName = EmpData?.school_name ?: "School Name Not Available"
                    titlePaint.textSize = 24f // Adjust the text size for the school name
                    canvas.drawText(schoolName, canvas.width / 2f, 50f + offsetInPixels, titlePaint)

                    val schoolAddress = MethodLibrary().getDataString("institute_address", context)
                    titlePaint.textSize = 16f // Adjust the text size for the school name
                    canvas.drawText(
                        schoolAddress,
                        canvas.width / 2f,
                        70f + offsetInPixels,
                        titlePaint
                    )

                    canvas.drawText("Employees Information", canvas.width / 2f, 140f, titlePaint)

                    EmpData?.let {
                        val dataList = listOf(
                            "Employee Name" to it.employee_name,
                            "Role" to it.employee_role,
                            "Date of Joining" to it.date_of_joining,
                            "UserName" to it.username,
                            "Password" to it.password,
                            "Status" to it.st_status,
                            "Experience" to it.experience,
                            "Email" to it.email,
                            "Mobile No." to it.number,
                            "Education" to it.education,
                            "F/H Name" to it.f_h_name,
                            "Monthly Salary" to it.monthly_salary,
                            "Date of Birth" to it.date_of_birth,
                            "Adhaar/PAN no." to it.national_id,
                            "Address" to it.home_address,
                            "Gender" to it.gender,
                            "Religion" to it.religion,
                            "Blood Group" to it.blood_group
                        )

                        val contentRect = Rect(50, 160, canvas.width - 50, 900)
                        canvas.drawRect(contentRect, blueBorderPaint)

                        for ((index, pair) in dataList.withIndex()) {
                            if (yPosition + rowHeight > maxContentHeight) {
                                pdfDocument.finishPage(page)
                                pageIndex++
                                page = pdfDocument.startPage(
                                    PdfDocument.PageInfo.Builder(
                                        pageWidth,
                                        pageHeight,
                                        pageIndex + 1
                                    ).create()
                                )
                                canvas = page.canvas
                                yPosition = contentStartY
                            }

                            val contentRect = Rect(50, 160, canvas.width - 50, 900)
                            canvas.drawRect(contentRect, blueBorderPaint)


                            drawKeyValuePair(
                                canvas,
                                pair.first,
                                pair.second,
                                yPosition,
                                headerPaint,
                                contentPaint,
                                index % 2 == 0
                            )
                            yPosition += rowHeight
                        }
                    }

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
        val rowHeight = 40f
        val xStart = 50f
        canvas.drawRect(xStart,
            yPosition - rowHeight + 10,
            canvas.width - xStart,
            yPosition + 20,
            if (isGrayBackground) grayPaint else Paint().apply {
                color = 0xFFFFFFFF.toInt()
            }) // White background


        canvas.drawText("$key:", xStart + 20f, yPosition, keyPaint)
        canvas.drawText(value ?: "N/A", xStart + 200f, yPosition, valuePaint)
    }
}
