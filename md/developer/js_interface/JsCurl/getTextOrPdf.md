# get

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
* [Result](#result)

## Overview

Download text or pdf file image to `/storage/emulated/0/Document/cmdclick/temp/download`

- `/storage/emulated/0/Document/cmdclick/temp/download` is temp downlaod directory to be delete automaticaly in next download


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
