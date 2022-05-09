package com.assignment.gabchat

import java.text.SimpleDateFormat
import java.util.*

class TimeMange {
    fun time(time: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(time)
    }


    fun date(time: Long): String {
        return SimpleDateFormat("MMMM dd", Locale.getDefault()).format(time)
    }
}