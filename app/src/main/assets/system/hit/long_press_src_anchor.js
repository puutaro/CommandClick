

let currentFilePathList = "${0}".replace(/^file:\/\//, '').split('/');
const parentDirPath = currentFilePathList.slice(0, currentFilePathList.length - 1).join('/');
const cmdclickCurrentUrl = "CMDCLICK_CURRENT_PAGE_URL";
const targetUrl = "CMDCLICK_LONG_PRESS_LINK_URL";


const LEAST_STRING_NUM = 300;

function makeTocArr(list){
    if(list.length <= 0) return [];
    var tocArr = [], curH2 = [], curH3 = []; 
    for (var i = 0; i < list.length; i++) {
        var e = list[i];
        var tagName = e.tagName ;
        if (tagName == "H1") {
            tocArr.push({text:e.textContent, children:(curH2=[])});
        } else if(tagName == "H2"){
            curH2.push({text:e.textContent, children:(curH3=[])});
        } else {
            curH3.push(e.textContent);
        }
    };
    return tocArr;
};


function makePtagSummaryTotal(summaryPList, summaryEntry, prefix="-"){
    var summaryEntryPTagTextTotal = "";
    var summaryEntryPreTagText = "";
    if(summaryPList.length <= 0) return summaryEntryPTagTextTotal;
    for(var summaryPreTag of summaryPList){
        summaryEntryPreTagText = summaryPreTag.textContent.substring(
            0, LEAST_STRING_NUM
        );
        if(!summaryEntryPreTagText.trim()) continue;
        summaryEntryPTagTextTotal = summaryEntryPTagTextTotal.concat(
            "\n\t\t\t\t", 
            prefix + " ", 
            summaryEntryPreTagText
        );
        if(
            summaryEntry.length + summaryEntryPTagTextTotal.length > LEAST_STRING_NUM
        ) break;
    };
    return summaryEntryPTagTextTotal;
};


function summaryComp(summary, doc){
    if(
        summary.length > LEAST_STRING_NUM
    ) return summary.replace(/\n\n*/g, "\n");
    let summaryPList = doc.querySelectorAll("p");
    var summaryEntry = summary;
    var summaryEntryPtagTextTotal = "";
    summaryEntryPtagTextTotal = makePtagSummaryTotal(
        summaryPList, 
        summaryEntry
    );
    summaryEntry = summaryEntry.concat(
        summaryEntryPtagTextTotal
    );
    if(
        summaryEntry.length > LEAST_STRING_NUM
    ) return summaryEntry.replace(/\n\n*/g, "\n");
    let summaryPreList = doc.querySelectorAll("pre");
    var summaryEntryPreTagTextTotal = "";

    summaryEntryPreTagTextTotal = makePtagSummaryTotal(
        summaryPreList, 
        summaryEntry,
        "--",
    );
    summaryEntry = summaryEntry.concat(
        summaryEntryPreTagTextTotal
    );
    if(summaryEntry) return summaryEntry.replace(/\n\n*/g, "\n");
    return "no summary";
};


function makeSummary(tocArr, doc){
    var summary = "";
    if(
        tocArr.length <= 0
    ) return summaryComp(summary, doc);
    for (var i in tocArr) {
        summary = summary.concat(tocArr[i].text, '\n');

        var ch = tocArr[i].children;
        if (ch.length <= 0) continue;
        for (var i2 in ch) {
            h2Con = ch[i2].text.trim().replaceAll('\n', ' ');
            if(!h2Con) break; 
            summary = summary.concat("\t\t", h2Con, '\n');

            h3ch = ch[i2].children;
            if (h3ch.length <= 0) continue;
            for (var i3 in h3ch){
                h3Con = h3ch[i3].trim().replaceAll('\n', ' ');
                if(!h3Con) break; 
                summary = summary.concat("\t\t\t\t", h3Con, '\n');
            };
        };
        if(
            summary.endsWith("\n\n")
        ) continue;
    };
    return summary.replace(/\n\n*/g, "\n");
};


var getHtml = jsCurl.get(
			targetUrl,
			"",
			"",
            2000
		);
var doc = document.createElement( 'html' );
doc.innerHTML = getHtml;
var list = doc.querySelectorAll("h1,h2,h3"); 

let tocArr = makeTocArr(list);
var summary = makeSummary(tocArr, doc);

if(summary.length < LEAST_STRING_NUM) {
    summary = summaryComp(summary, doc);
};
alert(summary);
