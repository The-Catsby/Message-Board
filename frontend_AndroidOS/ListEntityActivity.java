package com.alexrappa.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.FileOutputStream;

import cz.msebera.android.httpclient.Header;

public class ListEntityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        Intent intent = getIntent();
        String listType = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); // gets message value

        sendRequest(this, listType);

    }

    public void sendRequest(final Context context, final String listType) {
        String base_url = "https://cs-496-webapp.appspot.com/";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(context, (base_url + listType), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                TextView textView = new TextView(context);     // creates new TextView
                textView.setTextSize(18);
                textView.setText(response.toString());
                ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_list); // cast to ViewGroup bc its a superclass with addView() method
                layout.addView(textView);
                updateLog("List " + listType + ": " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                TextView textView = new TextView(context);     // creates new TextView
                textView.setTextSize(18);
                textView.setText("Error " + statusCode + ": " + response.toString());
                ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_list); // cast to ViewGroup bc its a superclass with addView() method
                layout.addView(textView);
                updateLog("Error " + statusCode + ": " + response.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void updateLog(String string) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("action_log", Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//        client.get(base_url, null, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//
//                TextView textView = new TextView(context);     // creates new TextView
//                textView.setTextSize(18);
//                textView.setText(responseBody.toString());
//                ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_list); // cast to ViewGroup bc its a superclass with addView() method
//                layout.addView(textView);
//            }
//
//
//            @Override
//            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
//                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                TextView textView = new TextView(context);     // creates new TextView
//                textView.setTextSize(18);
//                textView.setText("Error " + statusCode + ": " + responseBody.toString());
//                ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_list); // cast to ViewGroup bc its a superclass with addView() method
//                layout.addView(textView);
//            }
//
//        });
}

