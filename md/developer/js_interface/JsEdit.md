# JsEdit

`edit component` like `setVariableType` edit interface  

```js.js

jsEdit.getFromEditText(
  targetVariableName: String,
    )
  -> get target variable value stirng  from view
    
jsEdit.updateEditText(
  updateVariableName: String,
  updateVariableValue: String
  )
  - update `updateVariableName` view value

jsEdit.updateSpinner(
  updateVariableName: String,
  variableValue: String
  )
  - update `updateVariableName` spinner view selected value

jsEdit.updateByVariable(
  fannelScriptPath: String,
  targetVariableName: String,
  updateVariableValue: String,
    ) 
      -> update target variable  value

jsEdit.removeFromEditHtml(
  editPath: String(edit site source path),
  removeUri: String(remove uri)
)
  -> remoev uri from edit site source  

```
