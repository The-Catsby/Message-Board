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
 * Created by Alex on 12/4/2016.
 */

public class BoardListActivity extends AppCompatActivity {

    public final static String EXTRA_BOARDID = "com.alexrappa.myapplication.BOARD";
    public final static String EXTRA_NAME = "com.alexrappa.myapplication.NAME";
    public final static String EXTRA_USERID = "com.alexrappa.myapplication.USERID";
    public String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); // gets message value
        String username = intent.getStringExtra(MainActivity.EXTRA_username); // gets message value
        USER_ID = intent.getStringExtra(MainActivity.EXTRA_USERID); // gets message value

        intent = new Intent(this, UserProfileActivity.class);
        renderButton(intent, username, message);
    }

    @Override
    protected void onStart(){
        super.onStart();
        ViewGroup layout = (ViewGroup) findViewById(R.id.buttonList);
        layout.removeAllViewsInLayout();
        getBoards(this);
    }

    /*  Renders the Flash Header    */
    public void renderButton(final Intent intent, final String username, String message) {
        Button button = (Button) findViewById(R.id.user);
        button.setText(message);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra(EXTRA_NAME, username);
                intent.putExtra(EXTRA_USERID, USER_ID);
                startActivity(intent);

            }
        });
    }

    /*  Get Request to API to List All Boards   */
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
                if(list.length() > 0) {
                    for (i = 0; i < list.length(); i++) {
                        final String name = list.getJSONObject(i).getString("name");
                        final String id = list.getJSONObject(i).getString("key");
                        Button button = new Button(this);
                        button.setText(name);
                        ViewGroup layout = (ViewGroup) findViewById(R.id.buttonList);
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                goToBoard(id, name);
                            }
                        });
                        layout.addView(button);
                    }
                }else{
                    ViewGroup layout = (ViewGroup) findViewById(R.id.buttonList);
                    TextView textView = new TextView(this);
                    textView.setTextSize(18);
                    textView.setText("There are no Boards. Make One!");
                    layout.addView(textView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        intent.putExtra(EXTRA_BOARDID, id);
        intent.putExtra(EXTRA_NAME, boardName);
        intent.putExtra(EXTRA_USERID, USER_ID);
        startActivity(intent);
    }

    public void createBoard(View view){
        Intent intent = new Intent(this, CreateBoardActivity.class);
        startActivity(intent);
    }
}
