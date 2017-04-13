# DUtil

## 一个基于okhttp的文件下载、上传工具

***下载：支持多线程、断点续传下载，以及下载管理**，[原理、以及用法](http://www.jianshu.com/p/6c57c93009e4)

***上传：支持表单形式上传、直接将文件作为请求体上传**，[原理、以及用法](http://www.jianshu.com/p/a73344632b84)

## 如何添加到AndroidStudio

**Step 1. 添加JitPack仓库**

在当前项目等根目录下的 `build.gradle` 文件中添加如下内容:

``` gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

**Step 2. 添加项目依赖**

``` gradle
dependencies {
        compile 'com.github.Othershe:DUtil:1.1.1'
}
```
