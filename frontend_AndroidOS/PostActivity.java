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

import cz.msebera.android.httpclient.Header;

import static com.alexrappa.myapplication.R.id.textView;

/**
 * Created by Alex on 12/4/2016.
 */

public class PostActivity extends AppCompatActivity {
    public final static String EXTRA_postid = "com.alexrappa.myapplication.POSTID";
    public final static String EXTRA_title = "com.alexrappa.myapplication.title";
    public final static String EXTRA_content = "com.alexrappa.myapplication.content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);

        //Get Board name & ID from previous intent
        Intent intent = getIntent();
        final String post_id = intent.getStringExtra(BoardDisplayActivity.EXTRA_id);
        String user_id = intent.getStringExtra(BoardDisplayActivity.EXTRA_USERID);
        final String title = intent.getStringExtra(BoardDisplayActivity.EXTRA_title);
        final String content = intent.getStringExtra(BoardDisplayActivity.EXTRA_content);
        String author = intent.getStringExtra(BoardDisplayActivity.EXTRA_author);

        getAuthor(author);

        //Set Post Title
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText((title));
        //Set Post Author
//        textView = (TextView) findViewById(R.id.author);
//        textView.setText(textView.getText().toString().concat(author));
        //Set Post Content
        textView = (TextView) findViewById(R.id.content);
        textView.setText(content);

        //If user logged in is the post Author, Render a Delete Post button
        if(user_id.equals(author)){
            //set Delete Button
            Button button = (Button) findViewById(R.id.delete);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    deletePost(post_id);
                }
            });
            //set Edit Button
            button = (Button) findViewById(R.id.edit);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    editPost(post_id, title, content);
                }
            });
        }
    }

    public void editPost(String post_id, String title, String content){
        Intent intent = new Intent(this, EditPostActivity.class);
        intent.putExtra(EXTRA_postid, post_id);
        intent.putExtra(EXTRA_title, title);
        intent.putExtra(EXTRA_content, content);
        startActivity(intent);
    }

    private void deletePost(String id){ //used to pass context
        deletePost(this, id);
    }
    private void getAuthor(String id) {
        String base_url = "https://cs496backendapi-151019.appspot.com/user/";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, base_url.concat(id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    Log.e("JSON: ", response.toString());
                    JSONObject payload = response.getJSONObject("payload");
                    String username = payload.getString("username");
                    renderUsername(username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                String username = "Deleted";
                renderUsername(username);
            }

        });
    }
    private void renderUsername(String username){
        TextView textView = (TextView) findViewById(R.id.author);
        textView.setText(textView.getText().toString().concat(username));
    }

    private void deletePost(final Context context, String id) {
        String base_url = "https://cs496backendapi-151019.appspot.com/post/";
        AsyncHttpClient client = new AsyncHttpClient();
        client.delete(this, base_url.concat(id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    String message = response.getString("message");
                    ViewGroup layout = (ViewGroup) findViewById(R.id.postLayout);
                    layout.removeAllViews();

                    EditText editText = new EditText(context);
                    editText.setText(message);
                    editText.setTextSize(25);
                    editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    editText.setBackgroundColor(0xff00ef00);
                    layout.addView(editText);
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
                    //renderPosts(statusCode, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void editPost(View view){

    }

}
