# getFormValue

Table
-----------------
* [Result](#overview)
* [Argument](#argument)
  * [title](#title)
  * [message](#message)
  * [suggestVars](#suggestVars)
    * [Key](#key)
    * [Format](#format)


## Result

Target variable value string


```js.js

jsDialog.getFormValue(
	targetVariableName: String,
	contentsTabSepalateFormJsReturnValue: String
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| targetVariableName | string | csv data tag that you want to put |
| contentsTabSepalateFormJsReturnValue | string | formDialog return value with replace new line with tab |


ex1) 

```js.js
jsDialog.getFormValue(
	"targetVariableName1",
	"targetVariableName1=aa\ntargetVariableName2=bb\ntargetVariableName3=cc"
            .replaceAll("\n", "\t"),
  )
	-> "aa"
;
```

