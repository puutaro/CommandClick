package com.puutaro.commandclick.service.lib.music_player.libs

import java.util.concurrent.TimeUnit

object MiliToDisplayTimeForMusic {
    fun convert(
        currentPosi: Int?
    ): String {
        if(
            currentPosi == null
        ) return String()
        var millis = currentPosi.toLong()
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        return "${hours}:${minutes}:${seconds}"

    }
}