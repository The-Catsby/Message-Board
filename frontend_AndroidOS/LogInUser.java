package com.alexrappa.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alex on 12/1/2016.
 */

public class LogInUser extends AppCompatActivity {
    public final static String EXTRA_FLASH = "com.alexrappa.myapplication.FLASH";
    public final static String EXTRA_KEY = "com.alexrappa.myapplication.ID";
    public final static String EXTRA_NAME = "com.alexrappa.myapplication.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

//        Intent intent = getIntent();
//        String userPassword = intent.getStringExtra(MainActivity.EXTRA_PASSWORD); // gets message value
//        String userName = intent.getStringExtra(MainActivity.EXTRA_NAME); // gets message value
//
//        sendRequest(this, userName, userPassword);
    }



    private void getBoards(final Context context) {
        String base_url = "https://cs496backendapi-151019.appspot.com/board";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(context, base_url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    //int status = response.getInt("status");
                    //String message = response.getString("message");
                    renderBoards(statusCode, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    //int status = response.getInt("status");
                    //String message = response.getString("message");

                    renderBoards(statusCode, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String string, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    //int status = response.getInt("status");
                    //String message = response.getString("message");
                    renderBoards(statusCode, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void renderBoards(int status, JSONObject response) {
        if (status == 200) {
            //Parse JSON object
            JSONArray list = null;
            int i = 0;
            try {
                list = response.getJSONArray("payload");
                //For each Object in payload
                for (i = 0; i < list.length(); i++) {
                    final String name = list.getJSONObject(i).getString("name");
                    final String id = list.getJSONObject(i).getString("key");
                    Button button = new Button(this);     // creates new TextView
                    button.setText(name);
                    ViewGroup layout = (ViewGroup) findViewById(R.id.buttonList);
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.e("Clicked: ", name);
                            goToBoard(id, name);
                        }
                    });
                    layout.addView(button);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            TextView textView = (TextView) findViewById(R.id.boardList);
//            textView.setText(string);
//            textView.setBackgroundColor(0xff00ef00);
        } else {
            String string;
            if (response != null) {
                string = response.toString();
            } else {
                string = "No Message Boards. Make A New One!";
            }
            TextView textView = new TextView(this);
            textView.setText(string);
            textView.setTextSize(18);
            ViewGroup layout = (ViewGroup) findViewById(R.id.buttonList);
            layout.addView(textView);
        }
    }

    private void goToBoard(String id, String boardName) {
        Intent intent = new Intent(this, BoardDisplayActivity.class);
        intent.putExtra(EXTRA_KEY, id);
        intent.putExtra(EXTRA_NAME, boardName);
        startActivity(intent);
    }

    public void createBoard(View view){
        Intent intent = new Intent(this, CreateBoardActivity.class);
        startActivity(intent);
    }
}