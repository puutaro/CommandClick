
# JsScript

This interface is utility in order to controll `fannel` script.

  

```js.js

jsScript.subLabelingVars(
		jsContents: String
	  )
		-> Labeling Section Contents

jsScript.subSettingVars(
  jsContents: String
  )
  -> Setting Section Contents

jsScript.subCmdVars(
  jsContents: String
  )
  -> Comamnd Section Contents

jsScript.subValOnlyValue(
  targetVariableName: String,
  VariableValueStringContents: String
  )
  ->  Variable value String Contents

jsScript.bothQuoteTrim(
  VariableValueString: String
  ) -> VariableValueString removed both edge quote  
jsScript.replaceSettingVariable(
  scriptContents: String,
  replaceTabList: String
  )
  -> File contents String

jsScript.replaceVariableInHolder(
  scriptContents: String,
  replaceTabList: String
  )
  -> File contents String  

```
