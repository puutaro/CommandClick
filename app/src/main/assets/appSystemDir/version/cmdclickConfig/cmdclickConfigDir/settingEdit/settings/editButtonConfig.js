
visible=ON,
icon=list,

click=
    acVar=runChangeToTable
        ?importPath=`${cmdclickConfigChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
