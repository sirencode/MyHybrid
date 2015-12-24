目的：搭建一个android端的混合式开发的框架，实现js与native的互相调用，附带实现资源包和APK的自动升级模块。

执行顺序

1 APP启动

2 解析配置文件

3 解压H5资源包到指定目录

4 请求APK升级接口

5 请求H5资源升级接口


技术实现：
1 首先实现自定义的webview，预防4.2以前，js反向注入的bug - BaseWebView
2 实现js调用native的接口 JSInterface
3 调通native和之间的互调工作
4 解析配置文件config.xml
5 md5校验zip包是否被更改
5 将zip包拷贝到"data/data/" + PAGNAME + "/webroot/download/目录
5 获取download目录下面的所有.zip文件，之后解压
6 解压本地资源到data/data/<包名>/data/data/" + PAGNAME + "/webroot/目录（每次都解压，防止资源文件被篡改）
7 网络请求，检查更新,APK检查版本号，h5资源包检查每个zip包得md5值,zip包可以是多个，解压到平行目录
8 Toast单例处理
9 JSON解析，获取更新状态码，（强制更新，建议更新等等）
10 更新提示页面处理
11 下载更新资源
12 APK下载到sd卡//DownLoad/目录，并自动安装
13 H5资源包下载到"data/data/" + PAGNAME + "/webroot/download/目录，重启即可.
14 更新时字段控制：

