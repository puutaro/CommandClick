# listIndexConfig.js

Config for [list index mode](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_index.md) in edit's [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)  
This config is for list.  


Table
-----------------
<!-- vim-markdown-toc GFM -->

* [List index config format](#list-index-config-format)
* [List index config key](#list-index-config-key)
    * [type](#type)
       * [Format for type](#format-for-type)
       * [Value table for type](#value-table-for-type)
    * [layout](#layout)
        * [Format for layout](#format-for-layout)
        * [Value table for layout](#value-table-for-layout)
	* [name](#name)
        * [Format for name](#format-for-name)
        * [Key-value table for name](#key-value-table-for-name)
        * [Ex for name](#ex-for-name) 
	* [desc](#desc)
        * [Format for desc](#format-for-desc)
        * [Key-value table for desc](#key-value-table-for-desc)
        * [Ex for desc](#ex-for-desc)
    * [list](#list)
        * [Format for list](#format-for-list)
        * [Key-value table for list](#key-value-table-for-list)
        * [Ex for list](#ex-for-list)
    * [searchBox](#searchbox)
        * [Format for searchBox](#format-for-searchBox)
        * [Key-Value table for searchBox](#key-value-table-for-searchBox)
        * [Ex for searchBox](#ex-for-searchBox)
    * [click](#click)
      * [Format for click](#format-for-click)
      * [Key-Value table for click](#key-value-table-for-click)
      * [Ex for click](#ex-for-click)
    * [longClick](#longclick)
    * [delete](#delete)
        * [Format for delete](#format-for-delete)
        * [Key-value table for delete](#key-value-table-for-delete)
        * [Ex for delete](#ex-for-delete)
    * [alter](#alter)

    
## List index config format

```js.js
{config key1}=
    ...,
{config key2}=
    ...,
{config key2}=
    ...,
.
.
.  
```

- Each config key is concat by `,`

## List index config key

### type

List type

### Format for type

value

### Value table for type

| value        | Description                 | 
|-----------------|-----------------------------|
| `normal` | Handle file list (default)  |
| `tsvEdit` | Handle two colmun tsv lines |

- `tsvEdit` treat key value tsv (two column tsv without header)

### layout

Decide layout

#### Format for layout

value

#### Value table for layout

| value        | Description                        | 
|-----------------|------------------------------------|
| `linear` | Display as linear layout (default) |
| `grid` | Display as grid layout             |


### name

Display list name

#### Format for name

key-value

- ex
```js.js
name=
    {key1}={value1}  
    |{key2}={value2}  
    |{key3}=...
    |...    
```

- key is concat by `|`

#### Key-value table for name

| Key name        | value                     | Description                     | 
|-----------------|---------------------------|---------------------------------|
| `onHide`        | `ON` <br> `OFF` (default) | Hide file name                  |
| `length`        | num string                | Limit display file name         |
| `removeExtend` | None                      | Remove extend when set this key |
| `compPrefix`  | string                    | Comp prefix list                |
| `compSuffix`    | string                    | Comp suffix                     |
| `shellPath`     | path string               | Make file name by shell         |

#### Ex for name

```js.js
name=
	|length=50
	|onHide=
	|removeExtend=
	|length=50
	|shellPath=${SHELL_PATH},
```

##### `${SHELL_PATH}` contents

```sh.sh
echo "${EDIT_TARGET_CONTENTS}" \
	| ${b} awk '{print "put name"$0}'
```

- `${EDIT_TARGET_CONTENTS}` -> Auto replace with intermediate file name contents
- `${b}` -> busybox symlink path


### desc

Description setting in cardview 

#### Format for desc

key-value

- ex

```js.js
desc=
    {key1}={value1}  
    |{key2}={value2}  
    |{key3}=...
    |...    
```

#### Key-value table for desc

| Key name | Description                                                                 | 
| --------- |-----------------------------------------------------------------------------| 
| `length` | hide file name                                                              |
| `shellPath` | make file name by shell: `${EDIT_TARGET_CONTENTS}` is intermediate desc con |

- Concat by `|`


#### Ex for desc

```js.js
desc=
	length=10
	|shellPath="${SHELL_PATH}"
```

##### ${SHELL_PATH} contents

```sh.sh
echo "${EDIT_TARGET_CONTENTS}" \
	| ${b} awk '{print $0}'
```

- `${EDIT_TARGET_CONTENTS}` -> Auto replace with edit target contents
- `${b}` -> busybox symlink path

### list

List setting

#### Format for list

key-value

- ex

```js.js
desc=
    {key1}={value1}  
    |{key2}={value2}  
    |{key3}=...
    |...    
```

#### Key-value table for list

| Key name           | value                                   | Description                                                                                                                                            | 
|--------------------|-----------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------| 
| `listDir`          | path string                             | List dir path or tsv path                                                                                                                              |
| `prefix`           | string                                  | `OR` prefix list by separated `&`                                                                                                                      |
| `suffix`           | string                                  | `OR` suffix list by separated `&`                                                                                                                      |
| `filterShellPath` | path string                             | Shell path for making filter string: <br> `${ITEM_NAME}` mean bellow <br> `type` is  <br> `normal` -> file name <br> `tsvEdit` -> first column (title) |
| `editByDrag`    | ${key}=${value}                         | Disable to drag and sort edit by bellow <br> `editByDragDisable`=`ON` -> disable <br> other -> enable                                                  |
| `sortType`       | `lastUpdate` <br> `sort` <br> `reverse` | `lastUpdate` -> order by last update <br> `sort` -> normal sort <br> `reverse` -> reverse sort                                                         |
| `compPath`       | path string    | Complete list by comp path contents                                                                                                                    |
| `onReverseLayout` | `ON`<br/> other (default)   | Enable reverse layout                                                                                                                                  |
| `onOnlyExistPath` | `ON`<br/> other (default)  | Enable if body is exist path <br/> when `type` = `tsvEdit`                                                                                             |

- Enable to specify by `file://` prefix
- Concat by `|`

#### Ex for list

##### [[type](#type) is normal] case

```js.js

type=normal,
list=
    listDir=`${LIST_DIR_PATH}`
    |editByDrag=
    	editByDragDisable=ON
    |compPath=`${COMP_PATH}`
    ,
```

###### COMP_PATH contents

```COMP_PATH.tsv
file name1
file name1
file name1 
```

- Specify one column tsv file in [type](#type) is `normal`

##### [[type](#type) is `tsvEdit` and file prefix specify] case

```js.js

type=tsvEdit,
list=
    listDir=`file://${SETTING_PATH}`
    |prefix=`file://${SETTING_PATH}`
    |suffix=`file://${SETTING_PATH}`
    |onOnlyExistPath=ON
    |compPath=`${COMP_PATH}`
    ,
```

###### SETTING_PATH contents

```LIST_DIR_PATH.tsv
listDir	${TSV_PATH}
prefix	prefix1&prefix2
suffix	suffix1&suffix2
```

- Specify tsv file in `file://` prefix

###### COMP_PATH contents

```COMP_PATH.tsv
comp title1	comp body1
comp title2	comp body2
comp title3	comp body3 
```

- Specify two column tsv file in `type` is `tsvEdit`

##### [[layout](#layout) is grid and file prefix specify] case

```js.js

type=tsvEdit,
layout=grid,
list=
    listDir=`file://${SETTING_PATH}`
    |prefix=`file://${SETTING_PATH}`
    |suffix=`file://${SETTING_PATH}`
    |compPath=`${COMP_PATH}`
    ,
```

###### SETTING_PATH contents

```LIST_DIR_PATH.tsv
listDir	${GRID_DIR_PATH}
prefix	prefix1&prefix2
suffix	suffix1&suffix2
```

- Specify tsv file in `file://` prefix

###### COMP_PATH contents

```COMP_PATH.tsv
comp title1	comp body1
comp title2	comp body2
comp title3	comp body3 
```

- Specify two column tsv file in `type` is `tsvEdit`

### searchBox

Search box setting

#### Format for searchBox

key-value  

- ex 

```js.js
searchBox=
    {key1}={value1}  
    |{key2}={value2}  
    |{key3}=...
    |...    
```

#### Key-value table

| Key name           | value                      | Description          | 
|--------------------|----------------------------|----------------------| 
| `hint`          | string                     | Search box hint      |
| `visible`           | `OFF` <br> other (default) | Search box visiblity |

- Concat by `|`

#### Ex for searchBox

```js.js
searchBox=
    hint="hint string"  
    |visible=ON    
```


### click

Click action

#### Format for click

key-value

- ex 

```js.js
click=
    {key1}={value1}  
    |{key2}={value2}  
    |{key3}=...
    |...    
```

#### Key-value table for click

| Key name      | value         | Description                    | 
|---------------|---------------|--------------------------------| 
| `func`        | js action macro | js action process macro        |
| `args`        | js action args | js action process macro        |
| js action key | js action con | user implemented js action con |

- Concat by `?`

#### Ex for click

```js.js
  click=
      func=COPY_FILE_SIMPLE
          ?args=
              copyDestiTsvPathCon=`
                      $COPY_DEST_DIR_PATH1}\ttsv\n
                      $COPY_DEST_DIR_PATH1}\ttsv\n
                  `
              &extra=
                withFile="ON"
```


#### Js action macro

-> [Js action macro for list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md)


### longClick

Long click action  
Similer to [click](#click)  

### delete

List item delete setting  
This setting apply to swipe delete and [SIMPLE_DELETE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#simple_delete)

#### Format for delete

key-value

- ex
```js.js
delete=
    {key1}={value1}  
    |{key2}={value2}  
    |{key3}=...
    |...
```

#### Key-value table

| Key name           | value                             | Description                       | 
|--------------------|-----------------------------------|-----------------------------------| 
| `disableDeleteConfirm`          | `ON` <br> other (default)         | Delete confirm switch             |
| `onDeleteConFile`           | `OFF` <br> other (default)        | delete file in `type` = `tsvEdit` |
| `withJsAction`           | js action con | Trigger js action on delete       |

- Concat by `|`

#### Ex for delete

##### standard case

```js.js
	disableDeleteConfirm=OFF
	|onDeleteConFile=ON,
```

##### js action case

```js.js
delete=
	withJsAction=`
        tsvVars="listDir => asciiDirPath"
            ?importPath="${image2AsciiArtAsciiListIndexTsvPath}"
        |var=imageDirPath
            ?value="${asciiDirPath}/${imageDirName}"
        |acVar=runDeleteImage
            ?importPath=
                "${image2AsciiArtDeleteWithAction}"
            ?replace=
                DELETE_DIR_PATH="${imageDirPath}"
                &IMAGE_NAME="${ITEM_NAME}"
    `,
```

### alter

Alter by condition [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)