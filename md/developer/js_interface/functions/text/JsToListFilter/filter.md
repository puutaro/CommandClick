# jsToListFilter.filter

## Definition

```js.js
function jsToListFilter.filter(
	${linesString},
	${separatorString},
	${matchLinesString},
	${extraMapConString},
) -> filterCon
```


## Definition by js action

```js.js
var=filterCon
	?func=jsToListFilter.filter
	?args=
		&lines=${String}
		&separator=${String}
		&matchLines=${String}
		&extraMapCon=${String}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

## Description

## Description

Filter contents.
In [js action](), this function is corresponded to filter method in other language's collection.

## lines arg

Filter src contents

## separator arg

separator for contents

- Convert contents to list by this separator in inner process

## matchLines arg

Use match contents for match process

- Convert this contents to list by this separator, and match element to element in inner process

## extraMapCon arg

filter setting

| Key name        | value                     | Description                     |
|-----------------|---------------------------|---------------------------------|
| `removeRegex`        | regex string | Remove match string, <br> Enable Multiple specifications by  removeRegex1, removeRegex2, removeRegex3... <br> apply by order    |
| `replaceStr`        | string                | When this option specified, `removeRegex`'s match string is replaced with this string  <br> Enable Multiple specifications by replaceStr1, replaceStr2, replaceStr3... <br> apply by order   |
| `compPrefix`        | string                | Comp prefix <br> Enable Multiple specifications by compPrefix1, compPrefix2, compPrefix3... <br> apply by order   |
| `compSuffix` | string                      | Comp suffix <br> Enable Multiple specifications by compPrefix1, compPrefix2, compPrefix3... <br> apply by order   |
| `matchRegex`  | string                    | match to src element,  <br> Enable Multiple specifications by matchRegex1, matchRegex2, matchRegex3... <br> apply by order   |
| `matchRegexMatchType`    | `normal` (default) / `deny`                    | How to match `matchRegexes                     |
| `matchCondition`     | `and` (default) / `or`   | condition for `matchRegexes`          |
| `linesMatchType`     | `normal` (default) / `deny`    | How to match `lines` and `matchLines`       |
| `shellPath`     | path string               | shell path to remake src element by shell script |
| `shellArgs`     | key-values separated by `?`               | shell script args. <br> Replace this arg name with value on execute  |
| `shellOutput`     | string              | replace output with this string, if output is exist         |
| `shellFannelPath`     | path string               | Fannel path used by inner process         |

- Enable to filter key-value two field tsv by using ${key}, ${value} and ${line} variables in shellPath
- ${key}, ${value}, ${line} is first field, second field, total in two field tsv line

## Example 1

```js.js
var=runFilter
    ?func=jsToListFilter.filter
    ?args=
        lines=`${src contents}`
        &separator="NEW_LINE"
        &matchLines=`${match contents}`
        &extraMap=`
            |removeRegex1="^[	]*"
            |removeRegex2="[	]*$"
            |removeRegex3="[,]"
            |matchRegex1="[a-zA-Z]+"
            |linesMatchType=deny
        `
		```

		- ${src contents} con

```txt.txt
aa
bb
cc
```

- ${match contents} con

```txt.txt
aa
bb
```

- output

```txt.txt
cc
```

## Example 2

```js.js
var=runFilter
    ?func=jsToListFilter.filter
    ?args=
        lines=`${src contents}`
        &separator="NEW_LINE"
        &matchLines=`${match contents}`
        &extraMap=`
            |removeRegex1="^[	]*"
            |removeRegex2="[	]*$"
            |removeRegex3="[,]"
            |removeRegex4="^//.*"
            |matchRegex1="[a-zA-Z]+"
        `
		```

		- ${src contents} con

```txt.txt
aa
bb
cc
//dd
```

- ${match contents} con

```txt.txt
aa
bb
```

- output

```txt.txt
aa
bb
```



## Src

-> [jsToListFilter.filter](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/text/JsToListFilter.kt#L30)


