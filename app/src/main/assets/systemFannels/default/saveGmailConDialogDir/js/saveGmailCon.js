
tsvimport `${saveGmailConArgsTsvPath}`;
    use (
       urlConSaveParentDirPath,
       compSuffix,
    );

let titleEntry =
    document.querySelector('[role="heading"]').textContent;
const title = makeTitle(titleEntry);
const mailCon = makeMailCon(titleEntry);
jsToast.short(`Register ok`);
jsToolbar.addUrlCon_S(
    title,
    mailCon,
    `${urlConSaveParentDirPath}`,
    `${compSuffix}`,
);

function makeTitle(
    titleEntry
){
    return replaceSimble(
        titleEntry
    ).replaceAll(/[\\"#$%&'()~^|{}\\[\\];:`<>*\t]/g, "");
}

function makeMailCon(
    titleEntry,
){
    const body = document.getElementById("views").textContent;
    const bodyStartNum = body.lastIndexOf(titleEntry);
    return body.substring(bodyStartNum);
}

function replaceSimble(
    targetStr
){
    let exp = /[-A-Z0-9+&@#\/&#37;?=~_|!:,.;＃＄％＆（）＝〜＾｜￥｛｝。、＜＞＊]/ig;
    return targetStr.replaceAll(exp, "");
}
