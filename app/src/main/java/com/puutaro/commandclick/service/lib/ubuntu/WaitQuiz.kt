package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.common.variable.extra.WaitQuizPair

class WaitQuiz {
    private var currentQuestStr = String()
    private val quizPrefix = "Q. "

    fun echoQorA(): String {
        if(currentQuestStr.isEmpty()){
            val quizPairIndex = getRndInt()
            val quizPair = quizPairList.getOrNull(quizPairIndex)
                ?: return String()
            currentQuestStr = quizPair.first
            return "$quizPrefix$currentQuestStr"
        }
        val currentIndex = getIndex(currentQuestStr)
        val answer = quizPairList.getOrNull(currentIndex)?.second
            ?: return String()
        currentQuestStr = String()
        return answer
    }

    private fun getIndex(
        currentQuestStr: String
    ): Int {
        return quizPairList.indexOfFirst {
            val question = it.first
            question == currentQuestStr
        }
    }

    private fun getRndInt(): Int {
        return (quizPairList.indices).shuffled().last()
    }

    private val quizPairList = WaitQuizPair.quizPairList
}
