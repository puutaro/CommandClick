# js action macro for list index

js action macro is certain js process for list index

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [CAT](#cat)
  * [type support table for CAT](#type-support-table-for-cat)
  * [CAT ex](#cat-ex)
* [COPY_PATH](#copy_path)
  * [type support table for COPY_PATH](#type-support-table-for-copy_path)
  * [COPY_PATH ex](#copy_path-ex)
* [COPY_FILE](#copy_file)
  * [type support table for COPY_FILE](#type-support-table-for-copy_file)
  * [COPY_FILE args](#copy_file-args)
  * [macro for initialPath](#macro-for-initialpath)
  * [js interface supporting COPY_FILE](#js-interface-supporting-copy_file)
  * [COPY_FILE ex](#copy_file-ex)
* [COPY_FILE_HERE](#copy_file_here)
  * [type support table for COPY_FILE_HERE](#type-support-table-for-copy_file_here)
  * [js interface supporting COPY_FILE_HERE](#js-interface-supporting-copy_file_here)
  * [COPY_FILE_HERE ex](#copy_file_here-ex)
* [COPY_FILE_SIMPLE](#copy_file_simple)
  * [type support table for COPY_FILE_SIMPLE](#type-support-table-for-copy_file_simple)
  * [COPY_FILE_SIMPLE args](#copy_file_simple-args)
    * [copyDestiTsvPathCon for COPY_FILE_SIMPLE](#copydestitsvpathcon-for-copy_file_simple)
    * [extra for COPY_FILE_SIMPLE](#extra-for-copy_file_simple)
  * [COPY_FILE_SIMPLE ex](#copy_file_simple-ex)
* [DELETE](#delete)
  * [type support table for DELETE](#type-support-table-for-delete)
  * [js interface supporting DELETE](#js-interface-supporting-delete)
  * [DELETE ex](#delete-ex)
* [SIMPLE_DELETE](#simple_delete)
  * [type support table for SIMPLE_DELETE](#type-support-table-for-simple_delete)
  * [SIMPLE_DELETE setting](#simple_delete-setting)
  * [js interface supporting SIMPLE_DELETE](#js-interface-supporting-simple_delete)
  * [SIMPLE_DELETE ex](#simple_delete-ex)
* [DESC](#desc)
  * [js interface supporting DESC](#js-interface-supporting-desc)
  * [DESC ex](#desc-ex)
* [EDIT_C](#edit_c)
  * [type support table for EDIT_C](#type-support-table-for-edit_c)
  * [EDIT_C ex](#edit_c-ex)
* [EDIT_S](#edit_s)
  * [type support table for EDIT_S](#type-support-table-for-edit_s)
  * [EDIT_S ex](#edit_s-ex)
* [MENU](#menu)
  * [type support table for MENU](#type-support-table-for-menu)
  * [MENU args](#menu-args)
  * [MENU ex](#menu-ex)
* [RENAME](#rename)
  * [type support table for RENAME](#type-support-table-for-rename)
  * [js interface supporting RENAME](#js-interface-supporting-rename)
  * [RENAME ex](#rename-ex)
* [SIMPLE_EDIT](#simple_edit)
  * [type support table for SIMPLE_EDIT](#type-support-table-for-simple_edit)
  * [js interface supporting SIMPLE_EDIT](#js-interface-supporting-simple_edit)
  * [SIMPLE_EDIT ex](#simple_edit-ex)
* [WRITE](#write)
  * [type support table for WRITE](#type-support-table-for-write)
  * [js interface supporting WRITE](#js-interface-supporting-write)
  * [WRITE ex](#write-ex)

## CAT

Cat file contents

### type support table for CAT

| type | descripton                                             |
| --------- |--------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Cat file contents                                      |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Cat file contents, if second field is exist file path |

### CAT ex

```js.js
func=CAT
```

## COPY_PATH

Copy path to clipboard

### type support table for COPY_PATH

| type | descripton                                   |
| --------- |----------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy selected file                           |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy line and file, if second field is exist file path |

### COPY_PATH ex

```js.js
|func=COPY_PATH
```

## COPY_FILE

Copy file to other dir

### type support table for COPY_FILE

| type | descripton                                                |
| --------- |-----------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy selected file                                        |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy line and file, if second field is selected file path | 

### COPY_FILE args

| type        | descripton                 |
|-------------|----------------------------|
| initialPath | Initial copy dest dir path |
| macro       | Macro for dest dir path    |
| tag         | tag for dest dir save file |

### macro for initialPath

Macro for dest dir path

| type        | descripton                               |
|-------------|------------------------------------------|
| `FROM_RECENT_DIR` | Get dest dir from recent select dest dir |


### js interface supporting COPY_FILE

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsCopyItem/copyFile_S.md)

### COPY_FILE ex

```js.js
|func=COPY_FILE
  ?args=
      &initialPath="${00}/AppDir"
      &macro=FROM_RECENT_DIR
      &tag=jsImporter,
```

## COPY_FILE_HERE

Copy selected file in current dir

### type support table for COPY_FILE_HERE

| type | descripton                                 |
| --------- |--------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy selected file                         |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy line and file, if second field is exist file path |

### js interface supporting COPY_FILE_HERE

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsCopyItem/copyFileHere_S.md)

### COPY_FILE_HERE ex

```js.js
|func="COPY_FILE_HERE"
```

## COPY_FILE_SIMPLE

Copy file to dir or tsv that you select from customize dir or tsv list

### type support table for COPY_FILE_SIMPLE

| type | descripton                                 |
| --------- |--------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy file                                  |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy line, if second field is exist file path | 

### COPY_FILE_SIMPLE args

#### copyDestiTsvPathCon for COPY_FILE_SIMPLE

path-type two column tsv

- ex
```js.js
copyDestiTsvPathCon=`
  $COPY_DEST_DIR_PATH1}\ttsv\n
  $COPY_DEST_DIR_PATH1}\ttsv\n
  `
```

#### extra for COPY_FILE_SIMPLE

key-value con

| key      | descripton                                                                                                                                            |
|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| withFile | Copy with file when [type is tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | 

- ex
```js.js
extra=`
  withFile=ON
  `
```

### COPY_FILE_SIMPLE ex


##### [type is normal] case

```js.js
func=COPY_FILE_SIMPLE
  ?args=
      copyDestiTsvPathCon=`
              $COPY_DEST_DIR_PATH1}\tdir\n
              $COPY_DEST_DIR_PATH1}\tdir\n
          `
```

##### [type is tsvEdit] case

```js.js
func=COPY_FILE_SIMPLE
  ?args=
      copyDestiTsvPathCon=`
              $COPY_DEST_TSV_PATH1}\ttsv\n
              $COPY_DEST_TSv_PATH1}\ttsv\n
          `
      &extra=
        withFile="ON"
```

## DELETE

Delete selected file

### type support table for DELETE


| type | descripton          |
| --------- |---------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Delete file |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | None                | 

### js interface supporting DELETE

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsDeleteItem/delete_S.md)

### DELETE ex

```js.js
|func="DELETE"
```

## SIMPLE_DELETE

Delete selected file

### type support table for SIMPLE_DELETE

| type | descripton                          |
| --------- |-------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Delete selected file                |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Delete line and file, if second field is file | 

### SIMPLE_DELETE setting

`SIMPLE_DELETE` is configured by bellow  
Mainly, how to popup confirm dialog, and to delete file when [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) is `tsvEdit`  

-> [delete](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md) in list index config  

### js interface supporting SIMPLE_DELETE

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsDeleteItem/simpleDelete_S.md)

### SIMPLE_DELETE ex

```js.js
click=
  |func="SIMPLE_DELETE"
,

delete=
    disableDeleteConfirm=OFF
	|onDeleteConFile=ON
,    
```

## DESC

Display description: `README.md` or other markdown

### js interface supporting DESC

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsDesc/show_S.md)

### DESC ex

```js.js
|func="DESC"
```

## EDIT_C

Edit [command varialble](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)

### type support table for EDIT_C


| type | descripton       |
| --------- |------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit [cmd vals](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | None             | 

### EDIT_C ex

```js.js
|func="EDIT_C"
```

## EDIT_S

Edit [setting varialble](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#setting-variable)

### type support table for EDIT_S

| type | descripton       |
| --------- |------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit [cmd vals](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables) |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | None             | 

### EDIT_S ex

```js.js
|func="EDIT_S"
```

## MENU

Launch menu

### MENU args

| arg     | descripton           |
|---------|----------------------|
| `menuPath` | [menu config path](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/menuConfig.md) |
| `title`    | string               | title            |


### MENU ex

```js.js
|func=MENU
  ?args=
    menuPath=`${MENU_PATH}`,
```

## RENAME

Rename file name

### type support table for RENAME


| type | descripton                                                                 |
| --------- |----------------------------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Rename file name                                                           |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Rename first field. <br> If this field is exist file path, rename file name | 

### js interface supporting RENAME

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsRenameItem/rename_S.md)

### RENAME ex

```js.js
|func=RENAME
```

## SIMPLE_EDIT

Edit file contents

### type support table for SIMPLE_EDIT

| type | descripton                                                                          |
| --------- |-------------------------------------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit file contents                                                                  |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit second field path contents, if this field is exist file path | 

### js interface supporting SIMPLE_EDIT

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsEditorItem/edit_S.md)

### SIMPLE_EDIT ex

```js.js
|func="SIMPLE_EDIT"
```

## WRITE

Edit file contents by another edit app

### type support table for WRITE

| type | descripton                                                                          |
| --------- |-------------------------------------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit file contents   |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit second field path contents, if this field is exist file path | 

### js interface supporting WRITE

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/list_index/JsEditorItem/write_S.md)

### WRITE ex

```js.js
|func="WRITE"
```

