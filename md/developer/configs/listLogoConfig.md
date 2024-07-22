# listLogoConfig.js

Config for [list logo mode](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_logo.md) in edit's [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)  
This config is for logo image of list.    
Also, when [layout](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) is grid, this config is more important.   

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [How to specify](#how-to-specify)
* [List logo config format](#list-logo-config-format)
* [List logo config key](#list-logo-config-key)
    * [mode](#mode)
    * [Format for mode](#format-for-mode)
    * [Value table for mode](#value-table-for-mode)
    * [logo](#logo)
    * [Format for logo](#format-for-logo)
    * [oneSideLength](#onesidelength)
        * [Format for oneSideLength](#format-for-onesidelength)
    * [type](#type)
        * [Format for type](#format-for-type)
        * [Value table for type](#value-table-for-type)
        * [Ex for type](#ex-for-type)
    * [disable](#disable)
        * [Format for disable](#format-for-disable)
        * [Value table for disable](#value-table-for-disable)
        * [Ex for disable](#ex-for-disable)
    * [icon](#icon)
        * [Format for icon](#format-for-icon)
        * [name](#name)
            * [Format for name in icon](#format-for-name-in-icon)
            * [Value table for name in icon](#value-table-for-name-in-icon)
            * [Ex for name in icon](#ex-for-name-in-icon)
        * [color](#color)
            * [Format for color in icon](#format-for-color-in-icon)
            * [Value for color in icon](#value-for-color-in-icon)
            * [Ex for color in icon](#ex-for-color-in-icon)
        * [bkColor](#bkcolor)
            * [Format for bkColor in icon](#format-for-bkcolor-in-icon)
            * [Value for bkColor in icon](#value-for-bkcolor-in-icon)
            * [Ex for bkColor in icon](#ex-for-bkcolor-in-icon)
        * [nameConfigPath](#nameconfigpath)
            * [Format for nameConfigPath in icon](#format-for-nameconfigpath-in-icon)
            * [About tsv con for nameConfigPath in icon](#about-tsv-con-for-nameconfigpath-in-icon)
            * [listDirPath macro for nameConfigPath tsv](#listdirpath-macro-for-nameconfigpath-tsv)
            * [map contents value for nameConfigPath tsv](#map-contents-value-for-nameconfigpath-tsv)
            * [Ex for nameConfigPath in icon](#ex-for-nameconfigpath-in-icon)
    * [click](#click)
        * [Format for click](#format-for-click)
        * [Key-value table for click](#key-value-table-for-click)
        * [Pre reserved variable](#pre-reserved-variable)
        * [Ex for click](#ex-for-click)
        * [Js action macro](#js-action-macro)
    * [longClick](#longclick)
    * [alter](#alter)

## How to specify

1. Set [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) like bellow

```js.js
{variableName}:LI=
```

2. Specify by setting variables([qrDialogConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#playbuttonconfig)),[editButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editbuttonconfig)), [settingButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#settingbuttonconfig)), [extraButtonConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#qrdialogconfig))) in fannel

```js.js
/// SETTING_SECTION_START
qrDialogConfig="file://${qr dialog config path1}"
/// SETTING_SECTION_END
```

- `${qr dialog config path1}` con

```js.js
mode=
    tsvEdit,

logo=
    oneSideLength=40
    |icon=
        name=star
        ?bkColor=navy
        ?color=lightAo,

click=
    acVar=runCopyToLike
        ?importPath=
            `${cmdTtsPlayerCopyToOtherAction}`
        ?replace=
            COPY_TSV_PATH_TO_TYPE_CON=`${cmdTtsPlayerLikePlayListPath}`
    |alter=`
        shellIfPath=JUDGE_LIST_DIR
        |ifArgs=
            tsvPath=${cmdTtsPlayerManagerListIndexTsvPath}
            ?tsvValue=${cmdTtsPlayerLikePlayListPath}
            ?alterCon="?when=false"
            `,

```

## List logo config format

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

## List logo config key

### mode

Logo mode

### Format for mode

value

### Value table for mode

| value        | Description                               | 
|-----------------|-------------------------------------------|
| `normal` | Enable qr logo, and enable image icon     |
| `tsvEdit` | None about qr logo, but enable image icon |

### logo

Logo mode

### Format for logo

key-value[key=value]

- ex

```js.js
logo=
    {key1}={value1}
    |{key2}=
        {key2-1}={value2-1}
        ?{key2-2}={value2-2}
    |{key3}={value3}
    |...
```

### oneSideLength

length of one side

#### Format for oneSideLength

num string

- ex 

```js.js
logo=
    oneSideLength=50
```

### type

Qr info type

#### Format for type

value

- ex

```js.js
type={value}
```


#### Value table for type

| value        | Description                 | 
|-----------------|----------------------|
| `fileCon` | Handle two colmun tsv lines |
| `gitClone` | Handle file list (default)  |


#### Ex for type

```js.js
logo=
    oneSideLength=50
    |type=gitCone,
```

### disable

Disable switch about logo  
This option is used in debut etx...  

#### Format for disable

value

#### Value table for disable

| value      | Description                | 
|------------|----------------------------|
| `ON`       | disable icon               |

#### Ex for disable

- ex

```js.js
logo=
    |oneSideLength=50
    |type=gitClone
    |disable=ON,
```

### icon

Icon config

#### Format for icon

key-value

- ex

```js.js
icon=
    {key}={value}
    |{key}={value}
    |{key}={value}
    |...
```

#### name

Icon name or it`s macro

##### Format for name in icon

value

##### Value table for name in icon

| value       | Description                                                                                                                                                                                                                          | 
|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| icon name   | [->icons](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md)                                                                                                                                      |
| `imagePath` | When [type is `normal`](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type), use file path as image path <br> When [type is `tsvEdit`](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type), use second field as image path |


##### Ex for name in icon

- ex

```js.js
logo=
    |oneSideLength=50
    |type=gitClone
    |disable=ON
    |icon=
        name=file,
```

#### color

Icon name or it`s macro

##### Format for color in icon

value

- ex

```js.js
color={value}
```

##### Value for color in icon

-> [color name](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/color.md)


##### Ex for color in icon

```js.js
logo=
    |oneSideLength=50
    |type=gitClone
    |disable=ON
    |icon=
        name=file
        ?color=yellow,
```

#### bkColor

Icon name or it`s macro

##### Format for bkColor in icon

value

- ex

```js.js
bkColor={value}
```

##### Value for bkColor in icon

-> [color name](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/color.md)


##### Ex for bkColor in icon

```js.js
logo=
    |oneSideLength=50
    |type=gitClone
    |disable=ON
    |icon=
        name=file
        ?color=yellow
        ?bkColor=white,
```

#### nameConfigPath

Icon name or it`s macro

##### Format for nameConfigPath in icon

tsv path

- ex

```js.js
nameConfigPath={tsv path}
```

##### About tsv con for nameConfigPath in icon

-> tsv path ({listDirPath}-{map contents value})

##### listDirPath macro for nameConfigPath tsv

| macro     | Description                                                              | 
|-----------|--------------------------------------------------------------------------|
| `default` | -> Default list dir path. <br> if no specify, this is normal fannel path |


##### map contents value for nameConfigPath tsv

| key       | Description           | 
|-----------|-----------------------|
| `name`    | -> [Detail](#name)    |
| `color`   | -> [Detail](#color)   |
| `bkColor` | -> [Detail](#bkColor) |

- Concat by `,`

##### Ex for nameConfigPath in icon

```js.js
logo=
    oneSideLength=60
    |icon=
        nameConfigPath=`${cmdTtsPlayerTableIconNameColorConfigPath}`,
```

- `${cmdTtsPlayerTableIconNameColorConfigPath}`

```js.js
${cmdTtsPlayerLikePlayListPath}	name=star,color=${iconColor},bkColor=${iconBkColor}
${cmdTtsPlayerPreviousTtsPlayListPath}	name=history,color=${iconColor},bkColor=${iconBkColor}
default	name=music,color=${iconColor},bkColor=${iconBkColor}
```

### click

Click action for logo  

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

| Key name      | value           | Description                    | 
|---------------|-----------------|--------------------------------| 
| `func`        | Js action macro | Js action process macro        |
| `args`        | Js action args  | Js action process macro        |
| js action key | Js action con   | User implemented js action con |

- Concat by `?`

#### Pre reserved variable

| Key name                 | Description                                                                                                                                                                                  | 
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| 
| `${ITEM_TITLE}` | When [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) is <br> -> `normal`: None <br> -> `tsvEdit`: second field      |
| `${ITEM_NAME}`  | When [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) is <br> -> `normal`: file name <br> -> `tsvEdit`: second field |
| `${INDEX_LIST_DIR_PATH}`  | When [type](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md#value-table-for-type) is <br> -> `normal`: list dir path <br> -> `tsvEdit`: None     |
| `${POSITION}`      | Item position number                                                                                                                                                                         |


#### Ex for click

- `FILE_CONTENTS` macro case

```js.js
  click=
      func=FILE_CONTENTS
```

- Js action case

```js.js
click=
    |tsvVars="listDir => asciiDirPathForQuiz"
        ?importPath=`${image2AsciiArtAsciiListIndexTsvPath}`
    |var=asciiDirPath
        ?value=`${asciiDirPathForQuiz}`
    |var=isOk
        ?func=jsDialog.imageDialog
        ?args=
            msg="What's image ?"
            &path=`${asciiDirPath}/{{ IMAGE_NAME }}`
            &imageDialogMapCon=
        |var=runExitJudge
            ?when=`!isOk`
            ?func=exitZero
    |var=imageDirPath
        ?value=`${asciiDirPath}/${imageDirName}`
    |var=runImageAnswerDialog
        ?func=jsDialog.imageDialog
        ?args=
            msg="Answer"
            &path=`${imageDirPath}/{{ IMAGE_NAME }}`
            &imageDialogMapCon=`hideButtons=ok`
    ,

```

#### Js action macro

-> [Js action macro for list logo](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_logo.md)  

### longClick

Long click action for logo  
Similar to [click](#click)  

### alter

Alter by condition [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)
