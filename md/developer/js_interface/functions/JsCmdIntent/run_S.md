# jsCmdIntent.run_S

## Definition

```js.js
function jsCmdIntent.run_S(
	${execCmdSourceString},
) -> runRun_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runRun_S
	?func=jsCmdIntent.run_S
	?args=
		&execCmdSource=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

Run command by termux

### Example
JsCmdIntent.run_S(
    "bash "$[bash script path}"
)

- Enable `> /dev/null` or `> /dev/null 2>&1`



## Src

-> [jsCmdIntent.run_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/JsCmdIntent.kt#L18)


