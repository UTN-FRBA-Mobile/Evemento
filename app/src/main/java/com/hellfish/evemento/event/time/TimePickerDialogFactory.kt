package com.hellfish.evemento.event.time

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

interface TimePickerDialogFactory {

    val onlyTimeFormatter: DateTimeFormatter
        get() = DateTimeFormat.forPattern("HH:mm")

    fun createLinkedTimePickerDialogs(context: Context?, startTimeView: TextView, endTimeView: TextView): Pair<TimePickerDialog, TimePickerDialog> =
            Pair(createTimePickerDialog(context, createTimeListener(startTimeView, endText = endTimeView)),
                    createTimePickerDialog(context, createTimeListener(endTimeView, startText = startTimeView)))

    fun createTimeListener(textView: TextView, startText: TextView? = null, endText: TextView? = null) =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val localTime = LocalTime(hour, minute, 0)
                startText?.updateTimeIfAfter(localTime, onlyTimeFormatter)
                endText?.updateTimeIfBefore(localTime, onlyTimeFormatter)
                textView.text= onlyTimeFormatter.print(localTime)
            }

    fun createTimePickerDialog(context: Context?, onTimeSetListener: TimePickerDialog.OnTimeSetListener?): TimePickerDialog = with(DateTime.now()) {
        return TimePickerDialog(context, onTimeSetListener, hourOfDay, minuteOfHour, true)
    }

    private fun TextView.updateTimeIfAfter(date: LocalTime, formatter: DateTimeFormatter) {
        if (formatter.parseLocalTime(text.toString()).isAfter(date)) text = formatter.print(date)
    }

    private fun TextView.updateTimeIfBefore(date: LocalTime, formatter: DateTimeFormatter) {
        if (formatter.parseLocalTime(text.toString()).isBefore(date)) text = formatter.print(date)
    }

    fun TimePickerDialog.updateTime(textView: TextView, formatter: DateTimeFormatter) = apply {
        val time = formatter.parseLocalTime(textView.text.toString())
        updateTime(time.hourOfDay, time.minuteOfHour)
    }

}