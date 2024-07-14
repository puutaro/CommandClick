# fannelStateRootTable.tsv

## OVERVIEW

Rooting fannel state

* [OVERVIEW](#overview)
* [What's fannel state ?](#what's-fannel-state-?)
* [fannelStateRootTable tsv ex](#fannelstateroottable-tsv-ex)
* [Format for fannelStateRootTable.tsv](#format-for-fannelstateroottable.tsv)
* [Macro path](#macro-path)
* [Controller](#controller)

## What's fannel state ?

Normally, [setting variable](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md) is set by main fannel.  
But, sometime, require other [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md)   
For that time, use fannel state.  
By fannel state, you can reference to other fannel [setting variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md) flexibly.

## fannelStateRootTable tsv ex

```tsv.tsv
${TABLE}\t${cmdTtsPlayerTableFannelPath}
${MANAGER}\t${cmdTtsPlayerManagerFannelDirPath}
${CONFIG}\t${cmdTtsPlayerConfigFannelPath}
default\t${cmdTtsPlayerTableFannelPath}
```


- `${cmdTtsPlayerTableFannelPath}` con

```js.js
/// SETTING_SECTION_START
settingImport=`${FANNEL_PATH}`
importDisableValList=`file://${cmdTtsPlayerTableImportDisableValListPath}`
terminalDo="OFF"
setVariableTypes=`file://${setVariableTypesForTable}`
qrDialogConfig=`file://${cmdTtsPlayerTableQrDialogConfigPath}`
listIndexConfig=`file://${cmdTtsPlayerTableListIndexConfigPath}`
settingButtonConfig=`file://${cmdTtsPlayerTableSettingBtnConfigPath}`
hideSettingVariables="manager,playBtns"
hideSettingVariables=`file://${configHidValPath}`
/// SETTING_SECTION_END
```

- `${cmdTtsPlayerManagerFannelDirPath}` con

```js.js
/// SETTING_SECTION_START
settingImport=`${FANNEL_PATH}`
importDisableValList="hideSettingVariables"
terminalDo="OFF"
setVariableTypes=`file://${setVariableTypesForManager}`
hideSettingVariables="table,playBtns"
hideSettingVariables=`file://${configHidValPath}`
qrDialogConfig=`file://${cmdTtsPlayerManagerQrDialogConfigPath}`
playButtonConfig=`file://${cmdTtsPlayerManagerPlayButtonConfigPath}`
editButtonConfig=`file://${cmdTtsPlayerManagerEditButtonConfigPath}`
settingButtonConfig=`file://${cmdTtsPlayerManagerSettingButtonConfigPath}`
listIndexConfig=`file://${cmdTtsPlayerManagerListIndexConfigPath}`
/// SETTING_SECTION_END
```

- `${cmdTtsPlayerConfigFannelPath}` con
.
.
.



## Format for fannelStateRootTable.tsv

```tsv.tsv
{state name1}\t{state path1 with setting variables}
{state name2}\t{state path2 with setting variables}
{state name3}\t{state path3 with setting variables}
.
.
.
```

## Macro path

| macro     | Description                                        | 
|-----------|----------------------------------------------------|
| `default` | default state in order not to specify `fannel state` |

## Controller

Controll this rooting by [fannelStateRootTable.tsv](https://github.com/puutaro/CommandClick/blob/master/md/developer/state/fannelStateRootTableTsv.md)
