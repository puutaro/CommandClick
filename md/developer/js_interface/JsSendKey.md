
# JsSendKey

Auto input key signal specified as string 


```js.js

jsSendKey.send(
    keyName: String,
)
	-> input text string

```

- pre reserved key string

`ctrl`, `shift`, `alt`, `enter` ,`down` ,`up` ,`left` ,`right` ,`pageDown` ,`pageUp` ,`esc` ,`home` ,`end` ,`backspace` ,`space`
            
ex1) 

```js.js
jsSendKey.send(
    "normat string",
)
```

- concat `+` with pre reserved key strings

ex1) normal str

```js.js
jsSendKey.send(
    "ctrl+shift+a",
)
```

ex2) 

```js.js
jsSendKey.send(
    "shift+a",
)
```

ex3)
```js.js
jsSendKey.send(
    "space",
)
```

