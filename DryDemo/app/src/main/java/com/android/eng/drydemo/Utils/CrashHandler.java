package com.android.eng.drydemo.Utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.android.eng.drydemo.Activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eng on 2018/5/22.
 */
@SuppressLint("SimpleDateFormat")
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private Context mContext;
    private static CrashHandler sInstance;

    private Thread.UncaughtExceptionHandler mHandler; // system default UncaughtExceptionHandler
    private Map<String, String> mInfo = new HashMap<>(); // device and exception info
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public CrashHandler() {
    }

    public static CrashHandler getsInstance() {
        if (sInstance == null) {
            sInstance = new CrashHandler();
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
        mHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * override uncaught exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (mHandler != null && !handleException(e)) {
            mHandler.uncaughtException(t, e);
        } else {
            AlarmManager amg = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("crash", true);
            PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            amg.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            System.gc();
        }
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) return false;
        try {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "程序出现异常，即将重启.", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
            getDeviceInfo(mContext);
            saveCrashInfoToFile(throwable);
            SystemClock.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * get device info
     */
    private void getDeviceInfo(Context context) {
        // get app version
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                mInfo.put("VersionName", info.versionName);
                mInfo.put("VersionCode", "" + info.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(TAG, "Error occurred when collect package info");
        }
        // get device info
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                mInfo.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
                LogUtils.e(TAG, "Error occurred when collect device info");
            }
        }
    }

    /**
     * save crash info to file
     */
    private String saveCrashInfoToFile(Throwable throwable) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            // append date string
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdFormat.format(new Date());
            sb.append("\r\n").append(date).append("\n");
            // append version info and device info
            for (Map.Entry<String, String> ent : mInfo.entrySet()) {
                String key = ent.getKey();
                String value = ent.getValue();
                sb.append(key).append("=").append(value).append("\n");
            }
            // get exception log info
            Writer writer = new StringWriter();
            PrintWriter pWriter = new PrintWriter(writer);
            throwable.printStackTrace(pWriter);
            pWriter.flush();
            pWriter.close();
            String result = writer.toString();
            // append exception info
            sb.append(result);
            return writeToFile(sb.toString());

        } catch (Exception e) {
            LogUtils.e(TAG, "Error occurred while writing file");
            sb.append("Error occurred while writing file");
            writeToFile(sb.toString());
        }
        return null;
    }

    /** write info to file and return file name */
    private String writeToFile(String str) throws Exception {
        String time = formatter.format(new Date());
        // file name
        String fileName = "crash-" + time + ".log";
        // sdcard usable
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = getGlobalPath();
            File file = new File(path);
            // crash menu exist or not
            if (!file.exists())
                file.mkdir();
            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(str.getBytes());
            fos.flush();
            fos.close();
        }
        return fileName;
    }

    /** get file path of crash info */
    private String getGlobalPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "log" + File.separator
                + File.separator + "DryDemo" + File.separator;
    }
}
