

/// SETTING_SECTION_START
setReplaceVariables="file://"
setVariableTypes="file://"
hideSettingVariables="setReplaceVariables"
hideSettingVariables="setVariableTypes"
/// SETTING_SECTION_END


const urlString = makeUrl();
// jsToast.short(
//     urlString + "--"
// );
if(!urlString) exitZero();
launchWebview(urlString);


function launchWebview(launchUrlString){
    const menuMapStrListStr= [
        `clickMenuFilePath=${leftMenuListFilePath}!longPressMenuFilePath=${leftLongPressMenuListFilePath}!dismissType=longpress!iconName=back`,
        `clickMenuFilePath=${centerMenuListFilePath}!longPressMenuFilePath=${centerLongPressMenuListFilePath}!iconName=search`,
        `clickMenuFilePath=${rightMenuListFilePath}!iconName=download_done`,
    ].join("|");
    const tsvCon = [
        `buttonType\t${CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT2}`,
        `clickKey\t${CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT3}`,
    ].join("\n");
    jsFileSystem.writeLocalFile(
        `${saveWebConArgsTsvPath}`,
        tsvCon,
    );
    // const longPressMenuListStr = [
    //     `srcImageAnchorMenuFilePath=${srcImageAnchorMenuListPath}`,
    //     `srcAnchorMenuFilePath=${srcAnchorMenuListPath}`,
    //     `imageMenuFilePath=${imageMenuListPath}`,
    // ].join("!");
    jsDialog.webView(
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
    jsToast.short(`externalExecLink: ${externalExecLink}\n${externalExecLink !== cmdclickExternalExecReplaceTextStr}`);
    if(
        externalExecLink !== cmdclickExternalExecReplaceTextStr
        && externalExecLink !== ""
    ) return externalExecLink;
    return "https://www.google.co.id/search?q=";
};