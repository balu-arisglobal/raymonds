package com.project.makeanapp.raymonds;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigPage extends FragmentActivity {

    private ProgressDialog pDialog;
    String shopNo;

    String obtainedURL ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String data = readDataInternally();
        if( data != "") {
            moveToStartFeedbackPage();
            }
        else{
            setContentView(R.layout.config_page);
            loadConfigPage();
        }
    }

    private void moveToStartFeedbackPage() {
        Intent moveToStartPage = new Intent(ConfigPage.this,startFeedbackPage.class);
        startActivity(moveToStartPage);
        finish();
    }


    public void loadConfigPage() {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        WebView view = (WebView)findViewById(R.id.pageView);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient());
        view.clearCache(true);
        view.clearHistory();
        view.getSettings().setPluginState(WebSettings.PluginState.ON);
        view.setLayerType(view.LAYER_TYPE_HARDWARE, null);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //view.loadUrl("http://172.20.10.3/config");
        view.loadUrl("file:///android_asset/index.html");
        view.addJavascriptInterface(new IJavascriptHandler(), "cpjs");

    }

    final class IJavascriptHandler {
        IJavascriptHandler() {
        };
        @JavascriptInterface
        public void sendToAndroid(String text1, String text2) {
            sendData(text1,text2);
        }
    }


    public void sendData(String key, String shopNo) {
            AppConfig.setFetchUrl(key);
            final String URL = AppConfig.getFetchUrl() + "?shopId=" + shopNo;
            fetchShopUrl(URL);
    }

    private void fetchShopUrl(final String config_key) {
        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, config_key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String connectionKey) {
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(connectionKey);
                            obtainedURL = jObj.getString("URL");
                            String data = readDataInternally();
                            if( data == "") {
                                System.out.println("data store internally");
                                storeDataInternally(config_key);
                                moveToStartFeedbackPage();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ConfigPage.this, "Oooops!! Check your URL , It might be wrong!" , Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ConfigPage.this, error.toString(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void storeDataInternally(final String url) {
        FileOutputStream fOut = null;
        try {
            System.out.println("storing file");
            fOut = openFileOutput("TestDetails.txt", MODE_PRIVATE);
            fOut.write(url.getBytes());
            fOut.close();
            System.out.println("stored to file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String readDataInternally() {
        String contents="";
        System.out.println("Reading file");
        try {
            FileInputStream fin = openFileInput("TestDetails");
            int count;
            while( (count = fin.read()) != -1){
                contents = contents + Character.toString((char)count);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return contents;
    }

}


