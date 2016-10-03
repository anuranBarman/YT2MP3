package com.anuranbarman.yt2mp3;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CheckInternet ci;
    EditText et;
    TextView tv;
    Button btn;
    String link;
    public boolean hasInternet;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Typeface type=Typeface.createFromAsset(getAssets(),"font.ttf");
        et=(EditText)findViewById(R.id.link_editText);
        tv=(TextView)findViewById(R.id.title_text);
        tv.setTypeface(type);
        btn=(Button)findViewById(R.id.convert_button);
        btn.setOnClickListener(this);
        btn.setTypeface(type);
        ci=new CheckInternet();
        ci.execute();
        //int permissionCheckExtStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //int permissionCheckReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //if(permissionCheckExtStorage != PackageManager.PERMISSION_DENIED || permissionCheckReadStorage !=PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    200);
        //}

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 200:
                if(grantResults.length<=0 && grantResults[0] !=PackageManager.PERMISSION_GRANTED && grantResults[1] !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            200);
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        link=et.getText().toString();
        if(link.isEmpty() || (!link.contains("youtube"))){
            Toast.makeText(this,"Link is not Valid.",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i=new Intent(MainActivity.this,Download.class);
        i.putExtra("link",link);
        startActivity(i);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("CHECK", "Error checking internet connection", e);
            }
        } else {
            Log.d("CHECK", "No network available!");
        }
        return false;
    }

    class CheckInternet extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            hasInternet=hasActiveInternetConnection();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(hasInternet==false){
                AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alerDialogBuilder.setMessage("The app does not work without Internet.Please connect to Internet.");
                alerDialogBuilder.setCancelable(false);
                alerDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = alerDialogBuilder.create();
                dialog.show();
            }
        }
    }
}
