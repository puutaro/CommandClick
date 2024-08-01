# jsToListMap.map

## Definition

```js.js
function jsToListMap.map(
	${conString},
	${separatorString},
	${extraMapConString},
) -> mapMadeCon
```


## Definition by js action

```js.js
var=mapMadeCon
	?func=jsToListMap.map
	?args=
		&con=${String}
		&separator=${String}
		&extraMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

## Description

## Description

Recreate src contents
In [js action](), this function corresponded to map method in other language's collection.

## con arg

Recreate this con

## separator arg

separator for contents

- Convert contents to list by this separator in inner process

## extraMapCon arg

filter setting

| Key name        | value                     | Description                     |
|-----------------|---------------------------|---------------------------------|
| `removeRegex`        | regex string | Remove match string, <br> Enable Multiple specifications by  removeRegex1, removeRegex2, removeRegex3... <br> apply by order    |
| `replaceStr`        | string                | When this option specified, `removeRegex`'s match string is replaced with this string  <br> Enable Multiple specifications by replaceStr1, replaceStr2, replaceStr3... <br> apply by order   |
| `compPrefix`        | string                | Comp prefix <br> Enable Multiple specifications by compPrefix1, compPrefix2, compPrefix3... <br> apply by order   |
| `compSuffix` | string                      | Comp suffix <br> Enable Multiple specifications by compPrefix1, compPrefix2, compPrefix3... <br> apply by order   |
| `shellPath`     | path string               | shell path to remake src element by shell script |
| `shellArgs`     | key-values separated by `?`               | shell script args. <br> Replace this arg name with value on execute  |
| `shellOutput`     | string              | replace output with this string, if output is exist         |
| `shellFannelPath`     | path string               | Fannel path used by inner process         |

- Enable to edit key-value two field tsv by using ${key}, ${value} and ${line} variables in shellPath
- ${key}, ${value}, ${line} is first field, second field, total in two field tsv line

## Example 1

```js.js
var=runMap
    ?func=jsToListFilter.map
    ?args=
        lines=`${src contents}`
        &separator="NEW_LINE"
        &matchLines=`${match contents}`
        &extraMap=`
            |removeRegex1="^[	]*"
            |replaceStr1="prefix1"
            |removeRegex2="[	]*$"
            |replaceStr2="suffix2"
        `
		```

		- ${src contents} con

```txt.txt
 aa
bb	
cc
```

- output

```txt.txt
prefix1aa
bbsuffix2
cc
```

## Example 2

```js.js
var=runMap
    ?func=jsToListFilter.map
    ?args=
        lines=`${src contents}`
        &separator="NEW_LINE"
        &matchLines=`${match contents}`
        &extraMap=`
            |compPrefix1=prefix1
            |compPrefix2=prefix2
            |compSuffix1=suffix1
        `
		```

		- ${src contents} con

```txt.txt
aa
bb
cc
```

- output

```txt.txt
prefix2Prefix1aa
bbSuffix2
cc
```

- before prefix`s first char is concat as Upper case

## Example key-value format

```js.js
var=runMap
    ?func=jsToListMap.map
    ?args=
        lines=`homeFannelsPath	${cmdclickConfigHomeFannelsPath}`
        &separator="NEW_LINE"
        &extraMap=`
            |shellFannelPath=${FANNEL_PATH}
            |shellPath=${cmdclickConfigDiffCurToBeforeFilePath}
            |shellOutput=${key}`
        `
		```

		- ${src contents} con

```txt.txt
aa	/storage/emulated/0/aa.txt
bb	/storage/emulated/0/bb.txt
cc	/storage/emulated/0/cc.txt
```

- output

```txt.txt
aa (Exist diff ${src_file_path} and ${dest_file_path})
cc (Exist diff ${src_file_path} and ${dest_file_path})
```

```sh.sh
src_file_path="${value}"
src_file_name="$(basename "${src_file_path}")"
dest_file_path="${cmdclickConfigTempDirPath}/${src_file_name}"
${b} diff           "${src_file_path}"           "${dest_file_path}"           2>/dev/null
```

- ${b} is busy box env path
- ${key}, ${value}, ${line} is first field, second field, total in two field tsv line
- ${cmdclickConfigTempDirPath} is definite by [replace variable](/home/xbabu/Desktop/share/android/CommandClick/md/developer/set_replace_variables.md)



## Src

-> [jsToListMap.map](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/text/JsToListMap.kt#L28)


