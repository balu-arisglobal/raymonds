package com.project.makeanapp.raymonds;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class startFeedbackPage extends FragmentActivity {
    private ProgressDialog pDialog;
    String obtainedURL;
    WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_feedback_page);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        String dataRead = readDataInternally();
        fetchShopUrl(dataRead);
    }

    private String readDataInternally() {
        String contents="";
        try {
            FileInputStream fin = openFileInput("TestDetails.txt");
            int count;
            while( (count = fin.read()) != -1){
                contents = contents + Character.toString((char)count);
            }
            System.out.println(contents);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return contents;
    }

    private void fetchShopUrl(final String config_key) {
        String tag_string_req = "req_login";
        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, config_key,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String connectionKey) {
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(connectionKey);
                            obtainedURL = jObj.getString("URL");
                            loadWebView(obtainedURL);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(startFeedbackPage.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(startFeedbackPage.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void loadWebView(String obtainedURL) {
        view = (WebView)findViewById(R.id.configURL);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient());
        view.clearCache(true);
        view.clearHistory();
        view.getSettings().setPluginState(WebSettings.PluginState.ON);
        view.setLayerType(view.LAYER_TYPE_HARDWARE, null);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
     //   view.loadUrl("http://172.20.10.3/test");
        view.loadUrl(obtainedURL);
        view.addJavascriptInterface(new IJavascriptHandler(), "cpjs");

    }

    /*@Override
    public void onAttachedToWindow()
    {

        super.onAttachedToWindow();
    }*/


    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);

    }
    final class IJavascriptHandler {
        IJavascriptHandler() {
        };
        @JavascriptInterface
        public void sendToAndroid(String text) {
            if (text.equals("true")){
                startFeedbackPage.this.finish();
            }
        }
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    }