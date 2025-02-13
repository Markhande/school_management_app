package com.example.schoolerp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.CalendarView
import java.util.*

class CustomCalendarView(context: Context, attrs: AttributeSet? = null) : CalendarView(context, attrs) {

    private val holidayDates = mutableSetOf<Long>()
    private val eventDates = mutableSetOf<Long>()

    private val holidayPaint = Paint().apply { color = Color.RED }
    private val eventPaint = Paint().apply { color = Color.GREEN }

    // Method to highlight specific dates
    fun highlightDates(holidays: Set<Long>, events: Set<Long>) {
        holidayDates.clear()
        eventDates.clear()
        holidayDates.addAll(holidays)
        eventDates.addAll(events)

        // Redraw the calendar to reflect the changes
        invalidate()
    }

    // Override onDraw to customize the date drawing behavior
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Loop through the dates and change background color for holidays and events
        holidayDates.forEach { date ->
            setBackgroundForDate(date, holidayPaint, canvas) // Red background for holidays
        }

        eventDates.forEach { date ->
            setBackgroundForDate(date, eventPaint, canvas) // Green background for events
        }
    }

    // Change background color of a specific date
    private fun setBackgroundForDate(date: Long, paint: Paint, canvas: Canvas) {
        val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
        val dayOfMonth = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val month = dateCalendar.get(Calendar.MONTH)
        val year = dateCalendar.get(Calendar.YEAR)

        // Get the first day of the month and its weekday
        val firstDayOfMonth = Calendar.getInstance().apply {
            set(year, month, 1)
        }

        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday, 1 = Monday, etc.

        // Calculate the X and Y positions of the date cell
        val col = (firstDayOfWeek + dayOfMonth - 1) % 7 // Column (0-6)
        val row = (firstDayOfWeek + dayOfMonth - 1) / 7 // Row (0-5)

        // Calculate cell width and height based on the calendar size
        val cellWidth = width / 7f // 7 columns (days in a week)
        val cellHeight = height / 6f // 6 rows (maximum for month view)

        // Calculate the top-left corner of the cell
        val xPos = col * cellWidth
        val yPos = row * cellHeight

        // Draw a rectangle as the background for the date
        canvas.drawRect(xPos, yPos, xPos + cellWidth, yPos + cellHeight, paint)
    }
}
