

setTimeout(
    function(){
        var firstSelection = document.getSelection().toString();
        if(!firstSelection){
            jsSelectionText.updateText("");
            jsToolBarCtrl.visibleSelectionBar(false);
            return;
        }
        jsSelectionText.updateText(
            firstSelection
        );

        document.addEventListener('selectionchange', detectSelectChange = function detect(e) {
            const selectText = document.getSelection().toString();
            jsSelectionText.updateText(selectText);
            if(selectText) return;
            jsSelectionText.updateText("");
            jsToolBarCtrl.visibleSelectionBar(false);
            document.removeEventListener('selectionchange', detectSelectChange);
        });
    },
    200
);