# cp2sd

Copy dir and file in storage to sd card or sd card dir and file to sd card

Table
-----------------

* [Usage](#usage)
* [Example](#example)
    * [Storage dir to sd card](#storage-dir-to-sd-card)
    * [Enable relative path for sd card path](#enable-relative-path-for-sd-card-path)
    * [Storage file to sd card](#storage-file-to-sd-card)
    * [Storage file to sd card](#storage-file-to-sd-card)
    * [Sd card dir to sd card](#sd-card-dir-to-sd-card)
    * [Sd card file to sd card](#sd-card-file-to-sd-card)


## Usage

```sh.sh
cp2sd  --from "${dir}" --to "${sd dir}"
--from|-f
-> from dir
--to|-t
-> dest sd dir path
[Optional] --vervose-mode|-v
show vervose
```

## Example

### Storage dir to sd card

```sh.sh
cp2sd \
	-f "${APP_ROOT_PATH}/ubuntu/backup/rootfs" \
	-t "${SD_ROOT_DIR_PATH}/copy/bk" 
```

- [APP_ROOT_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md#app_root_path)


### Enable relative path for sd card path

```sh.sh
cp2sd \
	-f "${APP_ROOT_PATH}/ubuntu/backup/rootfs" \
	-t "copy/bk" 
```

### Storage file to sd card

```sh.sh
cp2sd \
	-f "${APP_DIR_PATH}/default/test.txt" \
	-t "copy/bk2"
```

- [APP_ROOT_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md#app_dir_path)


### Storage file to sd card

```sh.sh
cp2sd \
	-f "${APP_DIR_PATH}/default/test.txt" \
	-t "copy/bk2"
```

- [APP_ROOT_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md#app_dir_path)

### Sd card dir to sd card

```sh.sh
mv2sd \
	-f "copy/bk" \
	-t "copy/bk2"
```

### Sd card file to sd card

```sh.sh
cp2sd \
	-f "${SD_ROOT_DIR_PATH}/copy/bk/test.txt" \
	-t "${SD_ROOT_DIR_PATH}/copy/bk2"
```

- [SD_ROOT_DIR_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md#app_dir_path#sd_root_dir_path)
