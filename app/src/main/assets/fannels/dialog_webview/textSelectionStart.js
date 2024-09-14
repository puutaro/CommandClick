

setTimeout(
    function(){
        var firstSelection = getTextSelection();
        if(!firstSelection){
            jsSelectionText.updateRegisterText("");
            jsToolBarCtrl.visibleSelectionBar(false);
            return;
        }
        jsSelectionText.updateSelectionTextView(firstSelection);
        jsSelectionText.updateRegisterText(
            firstSelection
        );

        document.addEventListener('selectionchange', detectSelectChange = function detect(e) {
            const selectText = getTextSelection();
            jsSelectionText.updateSelectionTextView(selectText);
            jsSelectionText.updateRegisterText(selectText);
            if(selectText) return;
            jsSelectionText.updateRegisterText("");
            jsToolBarCtrl.visibleSelectionBar(false);
            document.removeEventListener('selectionchange', detectSelectChange);
        });
    },
    50
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