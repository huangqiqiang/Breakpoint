# Breakpoint
断点续传Demo

![](https://github.com/huangqiqiang/Breakpoint/blob/master/image/ezgif.com-optimize.gif)

实写起来很简单  没什么难度  网上的demo 比较多都是用数据库 自己根据文件大小来判断是否下载完成 

Demo 下载地址 有时间在更新 完善点 https://github.com/huangqiqiang/Breakpoint
```
   URL url = new URL(mFileInfo.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);

            //从上次下载完成的地方下载
            int start = mFileInfo.getFinished();
            //设置下载位置(从服务器上取要下载文件的某一段)
            connection.setRequestProperty("Range", "bytes=" + start + "-" + mFileInfo.getLength());//设置下载范围
            //设置文件写入位置
            File file = new File(BreakpointManger.FILE_PATH, mFileInfo.getFileName());
            rwd = new RandomAccessFile(file, "rwd");
            //从文件的某一位置开始写入
            rwd.seek(start);
            finished += mFileInfo.getFinished();
```


遇到的错误  比较棘手  IO流没有正常关闭 或者异常 退出  或者在debug 版本 上 会出现 
####java.lang.IllegalStateException: Cannot set request property after connection is made error when setRequestProperty method is called after  url.openConnection();

https://stackoverflow.com/questions/22320418/setrequestproperty-throwing-java-lang-illegalstateexception-cannot-set-request
