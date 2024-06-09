// js/action

var=runCopyFannelFile
    ?func=jsFileSystem.copyFile
    ?args=
        srcFile=`${FANNEL_PATH}`
        &destiFile=`${BEFORE_FANNEL_PATH}`
|var=runCopyHomeFannelPathFile
    ?func=jsPath.basename
    ?args=
        path=`${cmdclickConfigHomeFannelsPath}`
    ?value=`${cmdclickConfigTempDirPath}/${it}`
    ?func=jsFileSystem.copyFile
    ?args=
        srcFile=`${cmdclickConfigHomeFannelsPath}`
        &destiFile=`${it}`
    ,
