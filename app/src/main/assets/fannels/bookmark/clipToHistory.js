

clipToHistory();

function clipToHistory(){
	const clipUrl = jsUtil.echoFromClipboard();
	if(!clipUrl) return;
	if(
		!clipUrl.startsWith('http://')
		&& !clipUrl.startsWith('https://')
	) return;
	const currentAppDirPath = "${01}".replace("/js", "");
	const historyPath = `${currentAppDirPath}/url/cmdclickUrlHistory.tsv`;
	const currentHistoryCon = jsFileSystem.readLocalFile(
		historyPath
	);
	const newLine = `${clipUrl}\t${clipUrl}`;
	const saveHisCon = `${newLine}\n${currentHistoryCon}`;
	jsFileSystem.writeLocalFile(
		historyPath,
		saveHisCon
	);
	jsToast.short("write ok");
	location.reload();
};
