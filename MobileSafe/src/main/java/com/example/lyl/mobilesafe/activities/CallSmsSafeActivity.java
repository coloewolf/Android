package com.example.lyl.mobilesafe.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.dao.BlackListDAO;
import com.example.lyl.mobilesafe.javabean.BlackNumberInfo;

import java.util.List;

/**
 * Created by lyl on 2016/6/29.
 *
 */
public class CallSmsSafeActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    //数据库总个数
    private int totalCount = 0;
    private int start = 0;
    private int max = 10;
    private LinearLayout ll_CallSmsSafe_loading;
    private ListView lv_blacklist;
    private BlackListDAO dao;
    private List<BlackNumberInfo> list;
    private MyAdapter myAdapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //隐藏load界面
            ll_CallSmsSafe_loading.setVisibility(View.INVISIBLE);
            if(myAdapter == null){
                myAdapter = new MyAdapter();
                lv_blacklist.setAdapter(myAdapter);
            }else {
                myAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsmssafe);
        ll_CallSmsSafe_loading = (LinearLayout) findViewById(R.id.ll_CallSmsSafe_loading);
        lv_blacklist = (ListView) findViewById(R.id.lv_blacklist);
        //给listView注册滚动监听事件
        lv_blacklist.setOnScrollListener(this);
        dao = new BlackListDAO(this);
        //初始化数据库总个数
        totalCount = dao.getTotalCount();
        //显示loading界面
        fillData();
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_CallSmsSafe_loading.setVisibility(View.VISIBLE);
        //开子线程更新数据
        new Thread(){
            @Override
            public void run() {
                if(list == null){
                    list = dao.getPartList(start,max);
                }else {
                    list.addAll(dao.getPartList(start, max));
                }
                //发消息让主线程更新UI
                Message msg = Message.obtain();
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * listView 滚动监听事件 当listView滚动状态发生变化时调用
     * @param view view对象
     * @param scrollState 滚动状态
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState){
            case SCROLL_STATE_IDLE: //空闲状态
                //判断listView界面上最后一个用户可见条目的位置信息
                int position = lv_blacklist.getLastVisiblePosition();
                int size = list.size();
                //position从0开始计算 size从1开始计算 当他们相等时说明到最底部了
                if(position == (size - 1)){
                    start += max;
                    if(start >= totalCount){
                        //说明数据已经加载到最后了
                        ToastUtils.show(this,"没有更多数据了");
                        return;
                    }
                    fillData();
                }
                break;
            case SCROLL_STATE_FLING: //滑动状态

                break;
            case SCROLL_STATE_TOUCH_SCROLL: //触摸滚动

                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 自定义listView适配器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 尽量复用convertView 减少view对象的创建次数 尽量减少子view对象查询次数 定义一个viewHolder
         * @param position 位置
         * @param convertView 历史回收的view对象
         * @param parent 父类对象
         * @return view对象
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if( convertView != null){
                //复用历史view对象
                view =convertView;
                viewHolder = (ViewHolder)view.getTag();
            }else {
                //只有当子view对象第一被创建时才查询id
                view = View.inflate(CallSmsSafeActivity.this, R.layout.items_callsmssaft, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_delete_blacknumber = (ImageView) view.findViewById(R.id.iv_delete_blacknumber);
                viewHolder.tv_phone = (TextView) view.findViewById(R.id.tv_blacklist_phone);
                viewHolder.tv_mode = (TextView) view.findViewById(R.id.tv_blacklist_mode);
                view.setTag(viewHolder);
            }
            //每次寻找要消耗过多资源
            String mode = list.get(position).getMode();
            if("1".equals(mode)){
                viewHolder.tv_mode.setText("电话拦截");
            }else if("2".equals(mode)){
                viewHolder.tv_mode.setText("短信拦截");
            }else if("3".equals(mode)){
                viewHolder.tv_mode.setText("全部拦截");
            }
            final String phone = list.get(position).getPhoneNumber();
            viewHolder.tv_phone.setText(phone);
            viewHolder.iv_delete_blacknumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据库记录
                            boolean result = dao.delete(phone);
                            if(result){
                                //删除ui界面的数据
                                list.remove(position);
                                //通知listview更新界面
                                myAdapter.notifyDataSetChanged();
                                ToastUtils.show(CallSmsSafeActivity.this,"删除成功");
                            }else {
                                ToastUtils.show(CallSmsSafeActivity.this,"删除失败");
                            }
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.show();
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

    /**
     * 子view的容器
     */
    private static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete_blacknumber;
    }

    /**
     * 添加黑名单
     * @param view view对象
     */
    public void addBlackNumber(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
        View dialogView = View.inflate(CallSmsSafeActivity.this, R.layout.dialog_add_blacknumber, null);
        final AlertDialog dialog = builder.create();
        final EditText et_black_number = (EditText) dialogView.findViewById(R.id.et_black_number);
        final RadioGroup rg_mode = (RadioGroup) dialogView.findViewById(R.id.rg_mode);
        Button bt_accept = (Button) dialogView.findViewById(R.id.btn_accept);
        bt_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNumber = et_black_number.getText().toString().trim();
                if(TextUtils.isEmpty(blackNumber)){
                    ToastUtils.show(CallSmsSafeActivity.this,"号码不能为空");
                    return;
                }
                int id = rg_mode.getCheckedRadioButtonId();
                String mode = "3";
                switch (id){
                    case R.id.rg_phone:
                        mode = "1";
                        break;
                    case R.id.rg_sms:
                        mode = "2";
                        break;
                    case R.id.rg_all:
                        mode = "3";
                        break;
                }
                boolean result = dao.add(blackNumber, mode);
                //刷新界面
                if(result){
                    ToastUtils.show(CallSmsSafeActivity.this,"添加黑名单成功");
                    BlackNumberInfo info = new BlackNumberInfo();
                    info.setPhoneNumber(blackNumber);
                    info.setMode(mode);
                    list.add(0,info);
                    //通知listview更新界面
                    myAdapter.notifyDataSetChanged();
                }else {
                    ToastUtils.show(CallSmsSafeActivity.this,"添加失败");
                }
                dialog.dismiss();
            }
        });
        Button bt_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        dialog.setView(dialogView,0,0,0,0);
        dialog.show();

    }
}
