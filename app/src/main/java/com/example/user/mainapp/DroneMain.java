package com.example.user.mainapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class DroneMain extends Fragment {
    private Context context;
    public static final String sIP = "118.217.34.116"; //server ip
    public static final int sPORT = 9000; //server port
    private final String TAG = "tcp";
    public SendData mSendData = null; //Thread Class
    private Button runVideo_btn;
    private Button stopVideo_btn;
    private View view;
    private final String START_MODEL = "runVideo";
    private final String STOP_MODEL = "stopVideo";
    private final String DETACH_MODEL = "detach";
    private InetAddress serverAddr;
    private Socket socket;
    private GetData getData;
    private BufferedReader in;
    private TextView checkTextView;
    private String sosPerson = "";
    private int isrun = 0;

    @SuppressLint("ValidFragment")
    private DroneMain() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drone_main, container, false);
        this.view = view;
        setEventListener();



        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"Detach");
        isrun = -1;
        mSendData.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ConnectServerThread connectServerThread = new ConnectServerThread();
        connectServerThread.start();
    }

    private void setEventListener() {
        runVideo_btn = (Button)view.findViewById(R.id.start_btn);
        stopVideo_btn = (Button)view.findViewById(R.id.stop_btn);
        checkTextView = (TextView)view.findViewById(R.id.searchTxt);


        mSendData = new SendData();


        runVideo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isrun = 1;
                mSendData.start();
            }
        });

        stopVideo_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isrun = 0;
                mSendData.start();
            }
        });

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static DroneMain getInstance() {
        return LazyHolder.INSTANCE;
    }


    class ConnectServerThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                serverAddr = InetAddress.getByName(sIP); //Ipv4 할당
                socket = new Socket(serverAddr, sPORT);  //Ipv4와 socket 맵핑

                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream())
                );
                getData = new GetData();
                getData.start();

            } catch (Exception e) {
                Log.e(TAG, "ExCeption "+e);
            }
        }
    }

    class GetData extends Thread{
        @Override
        public void run() {
            super.run();
            while(true) {
                try {
                    String str = "";
                    str = in.readLine();
                    Log.e(TAG, str + " 도착!");
                    if(!str.equals("")) {
                        sosPerson = str;
                        handler.sendEmptyMessage(0);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "GET DATA EXCEPTION = " + e);
                }
            }
        }
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0) {
                    checkTextView.setText(sosPerson);
                }
            }
        };

    }

    class SendData extends Thread{
        public void run() {
            super.run();
                try {
                    PrintWriter out = new PrintWriter(
                            new BufferedWriter(
                                    new BufferedWriter(
                                            new OutputStreamWriter(socket.getOutputStream())
                                    )
                            ), true); //true BufferSize안에 있는 데이터 초기화
                    if(isrun == 1) { //start_BTN
                        out.println(START_MODEL);
                    } else if (isrun == 0) { //stop_BTN
                        out.println(STOP_MODEL);
                    } else if(isrun == -1) {
                        out.println(DETACH_MODEL);
                    }

                } catch (Exception e) {

                }
            }

    }

    private static class LazyHolder {
        public static final DroneMain INSTANCE = new DroneMain();
    }
}
