package com.anuranbarman.yt2mp3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by anuran on 2/10/16.
 */

public class Splashscreen extends Activity {
    TextView app_name,slogan,myName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);
        app_name=(TextView)findViewById(R.id.app_name_text);
        slogan=(TextView)findViewById(R.id.slogan_text);
        myName=(TextView)findViewById(R.id.myName);
        Typeface type = Typeface.createFromAsset(getAssets(),"font.ttf");
        app_name.setTypeface(type);
        slogan.setTypeface(type);
        myName.setTypeface(type);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              Intent i = new Intent(Splashscreen.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },3000);
    }
}
