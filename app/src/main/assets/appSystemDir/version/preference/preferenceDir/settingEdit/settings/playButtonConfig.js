// js/setting

visible=ON,
icon=ok,
click=
	|onScriptSave=ON
	|acVar=runReflectSetingValAction
		?importPath=`${preferenceSettingEditOkBtnDirPath}/reflectSetingValAction.js`
	|acVar=runReflectRecentSettingVals
		?importPath=`${preferenceSettingEditOkBtnDirPath}/reflectRecentSettingVals.js`
	|var=runSetOkToast
		?func=jsToast.short
		?args=
			msg="Ok, set",
