
tsvimport `${saveWebConArgsTsvPath}`;
    use (
       urlConSaveParentDirPath,
       compSuffix,
       onSaveUrlHistory,
    );
const siteCon = document.body.innerText;
jsToast.short(`Register ok`);
registerUrlHistory();
jsToolbar.addUrlCon_S(
    document.title,
    siteCon,
    `${urlConSaveParentDirPath}`,
    `${compSuffix}`,
);

function registerUrlHistory(){
    if(
        `${onSaveUrlHistory}` != "ON"
    ) return;
    const url = location.href;
    if(!url) return;
    const title = document.title;
    if(!title) return;
    jsUrlHistory.save(
        title,
        url,
    );
}
