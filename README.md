# HashChecker
A simple hash calculator  
一个简单的 Hash 计算应用  
  
Feel free to do anything you like, for example, fork it and change it.  
随便对待它吧，你可以Fork并自行修改 。 

Now, you are able to check a file in other apps.(After 1.4)  
现在，你可以在其它应用中检查文件正确性。（1.4之后）

## HOW TO USE HASHCHECKER'S API IN OTHER APPS - FAST TUTORIAL
**Step 0**  
You should have a file and a value to compare with.  
你需要有一个文件和一个用于比对的值。  
Of course, an Android device with this app.  
当然还有，一台装有 HashChecker 的 Android 设备。
  
**Step 1**  
Create an Intent and put action into it.  
实例化一个 Intent 并向内放入 action 。  
For example,  
比如：
```
…
Intent intent = new Intent();
intent.setAction("net.rachel030219.hashchecker.action.CALCULATE_MD5");
…
```
Optional action list will be rolled out soon.  
可选的 action 列表将尽快放出。

**Step 2**  
Put in extra and set type.  
放入数据，设置类型。
```
…
intent.putExtra("net.rachel030219.hashchecker.extra.URI",uri);
intent.putExtra("net.rachel030219.hashchecker.extra.VALUE",value);
intent.setType("image/png");
…
```
For more information, please look up API DOC below.  
欲获得更多信息，请查阅下面的 API 文档。

**Step 3**  
Just send it out!  
射…呸，发出来吧！
```
…
startActivityForResult(intent,REQUEST_CODE);
…
@Override
protected void onActivityResult(int requestCode,int resultCode,Intent data){
    if (requestCode == REQUEST_CODE){
        switch(resultCode) {
            case RESULT_OK:
                // The file's md5 matches the one you put in! Congratulations!
                break;
            case RESULT_CANCELED:
                // The file's md5 does not match the one you put in… What's wrong with the file?
                break;
            default:
                // I do not know what happened either…
                break;
        }
    }
}
```

**And…**  
You've finished the tutorial! So easy, right?  
没了！很简单，不是吗？

## API DOC
> Sorry, I have not made this until you see this message. Just wait for a while!
***

欢迎关注 [Telegram Channel](https://telegram.me/rachelnotice) / [Twitter](https://twitter.com/tangrui003)

![Screenshot](./pic/Screenshot.png)

***
### LICENSE
```
   Copyright 2016 Rachel030219

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
