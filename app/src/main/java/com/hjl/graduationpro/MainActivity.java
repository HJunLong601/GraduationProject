package com.hjl.graduationpro;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hjl.graduationpro.Bluetooth.BluetoothDialog;
import com.hjl.graduationpro.Bluetooth.BluetoothHelper;
import com.hjl.graduationpro.Bluetooth.MyBluetoothReceiver;
import com.hjl.graduationpro.Grade.GradeActivity;
import com.hjl.graduationpro.View.MainView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,MyBluetoothReceiver.BroadcastListenr, BluetoothDialog.DialogListener {

    private ImageButton start;
    private ImageButton result;
    private ImageButton exit;
    private ImageButton bluetooth;


    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private MyBluetoothReceiver receiver;
    private BluetoothDialog dialog ;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mConnectedDev;
    private BluetoothHelper helper;

    public void setListener(MainViewListener listener) {
        this.listener = listener;
    }

    private MainViewListener listener;
    public static MainActivity sInstance = null;


    private final int PERMISSION_REQUEST_COARSE_LOCATION = 61;
    private final int REQUEST_ENABLE_CODE = 62;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "MainActivity";

    public interface MainViewListener{
        void onReceiveBluData(String data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        //

        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        //

        start = findViewById(R.id.main_startgame);
        result = findViewById(R.id.main_result);
        exit = findViewById(R.id.main_endgame);
        bluetooth = findViewById(R.id.connect_blue);

        start.setOnClickListener(this);
        result.setOnClickListener(this);
        exit.setOnClickListener(this);
        bluetooth.setOnClickListener(this);

        receiver = new MyBluetoothReceiver();
        receiver.setListenr(this);

        sInstance = this;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_startgame:
                Intent startGame = new Intent(MainActivity.this,GameActivity.class);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(startGame);
                break;
            case R.id.main_endgame:
                finish();
                break;
            case R.id.main_result:
                Intent showGrade = new Intent(MainActivity.this, GradeActivity.class);
                startActivity(showGrade);
                break;
            case  R.id.connect_blue:
                FragmentManager fragmentManager = getFragmentManager();
                dialog = new BluetoothDialog();
                dialog.setListener(this);
                dialog.setDeviceList(deviceList);
                dialog.setDevices(devices);

                dialog.show(fragmentManager,"MyDialogment");
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 注册广播接收器。
        // 接收蓝牙发现
        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filterFound);

        IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filterStart);

        IntentFilter filterFinish = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filterFinish);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 保证一定可以取消注册
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null){
            helper.disableConnected();
        }

    }

    /**
     *连接蓝牙设备
     */

    private void connectDevice(BluetoothDevice device){
        try{
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);

            if (helper == null) {
                helper = new BluetoothHelper(socket);
                helper.setBluetoothListenr(new BluetoothHelper.BluetoothListenr() {
                    @Override
                    public void onReciveMessage(String data) {
                        //Log.i(TAG,data);
                        if (listener != null){
                            listener.onReceiveBluData(data);
                        }

                    }

                    @Override
                    public void onConnectDecvice() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this,"蓝牙已连接",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onDisConnect() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"蓝牙已断开",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            helper.start();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * 初始化 打开蓝牙
     */
    private void initDevice (){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 检查设备是否支持蓝牙设备
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "设备不支持蓝牙");
            // 不支持蓝牙，退出。
            return;
        }
        // 如果用户的设备没有开启蓝牙，则弹出开启蓝牙设备的对话框，让用户开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "请求用户打开蓝牙");

            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE_CODE);
            // 接下去，在onActivityResult回调判断
        }
    }

    /**
     * 权限请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"权限获取成功",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * 蓝牙打开结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "打开蓝牙成功！");
                Toast.makeText(this,"蓝牙已开启",Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "放弃打开蓝牙！");
                Toast.makeText(this,"请开启蓝牙",Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.d(TAG, "蓝牙异常！");
            Toast.makeText(this,"蓝牙异常！开启失败！",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  蓝牙广播接收器接口 会在收到新的蓝牙设备广播时调用
     * @param device
     */

    @Override
    public void onFoundDeviceListenr(BluetoothDevice device) {
        if (device.getName() != null && device.getName().trim().length() != 0){ //对蓝牙设备进行过滤
            deviceList.add(device.getName());
            devices.add(device);
        }


        if (dialog != null){
            dialog.getmAdapter().notifyDataSetChanged();
        }

    }

    /**
     * Dialog 四个接口之一 点击取消按钮时
     */

    @Override
    public void onCancelClick() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        if (dialog != null){
            dialog.dismiss();
        }

    }

    /**
     * Dialog 四个接口之一 点击搜索按钮时
     */

    @Override
    public void onSearchDevice() {
        initDevice();
        deviceList.clear();
        devices.clear();
        dialog.getmAdapter().notifyDataSetChanged();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering() ){
            mBluetoothAdapter.cancelDiscovery();
        }

        if (mBluetoothAdapter != null){
            mBluetoothAdapter.startDiscovery();
            Toast.makeText(MainActivity.this,"正在搜索蓝牙设备...",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Dialog 四个接口之一 点击断开按钮时
     */

    @Override
    public void onDisable() {
        if (helper!= null){
            helper.disableConnected();
        }
        //关闭线程
        helper = null;
    }

    /**
     * Dialog 四个接口之一 点击Listview 的设备列表进行连接时
     */

    @Override
    public void onDevicesClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering() ){
            mBluetoothAdapter.cancelDiscovery();
        }

        if (helper != null){//避免线程重复开启闪退
            Toast.makeText(MainActivity.this,"蓝牙已连接或正在连接中...",Toast.LENGTH_SHORT).show();
        }else{
            mConnectedDev = devices.get(position);
            connectDevice(mConnectedDev);
        }
    }
}
