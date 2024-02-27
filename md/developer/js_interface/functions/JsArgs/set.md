# set

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Set args


```js.js
jsArgs.set(
	tabSepaleteArgs: string
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| tabSepaleteArgs | string | args sepalated by tab ex "{arg1}\t{arg2}\t..") |


ex1) 

```js.js

jsArgs.set(
	"firstArgs\tsecond_args"
)

let args = jsArgs.get().split("\t");
const firstArgs = args.at(0); // firstArgs
const secondArgs = args.at(0); // second_args

```

- `jsArgs.get` -> [jsArgs.get](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_interface/functions/JsArgs/get.md)
- `${0}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
