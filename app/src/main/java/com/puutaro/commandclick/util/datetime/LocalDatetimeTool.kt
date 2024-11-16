package com.puutaro.commandclick.util.datetime

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDatetimeTool {

    fun getLocalDatetimeFromString(
        datetimeStr: String,
    ): LocalDateTime {
        //フォーマットを指定
        return try {
            val format = "yyyy/MM/dd HH:mm:ss"
            val dtf = DateTimeFormatter.ofPattern(format)
            LocalDateTime.parse(datetimeStr, dtf)
        } catch (e: Exception){
            LocalDateTime.now()
        }
    }

    fun getDurationSec(
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long {
        val summerVacationDuration: Duration =
            Duration.between(start, end) // 期間分の時間を取得する
        return summerVacationDuration.seconds
    }

    fun getDurationMiliSec(
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long {
        val summerVacationDuration: Duration =
            Duration.between(start, end) // 期間分の時間を取得する
        return summerVacationDuration.toMillis()
    }

    fun convertStrToLocalDatetime(
        datetimeStr: String
    ): LocalDateTime {
        return LocalDateTime.parse(datetimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }


}