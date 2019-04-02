# MBase
平常接到一些私活，整理出来的代码，包括一些常用的工具类，基于kotlin的一些扩展函数，一个更符合自己习惯的基于Okhttp3的HTTP请求封装。自定义dialog，修改了hipermission代码的权限管理[解决8.0崩溃]，修改了bilibili - boxing代码的图片选择裁剪框架[解决部分机器拍照设备忙]。


# How to
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file
========


gradle
maven
sbt
leiningen
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency
========


	dependencies {
	        implementation 'com.github.fanchen001:MBase:v1.0.6'
	}
