


<br>    
<br>
<br>
<br>
<br>    
<br>
<br>
<br>

`CommandClick` is rich bookmarklet development android app.    
You can use and create a bookmarklet that goes beyond common sense.  
For example, ubuntu terminal emulator, ubuntu file manager, youtube scraping player, etc...  

<br>
<br>
<br>

I hoped.  
By one app, create multiple applet.  
So I put my eye at webview js interface.  
If I use bookmarklet on webview js interface, bring out os feature: file system, media player, connection with other system via webview.      
<br>
<br>
Although its implementation did well, I want to get more extensibility, for example, like the linux package system.   
If we could use apt package or other programing feature on ubuntu from bookmarklet, how splendid!   
If we can do that, we can use and create high functionality bookmarklet.  
<br>
<br>
Furthermore, I create original js library for bookmarklet.  
However, I didn't just want to make it, I wanted to make it more readable.  
Most of the engineers are mostly reading, even when they're writing code.  
This is because we believe that improving readability is directly linked to productivity.  
<br>
<br>

By the way  
if you make it, you'll want to spread it around, right?  
Just as useful ubuntu packages were spread through the apt system, I felt the need to consider a distribution system for CC as well.   
And, I decided to entrust the distribution of bookmarklet to QR code.  
By CommmandClick's original QR code, we can distribute via github.
<br>
<br>
<br>
This android app is bookmarklet developer tool, and at the same time distribution tool.

<br>
<br>
<br>

Welcome bookmarklet total development world.

<br>
<br>
<br>
<br>
<br>
<br>
<br>    
<br>
<br>
<br>
<br>    
<br>
<br>
<br>
<br>    
<br>
<br>
<br>
<br>    
<br>
<br>
<br>
<br>    
<br>
<br>
<br>

CommandClick
----

<img src="https://github.com/puutaro/CommandClick/assets/55217593/e4e6f75b-a35e-47f1-bb41-144d8ea88185" width="500">  


