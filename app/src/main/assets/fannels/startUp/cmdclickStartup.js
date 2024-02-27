

/// LABELING_SECTION_START
// * terminalDo is terminal screen select option (only shellScript)
//  - ON: cmdclick terminal (default)
//  - TERMUX: termux terminal,
//  - OFF: no terminal(backend exec)
// * editExecute is edit mode change
//  - NO is normal edit
//  - ONCE is one time edit and execute
//  - ALWAYS is always edit and execute
// * terminalSizeType is cmdclick terminal size option
//  - OFF: no adjust (default)
//  - LONG: LongSize
//  - SHORT: ShortSize
// * terminalOutputMode decide output mode in cmdclick terminal (basically, only shellScript)
//  - NORMAL: normal terminal output (default)
//  - REFLASH: Before terminal output, screen resflesh
//  - REFLASH_AND_FIRST_ROW: Before terminal output, screen resflesh and focus first row
//  - DEBUG: stdr + stderr
//  - NO: no output (bacground exec)
// * onAutoExec is ready for start and end script
//  - ON: start or end exec on
//  - OFF: exec off (default)
// * onUpdateLastModify is how updating file last modified status when executing
//  - ON: update this (default)
//  - OFF: no update this
// * historySwitch: switch app history with url history
//  - ON: switch
//  - OFF: no switch
//  - INHERIT: inherit config setting (default)
// * onHistoryUrlTitle is how adding url title to history
//  - ON: add
//  - OFF: no
// * urlHistoryOrButtonExec switch url history or button script exec
//  - INHERIT: inherit config setting
//  - URL_HISTORY: switch url history
//  - BUTTON_EXEC: switch url button script exec
// * onAdBlock: adblock switch
//  - INHERIT: inherit config setting
//  - ON: on
//  - OFF: off
// * onUrlLaunchMacro: url launch macro(when set, cmdclick web terminal don't output)
//  - OFF: no launch
//  - RECENT: recent use url launch
//  - FREQUENCY: most use url launch
// * onUrlHistoryRegister: url history update signal
//  - ON: update
//  - OFF: no update
// * execJsOrHtmlPath: execute javascript or html file path
//   - disable, when onUrlLaunchMacro is not OFF
// * terminalFontZoom adjust terminal font size (percentage)
// * terminalFontColor adjust terminal font color
// * terminalColor adjust terminal background color
// * setReplaceVariable is string replaced with certain string
//  - ex) setReplaceVariable="{replaceVariablle1}={repalce string1}"
//  - ex) setReplaceVariable="{replaceVariablle2}={repalce string2}"
//  - ex) setReplaceVariable="{replaceVariablle3}={repalce string3}"
//  - ex) setReplaceVariable="..."
// * setVariableType is cmdsection gui edit mini program, reference to github for detail (like gtk yad)
//  - ex) spinner: {cmdVariable}:CB=ON!OFF  
//  - ex) num crementer: {cmdVariable}:NUM=1!1..100!1 (({init})!{min}..{max}!{step})
//  - ex) file selector: {cmdVariable}:FL=
//  - ex) button: {cmdVariable}:BTN=
//    - button execute command 
//      ex) echo $0  
//             ("$0" is current shell path
//      ex) ::BackStack:: ls
//             ("::BackStack::" is backstack, only work when prefix
//      ex) ::BackStack:: ls
//             ("::BackStack::" enable terminal output
//      ex) top -n 1 > /dev/null  
//             (when suffix is "> /dev/null" or "> /dev/null 2>&1", no output
//  - ex) dir selector: {cmdVariable}:DIR=
//  - ex) read only: {cmdVariable}:RO=
//  - ex) password: {cmdVariable}:H=
//  - enable multiple specification
//  - ex) 
//  setVariableType="{cmdVar1}:CB=ON!OFF"
//  setVariableType="{cmdVar2}:FL="
//  setVariableType="..."
// * beforeCommand is before shell script execute, run command
// * afterCommand is after shell script execute, run command
// * scriptFileName is your shell file name
/// LABELING_SECTION_END


/// SETTING_SECTION_START
editExecute="ALWAYS"
terminalSizeType="OFF"
terminalOutputMode="NORMAL"
onAutoExec="ON"
onUpdateLastModify="OFF"
onHistoryUrlTitle="OFF"
historySwitch="INHERIT"
urlHistoryOrButtonExec="INHERIT"
onAdBlock="INHERIT"
onUrlLaunchMacro="RECENT"
execJsOrHtmlPath=""
homeScriptUrlsPath=""
terminalFontZoom="0"
terminalFontColor=""
terminalColor=""
setReplaceVariable=""
setVariableType=""
scriptFileName="cmdclickStartup.js"
/// SETTING_SECTION_END


/// CMD_VARIABLE_SECTION_START
/// CMD_VARIABLE_SECTION_END


/// Please write bellow with javascript


let args = jsArgs.get().split("\t");
const firstArgs = args.at(0);
const homeScriptUrlsPathMode = "homeScriptUrlsPath";
const currentAppDirPath = "${01}";
const fannelDirPath = `${currentAppDirPath}/${001}`;
const homeScriptUrlsPathDir = `${fannelDirPath}/homeScriptUrlsPath`;
const homeScriptUrlsPathsFile = `${homeScriptUrlsPathDir}/homeScriptUrlsPath.txt`;


modeSwitcher();

function modeSwitcher(){
	switch(firstArgs){
		case homeScriptUrlsPathMode:
			let fannelList = jsFileSystems.showDirList(
				currentAppDirPath
			).filter(function(path){
				const enableSuffix = path.endsWith(".js") 
					|| path.endsWith(".html");
				return !path.includes(scriptFileName)
					&& enableSuffix;
			});
			const fannelName = jsDialog.gridDialog(
		        "select add fannel",
		        "",
		        fannelList.join("\t")
		    );
		    alert(fannelName);
		    // const currentFannelsCon = jsFileSystem.readLocalFile(
			// 	homeScriptUrlsPathsFile
			// );
			// jsFileSystem.writeLocalFile(
		  	// 	homeScriptUrlsPathsFile,
			// 	`${currentFannelsCon}\n${currentFannelsCon}`
		  	// );
	};
};
