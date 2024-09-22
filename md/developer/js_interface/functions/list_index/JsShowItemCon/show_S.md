# jsShowItemCon.show_S

## Definition

```js.js
function jsShowItemCon.show_S(
	${selectedItemString},
	${listIndexPositionInt},
) -> runShow_S
```

- The `run` prefix annotation is a process annotation
## Definition by js action

```js.js
var=runShow_S
	?func=jsShowItemCon.show_S
	?args=
		&selectedItem=${String}
		&listIndexPosition=${Int}
```

- [js action](#) is annotation-oriented language based on javascript in `CommandClick`

- The `run` prefix definition on `var` is a process annotation, not a variable definition## Description

## Description

Show item contents

## Corresponding macro

-> [CAT](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#cat)

## selectedItem arg

file name for delete

## listIndexListPosition arg

list position in list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

## Example

```js.js
var=runCat
   ?func=jsDeleteItem.show_S
   ?args=
       &selectedItem=${item name}
       &listIndexListPosition=NO_QUOTE:${item index}

```

       

## Src

-> [jsShowItemCon.show_S](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface/list_index/JsShowItemCon.kt#L15)


