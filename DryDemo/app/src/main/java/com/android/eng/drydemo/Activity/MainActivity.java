package com.android.eng.drydemo.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.eng.drydemo.Model.Sister;
import com.android.eng.drydemo.Network.SisterApi;
import com.android.eng.drydemo.Utils.PictureLoader;
import com.android.eng.drydemo.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "MainActivity";

    private Button btnShow;
    private Button btnFresh;
    private ImageView imgShow;

    private int curPos = 0;
    private int page = 1;
    private ArrayList<Sister> sisterList;

    private SisterApi sisterApi;
    private PictureLoader pictureloader;
    private SisterTask sisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        sisterApi = new SisterApi();
        pictureloader = new PictureLoader();
        initData();
        initUI();
    }

    private void initUI() {
        Log.d(TAG, "initUI: ");
        btnShow = (Button) findViewById(R.id.btn_show);
        btnFresh = (Button) findViewById(R.id.btn_fresh);
        imgShow = (ImageView) findViewById(R.id.image_view);
        btnShow.setOnClickListener(this);
        btnFresh.setOnClickListener(this);
    }

    private void initData() {
        Log.d(TAG, "initData: ");
        sisterList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fresh:
                page++;
                Log.d(TAG, "onClick: page - " + page);
                sisterTask = new SisterTask(page);
                sisterTask.execute();
                curPos = 0;
                break;
            case R.id.btn_show:
                if (sisterList != null && !sisterList.isEmpty()) {
                    if (curPos > 9) {
                        curPos = 0;
                    }
                    Log.d(TAG, "onClick: pos - " + curPos);
                    pictureloader.load(imgShow, sisterList.get(curPos).getUrl());
                    curPos++;
                }
                break;
        }
    }

    private class SisterTask extends AsyncTask<Void, Void, ArrayList<Sister>> {
        private int page;

        public SisterTask(int page) {
            this.page = page;
        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: ");
            return sisterApi.fetchSister(10, page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            sisterList.clear();
            sisterList.addAll(sisters);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            sisterTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (sisterTask != null) {
            sisterTask.cancel(true);
        }
    }
}