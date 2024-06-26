# js action macro for list index

js action macro is certain js process macro for list index

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [CAT](#cat)
  * [CAT ex](#cat-ex)
* [COPY_PATH](#copy_path)
  * [COPY_PATH ex](#copy_path-ex)
* [COPY_FILE](#copy_file)
  * [COPY_FILE args](#copy_file-args)
  * [COPY_FILE ex](#copy_file-ex)
* [COPY_FILE_HERE](#copy_file_here)
  * [COPY_FILE_HERE ex](#copy_file_here-ex)
* [COPY_FILE_SIMPLE](#copy_file_simple)
  * [COPY_FILE_SIMPLE args](#copy_file_simple-args)
  * [COPY_FILE_SIMPLE ex](#copy_file_simple-ex)
* [DELETE](#delete)
  * [DELETE args](#delete-args)
  * [DELETE ex](#delete-ex)
* [SIMPLE_DELETE](#simple_delete)
  * [SIMPLE_DELETE ex](#simple_delete-ex)
* [DESC](#desc)
  * [DESC ex](#desc-ex)
* [EDIT_C](#edit_c)
  * [EDIT_C ex](#edit_c-ex)
* [EDIT_S](#edit_s)
  * [EDIT_S ex](#edit_s-ex)
* [MENU](#menu)
  * [MENU args](#menu-args)
  * [MENU ex](#menu-ex)
* [RENAME](#rename)
  * [RENAME ex](#rename-ex)
* [SIMPLE_EDIT](#simple_edit)
  * [SIMPLE_EDIT ex](#simple_edit-ex)
* [WRITE](#write)
  * [WRITE args](#write-args)
  * [WRITE ex](#write-ex)


## CAT

Cat file contents

## type support table for CAT

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

## type support table for COPY_PATH

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

## type support table for COPY_FILE

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

#### macro

Macro for dest dir path

| type        | descripton                               |
|-------------|------------------------------------------|
| `FROM_RECENT_DIR` | Get dest dir from recent select dest dir |

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

## type support table for COPY_FILE_HERE

| type | descripton                                 |
| --------- |--------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy selected file                         |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy line and file, if second field is exist file path |

### COPY_FILE_HERE ex

```js.js
|func="COPY_FILE_HERE"
```

## COPY_FILE_SIMPLE

Copy file to dir or tsv that you select from customize dir or tsv list

## type support table for COPY_FILE_SIMPLE

| type | descripton                                 |
| --------- |--------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy file                                  |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Copy line, if second field is exist file path | 

### COPY_FILE_SIMPLE args

#### copyDestiTsvPathCon

path-type two column tsv

- ex
```js.js
copyDestiTsvPathCon=`
  $COPY_DEST_DIR_PATH1}\ttsv\n
  $COPY_DEST_DIR_PATH1}\ttsv\n
  `
```

#### extra

key-value con

| type | descripton                                                                                                                                            |
| --------- |-------------------------------------------------------------------------------------------------------------------------------------------------------|
| withFile | Copy with file when [type is tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | 

- ex
```js.js
extra=`
  withFile=ON
  `
```

### COPY_FILE_SIMPLE ex


#### [type is `normal`] case

```js.js
func=COPY_FILE_SIMPLE
  ?args=
      copyDestiTsvPathCon=`
              $COPY_DEST_DIR_PATH1}\tdir\n
              $COPY_DEST_DIR_PATH1}\tdir\n
          `
```

#### [type is `tsvEdit`] case

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

## type support table for DELETE


| type | descripton          |
| --------- |---------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Delete file |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | None                | 


### DELETE ex

```js.js
|func="DELETE"
```

## SIMPLE_DELETE

Delete selected file

## type support table for SIMPLE_DELETE

| type | descripton                          |
| --------- |-------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Delete selected file                |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Delete line and file, if second field is file | 

### SIMPLE_DELETE setting

`SIMPLE_DELETE` is configured by bellow  
Mainly, how to popup confirm dialog, and to delete file when [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) is `tsvEdit`  

-> [delete](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md) in list index config  

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

### DESC ex

```js.js
|func="SIMPLE_DELETE"
```

## EDIT_C

Edit [command varialble](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md#cmd-variables)

## type support table for EDIT_C


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

## type support table for EDIT_S

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

## type support table for MENU

| type |  descripton |
| --------- | --------- |
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) |  |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) |  | 

### MENU args

| arg     | descripton       |
|---------|------------------|
| `menuPath` | menu config path |


### MENU ex

```js.js
|func=MENU
  ?args=
    menuPath=`${MENU_PATH}`,
```

## RENAME

Rename file name

## type support table for RENAME


| type | descripton                                                                 |
| --------- |----------------------------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Rename file name                                                           |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Rename first field. <br> If this field is exist file path, rename file name | 


### RENAME ex

```js.js
|func=RENAME
```

## SIMPLE_EDIT

Edit file contents

## type support table for SIMPLE_EDIT

| type | descripton                                                                          |
| --------- |-------------------------------------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit file contents                                                                  |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit second field path contents, if this field is exist file path | 

### SIMPLE_EDIT ex

```js.js
|func="SIMPLE_EDIT"
```

## WRITE

Edit file contents by another edit app

## type support table for WRITE

| type | descripton                                                                          |
| --------- |-------------------------------------------------------------------------------------|
| [normal](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit file contents   |
| [tsvEdit](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) | Edit second field path contents, if this field is exist file path | 


### WRITE ex

```js.js
|func="WRITE"
```

