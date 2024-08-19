// js/action

|acVar=runCopyFannelToTempDir
	?importPath=`${copyBeforeFileToTempDir}`
	?replace=
		&SRC_FILE_PATH=`${FANNEL_PATH}`
|acVar=runCopyHomeFannelPathToTempDir
	?importPath=`${copyBeforeFileToTempDir}`
	?replace=
		&SRC_FILE_PATH=`${preferenceHomeFannelsPath}`
|acVar=runCopyHomeScriptUrlsPathToTempDir
	?importPath=`${copyBeforeFileToTempDir}`
		?replace=
			&SRC_FILE_PATH=`${preferenceHomeScriptUrlsPath}`
|acVar=runCopySrcImageAnchorLongPressMenuToTempDir
	?importPath=`${copyBeforeFileToTempDir}`
	?replace=
		&SRC_FILE_PATH=`${srcImageAnchorLongPressMenuPath}`
|acVar=runCopyImageLongPressMenuToTempDir
	?importPath=`${copyBeforeFileToTempDir}`
	?replace=
		&SRC_FILE_PATH=`${imageLongPressMenuPath}`
|acVar=runCopySrcAnchorLongPressMenuToTempDir
	?importPath=`${copyBeforeFileToTempDir}`
	?replace=
		&SRC_FILE_PATH=`${srcAnchorLongPressMenuPath}`
