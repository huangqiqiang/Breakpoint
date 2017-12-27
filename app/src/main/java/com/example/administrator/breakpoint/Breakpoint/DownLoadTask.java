package com.example.administrator.breakpoint.Breakpoint;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author : huangqiqiang
 * @Package : com.beidouxh.navigationais.net.Breakpoint
 * @FileName :   DownLoadTask
 * @Date : 2017/12/15  9:00
 * @Descrive : TODO
 * @Email :
 */

public class DownLoadTask extends Thread {
    private FileInfo mFileInfo;
    private int finished = 0;//当前已下载完成的进度
    DownLoadHander mDownLoadHander;


    public DownLoadTask(FileInfo fileInfo,  OnDownLoadListener onDownLoadListener) {
        mFileInfo = fileInfo;
        mDownLoadHander = new DownLoadHander(onDownLoadListener);
    }


    @Override
    public void run() {
        //停止下载
        if (mFileInfo.isStop()) {
            mFileInfo.setDownLoading(false);
            return;
        }

        getLength();
        // 一样的大小 表示下载完成
        if (mFileInfo.getLength() <= mFileInfo.getFinished()) {
            Message message = new Message();
            message.what = 2;
            mDownLoadHander.handleMessage(message);
            return;
        }

        HttpURLConnection connection = null;
        RandomAccessFile rwd = null;
        InputStream is = null;
        try {
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
            if (connection.getResponseCode() == 206) {//文件部分下载，返回码为206
                is = connection.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    //写入文件
                    rwd.write(buffer, 0, len);
                    finished += len;
                    mFileInfo.setFinished(finished);
                    //更新界面显示
                    Message message = new Message();

                    message.what = 001;
                    message.arg1 = mFileInfo.getLength();
                    message.arg2 = mFileInfo.getFinished();
                    mDownLoadHander.sendMessage(message);
                    //停止下载
                    if (mFileInfo.isStop()) {
                        mFileInfo.setDownLoading(false);
                        //更新界面显示
                        Message messages = new Message();
                        messages.what = 4;
                        mDownLoadHander.sendMessage(messages);

                        return;
                    }
                }
                //下载完成
                mFileInfo.setDownLoading(false);
                Message message = new Message();
                message.what = 002;
                mDownLoadHander.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = 3;
            mDownLoadHander.sendMessage(message);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (rwd != null) {
                    rwd.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = 3;
                mDownLoadHander.sendMessage(message);
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

    }


    /**
     * 首先开启一个线程去获取要下载文件的大小（长度）
     */
    private void getLength() {
        HttpURLConnection connection = null;
        try {
            //连接网络
            URL url = new URL(mFileInfo.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            int length = -1;
            if (connection.getResponseCode() == 200) {//网络连接成功
                //获得文件长度
                length = connection.getContentLength();
            }
            if (length <= 0) {
                //连接失败
                return;
            }

            mFileInfo.setLength(length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DownLoadHander extends Handler {
        OnDownLoadListener mOnDownLoadListener;

        public DownLoadHander(OnDownLoadListener onDownLoadListener) {
            mOnDownLoadListener = onDownLoadListener;

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 001:
                    mOnDownLoadListener.updateProgress(msg.arg1, msg.arg2);
                    break;
                case 002:
                    mOnDownLoadListener.updateStatus(2);
                    break;
                case 003:
                    mOnDownLoadListener.updateStatus(3);
                    break;
                case 4:
                    mOnDownLoadListener.updateStatus(4);
                    break;
            }

        }
    }


}
