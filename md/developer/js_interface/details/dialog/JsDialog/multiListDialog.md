Table
-----------------
* [Result](#result)
* [Argument](#argument)


## Result

Show multi select list dialog
-> selected elements string seprated by newline 


```js.js

jsDialog.multiListDialog(
	title: String,  
	currentItemListStr: String,  
	preSelectedItemListStr: String,  
    )
```

## Argument

| arg name | type | description                               |
| -------- | -------- |-------------------------------------------|
| title | string | title string                              |
| currentItemListStr | string | list string sepalated by newline          |
| preSelectedItemListStr | string | selected elements string sepalated by newline |


ex1)   

```js.js

jsDialog.multiListDialog(
	"sample",  
	"item1\titem2\titem3\titem4\titem5"  
	"item1\titem3",  
);

(select item3 item5)

	-> "item3\titem5";

```

