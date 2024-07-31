# settingimport

Enable config file import

- This is second priority

## Enable configs

Enable all config like [this](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs)

## Example

- `${list index config path1}` con

### src list index config

```js.js
type=
    tsvEdit,

list=
    listDir=`${cmdTtsPlayerTableTsvPath}`
    |compPath=`${cmdTtsPlayerTableInitTsvConPath}`
    |prefix=`${TTS_PREFIX}`
    |suffix=`${TSV_SUFFIX}`
    ,

name=
    removeExtend=,

click=
    enableUpdate=ON
    |acVar=runToConfigState
        ?importPath=
            `${cmdTtsPlayerChangeStateAction}`
        ?replace=
            STATE=`${MANAGER}`
            &ON_LIST_DIR_UPDATER=ON
            &ON_PLAY_INFO_SAVE=ON,

longClick=
    |func=MENU
        ?args=
            menuPath=
                `${cmdTtsPlayerTableLongPressListIndexMenuPath}`,


```

### import list index config

```js.js

click=
    enableUpdate=OFF
    |func=RENAME,

searchBox=
    visible=OFF,

```

### result list index config

```js.js
type=
    tsvEdit,

list=
    listDir=`${cmdTtsPlayerTableTsvPath}`
    |compPath=`${cmdTtsPlayerTableInitTsvConPath}`
    |prefix=`${TTS_PREFIX}`
    |suffix=`${TSV_SUFFIX}`
    ,

name=
    removeExtend=,

click=
    enableUpdate=OFF
    |func=RENAME,

longClick=
    |func=MENU
        ?args=
            menuPath=
                `${cmdTtsPlayerTableLongPressListIndexMenuPath}`,

searchBox=
    visible=OFF,

```

- Override click main key with second click key
- Add search box key
