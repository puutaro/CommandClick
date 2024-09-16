

schBoxFocus();

function schBoxFocus(){
    var focusOk = false;
    var textAreas = document.getElementsByTagName("textarea");
    [...textAreas].forEach(
        function(el, index){
            if(!el) return;
            const isAreaLabel = el.getAttribute('aria-label');
            if(!isAreaLabel) return;
            setTimeout(
                function(){
                    el.focus();
                    el.select();
                },
                200
            );
            return;
        });
}
