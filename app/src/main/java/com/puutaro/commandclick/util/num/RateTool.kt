package com.puutaro.commandclick.util.num

object RateTool {
    fun randomByRate(
        baseValue: Float,
        minRate: Float,
        maxRate: Float,
    ): Int {
        val minValue = (baseValue * minRate).toInt()
        val maxValue = (baseValue * maxRate).toInt()
        return (minValue..maxValue).random()

    }
}