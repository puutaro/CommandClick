// js/action

var=runCopyFannelFile
    ?func=jsFileSystem.copyFile
    ?args=
        srcFile=`${FANNEL_PATH}`
        &destiFile=`${BEFORE_FANNEL_PATH}`
|var=runCopyHomeFannelPathFile
    ?func=jsPath.basename
    ?args=
        path=`${preferenceHomeFannelsPath}`
    ?value=`${preferenceTempDirPath}/${it}`
    ?func=jsFileSystem.copyFile
    ?args=
        srcFile=`${preferenceHomeFannelsPath}`
        &destiFile=`${it}`
    ,
