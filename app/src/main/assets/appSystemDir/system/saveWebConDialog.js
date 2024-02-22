

/// SETTING_SECTION_START
setReplaceVariables="file://"
/// SETTING_SECTION_END


const urlString = makeUrl();
if(!urlString) exitZero();
launchWebview(urlString);


function launchWebview(launchUrlString){
    const menuMapStrListStr= makeMenuMapStr();
    const tsvCon = [
        `urlConSaveParentDirPath\t${CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT3}`,
        `compSuffix\t${CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT4}`,
    ].join("\n");
    jsFileSystem.writeLocalFile(
        `${saveWebConArgsTsvPath}`,
        tsvCon,
    );
    jsDialog.webView_S(
        launchUrlString,
        "${0}",
        menuMapStrListStr,
        "",
    );
};


function makeUrl(){
    const externalExecLink = "${CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT1}";
    const cmdclickExternalExecReplaceTextStr = "${CMDDLICK_ENCRPT_EXTERNAL_EXEC_REPLACE_TXT1}".replace(
        "_ENCRPT",
        ""
    );
    if(
        externalExecLink !== cmdclickExternalExecReplaceTextStr
        && externalExecLink !== ""
    ) return externalExecLink;
    return "https://www.google.co.id/search?q=";
};

function makeMenuMapStr(){
    const onSearchBtn = `${CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT2}`;
    switch(true){
        case onSearchBtn === "OFF":
            return [
                `clickMenuFilePath=${leftMenuListFilePath}!longPressMenuFilePath=${leftLongPressMenuListFilePath}!dismissType=longpress!iconName=back`,
                `clickMenuFilePath=${rightMenuListFilePath}!iconName=download_done`,
            ].join("|");
        default:
            return [
                `clickMenuFilePath=${leftMenuListFilePath}!longPressMenuFilePath=${leftLongPressMenuListFilePath}!dismissType=longpress!iconName=back`,
                `clickMenuFilePath=${centerMenuListFilePath}!longPressMenuFilePath=${centerLongPressMenuListFilePath}!iconName=search`,
                `clickMenuFilePath=${rightMenuListFilePath}!iconName=download_done`,
            ].join("|");
    }
}