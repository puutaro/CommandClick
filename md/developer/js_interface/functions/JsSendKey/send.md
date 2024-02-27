
# JsSendKey


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Auto input key signal specified as string 


```js.js

jsSendKey.send(
  keyName: String,
)
	-> input text string

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| keyName | string | key name |


- pre reserved key string

`ctrl`, `shift`, `alt`, `enter` ,`down` ,`up` ,`left` ,`right` ,`pageDown` ,`pageUp` ,`esc` ,`home` ,`end` ,`backspace` ,`space`

- concat `+` with pre reserved key strings


ex1) 

```js.js
jsSendKey.send(
    "normat string",
)
```



ex2) normal str

```js.js
jsSendKey.send(
    "ctrl+shift+a",
)
```

ex3) 

```js.js
jsSendKey.send(
    "shift+a",
)
```

ex4)
```js.js
jsSendKey.send(
    "space",
)
```

