diffSettingVals

|var=fannelCon
    ?func=jsFileSystem.read
        ?args=
            path=`${FANNEL_PATH}`
|var=cmdValCon
    ?func=jsScript.subCmdVars
    ?args=
        con=`${fannelCon}`
    ?func=jsToListFilter.filter
    ?args=
        lines=`${it}`
        &separator="\n"
        &matchLines=""
        &extraMapCon=`
            removeRegex1="^[ \t]+"
            |removeRegex2="[ \t]+$"
            |removeRegex3="^//.*"
            |matchRegex1="[a-zA-Z0-9]"
        `
|var=runReflectSettingValsByCmdVals
    ?func=jsScript.replaceSettingVariable
    ?args=
        con=`${fannelCon}`
        &cmdValCon=`${cmdValCon}`
    ?func=jsFileSystem.write
    ?args=
        path=`${FANNEL_PATH}`
        &cmdValCon=`${it}`
    ?func=jsFileSystem.updateWeekPastLastModified
    ?args=
        path=`${FANNEL_PATH}`

