package com.android.eng.drydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "MainActivity";

    private Button btnShow;
    private ImageView imgShow;
    private int curPos = 0;
    private ArrayList<String> urlList;
    private PictureLoader pictureloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        pictureloader = new PictureLoader();
        initData();
        initUI();
    }

    private void initUI() {
        btnShow = (Button) findViewById(R.id.btn_go_next);
        imgShow = (ImageView) findViewById(R.id.image_view);
        btnShow.setOnClickListener(this);
    }

    private void initData() {
        urlList = new ArrayList<>();
        urlList.add("http://ww4.sinaimg.cn/large/610dc034jw1f6ipaai7wgj20dw0kugp4.jpg");
        urlList.add("http://ww3.sinaimg.cn/large/610dc034jw1f6gcxc1t7vj20hs0hsgo1.jpg");
        urlList.add("http://ww4.sinaimg.cn/large/610dc034jw1f6f5ktcyk0j20u011hacg.jpg");
        urlList.add("http://ww1.sinaimg.cn/large/610dc034jw1f6e1f1qmg3j20u00u0djp.jpg");
        urlList.add("http://ww3.sinaimg.cn/large/610dc034jw1f6aipo68yvj20qo0qoaee.jpg");
        urlList.add("http://ww3.sinaimg.cn/large/610dc034jw1f69c9e22xjj20u011hjuu.jpg");
        urlList.add("http://ww3.sinaimg.cn/large/610dc034jw1f689lmaf7qj20u00u00v7.jpg");
        urlList.add("http://ww3.sinaimg.cn/large/c85e4a5cjw1f671i8gt1rj20vy0vydsz.jpg");
        urlList.add("http://ww2.sinaimg.cn/large/610dc034jw1f65f0oqodoj20qo0hntc9.jpg");
        urlList.add("http://ww2.sinaimg.cn/large/c85e4a5cgw1f62hzfvzwwj20hs0qogpo.jpg");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_last:
                if (curPos <= 0) {
                    curPos = 9;
                }
                curPos--;
                Log.d(TAG, "onClick: pos - " + curPos);
                pictureloader.load(imgShow, urlList.get(curPos));
                break;
            case R.id.btn_go_next:
                if (curPos > 9) {
                    curPos = 0;
                }
                Log.d(TAG, "onClick: pos - " + curPos);
                pictureloader.load(imgShow, urlList.get(curPos));
                curPos++;
                break;
        }
    }
}
