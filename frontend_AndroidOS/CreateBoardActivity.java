package com.alexrappa.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alex on 12/4/2016.
 */

public class CreateBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
    }

    public void createBoard(View view){
        EditText editText = (EditText) findViewById(R.id.boardName);
        String name = editText.getText().toString();

        String base_url = "https://cs496backendapi-151019.appspot.com/board";
        // Create Post Data
        RequestParams params = new RequestParams();
        params.put("name", name);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(this, base_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    //int status = response.getInt("status");
                    String message = response.getString("message");
                    renderPage(statusCode, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    //int status = response.getInt("status");
                    String message = response.getString("message");
                    renderPage(statusCode, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void renderPage(int status, String string) {
        if (status == 200) {
            ViewGroup main = (ViewGroup) findViewById(R.id.main);
            main.removeAllViews();
            TextView textView = new TextView(this);
            textView.setText(string);
            textView.setTextSize(24);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setBackgroundColor(0xff00ef00);
            main.addView(textView);
        } else {
            TextView textView = (TextView) findViewById(R.id.flashHeader);
            textView.setText(string);
            textView.setBackgroundColor(0xfff00000);
        }
    }
}
