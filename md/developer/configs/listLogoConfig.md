# listLogoConfig.js

Config for [list logo mode](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types/list_logo.md) in edit's [setVariableTypes](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)  
This config is for logo image of list.

Table
-----------------
<!-- vim-markdown-toc GFM -->

* [List logo config format](#list-logo-config-format)
* [List logo config key](#list-logo-config-key)
    * [type](#type)
        * [Format for type](#format-for-type)
        * [Value table for type](#value-table-for-type)
    * [layout](#layout)
        * [Format for layout](#format-for-layout)
        * [Value table for layout](#value-table-for-layout)
    * [name](#name)
        * [Format for name](#format-for-name)
        * [Key-value table for name](#key-value-table-for-name)
        * [Ex for name](#ex-for-name)
    * [desc](#desc)
        * [Format for desc](#format-for-desc)
        * [Key-value table for desc](#key-value-table-for-desc)
        * [Ex for desc](#ex-for-desc)
    * [list](#list)
        * [Format for list](#format-for-list)
        * [Key-value table for list](#key-value-table-for-list)
        * [Ex for list](#ex-for-list)
    * [searchBox](#searchbox)
        * [Format for searchBox](#format-for-searchBox)
        * [Key-Value table for searchBox](#key-value-table-for-searchBox)
        * [Ex for searchBox](#ex-for-searchBox)
    * [click](#click)
        * [Format for click](#format-for-click)
        * [Key-Value table for click](#key-value-table-for-click)
        * [Ex for click](#ex-for-click)
    * [longClick](#longclick)
    * [delete](#delete)
        * [Format for delete](#format-for-delete)
        * [Key-value table for delete](#key-value-table-for-delete)
        * [Ex for delete](#ex-for-delete)
    * [alter](#alter)


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

##### Format for name in color

value

- ex

```js.js
color={value}
```

##### Value for name in color

-> [color name](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/color.md)


##### Ex for name in color

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

##### Format for name in bkColor

value

- ex

```js.js
bkColor={value}
```

##### Value for name in bkColor

-> [color name](https://github.com/puutaro/CommandClick/blob/master/md/developer/collection/color.md)


##### Ex for name in bkColor

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
