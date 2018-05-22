package com.android.eng.drydemo.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.eng.drydemo.DB.SisterDBHelper;
import com.android.eng.drydemo.ImageLoader.SisterLoader;
import com.android.eng.drydemo.Model.Sister;
import com.android.eng.drydemo.Network.SisterApi;
import com.android.eng.drydemo.Utils.NetworkUtils;
import com.android.eng.drydemo.Utils.PictureLoader;
import com.android.eng.drydemo.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "MainActivity";

    private SisterApi sisterApi;
    private PictureLoader pictureloader;
    private SisterLoader mLoader;
    private SisterTask sisterTask;
    private SisterDBHelper mDBHelper;

    private Button btnNext;
    private Button btnPrev;
    private ImageView imgShow;
    private ArrayList<Sister> sisterList;

    private int curPos = 0;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        sisterApi = new SisterApi();
        pictureloader = new PictureLoader();
        mLoader = SisterLoader.getInstance(this);
        mDBHelper = SisterDBHelper.getsInstance(this);
        initData();
        initUI();
    }

    private void initUI() {
        Log.d(TAG, "initUI: ");
        btnPrev = (Button) findViewById(R.id.btn_prev);
        btnNext = (Button) findViewById(R.id.btn_next);
        imgShow = (ImageView) findViewById(R.id.image_view);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
    }

    private void initData() {
        Log.d(TAG, "initData: ");
        sisterList = new ArrayList<>();
        sisterTask = new SisterTask();
        sisterTask.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_prev:
                if (curPos == 0) {
                    btnPrev.setVisibility(View.INVISIBLE);
                    break;
                }
                --curPos;
                if (curPos == sisterList.size() - 1) {
                    sisterTask = new SisterTask();
                    sisterTask.execute();
                } else if (curPos < sisterList.size()) {
                    mLoader.bindBitmap(sisterList.get(curPos).getUrl(),
                            imgShow, 400, 400);
                }
                break;
            case R.id.btn_next:
                btnPrev.setVisibility(View.VISIBLE);
                if (curPos < sisterList.size()) {
                    ++curPos;
                }
                if (curPos >= sisterList.size()) {
                    sisterTask = new SisterTask();
                    sisterTask.execute();
                } else if (curPos < sisterList.size())
                    mLoader.bindBitmap(sisterList.get(curPos).getUrl(),
                            imgShow, 400, 400);
                break;
        }
    }

    private class SisterTask extends AsyncTask<Void, Void, ArrayList<Sister>> {
        public SisterTask() {
        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: ");
            ArrayList<Sister> res = new ArrayList<>();
            if (page < (curPos + 1) / 10 + 1) {
                ++page;
            }
            if (NetworkUtils.isAvailable(getApplicationContext())) {
                res = sisterApi.fetchSister(10, page);
                if (mDBHelper.getNumOfSister() / 10 < page) {
                    mDBHelper.insertSisterList(res);
                }
            } else {
                res.clear();
                res.addAll(mDBHelper.getSistersLimit(page - 1, 10));
            }
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            sisterList.addAll(sisters);
            if (sisterList.size() > 0 && curPos + 1 < sisterList.size()) {
                mLoader.bindBitmap(sisterList.get(curPos).getUrl(),
                        imgShow, 400, 400);
            }
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
