# outputSwitch

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Monitor output switch
(Even if being webmode, terminal mode off, this inmterface switch on)

- [term](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#select-term)


```js.js
jsFileSystem.outputSwitch(
	switch: String
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| switch | string | term output switch: `on` -> enable, other -> disable  |


ex1) monitor output on

```js.js
jsFileSystem.outputSwitch(
	"on"
)
```

ex2) monitor output off

```js.js
jsFileSystem.outputSwitch(
	"off"
)
```

