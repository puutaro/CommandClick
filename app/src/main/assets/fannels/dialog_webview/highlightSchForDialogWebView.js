

/// LABELING_SECTION_START
// google search highlight text for dialog_webView @puutaro
/// LABELING_SECTION_END


const googleSearchUrl="https://www.google.co.id/search?q=";


let prefixEnum = {
	https: "https://",
	http: "http://",
};


const highlightUrlString = filterByWithinGgleSchBox(
    getSelectionText()
);
schBoxFocusOrSearch(highlightUrlString);
execSearch(
	appendUrlPrefix(highlightUrlString)
);

function schBoxFocusOrSearch(highlightUrlString){
    if(
        highlightUrlString
    ) return;
    if(
        !location.href.startsWith(googleSearchUrl)
    ){
        location.href = `${googleSearchUrl}`;
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

function filterByWithinGgleSchBox(selectedText){
    if(
        !selectedText
    ) return "";
    var schBoxStr = "";
    var textAreas = document.getElementsByTagName("textarea");
    [...textAreas].forEach(
        function(el, index){
            if(
                !el
            ) return;
            const isAreaLabel = el.getAttribute('aria-label');
            if(
                !isAreaLabel
            ) return;
            schBoxStr = el.value;
            return true;
        });
    if(
        schBoxStr == selectedText
    ) return "";
    return selectedText;
}


function appendUrlPrefix(highlightUrlString){
	switch(true){
		case highlightUrlString.startsWith(prefixEnum.https):
		case highlightUrlString.startsWith(prefixEnum.http):
			return highlightUrlString;
			break;
		case true:
			return `${googleSearchUrl}${highlightUrlString}`;
			break;
	};
};


function execSearch(urlString){
	switch(true){
		case urlString.startsWith(prefixEnum.https):
		case urlString.startsWith(prefixEnum.http):
			location.href = urlString;
			exitZero();
			break;
	};
};


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
