
|var=srcFilePath
	?value=`{{ SRC_FILE_PATH }}`
|var=runCopyBeforeFileToTemp
	?func=jsPath.basename
	?args=
		path=`${srcFilePath}`
	?value=`${cmdclickConfigTempDirPath}/${it}`
	?func=jsFileSystem.copyFile
	?args=
		srcFile=`${srcFilePath}`
		&destiFile=`${it}`
