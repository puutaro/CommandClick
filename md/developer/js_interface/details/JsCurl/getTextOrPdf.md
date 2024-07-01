# get

Table
-----------------
* [Result](#result)
* [Argument](#argument)

## Result

Download text or pdf file image to [temp download directory](https://github.com/puutaro/CommandClick/blob/master/md/developer/directory_structure.md#temp_download)



```js.js
jsCurl.getTextOrPdf(
  url: text or pdf url
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| url | string | text or pdf url |


ex1) 

```js.js
jsCurl.getTextOrPdf(
  "https://www.stats.govt.nz/assets/Uploads/Annual-enterprise-survey/Annual-enterprise-survey-2021-financial-year-provisional/Download-data/annual-enterprise-survey-2021-financial-year-provisional-csv.csv"
)

```
