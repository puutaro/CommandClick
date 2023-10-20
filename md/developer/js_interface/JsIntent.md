

# JsIntent

Enable launch intent from javascript


```js.js


jsIntent.launchEditSite(
	editPath: String,
	extraMapStr: format string (${key1}=value1|${key2}=value2|..),
	filterCode: javascipt code,
  )
	- ref: [html automaticaly creation command to edit target edit file]

	ex)
	jsIntent.launchEditSite(
		"${edit tsv path}",
		"srcPath=${src path}|onClickSort=true|onSortableJs=true|onClickUrl=true|onDialog=false",
		"urlString.startsWith('http');",
	  )
	- extraMapStr has key bellow
	 srcPath: src path for add url
	 onClickSort: boolean str
	 onSortableJs: boolean str
	 onClickUrl: boolean str
	 onDialog: boolean str


jsIntent.launchUrl(
	urlString: String
  )
	-> launch uri(not url but uri)

jsIntent.launchApp(
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

jsIntent.launchShortcut(
	currentAppDirPath: String,
	currentShellFileName: String
    )
	- launch index and fannel  

jsIntent.shareImage(
	shareImageFilePath: String
  )
	- share image intent


```
