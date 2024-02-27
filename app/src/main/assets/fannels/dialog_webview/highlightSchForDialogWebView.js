

/// LABELING_SECTION_START
// google search highlight text for dialog_webView @puutaro
/// LABELING_SECTION_END


const googleSearchUrl="https://www.google.co.id/search?q=";


let prefixEnum = {
	https: "https://",
	http: "http://",
};


const highlightUrlString = getSelectionText();
if(!highlightUrlString) {
	location.href = `${googleSearchUrl}`;
	exitZero();
};
execCpSearch(
	appendUrlPrefix(highlightUrlString)
);


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


function execCpSearch(urlString){
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
