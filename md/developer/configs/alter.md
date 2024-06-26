# Alter

Alter config key by condition

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [Setting key](#setting-key)
    * [name setting key](#name-setting-key)
    * [shellIfPath](#shellifpath)
        * [JUDGE_LIST_DIR](#judge_list_dir)
        * [JUDGE_LIST_DIR ifArgs table](#judge_list_dir-ifargs-table)
        * [JUDGE_LIST_DIR ex](#judge_list_dir-ex)
        * [JUDGE_TSV_VALUE](#judge_tsv_value)
        * [JUDGE_TSV_VALUE ifArgs table](#judge_tsv_value-ifargs-table)
        * [JUDGE_TSV_VALUE ex](#judge_tsv_value-ex)


## Setting key

| Key name    | value                  | Description                            | 
|-------------|------------------------|----------------------------------------| 
| `shellIfPath`      | shell path <br>  macro | shell path or macro                    |
| `shellIfCon`    | shell con              | shell con                              |
| `ifArgs`      | args for shell         | These depend on shellPath and shellCon | 

- Concat by context separator
setVariableTypes, config -> `?`
menu -> `|`   

### shellIfPath

macro table

| macro     | Description        | 
|-----------|--------------------|
| `JUDGE_LIST_DIR`  | judge by list dir  |
| `JUDGE_TSV_VALUE` | judge by tsv value |

#### JUDGE_LIST_DIR

Judge by [list dir](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#list-setting-key).   
This macro is many usecase in CommandClick  

##### JUDGE_LIST_DIR ifArgs table


| args name         | Description                                                                                                                                                | 
|-------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `tsvPath`         | [listDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#list-setting-key) tsv path (two column as key-value) |
| `tsvValue` | [listDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#list-setting-key) value you wont to base             |
| `alterCon` | Override setting key                                                                                                                                       |

- Require to specify `file://` prefix in [listDir](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#list-setting-key) in order to use this macro  

##### JUDGE_LIST_DIR ex

```js.js
delete=
    disableDeleteConfirm=OFF
    |onDeleteConFile=ON
    |alter=`
        shellIfPath=JUDGE_LIST_DIR
        |ifArgs=
            tsvPath=${cmdTtsPlayerManagerListIndexTsvPath}
            ?tsvValue=${cmdTtsPlayerPreviousTtsPlayListPath}
            ?alterCon="
                    |disableDeleteConfirm=ON
                    |onDeleteConFile=OFF
                "
        `
        ,
```

#### JUDGE_TSV_VALUE

Judge by tsv key-value.

##### JUDGE_TSV_VALUE ifArgs table


| args name  | Description                        | 
|------------|------------------------------------|
| `tsvPath`  | tsv path (two column as key-value) |
| `tsvKey`   | tsv key you wont to compare        |
| `tsvValue` | tsv value you wont to base         |
| `alterCon` | Override setting key               |


##### JUDGE_TSV_VALUE ex

```js.js
delete=
    disableDeleteConfirm=OFF
    |onDeleteConFile=ON
    |alter=`
        shellIfPath=JUDGE_TSV_VALUE
        |ifArgs=
            tsvPath=${cmdTtsPlayerManagerListIndexTsvPath}
            ?tsvKey=listDir
            ?tsvValue=${cmdTtsPlayerPreviousTtsPlayListPath}
            ?alterCon="
                    |disableDeleteConfirm=ON
                    |onDeleteConFile=OFF
                "
        `
        ,
```