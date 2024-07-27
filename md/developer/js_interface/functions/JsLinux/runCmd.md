# jsLinux.runCmd

## Definition

```js.js
function jsLinux.runCmd(
	${cmdStrString},
) -> cmdOutput
```


## Definition by js action

```js.js
var=cmdOutput
	?func=jsLinux.runCmd
	?args=
		&cmdStr=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

## Description

Run cmd by native android shell

### Example

```js.js
jsLinux.runCmd(
   "ls"
)
```

### Example js action version

```js.js
var=runCmd
   ?func=jsLinux.runCmd
   ?args=
        cmdStr="ls"
```



## Src

-> [jsLinux.runCmd](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsLinux.kt#L16)


