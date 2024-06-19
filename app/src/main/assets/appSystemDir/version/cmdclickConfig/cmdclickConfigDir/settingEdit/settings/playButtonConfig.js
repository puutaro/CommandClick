// js/setting

visible=ON,
icon=ok,
click=
	|onScriptSave=ON
	|acVar=runReflectSetingValAction
		?importPath=`${cmdclickConfigSettingEditOkBtnDirPath}/reflectSetingValAction.js`
	|acVar=runReflectRecentSettingVals
		?importPath=`${cmdclickConfigSettingEditOkBtnDirPath}/reflectRecentSettingVals.js`
	|var=runSetOkToast
		?func=jsToast.short
		?args=
			msg="Ok, set",
