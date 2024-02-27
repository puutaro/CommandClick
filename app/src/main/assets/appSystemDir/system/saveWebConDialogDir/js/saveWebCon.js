
tsvimport `${saveWebConArgsTsvPath}`;

const siteCon = document.body.innerText;
jsToast.short(`Register ok`);
jsToolbar.addUrlCon_S(
    document.title,
    siteCon,
    `${urlConSaveParentDirPath}`,
    `${compSuffix}`,
);
