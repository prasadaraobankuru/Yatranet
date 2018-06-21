package com.yatra.dependencies;


/**
 * Created by Prasad on 11/29/2017.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class DependencyListActivity extends AppCompatActivity  implements DataFetchListner {

    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    String result_responce="";
    public static ArrayList<dependencies>dependency_List = new ArrayList<dependencies>();;
    TextView emptyview;
    public static ListView Dependency_List;
    Dependency_adapter Dependency_List_Adapter;
    CountDownTimer cTimer = null;
    Toolbar toolbar;
    private Database mDatabase;
    SaveIntoDatabase task;
    SwipeRefreshLayout refresh;
    private int Db_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dependency_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dependencies List");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  cancelTimer();
                finish();
            }
        });

        //permissions
        if (!checkPermission()) {
            requestPermission();
        }

        Dependency_List = (ListView) findViewById(R.id.dep_list);
        emptyview = (TextView) findViewById(R.id.empty_view);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        //swipe to refresh the data
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                //getting data from cloud
                if (NetworkUtility.isNetworkAvailable(getApplicationContext())) {

                    DependencyList();
                } else {

                    Toast.makeText(getApplicationContext(), "Need Internet Connection to Refresh", Toast.LENGTH_SHORT).show();
                    Log.e("No net", "Need Internet Connection");
                    refresh.setRefreshing(false);

                }

            }


        });

        //sql data base creation
        mDatabase = new Database(this);


      Db_count = mDatabase.getCountByValue();

        if(Db_count > 0){

            //getting data from local db
            getFeedFromDatabase();
        }

       else {

            //getting data from cloud
            if(NetworkUtility.isNetworkAvailable(getApplicationContext())) {

                DependencyList();
            }

            else {

                Toast.makeText(getApplicationContext(),"Need Internet Connection to Load Data" ,Toast.LENGTH_SHORT).show();
                Log.e("No net", "Need Internet Connection");
            }

        }



    }

    private void getFeedFromDatabase() {

        mDatabase.fetchData(this);
        //refresh.setRefreshing(false);
        emptyview.setVisibility(View.INVISIBLE);

    }


   /* protected  void onStart() {

        super.onStart();

        blogList.clear();
        doTheAutoRefresh(3600000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            cancelTimer();
        }
        catch(Exception ex){}

    }*/

    private void  DependencyList(){



        final SpotsDialog dialog = new SpotsDialog(this);
        dialog.setMessage("Getting Dependencies Data");
        dialog.setCancelable(false);
        dialog.show();

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        wmlp.x = 100;   //x position
        wmlp.y = 100;   //y position
        dialog.getWindow().setAttributes(wmlp);
        dialog.setCanceledOnTouchOutside(false);


        dependency_List.clear();

        String blogurl = getApplicationContext().getString(R.string.url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, blogurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        result_responce = response;
                        //String exactRes = "{\"USERLIST\":[" + result_responce + "]}";
                        dialog.dismiss();
                        refresh.setRefreshing(false);
                        try {

                            JSONObject jsonResponse = new JSONObject(result_responce);
                            JSONArray ja = jsonResponse.getJSONArray("dependencies");

                            for (int i = 0; i < ja.length(); i++) {

                                dependencies dep = new dependencies();
                                //sublist1.setId(ja.getJSONObject(i).getString("id"));
                                dep.setSno(i);
                                dep.setId(ja.getJSONObject(i).getString("id"));
                                dep.setCdn_path(ja.getJSONObject(i).getString("cdn_path"));
                                dep.setName(ja.getJSONObject(i).getString("name"));
                                dep.setSizeInBytes(String.valueOf(ja.getJSONObject(i).getDouble("sizeInBytes")));
                                dep.setType(ja.getJSONObject(i).getString("type"));

                                dependency_List.add(dep);

                                task = new SaveIntoDatabase();
                                task.execute(dep);

                            }


                            Dependency_List_Adapter = new Dependency_adapter(DependencyListActivity.this, dependency_List);
                            Dependency_List.setAdapter(Dependency_List_Adapter);
                            Dependency_List_Adapter.notifyDataSetChanged();

                           if (dependency_List.isEmpty()) {

                               Dependency_List.setVisibility(View.GONE);
                               emptyview.setVisibility(View.VISIBLE);
                            }

                            else {

                               Dependency_List.setVisibility(View.VISIBLE);
                               emptyview.setVisibility(View.GONE);
                            }


                        } catch (Exception e) {

                            Log.e("",""+e);
                            dialog.dismiss();
                            refresh.setRefreshing(false);
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Problem In Connecting Server", Toast.LENGTH_LONG).show();
                       // mView.dismiss();
                        dialog.dismiss();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        Log.e("request", "ip: " + stringRequest);

    }

    private void doTheAutoRefresh(long PollCount) {


        cTimer = new CountDownTimer(PollCount, 10000) {
            public void onTick(long millisUntilFinished) {

                DependencyList();
            }
            public void onFinish() {

            }
        };
        cTimer.start();
    }


    //cancel timer
    public void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    //getting local db values and set to adpater
    @Override
    public void onDeliverData(dependencies dataModel) {

      //  if (!NetworkUtility.isDataExist(Dependency_List_Adapter, dataModel.getSno()))
            Dependency_List_Adapter.addData(dataModel);

        Dependency_List.setAdapter(Dependency_List_Adapter);
        Dependency_List_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onHideDialog() {

    }

    //save data in local db one by one object from asynch task
    public class SaveIntoDatabase extends AsyncTask<dependencies,Void,Void> {
        // can use UI thread here
        private AsyncTask<dependencies, Void, Void> updateTask = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        // automatically done on worker thread (separate from UI thread)
        @Override
        protected Void doInBackground(dependencies... params) {
            dependencies dataModel = params[0];

            try {

                if(dataModel.getType().equalsIgnoreCase("IMAGE")) {
                    InputStream inputStream = new URL(dataModel.getCdn_path()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //  Bitmap bp=decodeUri(Uri.parse(dataModel.getPath()), 400);
                    //  pic.setImageBitmap(bp);
                    //set bitmap value to Picture
                    dataModel.setPicture(bitmap);
                }
                else{

                    Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.no_image);
                    dataModel.setPicture(bitmap);
                }
                //add data to database
                mDatabase = new Database(getApplicationContext());

                mDatabase.addData(dataModel);


            } catch (Exception e) {

                e.printStackTrace();

            }
            return null;
        }
    }


    //permission code
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");

       /* if (!addPermission(permissionsList, Manifest.permission.FLASHLIGHT))
            permissionsNeeded.add("Flash light");*/


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                /*showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;*/
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        // insertDummyContact();

    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DependencyListActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", okListener);
        builder.setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {

            permissionsList.add(permission);

            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))

                return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)

                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION

                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    // insertDummyContact();
                }
                else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showMessageOKCancel("Camera and Storage Permission required for this app",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                requestPermission();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.

                                                break;
                                        }
                                    }
                                });
                    }
                    //permission is denied (and never ask again is  checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        if(!checkPermission()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DependencyListActivity.this);
                            builder.setTitle("Permissions Settings");
                            builder.setMessage("If you want to download the dependency you need accept the permission...");
                            builder.setPositiveButton("go to Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    goToSettings();
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            builder.show();
                        }


                    }
                    //                            //proceed with logic by disabling the related features or quit the app.

                    // Permission Denied
                    //  Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();

                }
            }

            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + DependencyListActivity.this.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myAppSettings);
    }

    private boolean checkPermission() {

        int result1 = ContextCompat.checkSelfPermission(DependencyListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // int result1 = ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if ((result1 == PackageManager.PERMISSION_GRANTED)) {

            return true;

        }
        else {

            return false;

        }
        //permissions code

        // 31015594
    }
}