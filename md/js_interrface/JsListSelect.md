# JsListSelect


This interface exist for `LSB`, `ELSB`, `GB` and `MSB` `setVariableTypes` option (ref [Add]

 
```js.js



 	- update or remove method for editable list file checkbox
 	jsListSelect.updateListFileCon(
		targetListFilePath: String,
		itemText: String
	  )
		- update `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 


	jsListSelect.removeItemInListFileCon(
		targetListFilePath: String,
		itemText: String
	  )
		- remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` 


	jsListSelect.wrapRemoveItemInListFileCon(
                targetListFilePath: String,  
                removeTargetItem: String,  
                currentScriptPath: String,  
                replaceTargetVariable: String = String(),  
                defaultVariable: String = String()  
          )
		- remove item text from `listPath` file in `LSB`, `ELSB`, `GB` and `MSB` and update View

```
