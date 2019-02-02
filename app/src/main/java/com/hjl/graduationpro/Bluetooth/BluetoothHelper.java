package com.hjl.graduationpro.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothHelper extends Thread{

    public interface BluetoothListenr{
        /**
         * 收到数据
         * @param data
         */
        void onReciveMessage(String data);

        /**
         * 连接到设备
         */
        void onConnectDecvice();

        /**
         * 断开连接
         */
        void onDisConnect();
    }

    private BluetoothSocket socket;
    private BluetoothDevice device;
    private static final String TAG = "BluetoothHelper";

    private InputStream inputStream;
    private OutputStream outputStream;

    private BluetoothListenr bluetoothListenr;

    private final int BUFFER_SIZE =1024*4;
    private volatile Boolean quitState = false;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothHelper(BluetoothDevice device){
        this.device = device;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothHelper(BluetoothSocket socket){
        this.socket = socket;


    }


    @Override
    public void run() {
        try {

            socket.connect();
            bluetoothListenr.onConnectDecvice();
            Log.i(TAG,"连接蓝牙设备");

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            StringBuffer readMessage = new StringBuffer();

            while (!quitState){

                while ((len = inputStream.read(buffer)) != -1){
                    readMessage.append(new String(buffer,0,len,"GB2312"));
//                    Log.i("rec","msg size is: "+ readMessage.length());
                    if (readMessage.length() == "a,b".length()){
                        bluetoothListenr.onReciveMessage(readMessage.toString());
                        readMessage.delete(0,readMessage.length());
//                        Log.i("rec","send");
                    }

                    if (readMessage.length() >= 6){
                        readMessage.delete(0,readMessage.length());
//                        Log.i("rec",readMessage.length()+"");
//                        Log.i("rec","fail");
                    }


                }

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 设置监听器
     */
    public void setBluetoothListenr(BluetoothListenr bluetoothListenr){
        this.bluetoothListenr = bluetoothListenr;
    }

    /**
     * 发送消息数据
     * @param msg
     */
    public void sendMsg(final String msg){
        if (outputStream != null){
            try {
                byte[] bytes = msg.getBytes("GBK");
                outputStream.write(bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 断开蓝牙连接
     */
    public void disableConnected(){
        try {
            socket.close();
            bluetoothListenr.onDisConnect();
            quitState = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
