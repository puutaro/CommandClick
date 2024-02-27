# Termux_setup

Command Click is use [`RUN_COMMAND` Intent](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent) in termux  
and, require termux storage setting.
You can set by onley this menu press.


[Procedure]  
1. long press right bottom setting button  
2. `setting` -> `termux setup`   
3. Long press on termux
4. Click paste popup on termux  
5. Continue pressing `Enter` on termux
- clipboard contents:
   ```sh.sh
   pkg update -y && pkg upgrade -y \
   && yes | termux-setup-storage \
   && sed -r 's/^\#\s(allow-external-apps.*)/\1/' -i "$HOME/.termux/termux.properties" 
   ```

   - reference
      - Enable `allow-external-apps` [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#allow-external-apps-property-mandatory)
      - Add Storage permission. [detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#storage-permission-optional)
      - Execute `termux-setup-storage` on termux

6. Set strage access again in `android 11` (Optional)

> You may get "Permission denied" error when trying to access shared storage, even though the permission has been granted.
>  
> Workaround:
>
> Go to Android Settings --> Applications --> Termux --> Permissions
> Revoke Storage permission
> Grant Storage permission again

[detail](https://wiki.termux.com/wiki/Termux-setup-storage)

7. Set `Draw Over Apps permission` in `android 11+` (Optinal)

> You can grant Termux the Draw Over Apps permission from its App Info activity:
> `Android Settings` -> `Apps` -> `Termux` -> `Advanced` -> `Draw over other apps`.

[detail](https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent/06f1de1b262d7612497e76463d8cc34ba7f49832#draw-over-apps-permission-optional)

- When above method cannot settle down, `CommandClick` or `Termux` restart, and system reboot.
