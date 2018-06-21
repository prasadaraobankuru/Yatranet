package com.yatra.dependencies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class ImagePreviewActivity extends AppCompatActivity {
    String fileName;
    String offline_imgName;
    public int n;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_preview);

     /*   Bundle bundle = getIntent().getExtras();
        fileName = bundle.getString("imgName");*/

        fileName = getIntent().getExtras().getString("imgName");
        offline_imgName = getIntent().getExtras().getString("imgName");

        ImageView vwPreview = (ImageView)findViewById(R.id.imgPreview);

    /*    Picasso.with(this)
                .load(new File(fileName))
                .fit().centerCrop()
                .into(vwPreview);*/

    //load preview image
    try {

         Picasso.with(this)
                .load(fileName)
                .fit().centerCrop()
                .into(vwPreview);
    }
      catch (Exception e){

      }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabShare);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(fileName,getApplicationContext());
            }
        });




        //Bitmap myBitmap = BitmapFactory.decodeFile(fileName);
       // vwPreview.setImageBitmap(myBitmap);



    }

    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void shareImage(String url, final Context context) {

        if(NetworkUtility.isNetworkAvailable(context)) {

            try {
                Picasso.with(context).load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        //String photoURI = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), getLocalBitmapUri(bitmap, context));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            SaveImage(context, bitmap);
                            shareScreenshot(context);
                            // Marshmallow+
                        } else {
                            //below Marshmallow
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("image/*");
                            i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                            i.putExtra(Intent.EXTRA_TEXT, "Hey please check this application " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                            context.startActivity(Intent.createChooser(i, "Share Image"));
                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            } catch (Exception e) {
                Log.e("error", "Exception", e);
            }
        }

        else{

          /*  Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("image*//*");
            i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(offline_imgName, context));
            i.putExtra(Intent.EXTRA_TEXT, "Hey please check this application " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
            context.startActivity(Intent.createChooser(i, "Share Image"));*/
        }


    }
    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    public void SaveImage(Context ctx, Bitmap finalBitmap)
    {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void shareScreenshot(Context ctx)
    {
        String photoPath = Environment.getExternalStorageDirectory() + "/saved_images" + "/Image-" + n + ".jpg";
        File F = new File(photoPath);
        //Uri U = Uri.fromFile(F);
        //  Uri U = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", F);

        // TODO your package name as well add .fileprovider
        Uri U = FileProvider.getUriForFile(getApplicationContext(), "com.embedtechnologies.cameraposeguide.fileprovider", F);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/png");
        i.putExtra(Intent.EXTRA_STREAM, U);
        i.putExtra(Intent.EXTRA_TEXT, "Hey please check this application " + "https://play.google.com/store/apps/details?id=" + ctx.getPackageName());
        ctx.startActivity(Intent.createChooser(i, "Share Image"));
    }
}
