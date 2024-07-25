# TXTP

Set edit text property in [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md)


## Example

- Ex to set app header title

```js.js
appHeader:
    TXTP:RO=
        onUnderLine=OFF
        ?shellPath=MAKE_HEADER_TITLE
        ?args=
            fannelPath=`${FANNEL_PATH}`
            &extraTitle=`file://${cmdYoutuberPlayInfoPath}`
        ,
```

- `RO` -> other [setVariableType](https://github.com/puutaro/CommandClick/blob/master/md/developer/set_variable_types.md) option

## Format

key-value

- ex 

```js.js
:TXTP=
    {key1}={value1}
    ?{key2}={value2}
    ?{key3}={value3}
.
.
.
```


### Key-value table for `TETP`

| Key name        | value                  | Description                                                                                                                                      | 
|-----------------|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `size`        | Int                    | text size                                                                                                                                        |
| `height`        | Int                    | edit text view height                                                                                                                            |
| `onUnderLine` | `OFF` <br> other       | Underline switch                                                                                                                                 |
| `hint`  | string                 | hint string                                                                                                                                      |
| `shellPath`             | path string <br> macro | make title string by shell script <br> -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/native_shell/shellPath.md)   |
| (Deprecated) `shellCon` | shell contents         | make title string by shell contents <br> -> [detail](https://github.com/puutaro/CommandClick/blob/master/md/developer/native_shell/shellPath.md) |
