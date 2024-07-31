# editBoxTitleConfig

Config for edit box title.  


Table
---------------

* [How to specify](#how-to-specify)
* [Edit box title config format](#edit-box-title-config-format)
* [Edit box title config key](#edit-box-title-config-key)
    * [text](#text)
        * [Format for text](#format-for-text)
        * [Key-value table for text](#key-value-table-for-text)
        * [key-value table for shellPath](#key-value-table-for-shellpath)
    * [image](#image)
        * [Format for image](#format-for-image)
        * [Key-value table for image](#key-value-table-for-image)
* [settingimport](#settingimport)


## How to specify

Specify by setting variables([listIndexConfig]((https://github.com/puutaro/CommandClick/blob/master/md/developer/setting_variables.md#editboxtitleconfig))) in fannel

```js.js
/// SETTING_SECTION_START
editBoxTitleConfig="file://${edit box title config path1}"
/// SETTING_SECTION_END
```


- `${list index config path1}` con

```js.js
text=
	|shellPath=MAKE_HEADER_TITLE
	|args=fannelPath=`${FANNEL_PATH}`
		?coreTitle=`${coreTitle}`
		?extraTitle=`file://${cmdclickConfigEditInfoPath}`,

```

## Edit box title config format

```js.js
{config key1}=
    ...,
.
.
.  
```

- Each config key is concat by `,`

## Edit box title config key

### text

Title text

#### Format for text

key-value[key-value]

#### Key-value table for text

| Key name                | value               | Description                                | 
|-------------------------|---------------------|--------------------------------------------|
| `disable`               | `ON` / `OFF`        | disable title itself (contain title image) |
| `shellPath`             | path string / macro | make title string by shell script          |
| (Deprecated) `shellCon` | shell contents      | make title string by shell contents        |


#### key-value table for shellPath


| Key name                | value                      | Description            | 
|-------------------------|----------------------------|------------------------|
| `args`             | variableName-variableValue | add arg to shellscript |

- Reflect args by replacing shell arg variable

### image

Title text

#### Format for image

key-value

#### Key-value table for image

| Key name                | value               | Description                              | 
|-------------------------|---------------------|------------------------------------------|
| `disable`               | `ON` / `OFF`        | disable title image |



## settingimport

Import enable to this config, -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/settingImport.md)

