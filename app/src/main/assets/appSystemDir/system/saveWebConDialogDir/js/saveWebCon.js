
tsvimport `${saveWebConArgsTsvPath}`;

const fileName = jsDialog.prompt(
    "Input file name",
    "",
    "",
);
if(!fileName) exitZero();
const siteCon = document.body.innerText;
jsToast.short("sitecon" + siteCon + "\n" + `buttonType: ${buttonType}`);
jsToolbar.addUrlCon(
    `${clickKey}`,
    `${buttonType}`,
    fileName,
    siteCon,
);
