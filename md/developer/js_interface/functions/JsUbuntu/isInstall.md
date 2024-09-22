# jsUbuntu.isInstall

## Definition

```js.js
function jsUbuntu.isInstall(
	${installStampFilePathString},
	${expectStampConString},
	${installConfirmTitleAndMessageString},
	${installOneListString},
	${cautionTitleAndMessageString},
) -> isInstall
```


## Definition by js action

```js.js
var=isInstall
	?func=jsUbuntu.isInstall
	?args=
		&installStampFilePath=${String}
		&expectStampCon=${String}
		&installConfirmTitleAndMessage=${String}
		&installOneList=${String}
		&cautionTitleAndMessage=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

## Description

Dialog prompting installation.
Mainly, used to install pkg to Ubuntu

### installStampFilePath arg

Stamp file path indicating that user has installed

### expectStampCon

Stamp file contents

### installConfirmTitleAndMessage

Install confirm title and message separated by `|`

### installOneList

Install button label

### cautionTitleAndMessage

Caution Dialog title and msg separated by `|`, when user don't install


## Example

```js.js
var=isInstall
    ?func=jsUbuntu.isInstall
    ?args=
        installStampFilePath=`${cmdYoutuberInstallStampFilePath}`
        &expectStampCon=`${INSTALL_STAMP_CON}`
        &confirmTitleAndMsg="Press install button|"
        &installOneList="install	puzzle"
        &cautionTitleAndMsg="Caution!|Install by ⚙️ button"
```


## Src

-> [jsUbuntu.isInstall](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsUbuntu.kt#L299)


