# list contents select box

This is select box that select from list contents file.  
This option is one of the most used option for fannel.  


Table
----------------------

* [Example](#example)
* [Format](#format)
* [Key-value table for ELSB](#key-value-table-for-elsb)
    * [saveTags](#savetags)
        * [put button tag ex](#put-button-tag-ex)
        * [Pre reserved button tag ex](#pre-reserved-button-tag-ex)
    * [saveFilterShellPath](#savefiltershellpath)
        * [Example for saveFilterShellPath](#example-for-savefiltershellpath)
    * [compList](#complist)
        * [Normal example for compList](#normal-example-for-complist)
        * [File prefix example for compList](#file-prefix-example-for-complist)

## Example

- Ex

```js.js
extraButton:
    TXT:TXTP:ELSB:BTN:HL=
        onUnderLine=ON
            ?height=`${lineHeight}`
            ?hint=" web search"
        |listPath=${cmdYoutuberSearcherListFilePath}
            ?limitNum=20
            ?saveTags=ok
            ?saveValName="extraButton"
        |cmd=jsac "func=jsMusic.stop"
            ?label="■"
            ?size=`${textSize}`
            ?onBoarder=OFF,
        ,
```

- `TXT` -> other [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) option
- `TXTP` -> other [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) option
- `BTN` -> other [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) option
- `HL` -> other [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) option

## Format

key-value

- ex

```js.js
:ELSB=
    {key1}={value1}
    ?{key2}={value2}
    ?{key3}={value3}
.
.
.
```


## Key-value table for ELSB

| Key name        | value             | Description                                             | 
|-----------------|-------------------|---------------------------------------------------------|
| `listPath`        | select list path  | Select list path that each item is separated by newline |
| `limitNum`        | Int               | Select list limit num                                   |
| `selectJsPath` | js path           | js path triggered by being selected                     |
| `initMark`  | string            | Specify init mark to select list                        |
| `initValue`             | string            | Edit text init string triggered by init mark            |
| `saveTags` | string            | [detail](#savetags)                                     |
| `saveFilterShellPath` | shell path        | [detail](savefiltershellpath)                           |
| `saveValName` | val name for save | Val name value specified here is saved to list path     |
| `compList` | string <br> path  | [detail](#complist)                                     |


### saveTags

Save when click element with this tag
Mainly, this tag is set to button 

- Pre reserved button ([toolbar button](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/toolbarButtonConfig.md)) tag table

| button tag | Description                                                                                                                                       | 
|------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `ok`       | play button ([playButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#playbuttonconfig))          |
| `edit`     | edit button ([editButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editbuttonconfig))          |
| `setting`  | setting button ([settingButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#settingbuttonconfig)) |
| `extra`    | extra button ([extraButtonConfig](https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#extrabuttonconfig))       |

#### put button tag ex

```js.js
listConBox:
    ELSB=
        |listPath=${cmdYoutuberSearcherListFilePath}
            ?saveTags=button1
        ,

tagButton
    :BTN=
        cmd=jsac "func=~"
        ?tag=button1

```

#### Pre reserved button tag ex

```js.js
listConBox:
    ELSB=
        |listPath=${cmdYoutuberSearcherListFilePath}
            ?saveTags=ok
        ,

```

### saveFilterShellPath

Filter value by shell script before save to list contents path  

- `${CMDCLICK_TEXT_CONTENTS}` is value variable  

#### Example for saveFilterShellPath

```js.js
    listPath=${list contents path}
        ?saveFilterShellPath=${save filter shell path}
        ,
```

- ${save filter shell path} con

```sh.sh
echo "${CMDCLICK_TEXT_CONTENTS}" | ${b} sed `s/aaa//g`

```
- ${b} is busy box env path
- If filter result is blank, not save

### compList

Comp list path by comp list specified here

- Enable to specify file path by `file://` prefix


#### Normal example for compList

```js.js
    listPath=${list contents path}
        ?compList="comp1&comp2&comp3"
        ,
```

- `${list contents path}`

```txt.txt
list1
list2
list3
.
.
.
```



#### File prefix example for compList

```js.js
    listPath=${list contents path}
        ?compList="file://${comp list path}"
        ,
```

- `${list contents path}`

```txt.txt
list1
list2
list3
.
.
.
```

- `${comp list path}`

```txt.txt
comp1
comp2
comp3
```
