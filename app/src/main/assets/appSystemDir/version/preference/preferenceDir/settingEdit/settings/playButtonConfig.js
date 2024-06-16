
visible=ON,
icon=ok,

click=
    onScriptSave=ON
    |acVar=runReflectSetingValAction
        ?importPath=
            `${preferenceSettingEditOkBtnDirPath}
                /reflectSetingValAction.js`
    |acVar=runReflectRecentSettingVals
        ?importPath=
            `${preferenceSettingEditOkBtnDirPath}
            /reflectRecentSettingVals.js`
    |acVar=runToTableState
        ?importPath=
            `${preferenceChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
