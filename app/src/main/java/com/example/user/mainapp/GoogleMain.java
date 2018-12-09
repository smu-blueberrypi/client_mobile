package com.example.user.mainapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMain extends Fragment  {
    private Context context;
    private GoogleMap mGoogleMap = null;
    private LatLng sosPosition;

    @SuppressLint("ValidFragment")
    private GoogleMain() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.google_main, container, false);



        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static GoogleMain getInstance() {
        return LazyHolder.INSTANCE;
    }




    private static class LazyHolder {
        public static final GoogleMain INSTANCE = new GoogleMain();
    }
}
