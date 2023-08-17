
# jsFileSystem

This interface controll file system.

```js.js

jsFileStystem.showFileList(
	dirPath: String
  )
	-> return filelist tab sepalated

jsFileStystem.showDirList(
	dirPath: String
  )
	-> return filelist tab sepalated

jsFileStystem.readLocalFile(
	path: String
   )
	->  read local file and return file contents string

jsFileStystem.writeLocalFile(
	path: String, contents: String
  )
	- write local file

jsFileStystem.jsFile(
	filename: String,
	terminalOutPutOption: String
  )
	- write local monitor file  
jsFileStystem.removeFile(
	path: String
  )
	- remove local file

jsFileStystem.createDir(
	path: String
  )
	- creaate local dirctory

jsFileStystem.removeDir(
	path: String
)
	- remove local direcotry

jsFileStystem.copyDir(
	sourcePath: String,
	destiDirPath: String
  )
	- copy local directory

jsFileSystem.outputSwitch(
	switch: String
)
	- switch == on, then enable terminal output.
		other default.
		(althogh being webmode, terminal mode off, this inmterface switch on)

jsFileSystem.isFile(
	filePath: String
   )
	-> boolean

jsFileSystem.isDir(
	DirectoryPath: String
   )
	-> boolean

jsFileSystem.removeAndCreateDir(
	dirPath: String
  )
	-> remove and create directory

```


