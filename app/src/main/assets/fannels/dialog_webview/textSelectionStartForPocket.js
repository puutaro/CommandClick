

setTimeout(
    function(){
        var firstSelection = getTextSelection();
        if(!firstSelection){
            jsWebViewDialogManager.visibleSelectionBar(false);
            return;
        }
        jsWebViewDialogManager.updateSelectionTextView(firstSelection);
        document.addEventListener('selectionchange', detectSelectChange = function detect(e) {
            const selectText = getTextSelection();
            jsWebViewDialogManager.updateSelectionTextView(selectText);
            if(selectText) return;
            jsWebViewDialogManager.visibleSelectionBar(false);
            document.removeEventListener('selectionchange', detectSelectChange);
        });
    },
    100
);

function getTextSelection(){
    if (window.getSelection) {
        var selectionRange = window.getSelection();
        return selectionRange.toString();
    }
    if (document.selection.type == 'None') {
        return "";
    }
    var textRange = document.selection.createRange();
    return textRange.text;
}
