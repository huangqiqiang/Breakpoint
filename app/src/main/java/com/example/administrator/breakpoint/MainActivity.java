package com.example.administrator.breakpoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.breakpoint.Breakpoint.BreakpointManger;
import com.example.administrator.breakpoint.Breakpoint.OnDownLoadListener;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    ProgressBar mProgressBar;
    Button mButton1, mButton2;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);
        mButton1 = findViewById(R.id.button);
        mButton2 = findViewById(R.id.button2);
        mTextView = findViewById(R.id.textView);

        final OnDownLoadListener downloadListener = new OnDownLoadListener() {
            @Override
            public void updateProgress(int max, int progress) {
                System.out.println(progress);
                mProgressBar.setProgress(progress);
                mProgressBar.setMax(max);
                mTextView.setText(new BigDecimal((progress / Double.valueOf(max)) * 100).setScale(2, BigDecimal.ROUND_DOWN).toString());

            }

            @Override
            public void updateStatus(int status) {
                switch (status) {
                    case 2:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("下载完成");
                            }
                        });
                        break;
                    case 3:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("异常");
                            }
                        });
                        break;
                    case 4:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("暂停下载");
                            }
                        });

                        break;
                }
            }
        };


        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BreakpointManger.getInstance().addDownLoadFile(BreakpointManger.KEY_FILE_NAME, downloadListener);
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BreakpointManger.getInstance().stopDownLoadAll();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BreakpointManger.getInstance().stop(BreakpointManger.KEY_FILE_NAME);
                File file = new File(BreakpointManger.FILE_PATH, BreakpointManger.KEY_FILE_NAME);
                if (file.exists()) {
                    file.delete();
                }
                BreakpointManger.getInstance().addDownLoadFile(BreakpointManger.KEY_FILE_NAME, downloadListener);
            }
        });

    }
}
