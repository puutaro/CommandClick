
# JsDialog

Launch dialog 


```js.js

	jsDialog.prompt(
		title: String,
		message: String,
	  )
		-> input text string

 	- jsDialog.listJsDialog(
		listSource: String(tab sepalate)
	   )
		-> selected list

 	jsDialog.formJsDialog(
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

	jsDialog.getFormValue(
		targetVariableName: String,
        	contentsTabSepalateFormJsReturnValue: String
	  )
		-> target variable value
				
				
	jsDialog.multiListDialog(
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

	jsDialog.gridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected image path

	jsDialog.onlyImageGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected image path

	jsDialog.onlySpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		(this dialog only image grid view without file name search)
		-> selected spannable image path

	jsDialog.multiSelectGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected spannable image paths

	jsDialog.onlySpannableGridDialog(
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

	jsDialog.multiSelectSpannableGridDialog(
		title: String,
        	message: String,
        	imagePathListTabSepalateString: String
	  )
		-> selected spannable image paths

	jsDialog.asciiArtDialog(
		title: String,
        	imagePath: String
	  )
		-> display ascii art with share button

	jsDialog.imageDialog(
		title: String,
        	imagePath: String
	  )
		-> display image with share button

	jsDialog.webview(
		urlStr: String,
		currentFannelPath: String,
	        centerMenuMapStr: String(ex onSwitch=\tmenuFilePath=~), *onSwitch exchange click and long click 
	        rightMenuMapStr: String(ex onSwitch=\tmenuFilePath=~), *onSwitch exchange click and long click 
	        srcAnchorImageMapStr: String(ex menuFilePath=~), 
	        srcAnchorMapStr: String(ex menuFilePath=~), 
	        imageMapStr: String(ex menuFilePath=~), 
	  )
		- launch webview dialog with url 



```
