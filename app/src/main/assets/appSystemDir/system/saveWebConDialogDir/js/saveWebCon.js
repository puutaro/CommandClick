
tsvimport `${saveWebConArgsTsvPath}`;

const fileName = jsDialog.prompt(
    "Input file name",
    "",
    "",
).trim().replaceAll("\n", "").trim();
if(!fileName) exitZero();
const siteCon = document.body.innerText;
jsToast.short(`Register ok`);
jsToolbar.addUrlCon(
    `${clickKey}`,
    `${buttonType}`,
    fileName,
    siteCon,
);
