package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.javabean.CacheInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/7.
 *
 */
public class CleanCacheActivity extends AppCompatActivity{

    private static final int SCAN_FINISH = 1; //扫描完毕
    private static final int SCANING_APP = 2;

    private ListView lv_cleancache_list;
    private ProgressBar pb_cleancache_progress;
    private FrameLayout fl_cleancache_scanstatus;
    private TextView tv_cleancache_status;
    private Context context;
    private PackageManager pm;
    private List<CacheInfo> cacheInfoList;
    private MyAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCAN_FINISH:
                    ToastUtils.show(CleanCacheActivity.this,"扫描完毕");
                    fl_cleancache_scanstatus.setVisibility(View.GONE);
                    if(cacheInfoList.size() > 0 ) {
                        adapter = new MyAdapter();
                        lv_cleancache_list.setAdapter(adapter);
                    }else {
                        ToastUtils.show(CleanCacheActivity.this,"您的手机非常干净！！");
                    }
                    break;
                case SCANING_APP:
                    String appname = (String) msg.obj;
                    tv_cleancache_status.setText("正在扫描："+appname);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleancache);
        context = CleanCacheActivity.this;
        pm = getPackageManager();

        lv_cleancache_list = (ListView) findViewById(R.id.lv_cleancache_list);
        fl_cleancache_scanstatus = (FrameLayout) findViewById(R.id.fl_cleancache_scanstatus);
        pb_cleancache_progress = (ProgressBar) findViewById(R.id.pb_cleancache_progress);
        tv_cleancache_status = (TextView) findViewById(R.id.tv_cleancache_status);

        scanCache();
    }

    /**
     * 扫描缓存
     */
    private void scanCache() {
        fl_cleancache_scanstatus.setVisibility(View.VISIBLE);
        cacheInfoList = new ArrayList<CacheInfo>();
        new Thread(){
            @Override
            public void run() {
                //扫描全部应用的包名
                try {
                    Method method = PackageManager.class.getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class);
                    List<PackageInfo> infoList = pm.getInstalledPackages(0);
                    pb_cleancache_progress.setMax(infoList.size());
                    int progress = 0;
                    for(PackageInfo info : infoList){
                        String packageName = info.packageName;
                        method.invoke(pm, packageName, new MyPackageStatsObserver());
                        progress++;
                        pb_cleancache_progress.setProgress(progress);
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //数据准备好了，通知界面更新
                Message message = Message.obtain();
                message.what = SCAN_FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    private class MyPackageStatsObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            try {
                String packageName = pStats.packageName.toString();
                String appname = pm.getPackageInfo(packageName, 0).applicationInfo.loadLabel(pm).toString();
                long cacheSize = pStats.cacheSize;
                Drawable appicon = pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm);
                Message message = Message.obtain();
                message.what = SCANING_APP;
                message.obj = appname;
                handler.sendMessage(message);
                if(cacheSize > 0) {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.setCacheSize(cacheSize);
                    cacheInfo.setAppIcon(appicon);
                    cacheInfo.setAppPackageName(packageName);
                    cacheInfo.setAppName(appname);
                    cacheInfoList.add(cacheInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cacheInfoList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            final CacheInfo cacheInfo;
            if(convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                holder = new ViewHolder();
                view = View.inflate(context, R.layout.items__cleancache, null);
                holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
                holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
                holder.tv_appsize = (TextView) view.findViewById(R.id.tv_appsize);
                holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                view.setTag(holder);
            }
            cacheInfo = cacheInfoList.get(position);
            holder.tv_appname.setText(cacheInfo.getAppName());
            holder.tv_appsize.setText(Formatter.formatFileSize(context,cacheInfo.getCacheSize()));
            holder.iv_appicon.setImageDrawable(cacheInfo.getAppIcon());
            //清除缓存
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Method[] methods = PackageManager.class.getMethods();
                    for(Method method : methods){
                        if("deleteApplicationCacheFiles".equals(method.getName())){
                            try {
                                method.invoke(pm, cacheInfo.getAppPackageName(), new MyPackageDataObserver());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                }
            });
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private static class ViewHolder{
        private TextView tv_appname;
        private TextView tv_appsize;
        private ImageView iv_appicon;
        private ImageView iv_delete;

    }

    private class MyPackageDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            //删除完毕调用的方法
        }
    }

    /**
     * 清理全部应用缓存的点击事件
     * @param view view对象
     */
    public void cleanAll(View view){
        Method[] methods = PackageManager.class.getMethods();
        for(Method method : methods){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(pm, Integer.MAX_VALUE,new MyPackageDataObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //scanCache();
                return;
            }
        }
    }
}
