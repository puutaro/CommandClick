
|var=srcFilePath
    ?value=`{{ SRC_FILE_PATH }}`
|var=runCopyBeforeFileToTemp
	?func=jsPath.basename
	?args=
		path=`${srcFilePath}`
	?value=`${preferenceTempDirPath}/${it}`
	?func=jsFileSystem.copyFile
	?args=
		srcFile=`${srcFilePath}`
		&destiFile=`${it}`
