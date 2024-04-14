
schBoxFocusOrSearch();

function schBoxFocusOrSearch(){
    const googleSearchUrl="https://www.google.co.id/search?q=";
    if(
        !location.href.startsWith(googleSearchUrl)
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
            el.blur();
            el.focus();
            el.select();
            focusOk = true;
            return true;
        });
    if(focusOk) exitZero();
}