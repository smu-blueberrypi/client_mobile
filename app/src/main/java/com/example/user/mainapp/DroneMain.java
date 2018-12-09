package com.example.user.mainapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DroneMain extends Fragment {
    private Context context;
    public static final String sIP = "118.217.34.116"; //server ip
    public static final int sPORT = 9000; //server port
    private final String TAG = "tcp";
    private static SendData mSendData; //Thread Class
    private SendData stSend;
    private SendData detach;

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
    private double searchPer;
    private String per;
    private ListView listView;
    private ListViewAdapter adapter;
    private Geocoder geocoder;
    private NotificationHelper mNotificationHelper;

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

        listView =(ListView)view.findViewById(R.id.listView);
        adapter = new ListViewAdapter();
        listView.setAdapter(adapter);
        mNotificationHelper = new NotificationHelper(context);

        mSendData = new SendData();
        stSend = new SendData();
        detach = new SendData();
        setEventListener();



        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"Detach");
        isrun = -1;
        detach.start();
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


        //imageView = (ImageView)view.findViewById(R.id.imageView);





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
                stSend.start();
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
            while(true) {
                try {
                    String str = "";


                    str = in.readLine();
                    Log.e(TAG, str + " 도착!");
                    if(!str.equals("")) {
                        sosPerson = str;
                        handler.sendEmptyMessage(0);

                        /**
                        decodedString = Base64.decode(str, Base64.DEFAULT);
                        decodedByte = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                        if(!decodedByte.equals(null)) {
                            Log.e(TAG,"DecodeString = "+decodedString);
                            Log.e(TAG, "decodeByte = "+decodedByte);
                        }
                        **/
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
                    String lat;
                    String lng;
                    String alt;
                    String object;
                    try {
                        JSONObject jsonObject = new JSONObject(sosPerson);
                        lat = jsonObject.getString("lat");
                        lng = jsonObject.getString("lon");
                        alt = jsonObject.getString("alt");
                        object = jsonObject.getString("object");


                        per = object.substring(object.length()-4, object.length());

                        searchPer = Double.parseDouble(per);

                        searchPer = searchPer*100.0;

                        per = (int)searchPer+"%";

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        String formatDate = simpleDateFormat.format(date);

                        adapter.addList(per,"lat = "+lat,"lng = "+lng,"alt = "+alt+"m",formatDate);
                        DroneGPS.setLatitude(Double.parseDouble(lat));
                        DroneGPS.setLongtitude(Double.parseDouble(lng));
                        adapter.notifyDataSetChanged();

                        List<Address> list = null;
                        GeocodeUtil(context);
                        list = geocoder.getFromLocation(Double.parseDouble(lat),Double.parseDouble(lng),10);
                        checkTextView.setText(list.get(0).getAddressLine(0).replaceFirst("대한민국 충청남도",""));

                        NotificationCompat.Builder nb = mNotificationHelper.getsendNotification(per+"% height"+alt+"m help sos",list.get(0).getAddressLine(0).replaceFirst("대한민국 충청남도","") +" time : "+formatDate);
                        mNotificationHelper.getManager().notify(1,nb.build());
                    }catch (Exception e) {

                    }




                    //Glide.with(view).load(decodedByte).into(imageView);

                }
            }
        };

    }

    class SendData extends Thread{
        public void run() {
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
    private void GeocodeUtil(Context context) {
        geocoder = new Geocoder(context);
    }
    public ArrayList<String> getAddressListUsingGeolocation(GeoLocation location) {
        ArrayList<String> resultList = new ArrayList<>();

        try {
            List<Address> list = geocoder.getFromLocation(location.lat, location.lng, 10);

            for (Address addr : list) {
                resultList.add(addr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    private static class GeoLocation {
        double lat;
        double lng;

        public GeoLocation(double latitude, double longtiude) {
            this.lat = latitude;
            this.lng = longtiude;
        }
    }


    private static class LazyHolder {
        public static final DroneMain INSTANCE = new DroneMain();
    }
}
