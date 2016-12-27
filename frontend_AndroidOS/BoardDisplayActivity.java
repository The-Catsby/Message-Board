package com.alexrappa.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alex on 12/3/2016.
 */

public class BoardDisplayActivity extends AppCompatActivity {
    public final static String EXTRA_BOARDID = "com.alexrappa.myapplication.BOARDID";
    public final static String EXTRA_USERID = "com.alexrappa.myapplication.USERID";
    public final static String EXTRA_POSTID = "com.alexrappa.myapplication.POSTID";
    public final static String EXTRA_title = "com.alexrappa.myapplication.title";
    public final static String EXTRA_content = "com.alexrappa.myapplication.content";
    public final static String EXTRA_id = "com.alexrappa.myapplication.id";
    public final static String EXTRA_author = "com.alexrappa.myapplication.author";
    public String BOARD_ID;
    public String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        //Get Board name & ID from previous intent
        Intent intent = getIntent();
        String name = intent.getStringExtra(BoardListActivity.EXTRA_NAME);
        BOARD_ID = intent.getStringExtra(BoardListActivity.EXTRA_BOARDID);
        USER_ID = intent.getStringExtra(BoardListActivity.EXTRA_USERID);

        //Set Board Title
        TextView textView = (TextView) findViewById(R.id.boardTitle);
        String text = "Board: ";
        textView.setText(text.concat(name));
    }

    @Override
    protected void onStart(){
        super.onStart();
        ViewGroup layout = (ViewGroup) findViewById(R.id.postList);
        layout.removeAllViewsInLayout();
        getPosts(this, BOARD_ID);
    }

    private void getPosts(final Context context, String id) {
        String base_url = "https://cs496backendapi-151019.appspot.com/board/";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(context, base_url.concat(id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    //int status = response.getInt("status");
                    //String message = response.getString("message");
                    renderPosts(statusCode, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    //int status = response.getInt("status");
                    String message = response.getString("message");
                    renderPosts(statusCode, response);
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
                    renderPosts(statusCode, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void renderPosts(int status, JSONObject response) {
        if (status == 200) {
            //Parse JSON object
            JSONArray list = new JSONArray();
            int i = 0;
            try {
                list = response.getJSONArray("payload");
                //For each Object in payload
                if(list.length() > 0) {
                    for (i = 0; i < list.length(); i++) {
                        final String title = list.getJSONObject(i).getString("title");
                        final String content = list.getJSONObject(i).getString("content");
                        final String author = list.getJSONObject(i).getString("author");
                        final String id = list.getJSONObject(i).getString("key");
                        Button button = new Button(this);     // creates new TextView
                        button.setText(title);
                        ViewGroup layout = (ViewGroup) findViewById(R.id.postList);
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //Log.e("Clicked: ", title);
                                goToPost(id, title, content, author);
                            }
                        });
                        layout.addView(button);
                    }
                }else{
                    ViewGroup layout = (ViewGroup) findViewById(R.id.postList);
                    TextView textView = new TextView(this);
                    textView.setTextSize(18);
                    textView.setText("There are no Posts. Make One!");
                    layout.addView(textView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //Get message from JSON
            try{
                String message = response.getString("message");
                if(message.length() < 0)
                    message = "No Posts. Make A New One!";
                TextView textView = new TextView(this);
                textView.setText(message);
                textView.setTextSize(18);
                ViewGroup layout = (ViewGroup) findViewById(R.id.postList);
                layout.addView(textView);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        intent.putExtra(EXTRA_USERID, USER_ID);
        intent.putExtra(EXTRA_BOARDID, BOARD_ID);
        startActivity(intent);
    }

    public void goToPost(String id, String title, String content, String author){
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra(EXTRA_id, id);
        intent.putExtra(EXTRA_title, title);
        intent.putExtra(EXTRA_content, content);
        intent.putExtra(EXTRA_author, author);
        intent.putExtra(EXTRA_USERID, USER_ID);
        startActivity(intent);    }
}