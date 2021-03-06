package com.yatra.dependencies;

/**
 * Created by Prasad on 8/31/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class Download_Dependency_File extends Activity {

    // button to show progress dialog
    Button btnShowProgress;

    // Progress Dialog

    TextView my_image;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    private ProgressDialog pDialog;
    // File url to download
    private static String Dev_url,Dev_Name,Dev_Type,eventno;
   // private DownloadVideosDbHelper dbHelper ;
    File path,file;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadfile);
        Dev_url = getIntent().getExtras().getString("URL");
        Dev_Name= getIntent().getExtras().getString("Name");
        Dev_Type= getIntent().getExtras().getString("type");

        // show progress bar button
         // btnShowProgress = (Button) findViewById(R.id.btnProgressBar);
         // Image view to show image after downloading
         //    my_image = (TextView) findViewById(R.id.my_image);
         /**
         * Show Progress bar click event
         * */
                // starting new Async Task
                new DownloadFileFromURL().execute(Dev_url);
           //     dbHelper = new DownloadVideosDbHelper(this);
    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                try {
                     path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);

                 if(Dev_Type.equalsIgnoreCase("IMAGE")){

                     file = new File(path,"dependency"+".jpg");

                 }
                 else{

                     file = new File(path,"dependency"+".mp4");

                 }

                    file.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append("aa");


                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress(""+(int)((total*100)/lenghtOfFile));

                        // writing data to file
                        fOut.write(data, 0, count);
                    }
                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();
                    input.close();

                   // Toast.makeText(getApplicationContext(), "File created" +path+Video_Name, Toast.LENGTH_LONG).show();

                }
                catch(Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }




            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
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
            dismissDialog(progress_bar_type);

            Log.d("path: ", file.getAbsolutePath());
            Log.d("file: ", path.getAbsolutePath());

                Toast.makeText(Download_Dependency_File.this, "Downloaded Successfully"+path.getAbsolutePath(), Toast.LENGTH_LONG).show();

               Intent i = new Intent(getApplicationContext(),  DependencyListActivity.class);

                startActivity(i);
            }



            // setting downloaded into image view
          //  my_image.setImageDrawable(Drawable.createFromPath(imagePath));




    }
}