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

   http://192.168.57.1:8080/MyWebAPI/UpdateServlet?appID=101&platform=Android

   APK版本检查接口：
   http get请求
   序号	   字段名称	              字段类型	字段说明	约束条件	 是否必输	      备注
   输入信息 获取Native版本控制信息（参数信息）
   1    	appID	               String	app编号	　	        Y	      APP在集团的注册ID
   2	platform	               String	IOS/Android	　	    Y
   　
   输出
   1	lastAppVersion	           String	最新应用版本号	　	　	         示例：1.0.1
   2   	updateMsg	               String	升级提示	　	　	　            XX理由，需要您升级。
   3 	updateFlag	               String	升级标志	　	　	             1建议升级，2强制升级
   4	appDownLoadUrl	           String	下载路径	　	　	             http://wwww...........xx.apk
   5	appSize	                   String	应用安装包大小	　	　	         示例：3.2M
   6    appName                    String   当前APK的名字                  xxx.1.0.1.apk

   {'lastAppVersion':'1.1', 'updateMsg':'please update.','updateFlag':'1','appDownLoadUrl':'"+appDownLoadUrl+"','appSize':'5.2M','appName':'test.apk'}

   H5资源升级接口：
   http get请求

   序号	      字段名称	          字段类型	 字段说明	 约束条件	  是否必输	  备注
   输入信息 获取H5版本控制信息
   1    	appID	               String	app编号	            Y	　	10001台州10002 银座
   2	appVersion	               String	当前版本号	        Y	　	1.0.0(根据版本来返回，对应版本的最新资源包)
   3	platform	               String	IOS/Android	        Y	　	　
   输出
   versionList版本列表 START
   1    moduleName	               String	模块名	　	                core.zip　	　
   2    updateUrl	               String	升级地址	　	　	　           http://wwww...........xx.zip
   3  	moduleMd5	               String	当前资源包的md5值	　	　	    52CCE38549D7A40837D42AE034B7B9CA

   { 'zips': [{ 'moduleName':'core.zip','updateUrl': '"+corePath+"','moduleMd5':'52CCE38549D7A40837D42AE034B7B9CA'},
   { 'moduleName':'webapp.zip','updateUrl':'"+webappPath+"','moduleMd5':'37EE12BE5479E4C96659D124A7880FA6'}]}
