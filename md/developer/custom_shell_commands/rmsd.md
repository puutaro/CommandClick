# rmsd

Remove file and dir from sd card

Table
-----------------

* [Usage](#usage)
* [Example](#example)
	* [Remove sd dir](#remove-sd-dir)
	* [Enable relative path for sd card path](#enable-relative-path-for-sd-card-path)
	* [Remove sd dir](#remove-sd-dir)

## Usage

```sh.sh
rmsd  --from "${dir}"
--from|-f
	-> from path
[Optional] --vervose-mode|-v
	-> show vervose
```

## Example


### Remove sd dir

```sh.sh
rmsd \
	-f "${SD_ROOT_DIR_PATH}/rm/bk"  
```

### Enable relative path for sd card path

```sh.sh
rmsd \
	-f "move/bk"  
```

- [SD_ROOT_DIR_PATH](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntu_env_variables.md#app_dir_path#sd_root_dir_path)


### Remove sd dir

```sh.sh
rmsd \
	-f "rm/bk/test.txt"
```
