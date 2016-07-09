package com.example.lyl.mobilesafe.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.Utils.Md5Utils;
import com.example.lyl.mobilesafe.Utils.ToastUtils;



/**
 * Created by lyl on 2016/6/27.
 *
 */
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private SharedPreferences sp;
    private GridView gv_home_list;
    private Dialog dialog;
    private AlertDialog.Builder build;
    private View view;
    private Button btn_accept;
    private Button btn_cancel;
    private EditText et_password;
    private static final String[] names = {"手机防盗","通讯卫士","软件管理","进程管理","流量统计",
            "手机杀毒","缓存清理","高级工具","设置中心"};
    private static final int[] icons = {R.mipmap.safe,R.mipmap.callmsgsafe,R.drawable.app_selector,R.mipmap.taskmanager,
            R.mipmap.netmanager,R.mipmap.trojan,R.mipmap.sysoptimize,R.mipmap.atools,R.mipmap.settings};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gv_home_list = (GridView) findViewById(R.id.gv_home_list);
        gv_home_list.setAdapter(new MyAdapter());
        gv_home_list.setOnItemClickListener(this);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        createStatusBarIcon();
    }

    private void createStatusBarIcon() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.default_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0: //手机防盗
                String password = sp.getString("password",null);
                //进入手机防盗 判断用户是否设置过手机防盗的密码
                if(TextUtils.isEmpty(password)){
                    //弹出设置密码的对话框
                    showSetupPasswordDialog();
                }else {
                    //弹出输入密码的对话框
                    showEnterPasswordDialog();
                }
                break;
            case 1: //通讯卫士
                IntentUtils.startActivity(HomeActivity.this,CallSmsSafeActivity.class);
                break;
            case 2: //软件管理
                IntentUtils.startActivity(HomeActivity.this,AppManagerActivity.class);
                break;
            case 3: //进程管理
                IntentUtils.startActivity(HomeActivity.this,TaskManagerActivity.class);
                break;
            case 4: //流量统计
                break;
            case 5: //手机杀毒
                IntentUtils.startActivity(HomeActivity.this,AntiVirusActivity.class);
                break;
            case 6: //缓存清理
                IntentUtils.startActivity(HomeActivity.this,CleanCacheActivity.class);
                break;
            case 7: //高级工具
                IntentUtils.startActivity(HomeActivity.this,AtoolsActivity.class);
                break;
            case 8: //进入设置中心
                IntentUtils.startActivity(HomeActivity.this,SettingActivity.class);
                break;
        }
    }

    /**
     * 显示输入密码的对话框
     */
    public void showEnterPasswordDialog() {
        build = new AlertDialog.Builder(this);
        view = View.inflate(this, R.layout.dialog_enter_pwd, null);
        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        btn_accept = (Button) view.findViewById(R.id.btn_accept);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        //点击确认输入框
        btn_accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String inputPassword = et_password.getText().toString().trim();
                String savedPassword = sp.getString("password",null);
                if(TextUtils.isEmpty(inputPassword)){
                    ToastUtils.show(HomeActivity.this,"输入密码不能为空");
                    return;
                }
                if(Md5Utils.md5encode(inputPassword,"salt").equals(savedPassword)){
                    //ToastUtils.show(HomeActivity.this,"密码输入正确，进入手机防盗界面");
                    dialog.dismiss();
                    //判断用户是否进入过设置向导界面，如果完成，进入手机防盗的界面，否则进入设置向导界面
                    boolean finishsetup = sp.getBoolean("finishsetup",false);
                    if(finishsetup){
                        //如果完成过，进入手机防盗的界面
                        IntentUtils.startActivity(HomeActivity.this,LostFindActivity.class);
                    }else {
                        //否则进入设置向导界面
                        IntentUtils.startActivity(HomeActivity.this,Setup1Activity.class);
                    }
                }else {
                    ToastUtils.show(HomeActivity.this,"密码输入错误！");
                }
            }
        });
        //点击取消输入框
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        build.setView(view);
        dialog = build.show();
    }

    /**
     * 显示设置密码的对话框
     */
    public void showSetupPasswordDialog() {
        build = new AlertDialog.Builder(this);
        //自定义对话框显示的内容
        view = View.inflate(this,R.layout.dialog_setup_pwd,null);
        btn_accept = (Button) view.findViewById(R.id.btn_accept);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        et_password = (EditText) view.findViewById(R.id.et_password);
        final EditText et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
        //点击确认按钮
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString().trim();
                String password_confirm = et_password_confirm.getText().toString().trim();
                if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)){
                    ToastUtils.show(HomeActivity.this,"密码不能为空");
                    return;
                }
                if(!password.equals(password_confirm)){
                    ToastUtils.show(HomeActivity.this,"2次密码输入不一致");
                    return;
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("password", Md5Utils.md5encode(password,"salt"));
                editor.apply();
                dialog.dismiss();
                //显示输入密码的对话框
                showEnterPasswordDialog();
            }
        });
        //点击取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        build.setView(view);
        dialog = build.show();
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,R.layout.items_home,null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
            TextView tv = (TextView) view.findViewById(R.id.tv_homeitem_name);
            tv.setText(names[position]);
            iv.setImageResource(icons[position]);
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

}
