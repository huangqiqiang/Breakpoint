package com.example.administrator.breakpoint.Breakpoint;

import android.os.Environment;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author : huangqiqiang
 * @Package : com.beidouxh.navigationais.net
 * @FileName :   BreakpointManger
 * @Date : 2017/12/14  17:51
 * @Descrive : TODO
 * @Email :
 */

public class BreakpointManger {
    public static String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/brewkpoint";
    public static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();
    static BreakpointManger mBreakpointManger;
    private Map<String, FileInfo> mFileInfoHashMap = new HashMap<>();//保存正在下载的任务信息


    public BreakpointManger() {
        //创建文件保存路径
        File dir = new File(FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized BreakpointManger getInstance() {
        if (mBreakpointManger == null) {
            mBreakpointManger = new BreakpointManger();
        }
        return mBreakpointManger;
    }
    public void stopDownLoadAll() {
        for (String key : mFileInfoHashMap.keySet()) {
            mFileInfoHashMap.get(key).setStop(true);
        }
    }
    public void addDownLoadFile(String fileName, final OnDownLoadListener onDownLoadListener) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setUrl("http://220.162.247.139:9090/map/" + fileName);
        File file = new File(FILE_PATH, fileInfo.getFileName());
        if (file.exists()) {
            fileInfo.setFinished((int) file.length());
        } else {
            fileInfo.setFinished(0);
        }
        DownLoadTask dsownLoadTask = new DownLoadTask(fileInfo,  onDownLoadListener);
        mFileInfoHashMap.put(fileInfo.getFileName(), fileInfo);
        sExecutorService.execute(dsownLoadTask);
    }

    public void stop(String file) {
        mFileInfoHashMap.get(file).setStop(true);
    }
}
