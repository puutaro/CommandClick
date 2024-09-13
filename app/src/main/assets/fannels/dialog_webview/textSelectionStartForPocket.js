

setTimeout(
    function(){
        var firstSelection = document.getSelection().toString();
        if(!firstSelection){
            jsWebViewDialogManager.visibleSelectionBar(false);
            return;
        }
        document.addEventListener('selectionchange', detectSelectChange = function detect(e) {
            const selectText = document.getSelection().toString();
            if(selectText) return;
            jsWebViewDialogManager.visibleSelectionBar(false);
            document.removeEventListener('selectionchange', detectSelectChange);
        });
    },
    200
);
