package com.anuranbarman.yt2mp3;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by anuran on 29/9/16.
 */

public class Download extends Activity implements View.OnClickListener {
    ProgressDialog pDialog;
    public static int progress_bar_type=ProgressDialog.STYLE_HORIZONTAL;
    Button downloadButton;
    TextView info,lengthVid,infoHeader,vidLenHeader,appNameHeader,memAva,memTota;
    String link,title,length,originalDownloadLink;
    final static String final_link="http://www.youtubeinmp3.com/fetch/?format=JSON&video=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.download);
        Bundle b=getIntent().getExtras();
        link=b.getString("link");
        downloadButton=(Button)findViewById(R.id.download_button);
        infoHeader=(TextView)findViewById(R.id.video_title_header);
        info=(TextView)findViewById(R.id.video_title);
        memAva=(TextView)findViewById(R.id.memAva);
        memTota=(TextView)findViewById(R.id.memTota);
        appNameHeader=(TextView)findViewById(R.id.title_text);
        lengthVid=(TextView)findViewById(R.id.video_length);
        vidLenHeader=(TextView)findViewById(R.id.video_length_header);
        Typeface type=Typeface.createFromAsset(getAssets(),"font.ttf");
        downloadButton.setTypeface(type);
        info.setTypeface(type);
        appNameHeader.setTypeface(type);
        infoHeader.setTypeface(type);
        vidLenHeader.setTypeface(type);
        lengthVid.setTypeface(type);
        memAva.setTypeface(type);
        memTota.setTypeface(type);
        downloadButton.setOnClickListener(this);
        new Generate().execute(link);
        StatFs stat = new StatFs(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        long memavailable=bytesAvailable / 1048576;
        long totalBytes=(long)stat.getBlockSize() * (long)stat.getBlockCount();
        long totalMemMB=totalBytes/1048576;
        memAva.setText("Memory Available : "+memavailable+" MB");
        memTota.setText("Total Memory : "+totalMemMB+" MB");

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case ProgressDialog.STYLE_HORIZONTAL:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading MP3. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public void onClick(View v) {
            new DownloadMP3().execute(originalDownloadLink);
    }


    class Generate extends AsyncTask<String,Void,String>{

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=ProgressDialog.show(Download.this,"Wait","Converting your video...");
        }

        @Override
        protected String doInBackground(String... params) {
            String linkFinal=final_link+params[0];
            try {
                URL url=new URL(linkFinal);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream is=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String webpage="",data="";
                while((data=reader.readLine()) !=null){
                    webpage +=data+"\n";
                }
                JSONObject jObject = new JSONObject(webpage);
                title=jObject.optString("title");
                length=jObject.optString("length");
                originalDownloadLink=jObject.optString("link");
                Log.d("LINK",originalDownloadLink);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            info.setText(title);
            lengthVid.setText(length);
        }
    }



    class DownloadMP3 extends AsyncTask<String,String,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(ProgressDialog.STYLE_HORIZONTAL);
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try {
                URL url = new URL(params[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream to write file
                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+title.trim()+".mp3");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(ProgressDialog.STYLE_HORIZONTAL);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+title.trim()+".mp3");
            if(file.exists())
                Toast.makeText(Download.this,"MP3 is downloaded.Check /sdcard/Download/ to get it.",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(Download.this,"Something gone wrong while downloading.Please try again.",Toast.LENGTH_LONG).show();
        }
    }
}
