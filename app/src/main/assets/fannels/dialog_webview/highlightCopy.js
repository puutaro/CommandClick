
const highlightText = getSelectionText();
if(!highlightText) exitZero();

jsToast.short(
	`copy: ${highlightText}`
);
jsUtil.copyToClipboard(
	highlightText, 12
);

function getSelectionText() {
    var text = "";
    if (window.getSelection) {
        text = window.getSelection().toString();
        window.getSelection().removeAllRanges();
    } else if (document.selection && document.selection.type != "Control") {
        text = document.selection.createRange().text;
    };
    return text;
};
