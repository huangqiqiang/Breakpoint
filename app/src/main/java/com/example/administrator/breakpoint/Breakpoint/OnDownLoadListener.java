package com.example.administrator.breakpoint.Breakpoint;

/**
 * @Author : huangqiqiang
 * @Package : com.beidouxh.navigationais.net.Breakpoint
 * @FileName :   OnDownLoadListener
 * @Date : 2017/12/15  9:45
 * @Descrive : TODO
 * @Email :
 */

public interface OnDownLoadListener {
    void updateProgress(int max, int progress);
    void  updateStatus(int status);
}
