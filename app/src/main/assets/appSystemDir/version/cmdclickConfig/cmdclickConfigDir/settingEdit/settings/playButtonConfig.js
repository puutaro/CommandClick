
visible=ON,
icon=ok,

click=
    onScriptSave=ON
    |acVar=runReflectSetingValAction
        ?importPath=
            `${cmdclickConfigSettingEditOkBtnDirPath}
                /reflectSetingValAction.js`
    |acVar=runReflectRecentSettingVals
        ?importPath=
            `${cmdclickConfigSettingEditOkBtnDirPath}
                /reflectRecentSettingVals.js`
    |acVar=runToTableState
        ?importPath=
            `${cmdclickConfigChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
