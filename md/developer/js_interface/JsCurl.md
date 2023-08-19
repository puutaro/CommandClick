
# JsCurl

Execute curl  by javasript


```js.js

jsCurl.get(
  mainUrl: string,
  queryParameter: String,
  header: String(ex Authorication\tbear token,contentType\ttext/plain..),
  Timeout: Int (miliSeconds)
  )
  -> get response

- jsCurl.getTextOrPdf(
  url: text or pdf url
   )
  -> download text or pdf file image to bellow `/storage/emulated/0/Document/cmdclick/temp/download`

jsCurl.getImage(
        url: String
    )
  download image to bellow `/storage/emulated/0/Document/cmdclick/temp/download`

```

