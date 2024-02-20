
tsvimport `${saveWebConArgsTsvPath}`;

const siteCon = document.body.innerText;
jsToast.short(`Register ok`);
jsToolbar.addUrlCon(
    document.title,
    siteCon,
    `${urlConSaveParentDirPath}`,
    `${compSuffix}`,
);
