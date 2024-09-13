
jsSelectionText.updateText(
    document.getSelection().toString()
);

document.addEventListener('selectionchange', detectSelectChange = function detect(e) {
    const selectText = document.getSelection().toString();
    jsSelectionText.updateText(selectText);
    if(selectText) return;
    jsSelectionText.updateText("");
    jsToolBarCtrl.visibleSelectionBar(false);
    document.removeEventListener('selectionchange', detectSelectChange);
});