// js/action

|acVar=runCopyFannelToTempDir
	?importPath=`${copyBeforeFileToTempPath}`
	?replace=
		&SRC_FILE_PATH=`${FANNEL_PATH}`
|acVar=runHomeFannelsPathToTempDir
	?importPath=`${copyBeforeFileToTempPath}`
	?replace=
		&SRC_FILE_PATH=`${cmdclickConfigHomeFannelsPath}`
