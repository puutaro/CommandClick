package com.puutaro.commandclick.service.lib.ubuntu

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable

class WaitQuiz {
    private val appName = "CommandClick"
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

    private val quizPairList = listOf(
        Pair(
            "App all feature not working?",
            "-> kill -> app kill"
        ),
        Pair(
            "What is the significance of ubuntu?",
            "-> pakcage system for ${appName}"
        ),
        Pair(
            "How to save battery?",
            "-> Adjust ${CommandClickScriptVariable.UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF} in config"
        ),
        Pair(
            "How to work ubuntu always?",
            "-> Set 0 ${CommandClickScriptVariable.UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF} in config"
        ),
        Pair(
            "What's webview dialog?",
            "-> Easily, change one url to gui application"
        ),
        Pair(
            "What's best js interface for developer?",
            "-> webview dialog"
        ),
        Pair(
            "How to shutdown ubuntu?",
            "-> setting -> setting -> config"
        ),
        Pair(
            "Ubuntu sleep time feature?",
            "-> Kill terminal process and sound system"
        ),
        Pair(
            "How long ubuntu sleep time?",
            "-> setting -> setting -> config"
        ),
        Pair(
            "What's this ubuntu size?",
            "-> About 1GB"
        ),
        Pair(
            "What's ${appName} concept?",
            "-> Swiss knife"
        ),
        Pair(
            "Where are fannel store?",
            "-> Search commandclick-repository"
        ),
        Pair(
            "Where are fannel store menu?",
            "-> Long press setting button -> install fannel"
        ),
        Pair(
            "Where are bookmark?",
            "-> Long press left bottom button"
        ),
        Pair(
            "What's this browser hot spot?",
            "-> Highlight text -> press net mark!"
        ),
        Pair(
            "How long does ubuntu sound set?",
            "-> 10 seconds at the latest"
        ),
        Pair(
            "How to quickly customize?",
            "-> Long press setting button -> edit_startup"
        ),
        Pair(
            "What's data management system in ${appName}?",
            "-> File system in under Document dir"
        ),
        Pair(
            "What's base merit in ${appName}?",
            "-> Rich customizability"
        ),
        Pair(
            "What's better than termux?",
            "-> one touched & one handed op"
        ),
        Pair(
            "Oh! ubuntu go Heaven!?",
            "-> Reinstall ${appName}"
        ),
        Pair(
            "Oh! ubuntu go Heaven?",
            "-> Reinstall ${appName}"
        ),
        Pair(
            "What's Ubuntu login system?",
            "-> Dropbear ssh server system"
        ),
        Pair(
            "How to init ubuntu?",
            "-> Remove /support/ubuntuSetupComp.txt"
        ),
        Pair(
            "What is the installation period for this ubuntu?",
            "-> About one month"
        ),
        Pair(
            "Oh no sound!?",
            "-> Press RESTART in notification"
        ),
        Pair(
            "Why did you add sound system to ubuntu?",
            "-> I think backend include sound"
        ),
        Pair(
            "Will you install GUI env on ubuntu?",
            "-> No, backend for ${appName}"
        ),
        Pair(
            "How do you think termux?",
            "-> Super backend"
        ),
        Pair(
            "What are the advantages of this ubuntu?",
            "-> Can use apt, programing from browser"
        ),
        Pair(
            "Can you use shell from js?",
            "-> Can, very fast"
        ),
        Pair(
            "What are the weaknesses of this sudo?",
            "-> Slow"
        ),
        Pair(
            "Which proot is easier to run on recent devices?",
            "-> sudo ok, UserLAnd's proot + fakeroot"
        ),
        Pair(
            "Which proot is easier to run on older devices?",
            "-> sudo ok, green-green-avk/build-proot-android"
        ),
        Pair(
            "What failure about ubuntu on android?",
            "-> Not working depend on proot edition and devices"
        ),
        Pair(
            "How to work ubuntu on almost android devices?",
            "-> UserLAnd's proot + fakeroot"
        ),
        Pair(
            "How to work ubuntu on almost android devices?",
            "-> UserLAnd's proot + fakeroot"
        ),
        Pair(
            "Where did you get proot?",
            "-> From CypherpunkArmory/UserLAnd"
        ),
        Pair(
            "What's hint about ubuntu on android system?",
            "-> CypherpunkArmory/UserLAnd, termux"
        ),
        Pair(
            "What's ${appName}'s origin?",
            "-> puutaro/cmdclick: pc edition"
        ),
        Pair(
            "How to exit ubuntu?",
            "-> Task kill, and click CANCEL in notification."
        ),
        Pair(
            "CommandClick possibility?",
            "-> To be our life hub"
        ),
        Pair(
            "How to realize sudo in ubuntu?",
            "-> By fakeroot"
        ),
        Pair(
            "How to use for expert?",
            "-> Make addon by shell"
        ),
        Pair(
            "What's relationship between js and shell?",
            "-> Base is js, extra is shell"
        ),
        Pair(
            "How to exec shell cmd?",
            "-> http2shell, or proot when service"
        ),
        Pair(
            "How to make this sound system?",
            "-> Transfer to port by pulseaudio"
        ),
        Pair(
            "What's difference between this and termux?",
            "-> Specialized in CommandClick"
        ),
        Pair(
            "How to work ubuntu on android?",
            "-> Login rootfs by proot"
        ),
        Pair(
            "How to create fannel?",
            "-> Go ${appName} github"
        ),
        Pair(
            "What's ${appName}'s terminal feature?",
            "-> One touch cmd"
        ),
        Pair(
            "What create by ${appName}?",
            "-> Standalone addon"
        ),
        Pair(
            "What enable in ubuntu?",
            "-> Terminal, apt, sound, etc.."
        ),
        Pair(
            "How long does it take to install?",
            "-> About 5 minutes"
        ),
        Pair(
            "Are strengths of ${appName} as browser?",
            "-> Web history oriented"
        ),
        Pair(
            "Are strengths of ${appName}' addon?",
            "-> Enforced by not only js but also shell"
        ),
        Pair(
            "Is ${appName} not suitable for?",
            "-> Tab supremacist"
        ),
        Pair(
            "What kind of app is ${appName}?",
            "-> Web browser enforced by js and shell"
        ),
        Pair(
            "Who is this app author?",
            "-> puutaro, web engineer, Japan"
        ),
        Pair(
            "What library is used in terminal?",
            "-> huashengdun/webssh"
        ),
        Pair(
            "Where is ${appName} history?",
            "-> Left bottom button"
        ),
        Pair(
            "Where is ${appName} setting?",
            "-> Right bottom button"
        ),
        Pair(
            "What is fannel?",
            "-> Shell or js enforced by ${appName}"
        ),
        Pair(
            "When was ${appName} made?",
            "-> 2023.02.01"
        ),
        Pair(
            "What was ${appName}'s original goal?",
            "-> termux cliend in the past"
        ),
        Pair(
            "Why did ${appName} install ubuntu?",
            "-> termux intent slow and independent from termux"
        ),
        Pair(
            "Careful point when installing Ubuntu?",
            "-> Rest in peace"
        ),
        Pair(
            "What is ubuntu version?",
            "-> 22.04 cloud image"
        ),
        Pair(
            "Why ${appName} introduce js?",
            "-> Speed, and many interface"
        ),
        Pair(
            "Precautions when using ${appName}?",
            "-> Don't use webview dialog on Terminal"
        ),
    )
}
