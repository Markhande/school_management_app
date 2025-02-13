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
import com.example.schoolerp.DataClasses.Student
import com.example.schoolerp.models.responses.InstituteDetails
import com.example.schoolerp.models.responses.InstituteProfileResponse
import com.example.schoolerp.util.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class SearchStudentPrintAdapter(
    private val context: Context,
    private val student: Student?
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
            PrintDocumentInfo.Builder("Student_slip.pdf")
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

        val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        CoroutineScope(Dispatchers.IO).launch {
            student ?.let {             // Load and draw the school logo on the left
                val schoolLogoBitmap = loadBitmapFromGlide(
                    ImageUtil.getFullImageUrl(
                        "institute_logos",
                        MethodLibrary().getDataString("profileLogo", context)
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

                    val schoolName = MethodLibrary().getDataString("school_name", context) ?: "School Name Not Available"
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


                    canvas.drawText("Student Information", canvas.width / 2f, 140f, titlePaint)

//        canvas.drawLine(50f, 70f, canvas.width - 50f, 160f, headerPaint)

                    student?.let {
                        drawKeyValuePair(
                            canvas,
                            "Student Name",
                            it.st_name,
                            200f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Registration",
                            it.registration_no,
                            250f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Class",
                            it.st_class,
                            300f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "UserName",
                            it.username,
                            350f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Password",
                            it.password,
                            400f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Status",
                            it.status,
                            450f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Date Of Admission",
                            it.dt_of_admission,
                            500f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Date of Birth",
                            it.dt_of_birth,
                            550f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Phone Number",
                            it.number,
                            600f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Religion",
                            it.religion,
                            650f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Cast",
                            it.cast,
                            700f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Gender",
                            it.gender,
                            750f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Blood Group",
                            it.blood_group,
                            800f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Disease if Any",
                            it.disease_if_any,
                            850f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Family",
                            it.family,
                            900f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Total Siblings",
                            it.siblings,
                            950f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Orphan Student",
                            it.orphan_student,
                            1000f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "OSC",
                            it.osc,
                            1050f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Previous School",
                            it.st_previous_school,
                            1100f,
                            headerPaint,
                            contentPaint,
                            true
                        )
                        drawKeyValuePair(
                            canvas,
                            "Identity Mark",
                            it.identification_mark,
                            1150f,
                            headerPaint,
                            contentPaint,
                            false
                        )
                        drawKeyValuePair(
                            canvas,
                            "Address",
                            it.address,
                            1200f,
                            headerPaint,
                            contentPaint,
                            true
                        )

                    }

                    val contentRect = Rect(50, 160, canvas.width - 50, 1250)
                    canvas.drawRect(contentRect, blueBorderPaint)

                    val innerRect = Rect(
                        contentRect.left + 5,
                        contentRect.top + 5,
                        contentRect.right - 5,
                        contentRect.bottom - 5
                    )
                    canvas.drawRect(innerRect, whiteBorderPaint)

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
        if (isGrayBackground) {
            canvas.drawRect(
                xStart,
                yPosition - rowHeight + 10,
                canvas.width - xStart,
                yPosition + 20,
                grayPaint
            )
        }

        canvas.drawText("$key:", xStart + 20f, yPosition, keyPaint)
        canvas.drawText(value ?: "N/A", xStart + 200f, yPosition, valuePaint)
    }

}

