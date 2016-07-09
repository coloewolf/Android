package com.example.lyl.mobilesafe.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.AppInfoUtils;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.Utils.SteamUtils;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.javabean.VersionInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends AppCompatActivity {

    private static final int NEED_UPDATE = 1;

    private TextView tv_splash_download;
    private TextView tv_splash_version;
    private RelativeLayout rl_splash_root;
    private SharedPreferences sp;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NEED_UPDATE:
                    showUpdateDialog(msg);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText(AppInfoUtils.getAppVersionName(this));
        rl_splash_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        tv_splash_download = (TextView) findViewById(R.id.tv_splash_download);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
        aa.setDuration(2000);
        rl_splash_root.startAnimation(aa);
        boolean update = sp.getBoolean("update",false);
        if(update) {
            checkVersion();
        }else {
            IntentUtils.startActivityForDelayAndFinish(SplashActivity.this,HomeActivity.class,2000);
        }
        copyDb("address.db");
        copyDb("commonnum.db");
        copyDb("antivirus.db");

        updtaDB();
        if(! sp.getBoolean("shortcut",false)) {
            createShortCut();
        }
    }

    /**
     * 创建应用的快捷图标
     */
    private void createShortCut() {
        SharedPreferences.Editor editor = sp.edit();
        //给桌面发消息
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //设置数据
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        Intent shortIntent = new Intent();
        shortIntent.setAction("com.com.example.lyl.mobilesafe.home");
        shortIntent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortIntent);
        sendBroadcast(intent);
        editor.putBoolean("shortcut", true);
        editor.apply();
    }



    /**
     * 更新数据
     */
    private void updtaDB() {
        // TODO: 2016/7/7  
        new Thread(){
            @Override
            public void run() {
                RequestParams params = new RequestParams("http://localhost:8080/updatadb.txt");

                x.http().get(params, new Callback.CommonCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        }.start();
    }


    /**
     * 检查版本信息
     */
    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(getResources().getString(R.string.serverurl));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();
                    if(code == 200){
                        //服务器配置的json文件对应的输入流
                        InputStream is = conn.getInputStream();
                        String json = SteamUtils.readSteam(is);
                        JSONObject jsonObject = new JSONObject(json);
                        int serverVersion = jsonObject.getInt("version");
                        String downloadurl = jsonObject.getString("downloadurl");
                        String desc = jsonObject.getString("desc");
                        VersionInfo vi = new VersionInfo();
                        vi.setVersion(serverVersion);
                        vi.setDownloadurl(downloadurl);
                        vi.setDesc(desc);
                        //判读服务器的版本号和本地的版本号是否一致，如果服务器的版本号大于本地的版本号
                        if(serverVersion > AppInfoUtils.getAppVersionCode(getApplicationContext())){
                            Message msg = Message.obtain();
                            msg.what = NEED_UPDATE;
                            msg.obj = vi;
                            handler.sendMessageDelayed(msg,2000);
                        }else {
                            IntentUtils.startActivityForDelayAndFinish(SplashActivity.this,HomeActivity.class,2000);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    ToastUtils.show(SplashActivity.this,"url路径错误：错误代码2001");
                    IntentUtils.startActivityForDelayAndFinish(SplashActivity.this,HomeActivity.class,2000);
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.show(SplashActivity.this,"网络错误：错误代码2002");
                    IntentUtils.startActivityForDelayAndFinish(SplashActivity.this,HomeActivity.class,2000);
                } catch (JSONException e){
                    e.printStackTrace();
                    ToastUtils.show(SplashActivity.this,"服务器端配置文件错误：错误代码2003");
                    IntentUtils.startActivityForDelayAndFinish(SplashActivity.this,HomeActivity.class,2000);
                }
            }
        }.start();


    }

    /**
     * 显示升级提醒的对话框
     * @param msg 消息
     */
    public void showUpdateDialog(Message msg) {
        final VersionInfo vi = (VersionInfo) msg.obj;
        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("升级提醒");
        builder.setMessage(vi.getDesc());
        builder.setPositiveButton("立刻升级",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final File file = new File(Environment.getExternalStorageDirectory(),"xxx.apk");
                RequestParams rp = new RequestParams(vi.getDownloadurl());
                rp.setSaveFilePath(file.getAbsolutePath());
                rp.setAutoResume(true);
                //只是回掉的时候new Callback.ProgressCallback就好。
                x.http().get(rp, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        ToastUtils.show(SplashActivity.this,"下载成功");
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtils.show(SplashActivity.this,"下载失败");
                        //tv_splash_download.setText(ex.getMessage());
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onWaiting() {

                    }

                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        tv_splash_download.setText(current+"/"+total);
                    }
                });


            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                IntentUtils.startActivityAndFinish(SplashActivity.this,HomeActivity.class);
            }
        });
        builder.show();
    }

    /**
     * 拷贝assets目录下的数据库到android系统的目录
     */
    public void copyDb(final String name){
        new Thread(){
            @Override
            public void run() {
                File file = new File(getFilesDir(),name);
                if(file.exists() && file.length() >0 ){
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = getAssets().open(name);
                    byte[] buf = new byte[1024];
                    int len;
                    while ( (len = is.read(buf)) != -1){
                        fos.write(buf,0,len);
                    }
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
