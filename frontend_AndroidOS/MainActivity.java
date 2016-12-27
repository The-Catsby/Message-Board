package com.alexrappa.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.alexrappa.myapplication.MESSAGE";
    public final static String EXTRA_USERID = "com.alexrappa.myapplication.USERID";
    public final static String EXTRA_username = "com.alexrappa.myapplication.username";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean autoLogin = true;
        intent = getIntent();
        String msg = intent.getStringExtra(UserProfileActivity.EXTRA_MESSAGE);
        if(msg != null) {
            if(msg.length() > 0)
                autoLogin = Boolean.valueOf(msg);
        }
        intent = new Intent(this, BoardListActivity.class);
        if(autoLogin) {
            JSONObject user = loadUserInfo();
            try {
                if (user.getString("username") != null && user.getString("password") != null)
                    sendRequest(this, "login", true, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*  Register & Login onClick() methods  */
    public void createUser(View view) { sendRequest(this, "register", false, null); }
    public void logInUser(View view) { sendRequest(this, "login", false, null); }

    public void renderFlash(String flash, boolean flag) {
        TextView textView = (TextView) findViewById(R.id.flashHeader);
        textView.setText(flash);
        if(flag)
            textView.setBackgroundColor(0xff00ef00);
        else
            textView.setBackgroundColor(0xfff00000);
    }

    public void sendRequest(final Context context, String action, boolean isSavedUser, JSONObject user) {
        String username = "";
        String password = "";

        if (!isSavedUser) {
            // Get text field
            EditText userName = (EditText) findViewById(R.id.userName);      // gets editText content
            EditText userPassword = (EditText) findViewById(R.id.userPassword);
            username = userName.getText().toString();
            password = userPassword.getText().toString();

            //Validate input length
            if (username.length() < 3) {
                renderFlash("Username must be at least 3 characters", false);
                return;
            }
            if (userPassword.length() < 6) {
                renderFlash("Passowrd must be at least 6 characters", false);
                return;
            }
        } else {
            try {
                username = user.getString("username").toString();
                password = user.getString("password").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        actuallySendRequest(context, username, password, action);
    }

    public void actuallySendRequest(final Context context, final String username, final String password, final String action){
        //Send Request to API
        String base_url = "https://cs496backendapi-151019.appspot.com/user";
        // Create Post Data
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("action", action);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(context, base_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    //Save user info
                    saveUserInfo(username, password);
                    //Parse JSON
                    JSONObject payload = response.getJSONObject("payload");
                    String id = payload.getString("key");
                    String message = response.getString("message");
                    //New Intent
                    intent.putExtra(EXTRA_MESSAGE, message);
                    intent.putExtra(EXTRA_USERID, id);
                    intent.putExtra(EXTRA_username, username);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    String message = response.getString("message");
                    renderFlash(message, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveUserInfo(String username, String password) {
        FileOutputStream outputStream;
        JSONObject user = new JSONObject();
        try {
            user.put("username", username);
            user.put("password", password);
            outputStream = openFileOutput("user", Context.MODE_PRIVATE);
            outputStream.write(user.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadUserInfo() {
        byte[] buffer = new byte[1024];
        FileInputStream inputStream;
        try{
            inputStream = openFileInput("user");
            inputStream.read(buffer, 0, 1024);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        String string = new String(buffer, StandardCharsets.UTF_8);
        try {
            JSONObject user = new JSONObject(string);
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}

