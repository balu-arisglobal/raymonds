package com.project.makeanapp.raymonds;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkForActiveConnection()){
            Toast.makeText(MainActivity.this,"Check Your Network Connection",
                    Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    repeatCheckForConnection();
                }
            }, 3000);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    movetoNextPage();
                }
            }, 3000);
        }
    }


    public void repeatCheckForConnection() {
        if (!checkForActiveConnection()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    repeatCheckForConnection();
                }
            }, 5000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    movetoNextPage();
                }
            }, 3000);
        }
    }

    public void movetoNextPage(){
        Intent i = new Intent(MainActivity.this, ConfigPage.class);
        startActivity(i);
        finish();
    }

    public boolean checkForActiveConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }
}
