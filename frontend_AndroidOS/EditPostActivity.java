package com.alexrappa.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alex on 12/4/2016.
 */

public class EditPostActivity  extends AppCompatActivity {

    public String POST_ID;
    public String TITLE;
    public String CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //Get ID's from Previous Intent
        Intent intent = getIntent();
        POST_ID = intent.getStringExtra(PostActivity.EXTRA_postid);
        TITLE = intent.getStringExtra(PostActivity.EXTRA_title);
        CONTENT = intent.getStringExtra(PostActivity.EXTRA_content);

        //Update Page with Post Info
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText("Update Your Post");

        EditText editText = (EditText) findViewById(R.id.title);
        editText.setText(TITLE);

        editText = (EditText) findViewById(R.id.content);
        editText.setText(CONTENT);

        Button button = (Button) findViewById(R.id.submit);
        button.setText("Update");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editPost();
            }
        });
    }

    public void editPost(){
        String title = ((EditText) findViewById(R.id.title)).getText().toString();
        String content = ((EditText) findViewById(R.id.content)).getText().toString();

        //Build URL For API
        String base_url = "https://cs496backendapi-151019.appspot.com/post/";
        base_url = base_url.concat(POST_ID);

        // Create Post Data
        RequestParams params = new RequestParams();
        params.put("title", title);
        params.put("content", content);

        AsyncHttpClient client = new AsyncHttpClient();
        client.put(this, base_url, params, new JsonHttpResponseHandler() {
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

