package com.puutaro.commandclick.common.variable.extra

import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsToListFilter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsToListMap
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.ubuntu.UbuntuExtraSystemShells
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles

object WaitQuizPair {

    private val appName = "CommandClick"

    fun makeQuizPairCon(
    ): String {
        return quizPairList.map {
            "${it.first}\t${it.second}"
        }.joinToString("\n")
    }
    private val UBUNTU_EXEC_MODE = CommandClickScriptVariable.UBUNTU_EXEC_MODE
    private val SHELL_EXEC_ENV = CommandClickScriptVariable.SHELL_EXEC_ENV
    private val HIDE_SETTING_VARIABLES = CommandClickScriptVariable.HIDE_SETTING_VARIABLES
    private val SET_VARIABLE_TYPE = CommandClickScriptVariable.SET_VARIABLE_TYPE
    private val SET_REPLACE_VARIABLE = CommandClickScriptVariable.SET_REPLACE_VARIABLE
    private val EDIT_EXECUTE = CommandClickScriptVariable.EDIT_EXECUTE
    private val notiCmd = "noti"
    private val sendBrdCtCmd = "send-broadcast"
    private val toastCmd = "toast"
    private val getRvarCmd="get_rvar"
    private val tsvar = "tsvar"
    private val isend = "isend"
    private val killPtree = "kill_ptree"
    private val rmsd = "rmsd"
    private val cp2sd = "cp2sd"
    private val mv2sd = "mv2sd"
    private val repbash = "repbash"
    private val jsAction = "js action"
    private val cmdTerminal = SystemFannel.cmdTerminal
    private val documentDir = "{documentDir}"
    val quizPairList = listOf(
        Pair(
            "Why introduce Ubuntu?",
            "-> To realize multiple feature",
        ),
        Pair(
            "What's Ubuntu positioning?",
            "-> Backend for all",
        ),
        Pair(
            "What's Ubuntu positioning?",
            "-> Backend for fannel, service",
        ),
        Pair(
            "What's frontend core library?",
            "-> ${jsAction}",
        ),
        Pair(
            "What are the funnel types?",
            "-> Box, list, and grid",
        ),
        Pair(
            "What's js original framework?",
            "-> ${jsAction}",
        ),
        Pair(
            "How to create fannel?",
            "-> TUTORIAL on github",
        ),
        Pair(
            "How to know fannel mechanism?",
            "-> TUTORIAL on github",
        ),
        Pair(
            "What's ${jsAction} feature?",
            "-> One of config in ${appName}",
        ),
        Pair(
            "What's ${jsAction} feature?",
            "-> Trigger from config",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.LIST_INDEX_CONFIG}?",
            "-> List config in fannel",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.LIST_INDEX_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.LIST_INDEX_CONFIG} in setting vars",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.QR_DIALOG_CONFIG}?",
            "-> Logo config in ${CommandClickScriptVariable.LIST_INDEX_CONFIG}",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.QR_DIALOG_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.QR_DIALOG_CONFIG} in setting vars",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.PLAY_BUTTON_CONFIG}?",
            "-> Play button config in fannel",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.PLAY_BUTTON_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.PLAY_BUTTON_CONFIG} in setting vars",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.EDIT_BUTTON_CONFIG}?",
            "-> Edit button config in fannel",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.EDIT_BUTTON_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.EDIT_BUTTON_CONFIG} in setting vars",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.SETTING_BUTTON_CONFIG}?",
            "-> Setting button config in fannel",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.SETTING_BUTTON_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.SETTING_BUTTON_CONFIG} in setting vars",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.EXTRA_BUTTON_CONFIG}?",
            "-> Setting button config in fannel",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.EXTRA_BUTTON_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.EXTRA_BUTTON_CONFIG} in setting vars",
        ),
        Pair(
            "What's menu config?",
            "-> Menu config in fannel",
        ),
        Pair(
            "How to specify menu?",
            "-> Use menu macro in ${jsAction} or config",
        ),
        Pair(
            "What's ${CommandClickScriptVariable.EDIT_BOX_TITLE_CONFIG}?",
            "-> Fannel title config",
        ),
        Pair(
            "How to specify ${CommandClickScriptVariable.EDIT_BOX_TITLE_CONFIG}?",
            "-> Use ${CommandClickScriptVariable.EDIT_BOX_TITLE_CONFIG} in setting vars",
        ),
        Pair(
            "What's motif in ${jsAction}?",
            "-> github action",
        ),
        Pair(
            "What's ${jsAction} feature?",
            "-> Put annotation to all js code.",
        ),
        Pair(
            "What's ${JsActionKeyManager.JsActionsKey.ACTION_VAR.key} merit in ${jsAction}?",
            "-> Var definition is pipe able",
        ),
        Pair(
            "What's ${JsActionKeyManager.JsActionsKey.TSV_VARS.key} merit in ${jsAction}?",
            "-> Bulk definite var by key-value tsv",
        ),
        Pair(
            "What's key-value tsv?",
            "-> Two column tsv",
        ),
        Pair(
            "What's key-value tsv?",
            "-> Var name and var value tsv",
        ),
        Pair(
            "What's ${jsAction} feature?",
            "-> Var definition language",
        ),
        Pair(
            "How to import other ${jsAction}?",
            "-> Use ${JsActionKeyManager.JsActionsKey.ACTION_VAR.key}",
        ),
        Pair(
            "What's ${jsAction}'s merit?",
            "-> Put annotation to all js code.",
        ),
        Pair(
            "How to use list by ${jsAction}",
            "-> Use ${JsToListFilter::javaClass.name} or  ${JsToListMap::javaClass.name}",
        ),
        Pair(
            "How to use map by ${jsAction}",
            "-> Use ${JsToListFilter::javaClass.name} or  ${JsToListMap::javaClass.name}",
        ),
        Pair(
            "What's ${jsAction}?",
            "-> Annotation oriented language",
        ),
        Pair(
            "What's ${jsAction}?",
            "-> Js framework in ${appName}",
        ),
        Pair(
            "What's ${repbash}?",
            "-> Bulk definition cmd in Ubuntu",
        ),
        Pair(
            "How to bulk difinition shell var in Ubuntu?",
            "-> Use ${repbash} cmd",
        ),
        Pair(
            "How to definite val from key-value tsv in Ubuntu?",
            "-> Use ${repbash} cmd",
        ),
        Pair(
            "How to definite val from key-value args in Ubuntu?",
            "-> Use ${repbash} cmd",
        ),
        Pair(
            "How to definite val from key-value args in Ubuntu?",
            "-> Use ${repbash} cmd",
        ),
        Pair(
            "How to restore rootfs in sd card?",
            "-> Switch ${CommandClickScriptVariable.ON_ROOTFS_SDCARD_SAVE} config op",
        ),
        Pair(
            "How to auto restore rootfs?",
            "-> Switch ${CommandClickScriptVariable.AUTO_EXEC_PATH} config op",
        ),
        Pair(
            "Cannot auto restore from sdcard!",
            "-> Confirm ON in ${CommandClickScriptVariable.ON_ROOTFS_SDCARD_SAVE}",
        ),
        Pair(
            "How to remove from sdcard in Ubuntu?",
            "-> Use ${rmsd} cmd",
        ),
        Pair(
            "How to move to sdcard in Ubuntu?",
            "-> Use ${mv2sd} cmd",
        ),
        Pair(
            "How to copy to sdcard in Ubuntu?",
            "-> Use ${cp2sd} cmd",
        ),
        Pair(
            "How to use pulseaudio in Ubuntu?",
            "-> Set ${UbuntuExtraSystemShells.UbuntuExtraSystemShellMacro.PULSE.macro} in ${UbuntuFiles.ubuntuExtraStartupShellPathsTsvName}",
        ),
        Pair(
            "How to make service in Ubuntu?",
            "-> Set ${UbuntuFiles.ubuntuExtraStartupShellPathsTsvName}",
        ),
        Pair(
            "What's Ubuntu position?",
            "-> Core"
        ),
        Pair(
            "Ubuntu rootfs setup span",
            "-> Avr 1 ~ 2min"
        ),
        Pair(
            "Ubuntu rootfs restore span",
            "-> Avr 20 ~ 1min"
        ),
        Pair(
            "Ubuntu rootfs backup span",
            "-> Avr 3 ~ 4min"
        ),
        Pair(
            "What's one of ${appName} feature",
            "-> File/Dir share by QR"
        ),
        Pair(
            "What's one of ${appName} feature",
            "-> File/Dir share by QR"
        ),
        Pair(
            "What's one of QR usage?",
            "-> to gmail"
        ),
        Pair(
            "What's one of QR usage?",
            "-> to tel"
        ),
        Pair(
            "What's one of QR usage?",
            "-> Set WIFI"
        ),
        Pair(
            "What's one of QR usage?",
            "-> to SMS"
        ),
        Pair(
            "What's one of QR usage?",
            "-> Download fannel"
        ),
        Pair(
            "What's one of QR usage?",
            "-> File/Dir share"
        ),
        Pair(
            "What's one of QR usage?",
            "-> Download fannel"
        ),
        Pair(
            "Why adopt QR feature?",
            "-> Good compatibility"
        ),
        Pair(
            "Why adopt QR feature?",
            "-> For development"
        ),
        Pair(
            "What's alternative fakeroot",
            "-> green-green-avk/build-proot-android in recent android"
        ),
        Pair(
            "Why select fakeroot",
            "-> Steady on many device"
        ),
        Pair(
            "Why does not exist pipe cmd",
            "-> No steady sdtin judge"
        ),
        Pair(
            "How to repair ${appName} ",
            "-> Restart"
        ),
        Pair(
            "What 'Restart' in ${appName}",
            "-> Master repair method"
        ),
        Pair(
            "What's repair concept ",
            "-> Restart like zombi"
        ),
        Pair(
            "How to reset ubuntu?",
            "-> Backup -> Restore in notification"
        ),
        Pair(
            "How to reset ubuntu?",
            "-> Remove /support/ubuntuSetupComp.txt"
        ),
        Pair(
            "How to relaod fannel?",
            "-> history -> click fannel"
        ),
        Pair(
            "What bug in ${cmdTerminal}?",
            "-> history -> click ${cmdTerminal}"
        ),
        Pair(
            "What restart in fannel?",
            "-> history -> click fannel"
        ),
        Pair(
            "How to ssh from pc terminal?",
            "-> ssh -p 10022 cmdclick@${'$'}{ipv4}. pass: cmdclick"
        ),
        Pair(
            "How to ssh from termux?",
            "-> ssh -p 10022 cmdclick@${'$'}{ipv4}. pass: cmdclick"
        ),
        Pair(
            "How to manipulate ubuntu from pc?",
            "-> ssh -p 10022 cmdclick@${'$'}{ipv4}. pass: cmdclick"
        ),
        Pair(
            "What's ssh password?",
            "-> cmdclick"
        ),
        Pair(
            "Do you tell me ssh pass?",
            "-> cmdclick"
        ),
        Pair(
            "What's ssh password?",
            "-> cmdclick"
        ),
        Pair(
            "How to ssh ubuntu?",
            "-> ssh -p 10022 cmdclick@${'$'}{ipv4}"
        ),
        Pair(
            "How to transfer other android?",
            "-> cp ${'$'}{documentDir}/cmdclick"
        ),
        Pair(
            "What's transfer concept?",
            "-> Due to ${'$'}{documentDir}/cmdclick"
        ),
        Pair(
            "What's ${'$'}{documentDir}/cmdclick?",
            "-> All data"
        ),
        Pair(
            "How to access ${'$'}{documentDir}/cmdclick by Ubuntu?",
            "-> Default ok"
        ),
        Pair(
            "What's env val of ${'$'}{documentDir}/cmdclick in Ubuntu?",
            "-> ${'$'}{APP_ROOT_PATH}"
        ),
        Pair(
            "What's ${'$'}{APP_ROOT_PATH} of Ubuntu?",
            "->${'$'}{documentDir}/cmdclick"
        ),
        Pair(
            "What notes to cancel backup?",
            "-> After copy, to be corruption"
        ),
        Pair(
            "What's advantage of backup?",
            "-> Create current rootfs"
        ),
        Pair(
            "What's advantage of backup?",
            "-> fast re-installation"
        ),
        Pair(
            "Shouldn't you use `grep | cut`?",
            "-> No, awk. Due to proot process cost"
        ),
        Pair(
            "How to watch sys log?",
            "-> setting -> select term 2"
        ),
        Pair(
            "Why set monitor_2 as sys log?",
            "-> Debug easily"
        ),
        Pair(
            "Why set monitor_2 as sys log?",
            "-> Debug easily"
        ),
        Pair(
            "How to debug ${appName}?",
            "-> setting -> select term 2"
        ),
        Pair(
            "What motif term number is?",
            "-> Linux, stdout, stderr"
        ),
        Pair(
            "What's default term number?",
            "-> 1"
        ),
        Pair(
            "How to change default term number?",
            "-> setting -> 'defaultMonitorFile'"
        ),
        Pair(
            "How set 'defaultMonitorFile' setting var?",
            "-> For debug"
        ),
        Pair(
            "How to create debug funnel?",
            "-> Change 'defaultMonitorFile' by setting -> add"
        ),
        Pair(
            "What's env tsv ?",
            "-> Optional env var file"
        ),
        Pair(
            "What's env tsv format?",
            "-> var name to var value"
        ),
        Pair(
            "Where's env tsv?",
            "-> echo ${'$'}{UBUNTU_BACKUP_ROOTFS_PATH}/${'$'}{UBUNTU_ENV_TSV_NAME}"
        ),
        Pair(
            "How to extract var value form tsv?",
            "-> Use tsvar cmd"
        ),
        Pair(
            "How can you move ubuntu to sdcard",
            "-> No, readonly"
        ),
        Pair(
            "How can you move ubuntu to share storage",
            "-> No, symlink not working"
        ),
        Pair(
            "How can you move ubuntu rootfs to share storage",
            "-> Yes, ${UsePath.cmdclickUbuntuBackupDirPath.replace(UsePath.rootPath, documentDir)}"
        ),
        Pair(
            "How to have multiple ubuntu backup",
            "-> Othere from ${UsePath.cmdclickUbuntuBackupDirPath.replace(UsePath.rootPath, documentDir)}"
        ),
        Pair(
            "Where's ubuntu backup?",
            "-> ${UsePath.cmdclickUbuntuBackupDirPath.replace(UsePath.rootPath, documentDir)}"
        ),
        Pair(
            "Where's default dir?",
            "-> ${UsePath.cmdclickAppDirPath.replace(UsePath.rootPath, documentDir)}"
        ),
        Pair(
            "Where's monitor dir?",
            "-> ${UsePath.cmdclickMonitorDirPath.replace(UsePath.rootPath, documentDir)}"
        ),
        Pair(
            "How to centrally manage to variable fannel ?",
            "-> Use \"${SET_REPLACE_VARIABLE}\" in setting variables"
        ),
        Pair(
            "Where's \"${SET_REPLACE_VARIABLE}\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "What's \"${SET_REPLACE_VARIABLE}\"?",
            "-> Centrally manage to variable fannel"
        ),

        Pair(
            "Where's \"${UBUNTU_EXEC_MODE}\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "What's \"${UBUNTU_EXEC_MODE}\"?",
            "-> Switch mode with background (default) or foreground "
        ),
        Pair(
            "How to switch exec mode?",
            "-> Use \"${UBUNTU_EXEC_MODE}\""
        ),

        Pair(
            "Where's \"${SHELL_EXEC_ENV}\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "What's \"${SHELL_EXEC_ENV}\"?",
            "-> Switch ubuntu to termux as shell env"
        ),
        Pair(
            "How to switch ubuntu to termux as shell env?",
            "-> Use \"${SHELL_EXEC_ENV}\""
        ),
        Pair(
            "Where's \"${EDIT_EXECUTE}\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "What's \"${EDIT_EXECUTE}\"?",
            "-> Convert fannel edit box to gui app"
        ),
        Pair(
            "How to convert edit box to gui app?",
            "-> Use \"${EDIT_EXECUTE}\""
        ),
        Pair(
            "Where's \"${HIDE_SETTING_VARIABLES}\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "How to hid seldom use \"setting variables\"  ?",
            "-> Use \"${HIDE_SETTING_VARIABLES}\""
        ),
        Pair(
            "Where's \"${SET_VARIABLE_TYPE}\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "What's \"${SET_VARIABLE_TYPE}\"?",
            "-> Convert cmd variables to gui section"
        ),
        Pair(
            "What's \"${SET_VARIABLE_TYPE}\"'s motif?",
            "-> Gnu yad"
        ),
        Pair(
            "How to modifier \"cmd variables\" ?",
            "-> Use \"${SET_VARIABLE_TYPE}\""
        ),
        Pair(
            "What's edit field name in ${SET_VARIABLE_TYPE} ?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:TXT=label=example\""
        ),
        Pair(
            "What's list select box in ${SET_VARIABLE_TYPE} ?",
            "-> Select from list file"
        ),
        Pair(
            "What's to \"cmd variables\" to list select box ?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:ELSB=listPath=~"
        ),
        Pair(
            "What's to \"cmd variables\" to list select box with text field ?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:TXT:ELSB=listPath=~"
        ),
        Pair(
            "What's to \"cmd variables\" to select box ?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:CB=cmd=OF!OFF"
        ),
        Pair(
            "What's to \"cmd variables\" to no label button ?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:btn:HL=cmd=~!label=this"
        ),
        Pair(
            "How to \"cmd variables\" to button?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:btn=cmd=~!label=this"
        ),
        Pair(
            "How to \"cmd variables\" to select box with button?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:CB:btn=ON!OFF|cmd=~!label=this"
        ),
        Pair(
            "How to \"cmd variables\" to select box with btn with edit label?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}TXT:CB:btn=label=ex|ON!OFF|cmd=~!label=this"
        ),
        Pair(
            "What's \"${SET_VARIABLE_TYPE}\" format?",
            "-> ${SET_VARIABLE_TYPE}=${'$'}{cmdVal}:${'$'}{op1}:${'$'}{op2}:..=${'$'}{op1v}|${'$'}{op2v}|"
        ),
        Pair(
            "What's \"${SET_VARIABLE_TYPE}\"?",
            "-> Central feature as low code"
        ),
        Pair(
            "What's \"${SET_VARIABLE_TYPE}\" use case?",
            "-> All"
        ),
        Pair(
            "How to edit \"${SET_VARIABLE_TYPE}\"?",
            "-> Open fannel script by editor"
        ),
        Pair(
            "How to edit \"${EDIT_EXECUTE}\"?",
            "-> Click dark green fannel and press edit button"
        ),
        Pair(
            "What's fannel directory name?",
            "-> ${'$'}{fannel name} + \"Dir\""
        ),
        Pair(
            "if fannel directory name is not ${'$'}{fannel name} + \"Dir\"?",
            "-> No control"
        ),
        Pair(
            "What's trick to make fannel?",
            "-> Use js ref to web"
        ),
        Pair(
            "What's trick to make ubuntu in fannel?",
            "-> As js buitin command"
        ),
        Pair(
            "Is this low code tool?",
            "-> Yes, for js and shell developer"
        ),
        Pair(
            "Is this web browser?",
            "-> Yes, plus ubuntu"
        ),
        Pair(
            "Am I using only web browser?",
            "-> Ok, history oriented browser"
        ),
        Pair(
            "Am I using only ubuntu?",
            "-> Ok, ${cmdTerminal} is for you."
        ),
        Pair(
            "I don't know about js or shell?",
            "-> Ok, pure user"
        ),
        Pair(
            "Ubuntu usage?",
            "-> Library for js"
        ),
        Pair(
            "Ubuntu usage ultimately?",
            "-> For background service"
        ),
        Pair(
            "What's most simple js interface?",
            "-> jsToast.short(\"hello\")"
        ),
        Pair(
            "What's js interface?",
            "-> Android and shell via js"
        ),
        Pair(
            "What's purpose in js interface?",
            "-> To use android and shell"
        ),
        Pair(
            "What's advantage in js interface?",
            "-> Use android and shell"
        ),
        Pair(
            "What's another advantage in js interface?",
            "-> Applet power"
        ),
        Pair(
            "Difference dark green and green in thumbnail?",
            "-> In dark green, exec from edit box"
        ),
        Pair(
            "What's ubuntu value?",
            "-> Free customization"
        ),
        Pair(
            "How to type in dark green screen ?",
            "-> No, use ${cmdTerminal}"
        ),
        Pair(
            "How to access ${cmdTerminal} ?",
            "-> Termial button in notification"
        ),
        Pair(
            "How to access ${cmdTerminal} ?",
            "-> History menu"
        ),
        Pair(
            "How to access url history?",
            "-> Long press history button"
        ),
        Pair(
            "What's launch url ubuntu?",
            "-> Use ${sendBrdCtCmd}"
        ),
        Pair(
            "Where's \"cmd variables\"?",
            "-> \"command variables section\" in fannel script"
        ),
        Pair(
            "Where's \"cmd variables\"?",
            "-> \"command variables section\" in fannel script"
        ),
        Pair(
            "What's usage for \"${killPtree}\" command?",
            "-> Type \"${killPtree} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${killPtree}\" command?",
            "-> kill script process tree"
        ),
        Pair(
            "How to kill script process tree?",
            "-> Use \"${killPtree}\" command"
        ),
        Pair(
            "What's usage for \"${toastCmd}\" command?",
            "-> Type \"${toastCmd} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${toastCmd}\" command?",
            "-> Toast message in shell"
        ),
        Pair(
            "How to toast message from shell?",
            "-> Use \"${toastCmd}\" command"
        ),
        Pair(
            "What's usage for \"${sendBrdCtCmd}\" command?",
            "-> Type \"${sendBrdCtCmd} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${sendBrdCtCmd}\" command?",
            "-> Send broadcast in shell"
        ),
        Pair(
            "How to send broadcast from shell?",
            "-> Use \"${sendBrdCtCmd}\" command"
        ),
        Pair(
            "What's usage for \"noti\" command?",
            "-> Type \"${notiCmd} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${notiCmd}\" command?",
            "-> Launch notification in shell"
        ),
        Pair(
            "How to launch notification from shell?",
            "-> Use \"${notiCmd}\" command"
        ),
        Pair(
            "What's mean of original cmd?",
            "-> Pull full feature of ${appName}"
        ),
        Pair(
            "What's concept of original cmd?",
            "-> For easy access ${appName} system"
        ),
        Pair(
            "What's contents of original cmd?",
            "-> shell and curl wrapper"
        ),
        Pair(
            "What's usage for ${tsvar}\" command?",
            "-> Type \"${tsvar} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${tsvar}\" command?",
            "-> Get \"tsv env\" in shell"
        ),
        Pair(
            "How to get \"tsv env\" in shell?",
            "-> Use \"${tsvar}\" command"
        ),
        Pair(
            "What's usage for ${isend}\" command?",
            "-> Type \"${isend} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${isend}\" command?",
            "-> Send intent command"
        ),
        Pair(
            "How to send intent from shell?",
            "-> Use \"${isend}\" command"
        ),
        Pair(
            "What's purpose shell command of ${appName}?",
            "-> interface to os and ${appName}"
        ),
        Pair(
            "What's purpose shell command of ${appName}?",
            "-> Usability "
        ),
        Pair(
            "What's \"tsv env\" in shell?",
            "-> Second env variable for ubuntu"
        ),
        Pair(
            "What's difference \"tsv env\" and normal env?",
            "-> env variable is few, tsv env is many"
        ),
        Pair(
            "What's disadvantage \"tsv env\" ?",
            "-> Slow about read speed"
        ),
        Pair(
            "What's usage for \"${getRvarCmd}\" command?",
            "-> Type \"${getRvarCmd} -h\" in ${cmdTerminal}"
        ),
        Pair(
            "What's \"${getRvarCmd}\" command?",
            "-> Get \"${SET_REPLACE_VARIABLE}\" in shell"
        ),
        Pair(
            "How to use  \"${SET_REPLACE_VARIABLE}\" in shell?",
            "-> Use \"${getRvarCmd}\" command"
        ),
        Pair(
            "Where's \"setting variables\"?",
            "-> \"setting variables section\" in fannel script"
        ),
        Pair(
            "What's \"setting variables\"?",
            "-> funnel setting values"
        ),
        Pair(
            "What's ubuntu original feature?",
            "-> Command bellow usr/local/bin/"
        ),
        Pair(
            "How not to delete ubuntu data by re-install?",
            "-> Press backup button in notification"
        ),
        Pair(
            "Which's best feature in ${appName}",
            "-> History button"
        ),
        Pair(
            "Which browser or ubuntu app is?",
            "-> As you think"
        ),
        Pair(
            "How not to trim space on \"|\", \"!\", \"&\", \",\" in Notification title",
            "-> Sorry, no solution"
        ),
        Pair(
            "How to ubuntu backup?",
            "-> Press backup button in notification"
        ),
        Pair(
            "What's cmd exec env?",
            "-> proot, ssh, httpshd"
        ),
        Pair(
            "What's ${appName}?",
            "-> Bothend. Generally backend + frontend"
        ),
        Pair(
            "App all feature not working?",
            "-> kill -> kill app"
        ),
        Pair(
            "What is the significance of ubuntu?",
            "-> Package system for ${appName}"
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
            "Why do you create webview dialog?",
            "-> webview is almighty"
        ),
        Pair(
            "Why do you create webview dialog?",
            "-> Dynamics for changing one url to gui app"
        ),
        Pair(
            "What famous fannel made by webview dialog?",
            "-> webSeacher.js, ggleTranslator.js, etc"
        ),
        Pair(
            "What's webview dialog merit?",
            "-> English study with only one hand"
        ),
        Pair(
            "What's aim of webview dialog?",
            "-> Infinity, becuase of direct web world"
        ),
        Pair(
            "What's aim of webview dialog?",
            "-> For world urls"
        ),
        Pair(
            "What's aim of webview dialog?",
            "-> For world js cdn"
        ),
        Pair(
            "Ubuntu vs webview dialog?",
            "-> Backend chanpion and frontend champion"
        ),
        Pair(
            "Does ${appName} has backend and frontend?",
            "-> Yes, 'fullend' app"
        ),
        Pair(
            "What's posibility of webview dialog?",
            "-> For enormous js cdn and url"
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
            "Why set ubuntu sleep time?",
            "-> Battery life as mobile"
        ),
        Pair(
            "What' concept of ubuntu sleep time?",
            "-> Battery life as mobile"
        ),
        Pair(
            "How to remove sleep time?",
            "-> Set 0 ${CommandClickScriptVariable.UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF} in config"
        ),
        Pair(
            "There is no sleep setting in termux?",
            "-> This ubuntu is a PC specification"
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
            "What's concept of fannel store?",
            "-> Share repository for all user"
        ),
        Pair(
            "What's motif of fannel store?",
            "-> Apt package sytem in Ubuntu"
        ),
        Pair(
            "What's advantage of fannel store?",
            "-> use and modify with ease"
        ),
        Pair(
            "Most simple fannel customize method?",
            "-> Rename another by edit 'scriptName' section"
        ),
        Pair(
            "Most simple fannel create method?",
            "-> Create another by edit 'scriptName' section"
        ),
        Pair(
            "How to mass produce same funnel?",
            "-> Edit 'scriptName' section repeatedly"
        ),
        Pair(
            "Where are bookmark?",
            "-> Long press left bottom button"
        ),
        Pair(
            "What's advantage of this bookmark?",
            "-> Looks just like a website"
        ),
        Pair(
            "What's advantage of this bookmark?",
            "-> With ease to go back and forward"
        ),
        Pair(
            "What's disadvantage of this bookmark?",
            "-> Move from website"
        ),
        Pair(
            "How to watch previous site?",
            "-> Long press history button"
        ),
        Pair(
            "How to advantage url history system?",
            "-> View a list of previous sites"
        ),
        Pair(
            "How to advantage url history system?",
            "-> Jump previous url by two touch"
        ),
        Pair(
            "How to advantage url history system?",
            "-> Don't have to tab"
        ),
        Pair(
            "How to advantage url history system?",
            "-> Can set fannel you like"
        ),
        Pair(
            "Why did you make url history system?",
            "-> First, Instead tab"
        ),
        Pair(
            "Why did you make url history system?",
            "-> Link to fannel system"
        ),
        Pair(
            "What's this browser hot spot?",
            "-> Highlight text -> press net mark!"
        ),
        Pair(
            "How long does ubuntu sound set?",
            "-> 5 seconds at the latest"
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
            "Why put main dir bellow document dir?",
            "-> Remains after installing or updating."
        ),
        Pair(
            "Why put main dir bellow document dir?",
            "-> Visible for user and developer"
        ),
        Pair(
            "What security risk to locate mai dir bellow document dir?",
            "-> Visibility and resilience to change won"
        ),
        Pair(
            "What's base merit in ${appName}?",
            "-> Rich customizability"
        ),
        Pair(
            "What's better than termux?",
            "-> Two touched & one handed op"
        ),
        Pair(
            "Why introduce ${cmdTerminal} as touch app?",
            "-> Touch is easier"
        ),
        Pair(
            "Why introduce ${cmdTerminal} as touch app?",
            "-> Good unlike termux"
        ),
        Pair(
            "What's possibility of ${cmdTerminal}?",
            "-> All usage due to touch priority"
        ),
        Pair(
            "What prohibited action in ${cmdTerminal}?",
            "-> Don't touch screen!"
        ),
        Pair(
            "What notes in ${cmdTerminal}?",
            "-> Screen is only for scroll"
        ),
        Pair(
            "Don't want to touch ${cmdTerminal} screen in?",
            "-> Other place touches are useful."
        ),
        Pair(
            "Let's make ${cmdTerminal} touchable, shall we?",
            "-> Usability won over touching xTermJs"
        ),
        Pair(
            "Tell me how to use cmdTerminal",
            "-> Touch key boardplace"
        ),
        Pair(
            "Oh! ubuntu go Heaven!?",
            "-> Reinstall ${appName}"
        ),
        Pair(
            "Oh! cannot stop ubuntu process?",
            "-> Force stop"
        ),
        Pair(
            "What's Ubuntu login system?",
            "-> Dropbear ssh server system"
        ),
        Pair(
            "Why use dropbear in login?",
            "-> Openssh down"
        ),
        Pair(
            "What's advantage dropbear?",
            "-> Light weight and steady in ${appName}"
        ),
        Pair(
            "How to init ubuntu?",
            "-> Remove /support/ubuntuSetupComp.txt"
        ),
        Pair(
            "What roll of /support dir?",
            "-> Has support file and cmd for ubuntu"
        ),
        Pair(
            "What operation of /support dir?",
            "-> Insert to new update in release"
        ),
        Pair(
            "What feature of /support dir?",
            "-> Don't edit, because of override in startup"
        ),
        Pair(
            "What is the development period for this ubuntu?",
            "-> About one month"
        ),
        Pair(
            "Oh no sound!?",
            "-> Press RESTART in notification"
        ),
        Pair(
            "Oh, cannot stop ubuntu process!?",
            "-> Press RESTART in notification"
        ),
        Pair(
            "Oh, ubuntu have samething of error!?",
            "-> Press RESTART in notification"
        ),
        Pair(
            "Is RESTART button concept?",
            "-> Almighty restoration like snes reset button"
        ),
        Pair(
            "Where's RESTART button of ubuntu?",
            "-> In notification"
        ),
        Pair(
            "Where's RESTART button of ubuntu?",
            "-> setting -> 'restart ubuntu' menu"
        ),
        Pair(
            "Why did you add sound system to ubuntu?",
            "-> I think backend include sound"
        ),
        Pair(
            "Will you install GUI env on ubuntu?",
            "-> No, only backend"
        ),
        Pair(
            "How do you think termux?",
            "-> Super backend"
        ),
        Pair(
            "How do you think termux?",
            "-> Respect, therefore, has runCommand intent"
        ),
        Pair(
            "What are the advantages of this ubuntu?",
            "-> Use apt, programing from browser"
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
            "How to start systemd?",
            "-> Impossible, instead, use while loop in shell"
        ),
        Pair(
            "Isn't it inconvenient without systemd?",
            "-> Trade off battery life in mobile"
        ),
        Pair(
            "What's aim of ${appName}?",
            "-> D.I.Y tool"
        ),
        Pair(
            "What's aim of ${appName}?",
            "-> free customize browser"
        ),
        Pair(
            "What's aim of ${appName}?",
            "-> Free customize 'bothend'"
        ),
        Pair(
            "What's ${appName}'s origin?",
            "-> puutaro/cmdclick: pc edition"
        ),
        Pair(
            "How to exit ubuntu?",
            "-> Task kill, and click RESTART in notification."
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
            "How to admin exec env?",
            "-> proot"
        ),
        Pair(
            "What's the position of httpshd?",
            "-> foreground and backgroud cmd env"
        ),
        Pair(
            "What's the position of proot?",
            "-> admin cmd env"
        ),
        Pair(
            "What's the fature of proot?",
            "-> slow but admin management"
        ),
        Pair(
            "What's the fature of httpshd?",
            "-> Fast"
        ),
        Pair(
            "What's system configure env?",
            "-> proot"
        ),
        Pair(
            "What's admin exec env?",
            "-> proot"
        ),
        Pair(
            "When does system call proot?",
            "-> Startup and kill"
        ),
        Pair(
            "Is times of calls to proot few?",
            "-> Yes, because of slow"
        ),
        Pair(
            "How httpshd speed is?",
            "-> 10 times faster than proot"
        ),
        Pair(
            "What led you to speed up ${appName}?",
            "-> Breaking the myth that proot apps are slow"
        ),
        Pair(
            "What led you to speed up ${appName}?",
            "-> Speed is life"
        ),
        Pair(
            "What's disadvantate httpshd?",
            "-> Cannot use background"
        ),
        Pair(
            "Where's difference proot and ssh?",
            "-> proot for startup, ssh for routine"
        ),
        Pair(
            "What's foreground exec env?",
            "-> httpshd"
        ),
        Pair(
            "How to make this sound system?",
            "-> Media player and tts"
        ),
        Pair(
            "What's mechanism streaming in media player?",
            "-> Use yt-dlp in Ubuntu"
        ),
        Pair(
            "Why insert sound system?",
            "-> Need for background service"
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
            "-> Two touch cmd"
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
            "What best advantage of ubuntu?",
            "-> Free customize"
        ),
        Pair(
            "What disadvantage of ubuntu?",
            "-> Data size"
        ),
        Pair(
            "What average size of ubuntu?",
            "-> 2GB"
        ),
        Pair(
            "Why select in many distri?",
            "-> Most popular"
        ),
        Pair(
            "Does we use shell from browser?",
            "-> Yes, from js"
        ),
        Pair(
            "Does we use python, go, rust, ok?",
            "-> Yes, please try"
        ),
        Pair(
            "How efficient rails is for ${appName}",
            "-> Good, but recommended to use from js"
        ),
        Pair(
            "Is js trigger such as shell?",
            "-> Yes, main front script"
        ),
        Pair(
            "What's main backend script?",
            "-> shell or otheres in ubuntu"
        ),
        Pair(
            "What's main backend script?",
            "-> shell or otheres in ubuntu"
        ),
        Pair(
            "If we use node js?",
            "-> Ok but, only shell can call noti, broadcast, etc"
        ),
        Pair(
            "If shell original feature of ${appName}?",
            "-> Access ${SET_REPLACE_VARIABLE} by get_var cmd"
        ),
        Pair(
            "What's effect of ${SET_REPLACE_VARIABLE}?",
            "-> Central management variable"
        ),
        Pair(
            "What's effect ex of ${SET_REPLACE_VARIABLE}?",
            "-> Isolated each file like micro service"
        ),
        Pair(
            "What's aim of fannel?",
            "-> Separated each feature micro service"
        ),
        Pair(
            "What's feature fannel?",
            "-> Standalone app and support app in ${appName}"
        ),
        Pair(
            "What's center of micro management in ${appName}?",
            "-> fannel"
        ),
        Pair(
            "What's mean fannel",
            "-> Essential micro app in ${appName}"
        ),
        Pair(
            "Is fannel used in system",
            "-> Yes, essential micro app in ${appName}"
        ),
        Pair(
            "Where's fannel example?",
            "-> ${cmdTerminal}, others in repository"
        ),
        Pair(
            "What's overview of this app?",
            "-> List box and edit box"
        ),
        Pair(
            "What's workflow of ${appName}?",
            "-> List box -> edit box"
        ),
        Pair(
            "What's workflow of fannel?",
            "-> Edit box -> exec"
        ),
        Pair(
            "Why make back up system?",
            "-> Time for update and install"
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
            "Why omit tab feature as browser?",
            "-> Priority history"
        ),
        Pair(
            "What's advantage in no tab?",
            "-> Don't have to close tab"
        ),
        Pair(
            "How do you think require tab?",
            "-> No, this has more usability"
        ),
        Pair(
            "More Convenient feature than tab?",
            "-> Highlight search"
        ),
        Pair(
            "Where's highlight search?",
            "-> Highlight text and press net button"
        ),
        Pair(
            "What's highlight search?",
            "-> Speed search system"
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
            "What library is used in ${cmdTerminal}?",
            "-> huashengdun/webssh"
        ),
        Pair(
            "Where is ${appName} history?",
            "-> Left bottom button"
        ),
        Pair(
            "What's is ${appName} history?",
            "-> Used fannel history"
        ),
        Pair(
            "What's is url history?",
            "-> Used url history"
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
            "-> termux client in the past"
        ),
        Pair(
            "termux position in ${appName}?",
            "-> Optional"
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
            "Hard proc in installing Ubuntu?",
            "-> Extract from rootfs"
        ),
        Pair(
            "When hard proc do in installing Ubuntu?",
            "-> After download"
        ),
        Pair(
            "What is ubuntu version?",
            "-> 22.04 cloud image"
        ),
        Pair(
            "What is ubuntu image location?",
            "-> CommandClick-Linux repo"
        ),
        Pair(
            "Where's js interface?",
            "-> webview"
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