# editAndSaveCmdVar

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Edit cmdvariable by form dialog


```js.js
jsValEdit.editAndSaveCmdVar(
    title: String,
    fContents: String,
    setVariableTypes: String,
    targetVariables: String,
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| title | string | title string |
| fContents | string | [fannel](https://github.com/puutaro/CommandClick/blob/master/md/developer/glossary.md#fannel) script path |
| setVariableTypes | string | [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) sepalated by tab |
| targetVariables | string | target variables sepalated by tab |

ex1) 

```js.js

const setVariableContents = [
  `TO_LANG:CB=-!ja!en!zh!es!ko`,
  `ON_SPEECH:LBL:CB=${TXT_LABEL}=THIS|ON!OFF`,
  `ON_BEFORE_SUMMARY:LBL:CB=${TXT_LABEL}=THIS|ON!OFF`,
  `ON_OUTPUT_IN_HISTRORY_CLICK:LBL:CB=${TXT_LABEL}=THIS|ON!OFF`,
  `PICH:TXT:LBL:NUM=${TXT_LABEL}=Pich (normal: 50)|!1..100!1`,
  `SUMMARY_LENGTH:TXT:LBL:NUM=${TXT_LABEL}=THIS|!100..1000!100`,
  `MAX_CONCUR:TXT:LBL:NUM=${TXT_LABEL}=Max concur (default: 5)|!2..20!1`,
].join("\t");

const varNameValCon = [
  `TO_LANG=${TO_LANG}`,
  `ON_SPEECH=${ON_SPEECH}`,
  `ON_BEFORE_SUMMARY=${ON_BEFORE_SUMMARY}`,
  `ON_OUTPUT_IN_HISTRORY_CLICK=${ON_OUTPUT_IN_HISTRORY_CLICK}`,
  `PICH=${PICH}`,
  `SUMMARY_LENGTH=${SUMMARY_LENGTH}`,
  `MAX_CONCUR=${MAX_CONCUR}`,
].join("\t");

jsValEdit.editAndSaveCmdVar(
  "Setting",
  "${NEWS_SPEECHER_PATH}",
  setVariableContents,
  varNameValCon,
);
```

-> [newsSpeecher.js](https://github.com/puutaro/commandclick-repository/blob/master/fannel/newsSpeecher.js)
