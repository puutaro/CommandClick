# listDialog


Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Show list dialog    
-> return selected element string  


```js.js

jsDialog.listDialog(
	listSource: String(new line sepalate)
)

```

## Argument

| arg name | type | description                       |
| -------- | -------- |-----------------------------------|
| listSource | string | list string sepalated by new line |

ex1)

```js.js
jsDialog.listDialog(
	"${el1}\n${el2}\n${el3}\n.."
);
```
