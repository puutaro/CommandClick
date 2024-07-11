# menuConfig.js

You can customize menu by this config

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [How to specify](#how-to-specify)
* [Menu config format](#menu-config-format)
* [Menu config key](#menu-config-key)
    * [name](#name)
        * [Format for name](#format-for-name)
        * [Ex for name](#ex-for-name)
    * [icon](#icon)
        * [Format for icon](#format-for-icon)
        * [Ex for icon](#ex-for-icon)
    * [func](#func)
        * [Format for func](#format-for-func)
        * [Ex for func](#ex-for-func)
    * [disable](#disable)
        * [Format for disable](#format-for-disable)
        * [value table for disable](#value-table-for-disable)
        * [Ex for disable](#ex-for-disable)
    * [parentName](#parentname)
        * [Format for parentName](#format-for-parentname)
        * [Ex for parentName](#ex-for-parentname)
    * [alter](#alter)

## How to specify

Specify by [MENU](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_index.md#menu), [MENU](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#menu), or [D_MENU](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#menu) macro

```js.js
|func=MENU
  ?args=
    menuPath=`${MENU_PATH}`
    &onHideFooter=,
```

- `${MENU_PATH}` con

```js.js
name=delete
|icon=cancel
|func=SIMPLE_DELETE,

name=Rename
|icon=edit_frame
|func=RENAME,

name=edit
|icon=edit
|func=SIMPLE_EDIT,
```

## Menu config format

key-value

```js.js
{key1-1}={value1-1}
|{ke1-2}={value1-2}
|{key1-3}={value1-3},
|...
|...,

{key2-1}={value2-1}
|{ke2-2}={value2-2}
|{key2-3}={value2-3},

{key3-1}={value3-1}
|{ke3-2}={value3-2}
|{key3-3}={value3-3},
|...
|...,

|...
|...,
.
.
.
```

- Each config key is concat by `,`

## Menu config key

### name

Menu name or sub menu name

#### Format for name

string

#### Ex for name

```js.js
name="menu"
```

### icon

Set menu icon

#### Format for icon

icon macro

ex) `about`, `file` ... 

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/icons.md)

#### Ex for icon

```js.js
name=SIMPLE_DELETE
|icon=cancel,
```

### func

Execute js action macro

#### Format for func

js action macro

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action) 

#### Ex for func

##### one menu case 

```js.js
name=delete
|icon=cancel
|func=SIMPLE_DELETE,
```
##### multiple menu case

```js.js
name=delete
|icon=cancel
|func=SIMPLE_DELETE,

name=Rename
|icon=edit_frame
|func=RENAME,

name=edit
|icon=edit
|func=SIMPLE_EDIT,
```

### disable

Execute js action macro

#### Format for disable

ON or other

-> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action)

#### value table for disable

| value  | Description            | 
|--------|------------------------|
| `ON`   | disable menu           |

#### Ex for disable

##### multiple menu case

```js.js
name=delete
|icon=cancel
|func=SIMPLE_DELETE,

name=Rename <- This is disable
|disable=ON
|icon=edit_frame
|func=RENAME,

name=edit
|icon=edit
|func=SIMPLE_EDIT,
```

### parentName

This enable sub menu, when you set parent menu name.

#### Format for parentName

Parent menu name

#### Ex for parentName

```js.js
name=delete
|icon=cancel
|func=SIMPLE_DELETE,

name=util
|icon=setting

name=Rename
|parentName=util
|disable=ON
|icon=edit_frame
|func=RENAME,

name=edit
|parentName=util
|icon=edit
|func=SIMPLE_EDIT,
```

### alter

Alter by condition [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/alter.md)
