package com.alexrappa.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Alex on 12/4/2016.
 */

public class UserProfileActivity extends AppCompatActivity implements LocationListener{
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public final static String EXTRA_MESSAGE = "com.alexrappa.myapplication.MESSAGE";

    public String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Get Board name & ID from previous intent
        Intent intent = getIntent();
        userId = intent.getStringExtra(BoardListActivity.EXTRA_USERID);
        String username = intent.getStringExtra(BoardListActivity.EXTRA_NAME);

        TextView textView = (TextView) findViewById(R.id.username);
        textView.setText(textView.getText().toString().concat(username));

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void buttonClick(View view) {
        deleteUser();
    }

    private void deleteUser() {
        String base_url = "https://cs496backendapi-151019.appspot.com/user/";
        AsyncHttpClient client = new AsyncHttpClient();
        client.delete(this, base_url.concat(userId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    String message = response.getString("message");
                    renderPage(statusCode, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                String message = response.toString();
                renderPage(statusCode, message);
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
            this.finishAffinity();

        } else {
            TextView textView = (TextView) findViewById(R.id.flashHeader);
            textView.setText("An Error Occured. Closing App");
            textView.setBackgroundColor(0xfff00000);
            this.finishAffinity();
        }
    }

    public void forgetUser(View view) {
        FileOutputStream outputStream;
        JSONObject user = new JSONObject();
        try {
            user.put("username", null);
            user.put("password", null);
            outputStream = openFileOutput("user", Context.MODE_PRIVATE);
            outputStream.write(user.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView textView = (TextView) findViewById(R.id.flash);
        textView.setText("User Credentials Removed!");
        textView.setBackgroundColor(0xff00ef00);
        textView.setTextSize(24);
    }

    public void logout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "false");
        startActivity(intent);
    }

    public void location() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

}


    @Override
    public void onLocationChanged(Location location) {
        TextView txtLat = (TextView) findViewById(R.id.flash);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }
}
