package com.vhennus.general.utils

import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale




fun prettyDate2(date:String):String{

    if(date.isEmpty() || date.isEmpty()) return ""

    val dinputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    val doutputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var prettyPostDate = ""
    var chatDate = ""
    // Parse the string into a Date object
    try {
//        val parsedDate = inputFormat.parse(chat.created_at)
//        chatDate = dinputFormat.parse(chat.created_at)?.toString() ?: ""

        val parsedDate = dinputFormat.parse(date)
        chatDate = parsedDate?.let { doutputFormat.format(it) } ?: ""

        val prettyTime = PrettyTime()
        prettyPostDate = prettyTime.format(parsedDate)
    } catch (e: ParseException) {
        CLog.error("PRETTY DATE ERROR", e.toString())
    }

    return chatDate
}