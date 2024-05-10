

/// SETTING_SECTION_START
setReplaceVariables="file://"
/// SETTING_SECTION_END


const urlString = makeUrl();
if(!urlString) exitZero();
launchWebview(urlString);


function launchWebview(launchUrlString){
    const menuMapStrListStr= makeMenuMapStr();
    const tsvCon = [
        `urlConSaveParentDirPath\t${EXTERNAL_EXEC_REPLACE_TXT2}`,
        `compSuffix\t${EXTERNAL_EXEC_REPLACE_TXT3}`,
    ].join("\n");
    jsFileSystem.writeLocalFile(
        `${saveGmailConArgsTsvPath}`,
        tsvCon,
    );
    jsDialog.webView_S(
        launchUrlString,
        "${0}",
        menuMapStrListStr,
        "",
        "",
    );
};


function makeUrl(){
    const externalExecLink = "${EXTERNAL_EXEC_REPLACE_TXT1}";
    const cmdclickExternalExecReplaceTextStr = "${ENCRPT_EXTERNAL_EXEC_REPLACE_TXT1}".replace(
        "ENCRPT_",
        ""
    );
    if(
        externalExecLink !== cmdclickExternalExecReplaceTextStr
        && externalExecLink !== ""
    ) return externalExecLink;
    return "https://www.google.co.id/search?q=";
}

function makeMenuMapStr(){
    // const onSearchBtn = `${EXTERNAL_EXEC_REPLACE_TXT2}`;
    return  [
        `clickMenuFilePath=${leftMenuListFilePath}?longPressMenuFilePath=${leftLongPressMenuListFilePath}?dismissType=longpress?label=⬅`,
        `clickMenuFilePath=${rightMenuListFilePath}?label=⬇︎️`,
    ].join("|");
}