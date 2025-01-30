
schBoxFocusOrSearch();

function schBoxFocusOrSearch(){
    const ggleSearchBase = "https://www.google.com/search?";
    if(
        !location.href.startsWith(ggleSearchBase)
    ){
        exitZero();
        return;
    }
    var focusOk = false;
    var textAreas = document.getElementsByTagName("textarea");
    [...textAreas].forEach(
        function(el, index){
            if(!el) return;
            const isAreaLabel = el.getAttribute('aria-label');
            if(!isAreaLabel) return;
//            el.blur();
            jsKeyboard.show();
            setTimeout(
                function(){
//                    el.blur();
                    el.focus();
                    el.select();
                },
                200
            );
            focusOk = true;
            return true;
        });
    if(focusOk) exitZero();
}