[![Android: 8+](https://img.shields.io/badge/Android-8+-blueviolet.svg?style=popout&logo=android)]((https://opensource.org/licenses/MIT))
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![GitHub release (with filter)](https://img.shields.io/github/v/release/puutaro/CommandClick)
![GitHub repo size](https://img.shields.io/github/repo-size/puutaro/CommandClick)
<img src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/com.mirfatif.permissionmanagerx&label=IzzyOnDroid&cacheSeconds=86400">  
![GitHub all releases](https://img.shields.io/github/downloads/puutaro/CommandClick/total)  


Screenshots
--------
<a href="https://github.com/user-attachments/assets/a923b09c-e9b2-4742-b8c2-05f3803fc4f8"><img src="https://github.com/user-attachments/assets/a923b09c-e9b2-4742-b8c2-05f3803fc4f8" width="30%" /></a>
<a href="https://github.com/puutaro/CommandClick/assets/55217593/2bed519f-1908-4d1d-bb75-4c455595998e"><img src="https://github.com/puutaro/CommandClick/assets/55217593/2bed519f-1908-4d1d-bb75-4c455595998e" width="30%" /></a>
<a href="https://github.com/puutaro/CommandClick/assets/55217593/2b04e38d-ba96-4194-85fc-d8302650bee6"><img src="https://github.com/puutaro/CommandClick/assets/55217593/2b04e38d-ba96-4194-85fc-d8302650bee6" width="30%" /></a>
<a href="https://github.com/puutaro/CommandClick/assets/55217593/101da895-a578-4667-b8e2-7728bbd9e568"><img src="https://github.com/puutaro/CommandClick/assets/55217593/101da895-a578-4667-b8e2-7728bbd9e568" width="30%" />
<a href="https://github.com/puutaro/CommandClick/assets/55217593/c8593de5-c408-4f05-ba5d-e611e6696537"><img src="https://github.com/puutaro/CommandClick/assets/55217593/c8593de5-c408-4f05-ba5d-e611e6696537" width="30%" /></a>
<a href="https://github.com/puutaro/commandclick-repository/assets/55217593/6f62911e-772c-4c04-8375-0998d1353612"><img src="https://github.com/puutaro/commandclick-repository/assets/55217593/6f62911e-772c-4c04-8375-0998d1353612" width="30%" /></a>

##### ●  Exist bookmarklet is simple. But `CommmandClick` bookmrklet is not only simple but also more functinal.  
##### ●  Exist bookmarklet is used to vanila javascript. `CommmandClick` bookmarklet is used to original fragmework that is more readable and maintainable.  
##### ●  Exist bookmarklet is limit feature. But, `CommmandClick` bookmarklet is versatile powerd by webview, ubuntu, media player.  
##### ●  Exist bookmarklet is not supported with distribution. But, `CommmandClick` bookmarklet is supported with distribution by QR code.  


- In `CommandClick`, call bookmarklet `fannel`.

Fannel (bookmarklet) Table
-----


<details>

<summary>fannel: <code>Ubuntu terminal</code>, ritch <code>QR</code> reader & creator, <code>youtube</code> scraping player, ubuntu file manager ...</summary>

<br>
<br>

`CommandClick` has multiple feature  by bookmarklet.   

<br>

Detail is bellow.

<br>


| Janre                               | fannel(bookmarklet) name                                                                                                                                                                                        |
|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| File transfer                       | [builtin](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#image-long-click) by QR reader                                          | 
| File transfer                       | [fileManager.js](https://github.com/puutaro/fileManager)                                                                                                                                                        | 
| fannel (bookmarklet) store          | [builtin](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#install-fannel)                                                                                                                          |
| fannel (bookmarklet) store          | [fannelStore.js](https://github.com/puutaro/fannelStore)                                                                                                                                                        |
| Typing tool                         | [selectTyper](https://github.com/puutaro/selectTyper)                                                                                                                                                           |   
| Ubuntu terminal                     | [cmdTerminal.js](https://github.com/puutaro/CommandClick?tab=readme-ov-file#setup-ubuntu)                                                  | 
| Ubuntu terminal                     | [sshTerminal.js](https://github.com/puutaro/sshTerminal)                                     |  
| Music player                        | [cmdMusicPlayerU.js](https://github.com/puutaro/cmdMusicPlayerU)                                                                              |  
| Youtube scraping player             | [cmdYoutuberU.js](https://github.com/puutaro/cmdYoutuberU)                                                                                          |  
| Text to speech                      | textToSpeech.js (builtin)  |
| Text to speech player               | [ttsPlsyer.js](https://github.com/puutaro/ttsPlayer)  |
| pdf to text reader and player       | [txtPdfViewer.js](https://github.com/puutaro/txtPdfViewer)|
| total pdf to text reader and player | [ctsvViewer.js](https://github.com/puutaro/ctsvViewer)                                                                                           |
| News scraping player                | [newsSpeecher.js](https://github.com/puutaro/newsSpeecher)                                                                                                                                                      |
| Google calendar register            | [gCalendarFormatter.js](https://github.com/puutaro/gCalendarFormatter)                                                                                                                                          |
| Clip board formater                 | [clipFormatMaker.js](https://github.com/puutaro/clipFormatMaker)                                                                                                                                                |  
| Train route seacher                 | [japanRouteSeacher.js](https://github.com/puutaro/japanRouteSearcher)                                                                                                                                           |  
| File manager                        | [fileManager.js](https://github.com/puutaro/fileManager)                                                                                                                                                        |  
| Input support tool                  | [selectTyper.js](https://github.com/puutaro/selectTyper)                                                                                                                                                        |  
| Pocket web search                   | [webSearcher.js](https://github.com/puutaro/webSearcher)                                                                                                                                                        | 
| Gpt3.5 client                       | askGpt35.js (builtin)                                                                                                                                   |
| Gpt3.5 client                       | [cmdGpt35.js](https://github.com/puutaro/cmdGpt35)                                                                                                                                    |
| Ascii art maker                     | [image2AsciiArt.js](https://github.com/puutaro/image2AsciiArt)                                                                                                                                                  |  
| Gmail draft saver                   | [sendToGmail.js](https://github.com/puutaro/sendClipToGmail)                                                                                                                                                    |

</details>


App installation
-----  
- Android 8+

get it on bellow link  

<a href="https://github.com/puutaro/CommandClick/releases" target="_blank"><img src="https://img.shields.io/github/v/release/puutaro/CommandClick"  width="170"></a>　　

<a href="https://apt.izzysoft.de/fdroid/index/apk/com.puutaro.commandclick/" target="_blank"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" width="170"></a>　　

- This app not spyware.  Sometimes, it is detected by malware checkers because of the following.

> `ACCESS_FINE_LOCATION` -> Ths require WIFI setting via QR reader .  
> `READ_EXTERNAL_STORAGE` -> CommandClick base is file system.   
> This app data is saved to file. So, without this permission, CC is not feasible.

-> detail is [this issue](https://github.com/puutaro/CommandClick/issues/11)

### [Optional] Change WebView

By edge, webView javascript feature is limited.  
So, javascript not working, recommend to change by [google play](https://play.google.com/store/apps).

- I found this case in samsung galaxy.


Setup Ubuntu
------

By set ubuntu, you can use all fannel (bookmarklet).   
It enables without the need for `termux` or rooting.      
And more, we can use custom command for fannel (bookmarklet) development.  


<img src="https://github.com/puutaro/CommandClick/assets/55217593/2406d8eb-b836-43eb-8dd0-1169c954e64b" width="400">  

-> [More detail](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#setup-ubuntu)

Setup other fannel (bookmarklet)
------

[Procedure]

1. Long press right bottom gear button
2. Click `install_fannel`
3. Click sync button in left bottom sync button  
4. You can install `fannel` by clicking.

<img src="https://github.com/user-attachments/assets/a347990d-6988-4b0b-87cb-23d554a3328b" width="400">  


Fannel (bookmarklet) Distribution
-------------------

Please follow bellow.

-> [Register fannel(bookmarklet) to repo](https://github.com/puutaro/commandclick-repository/tree/master?tab=readme-ov-file#your-fannel-register-condition).  

-> [Qr reader](https://github.com/puutaro/CommandClick/blob/master/USAGE.md#scan-qr)


Usage
------

<p>-> <a href="https://github.com/puutaro/CommandClick/blob/master/USAGE.md" target="_blank">USAGE.md</a></p> 


For fannel (bookmarklet) developer
--------

### -> [Quick start shell](https://github.com/puutaro/quickStartShell?tab=readme-ov-file)

### -> [Reference](https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md)


Acknowledge
--------

Thanks to awesome [UserLand](https://github.com/CypherpunkArmory/UserLAnd) and [PRoot](https://github.com/proot-me/proot), which make this project possible.

