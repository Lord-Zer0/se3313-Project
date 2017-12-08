package ca.uwo.eng.se3313.project.scavenggerhunt;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class InGameActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextView wordView;

    private Button btnCamera;

    String mCurrentPhotoPath;

    private ImageView mImageView;

    VisualRecognition watty;

    OkHttpClient httpClient;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingame_view);
        System.out.println("Sup");

        wordView = findViewById(R.id.wordText);
        btnCamera = findViewById(R.id.btnCamera);
        mImageView = findViewById(R.id.mImageView);
        watty = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20
        );
        watty.setApiKey("0d1c6b7800efff5a855e45c0638cfaa778d186ca");
        url = "http://54.167.215.132:2010";

        RequestTask requestTask = new RequestTask();

        requestTask.execute("yaaasssss");
    }

    class RequestTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String...urls) {
            try {
                Socket echoSocket = new Socket("54.167.215.132", 2010);

                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);

                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));

                out.println("GET /game/123456 ");

                // Consume the initial welcoming messages from the server
                return in.readLine();

            } catch (Exception e) {
                this.exception = e;
                System.out.println(this.exception);
                return null;
            }
        }

        protected void onPostExecute(String result) {
            System.out.println(result);

        }
    }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void btnCameraClick(View view) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);
            }
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    System.out.println(ex.getLocalizedMessage());
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    System.out.println(getContentResolver().getType(photoURI));
                    Cursor returnCursor =
                            getContentResolver().query(photoURI, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    System.out.println((returnCursor.getString(nameIndex)));
                    System.out.println((Long.toString(returnCursor.getLong(sizeIndex))));

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
  //  };
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //This happens after they take a picture.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if the picture is a thing, start watson AsyncTask.
        if (mCurrentPhotoPath != null) {
            RetrieveClassificationTask wattyTask = (RetrieveClassificationTask) new RetrieveClassificationTask().execute("Because I had to.");
        }
    }

    //this does a thing and then ships it to IBM. It returns what Watty boy thinks it is.
    class RetrieveClassificationTask extends AsyncTask<String, Void, ClassifiedImages> {

        private Exception exception;

        protected ClassifiedImages doInBackground(String...urls) {
            try {
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                // Determine how much to scale down the image
                int scaleFactor = 10;

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

                //get rid of that garb because aint nobody got space for dat
                mCurrentPhotoPath = null;

                //compress it to yummy bites for watty
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                        .imagesFile(bs)
                        .imagesFilename("scavengerhunt.png")
                        .build();

                //ship it to watty
                return watty.classify(classifyOptions).execute();
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(ClassifiedImages result) {
            //lets see what he says the picture was of ;)
            System.out.println(result);

        }
    }
}
