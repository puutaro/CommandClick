# listDialog


Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

Show list dialog    
-> return selected element string  


```js.js

jsDialog.listDialog(
	listSource: String(tab sepalate)
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| listSource | string | list string sepalated by tab |

ex1)

```js.js
jsDialog.listDialog(
	"${el1}\t${el2}\t${el3}\t.."
);
```
