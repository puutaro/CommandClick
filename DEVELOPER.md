
# Command Click Developer page
<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="700">  

  
This page is for developer. CommandClick true value change self made script to android app.   
I hope you get that knowledge.  
Mainly, `ComamndClick` is enforced by javascript. So, most bellow context is for javascript development.  
Although via `termux`, enforced by shellscript, this page's main contens is javascript.  


Table of Contents
-----------------
<!-- vim-markdown-toc GFM -->

* [Structure](#structure)
* [Fannel structure](#fannel-structure)
* [Setting variable](#setting-variable)
* [Edit execute once](#edit-execute-once)
* [Edit execute always](#edit-execute-always)
* [Import library](#import-library)
	* [Local path import](#local-path-import)
	* [Assets import](#assets-import)
	* [WEB import](#web-import)
* [Url command](#url-command)
* [Html automaticaly creation command to edit target edit file](#html-automaticaly-creation-command-to-edit-target-edit-file)
* [File api](#file-api)
* [JavaScript interface](#javascript-interface)
* [Javascript pre reserved word](#javascript-pre-reserved-word)
* [Include Javascript Library](#include-javascript-library)
* [Include css Library](#include-css-library)
* [Html tag output](#html-tag-output)
* [Html tag output](#html-tag-output)
* [Javascript TroubleShooting](#javascript-troubleshooting)
* [CommandClick repository](#commandclick-repository)


### Structure


<img src="https://github.com/puutaro/CommandClick/assets/55217593/e06a623e-0fd6-4325-ac9f-b795e2d2a4aa" width="500">  

### Fannel structure

`fannel` is `ComamndClick` using script  

<img src="https://github.com/puutaro/CommandClick/assets/55217593/866958e3-8643-4cf0-b610-000f8245397f" width="400">  

- setting variable contents  
  -> [Setting variable](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#setting-variable)

- cmd variable contents  
  user difinition setting variables  

- script contents  
  `javascript`' or `shellscript`' contents


### Setting variable 

  `CommandClick`'s system setting variables  
  
  -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/setting_variables.md)

  
### Edit execute once

One time edit and execute

![image](https://user-images.githubusercontent.com/55217593/216524059-97c35357-c0de-48c1-953f-b1e1478cf296.png)


### Edit execute always

![image](https://user-images.githubusercontent.com/55217593/216652110-4bc01a73-2b8b-42f2-8253-49062e775b66.png)

Always edit and execute. So called `Script2GUI`. It's great feature. 
How the script file turns into a GUI Application! 

  

### Import library  

`CommandClick` can import all file with uri. We can use javascript all of the world. This is `CommandClick` basic idea.  
`CommandClick` is open world app, as is, web browser, termux client, applicatoin maker,  applicatoin store, and library terminal.    
Bellow is how to import. You can enjoy this all range import application!  

#### Local path import

```js.js
ccimport {path}   
```

* current directory -> `./`  
* move parent direcoty -> ../  
* other check [Javascript pre order word](#javascript-pre-order-word)   

#### Assets import

```js.js
ccimport /android_asset/{relative path}  
```

#### WEB import

```js.js
ccimport {URL}  
```

* It is possible to download by curl {URL}


### Url command

Exec bellow command in `CommandClick` shellscript, so that you can launch web site.
(This command is only active when command click focus)

```sh.sh
am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "{url}"
```

```sh.sh
ex) am broadcast \
 -a "com.puutaro.commandclick.url.launch" \
 --es url "https://github.com/puutaro/CommandClick/edit/master/README.md"
```


### Html automaticaly creation command to edit target edit file 

Exec bellow command in `CommandClick` shellscript, so that you can make automaticaly make html, css and javascript.
(This command is only active when command click focus)

```sh.sh
am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "{target edit file path}" \
		--es src_path "{source file path}" \
		--es on_click_sort "boolean(sortable when link click)" \
		--es on_sortable_js "boolean(sortable link list)" \
		--es on_click_url "boolean(launch url when link click)" \
		--es filter_code "{javascript filter code}"
``` 

  - `edit_path` is file path edit by html, also file name is title  
	- ex)  Target edit file is tsv, which composed two row.
                urltitle1  urlString1
                urltitle2  urlString2
                urltitle3  urlString3
                .
                .
                .  
		  
  - (Optional) `src_path` is source file path for input text string, Ordinaly, first hit one line's title display default string, and hold first hit line's url  
	- ex)  Source file is tsv, which composed two row like above.  
  - (Optional) `on_click_sort` is how to sort top when link click.  
  - (Optional) `filter_code` filter target source file  by javascript code. default value is `true`. You can use `urlString` and `urlTitle` variable to filter.  
  
```sh.sh
ex) am broadcast \
		-a "com.puutaro.commandclick.html.launch" \
		--es edit_path "${PARENT_DIR_PATH}/tubePlayList" \
		--es src_path "${PARENT_DIR_PATH}/cmdclickUrlHistory" \
		--es on_click_sort "false" \
		--es on_sortable_js "true" \
		--es on_click_url "true" \
		--es filter_code "urlString.startsWith('http') && urlString.includes(\"youtube\");"
```

- edit html esxample

![image](https://user-images.githubusercontent.com/55217593/222952726-f5ce0753-f299-44cd-a9b0-a021c56d3b4c.png)




### File api
`CommandClick` automaticaly create files in `App directory`/`system`/`url`. This is used by system, alse is userinterface for app signal.
- `cmdclickUrlHistory` 
      - CommandClick use recent used url launch etc.
- `urlLoadFinished`
      - This is made when url load finished. When you make `fannenl`(javascript, shell, and html application), you may use this.

### JavaScript interface
`CommandClick` is javascript framework for andorid. Particularly, this methods strongly support your android app development(`fannel` development).  
This, so colled, android app row code library.

```js.js

 - jsFileStystem

 	- jsFileStystem.showFileList(
		dirPath: String
          )
		-> return filelist tab sepalated
 
	- jsFileStystem.showDirList(
		dirPath: String
	  )
		-> return filelist tab sepalated
 
 	- jsFileStystem.readLocalFile(
		path: String
	   )
		->  read local file and return file contents string

	- jsFileStystem.writeLocalFile(
		path: String, contents: String
	  )
		- write local file

	- jsFileStystem.jsFile(
		filename: String,
		terminalOutPutOption: String
	  )
		- write local monitor file  
	- jsFileStystem.removeFile(
		path: String
          )
		- remove local file
 
	- jsFileStystem.createDir(
		path: String
	  )
		- creaate local dirctory

	- jsFileStystem.removeDir(
		path: String
	)
		- remove local direcotry

	- jsFileStystem.copyDir(
		sourcePath: String,
		destiDirPath: String
	  )
		- copy local directory

	- jsFileSystem.outputSwitch(
		switch: String
	)
		- switch == on, then enable terminal output.
                        other default.
                        (althogh being webmode, terminal mode off, this inmterface switch on)
 
	- jsFileSystem.isFile(
		filePath: String
	   )
		-> boolean

	- jsFileSystem.isDir(
		DirectoryPath: String
	   )
		-> boolean

	- jsFileSystem.removeAndCreateDir(
	        dirPath: String
	  )
		-> remove and create directory


 - JsArgs

	- jsArgs.get()
		-> tabsepalete string  
			jsArgs soruce is jsf argument in edit  
			ex) setVariableType="jsf $0 fristargment 'secondargument 2'" 
				-> `fristargment`\t`secondargument 2`  

	- jsArgs.set(
		tabsepalete string
	    )
		-> argment set (ex "{arg1}\t{arg2}\t..")  


 - JsIntent

 	- jsIntent.launchEditSite(
		editPath: String,
		srcPath: String,
		onClickSort: String(true/false),
		onSortableJs: String(true/false),
		onClickUrl: String(true/false),
		filterCode: String,
		onDialog: String(true/false)
	  )
		- ref: [html automaticaly creation command to edit target edit file]

 	- jsIntent.launchUrl(
		urlString: String
          )
		-> launch uri(not url but uri)

	- jsIntent.launchApp(
		action: String,
		uriString: String,
		extraString: tabSepalatedString,
		extraInt: tabSepalatedString,
		extraLong: tabSepalatedString,
		extraFloat: tabSepalatedString
	   )
		- launch app site

		ex) bellow, launch google calendar  
			jsIntent.launchApp(
				"android.intent.action.INSERT",
				"content://com.android.calendar/events",
				"title=title\tdescription=description\teventLocation=eventLocation\tandroid.intent.extra.EMAIL=email",
				"",
				beginTime=167889547868058\tendTime=165678973498789",
				""
			);

	- jsIntent.launchShortcut(
		currentAppDirPath: String,
		currentShellFileName: String
	    )
		- launch index and fannel  

	- jsIntent.shareImage(
		shareImageFilePath: String
	  )
		- share image intent


 - JsDialog

	- jsDialog.prompt(
		title: String,
		message: String,
	  )
		-> input text string

 	- jsDialog.listJsDialog(
		listSource: String(tab sepalate)
	   )
		-> selected list

 	- jsDialog.formJsDialog(
		formSettingVariables: String(tab sepalate),
		formCommandVariables: String(tab sepalate)
	  )
 		 -> formSettingVariables tabsepalete string  return {key}={value} contents
 		 - setting reference [Add](#add)
 		 - ex) 
 				jsDialog.formJsDialog(
					"efcb:EFCB=tube\tnumber:NUM=2!1..100!1\tpassword:H=\ttxt:TXT=\tcb:CB=aa!bb\tcb2:CB=gg!tt\tcb3:ECB=gg!tt",  
					`efcb=\tefcb=tubeCrara\tnumber=\tpassword=\ttxt=cb2=tt\tdb3=gg`  
				)      
 			        -> efcb:EFCB=tubelist\nnumber:NUM=99\npassword:H=1234\ntxt:TXT=yrcy\ncb=aa\ncb2=tt\ncb3=tt

	- jsDialog.getFormValue(
		targetVariableName: String,
        	contentsTabSepalateFormJsReturnValue: String
	  )
		-> target variable value
				
				
	- jsDialog.multiListDialog(
		title: String,  
		currentItemListStr: String(tab sepalate),  
		preSelectedItemListStr: String(tab sepalate),  
	    )
		-> tab sepalated items
 		 	- ex) 
 				jsDialog.multiListDialog(
					"{item1}\t{item2}",  
					`{item1}\t{item2}\t{item3}\t{item4}`  
				)      
 			        -> {item1}\t{item2}\t{item4}

	- jsDialog.gridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected image path

	- jsDialog.onlyImageGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected image path

	- jsDialog.onlySpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected spannable image path

	- jsDialog.multiSelectGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected spannable image paths

	- jsDialog.onlySpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only spannable image grid view without file name search)
		-> selected spannable image path

	- jsDialog.multiSelectOnlyImageGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected image paths				

	- jsDialog.multiSelectSpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected spannable image paths

	- jsDialog.asciiArtDialog(
		title: String,
        	imagePath: String
	  )
		-> display ascii art with share button

	- jsDialog.imageDialog(
		title: String,
        	imagePath: String
	  )
		-> display image with share button

	- jsDialog.webview(
		urlStr: String,
		currentFannelPath: String,
	        centerMenuMapStr: String(ex onSwitch=\tmenuFilePath=~), *onSwitch exchange click and long click 
	        rightMenuMapStr: String(ex onSwitch=\tmenuFilePath=~), *onSwitch exchange click and long click 
	        srcAnchorImageMapStr: String(ex menuFilePath=~), 
	        srcAnchorMapStr: String(ex menuFilePath=~), 
	        imageMapStr: String(ex menuFilePath=~), 
	  )
		- launch webview dialog with url 

				
 - JsStop

 	- jsStop.how()
		-> Boolean
		(measure for `while roop` crush when application focus out)


 - JsToast

 	- jsToast.short(
		contents: string
	  )
		- short toast

	- jsToast.long(
		contents: string
	  )
		- long toast


 - JsCurl

 	- jsCurl.get(
		mainUrl: string,
		queryParameter: String,
		header: String(ex Authorication\tbear token,contentType\ttext/plain..),
		Timeout: Int (miliSeconds)
	  )
		-> get response

	- jsCurl.getTextOrPdf(
		url: text or pdf url
	   )
		-> download text or pdf file image to bellow `/storage/emulated/0/Document/cmdclick/temp/download`

	- jsCurl.getImage(
	        url: String
	    )
		download image to bellow `/storage/emulated/0/Document/cmdclick/temp/download`


 - JsUtil

 	- jsUtil.sleep(
		sleepMiriTime: Int
	  )
		- sleep miri seconds

	- jsUtil.copyToClipboard(
		copyString: String,
		fontSize: Int
	  )
		- copy to clipboard

	- jsUtil.echoFromClipboard()
		-> primary clipboard string

	- jsUtil.convertDateTimeToMiliTime(
		datetime: String(YYYY-MM-DDThh:mm)
	   )
		-> militime


 - JsUrl

 	- jsUrl.makeJsUrl(
		jsPath: String
	  )
		-> javascript:(
			function() { ${jsPathCoontents} }
	  	   )();

	- jsUrl.loadUrl(
		urlString: String
          )
		-> load url by webview  


 - JsScript

 	- jsScript.subLabelingVars(
		jsContents: String
	  )
		-> Labeling Section Contents

	- jsScript.subSettingVars(
		jsContents: String
	  )
		-> Setting Section Contents

	- jsScript.subCmdVars(
		jsContents: String
	  )
		-> Comamnd Section Contents

	- jsScript.subValOnlyValue(
		targetVariableName: String,
		VariableValueStringContents: String
	  )
		->  Variable value String Contents

	- jsScript.bothQuoteTrim(
		VariableValueString: String
	  ) -> VariableValueString removed both edge quote  
	- jsScript.replaceSettingVariable(
		scriptContents: String,
		replaceTabList: String
	  )
		-> File contents String

	- jsScript.replaceVariableInHolder(
		scriptContents: String,
		replaceTabList: String
	  )
		-> File contents String  


 - JsListSelect
	This interface exist for `LSB`, `ELSB`, `GB` and `MSB` `setVariableTypes` option (ref [Add]

 	update or remove method for editable list file checkbox 
 	- jsListSelect.updateListFileCon(
		targetListFilePath: String,
		itemText: String
	  )
		- update `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 

	- jsListSelect.removeItemInListFileCon(
		targetListFilePath: String,
		itemText: String
	  )
		- remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 

	- jsListSelect.wrapRemoveItemInListFileCon(
                targetListFilePath: String,  
                removeTargetItem: String,  
                currentScriptPath: String,  
                replaceTargetVariable: String = String(),  
                defaultVariable: String = String()  
          )
		- remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` and update View


 - JsFileSelect
 	This interface exist for `FCB`, `FSB` setVariableTypes` option (ref [Add]
 
	- execEditTargetFileName(  
        	targetVariable: rename target command variable string,  
        	renameVariable: rename destination command variable String,  
        	targetDirPath: file select direcoty path,  
        	settingVariables: setting variable with tab sepalator,   
        	commandVariables: command variable with tab sepalator, 
        	prefix: file select direcotry grep prefix string,  
		suffix: file select direcotry grep suffix string,  
        	scriptFilePath: fannel path string  
    	)
		- edit targetVariable value(file name) and update view by form dialog

 - JsDirSelect
 	This interface exist for `FCB`, `FSB` setVariableTypes` option (ref [Add]
 
	- execEditTargetFileName(  
        	targetVariable: rename target command variable string,  
        	renameVariable: rename destination command variable String,  
        	targetDirPath: file select direcoty path,  
        	settingVariables: setting variable with tab sepalator,   
        	commandVariables: command variable with tab sepalator, 
        	scriptFilePath: fannel path string,
		title: title string
    	)
		- edit targetVariable value(directory name) and update view by form dialog


 - JsEdit  
 	`edit component` edit tool

	- jsEdit.getFromEditText(
		targetVariableName: String,
	    )
		-> get target variable value stirng  from view
	    
 	- jsEdit.updateEditText(
		updateVariableName: String,
		updateVariableValue: String
	  )
		- update `updateVariableName` view value
	
	- jsEdit.updateSpinner(
		updateVariableName: String,
		variableValue: String
	  )
		- update `updateVariableName` spinner view selected value

	- jsEdit.updateByVariable(
		fannelScriptPath: String,
		targetVariableName: String,
		updateVariableValue: String,
	    ) 
     		-> update target variable  value

	- jsEdit.removeFromEditHtml(
		editPath: String(edit site source path),
		removeUri: String(remove uri)
	)
		-> remoev uri from edit site source  


 - JsCsv
 	csv edit tool

	- jsCsv.read(
		tag: String,
		csvPath: String,
		withNoHeader: String,
		csvOrTsv: String,
		limitRowNumSource: Int
	  )
		- save csv or tsv instance with tag, also header   
	 
	- jsCsv.readM(
		tag: String,
		csvString: String,
		csvOrTsv: String,
	 )
		- save csv or tsv instance with tag  
	 
 	- jsCsv.takeRowSize(
		tag: String
   	  )
		-> rowSize about csv(tsv) with tag

	- jsCsv.takeColSize(
		tag: String
	  )
		-> colSize about csv(tsv) with tag

	- jsCsv.isRead(
		tag: String
	   ) 
		(comfirm read completed  about csv(tsv) with tag)
		-> blank or String  
	
	- jsCsv.toHeader(  
        	tag: String,  
        	colNum: Int,  
    	)
		-> schema name  
	
	- jsCsv.toHeaderRow(
		tag: String,
		startColNumSource: Int,
		endColNumSource: Int,
	)
		-> headerList sepalated by tab   
	
	- jsCsv.toRow(
		tag: String,
		rowNum: Int,
		startColNumSource: Int,
		endColNumSource: Int,
	    )
		-> rowList sepalated by tab    
	
	- jsCsv.toCol(
		tag: String,
		colNum: Int,
		startRowNumSource: Int,
		endRowNumSource: Int,
	    )
		-> colList sepalated by tab    
	
	- jsCsv.toHtml(
		tsvString: String,
		onTh: String (empty -> ordinaly `td tag` html, some string -> `th tag` html)
	  )  
		convert tsv to html string  
		-> html string   
	
	- jsCsv.outPutTsvForDRow(
		tab: String
	   ) 
		convert row direction tsv to Tsv  
		-> tsv string

	- jsCsv.outPutTsvForDCol(
		tab: String
	  ) 
		convert col direction tsv to Tsv  
		-> tsv string

	- jsCsv.filter(
		srcTag: String,
		destTag: String,
		tabSepaFormura: String ({schema1},>,1500\t{schema2},in,Monday,\t{schema3},=,super man\t..)  
	    )
		-> save filterd tsv instance with tag, also header
 
	- jsCsv.selectColumn(
		srcTag: String,
		destTag: String,
		comaSepaColumns: String ({column1}\t{column2}\t{column3}\t..)  
	    )
		-> save culumn selected tsv instance with tag, also header
 
	- jsCsv.sliceHeader(
		tag: String,
		startColNumSource: Int,
		endColNumSource: Int,
		headerRow: String,
	    )
		-> header string sliced with tab delimiter   
	    
	    
- JsText

	- jsText.trans(
		tsvString
	   )
		-> String transposed row and col  


 - JsPath  
 	path edit tool

	- jsPath.compPrefix(  
		path: String,  
		prefix: String,  
	  )
		-> complete prefix     
	 
	- jsPath.compExtend(  
		path: String,  
		extend: String  
	    )
		-> complete suffix    
	 
 	- jsPath.checkExtend(  
 	 	tag: String,  
		extendTabSeparateStr: tab separated String  
	  )
		-> boolean (true when including tab separated extend String)

	- jsPath.checkPrefix(
		name: String,  
		prefixTabSeparateStr: String  
	    )
		-> boolean (true when including tab separated prefix String)
 
	- jsPath.removeExtend(  
	 	path: String,  
	 	extend: String  
	)
		-> remove extend 
	
	- jsPath.removePrefix(  
		path: String,  
		prefix: String  
	    )
		-> remove prefix      


- JsTextToSpeech

	- jsTextToSpeech.speech(  
		playListFilePath: String,    
		playMode: String(ordinaly|shuffle|reverse|number),  
		onRoop: String(empty or notEmply(roop on)),  
		playNumber: String (int string(valid in number mode),  
		toLang: text to speech language prefix string: en(english), zw(chinese), sp(spanish), ko(korean), ja(japanese))    
		onTrack: String(empty or notEmply(on Track)),    
		speed: String(int string)    
		pitch: String(int string)  
	)
		- execute text to speech 
	  
	- jsTextToSpeech.stop()

  
 - JsPdf
	- jsPath.extractText(  
  		path: pdf path string  
  	   )
		-> extracted text
```


### Javascript pre reserved word

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/js_pre_reserved_word.md) 


### Include Javascript Library  

First, I respect bellow javascript package author.  
Bellow respectable package is inclided assets. you can import like bellow.

- Sortable.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/Sortable.js"></script>`  
- jquery-ui -> Add html with `<script type="text/javascript" src="file:///android_asset/js/jquery-ui.min.js"></script>`  
- jquery-3.6.3.min.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/jquery-3.6.3.min.js"></script>`  
- long-press-event.min.js -> Add html with `<script type="text/javascript" src="file:///android_asset/js/long-press-event.min.js"></script>`  
- chart.min.js -> Add html with `<script src="file:///android_asset/js/chart.min.js" ></script>`  
- chartjs-adapter-date-fns.bundle.min.js -> Add html with `<script src="file:///android_asset/js/chartjs-adapter-date-fns.bundle.min.js"></script>`  


### Include css Library  

First, I respect bellow css package author.  
Bellow respectable package is inclided assets. you can import like bellow.

- jquery-ui.css -> Add html with `<link rel="stylesheet" href="file:///android_asset/css/jquery-ui.css">`  



### Html tag output

`CommandClick` script output trminal as html, so html tag is valid. You can use tag by bellow.
 - `<` -> `cmdclickLeastTag`
 - `>` -> `cmdclickGreatTag`

   - `Span tag` no working in script output. If you wont to use this, launch html file.
   - Url string automaticaly change anchor tag, but if you put 'href="' prefix in front of this string, no auto change.



### Javascript TroubleShooting  


- When your javascript's file cannot execute, you confirm how script step semicolon(`;`) exist except for function argument.  
	- Becuase javaxcript file convert one linear script string, as it, javascript:(function() { `${js contents}` })(); and webvoew.loadUrl().  

- Javascript's `while roop` ocationaly cuase crush. add bellow code to the roop.  

```js.js
	if(
		jsStop.how().includes("true")
	) throw new Error('exit');
```  


- Optinaly may replace delay function with `jsUtil.sleep($milisecond);`
	- The `Roop crush` is occur by memory leak.



### Commandclick-repository

CommandClick's fannel repository

`fannel` is ComamndClick using script (javascript, and shellscript)
For instance, your click script in CommandClick. One thing I can say that CommandClick is developed for the purpose of changing javaxcript and shellscript to gui appication. That applies to click script. It's so called Gui application. We can say so. I call the gui application `fannel`
  
[link](https://github.com/puutaro/commandclick-repository)  





