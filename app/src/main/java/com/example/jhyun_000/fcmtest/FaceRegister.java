package com.example.jhyun_000.fcmtest;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.jhyun_000.fcmtest.EmailPasswordActivity.user_email;

/**
 * Created by jhyun_000 on 2018-04-02.
 */

public class FaceRegister extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static int PERMISSION_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    Button button;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    TextView text1;
    TextView text2;
    TextView text3;
    String mCurrentPhotoPath;
    int threadNumber;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_ALBUM_MULTIPLE = 2;
    private Uri mImageCaptureUri;

    Uri imageuris[] = new Uri[3];
    String imageuuids[] = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_register);

        init();

        AWSMobileClient.getInstance().initialize(this).execute();

        permissionCheck();

//        dowloadData();
//        uploadData();
    }

    public void init() {
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
    }

    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE);
        }
    }

//    public File getPublicAlbumStorageDir(String albumName) {
//        // Get the directory for the user's public pictures directory.
//        Log.d("directory", "env dir : " + Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES));
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), albumName);
//        if (!file.mkdirs()) {
//            Log.e("error", "Directory not created");
//        }
//        return file;
//    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    //download
    public void dowloadData() {
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                downloadWithTransferUtility();
            }

//            @Override
//            public void onComplete() {
//                downloadWithTransferUtility();
//            }
        }).execute();
    }

    public void downloadWithTransferUtility() {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        TransferObserver downloadObserver =
                transferUtility.download(
                        "public/example-image.png",
//                        getPublicAlbumStorageDir("example-images.png"));
//                        new File("C:\\Users\\jhyun_000\\Desktop\\example-image.png"));
//                        new File(getApplicationContext().getFilesDir(), "example-image.png"));
                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "example-image.png"));

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    Log.d("directory", String.valueOf(getApplicationContext().getFilesDir()));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("MainActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.d("Error", ex.getMessage());
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == downloadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("YourActivity", "Bytes Transferrred: " + downloadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + downloadObserver.getBytesTotal());
    }


    //upload
//    public void uploadData(){
//        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
//            @Override
//            public void onComplete(AWSStartupResult awsStartupResult) {
//                uploadWithTransferUtility();
//            }
//        }).execute();
//    }
//
//    public void uploadWithTransferUtility() {
//
//        TransferUtility transferUtility =
//                TransferUtility.builder()
//                        .context(getApplicationContext())
//                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
//                        .build();
//
//        TransferObserver uploadObserver =
//                transferUtility.upload(
//                        "uploads/s3Key.jpg",
////                        new File("/path/to/file/localFile.txt"));
//                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "example-image.png"));
//
//        // Attach a listener to the observer to get state update and progress notifications
//        uploadObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    // Handle a completed upload.
//                }
//            }


    public void uploadData(final Uri uri, final String name) {
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                uploadWithTransferUtility(uri, name);
            }
        }).execute();
    }

    public void uploadWithTransferUtility(Uri uri, final String uuid) {

        String replaced = user_email.replaceAll("[@.]", "-");
        final String madeKey = replaced + "/user/" + uuid + ".jpg";

//        final AmazonS3Client s3client = new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider());
//        s3client.setObjectAcl("androidprojectapp-userfiles-mobilehub-1711223959", madeKey, CannedAccessControlList.PublicRead);
//        AccessControlList acl = new AccessControlList();
//        acl.grantPermission(CannedAccessControlList.PublicRead);


//        AmazonS3Client s3Clcient = new AmazonS3Client();

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
//                        .s3Client(s3client)
                        .build();

//        .setObjectAcl("androidprojectapp-userfiles-mobilehub-1711223959", madeKey, CannedAccessControlList.PublicRead)

        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, uri);

            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(this, uri);
//
//        Toast.makeText(this, user_email, Toast.LENGTH_SHORT).show();
//        String replaced = user_email.replace("@.", "-");
//        String replaced = user_email.replace("[@.]", "-");
//        String replaced = user_email.replaceAll("[@.]", "-");
//        String madeKey = replaced + "/user/" + uuid + ".jpg";
        Toast.makeText(this, madeKey, Toast.LENGTH_SHORT).show();

        TransferObserver uploadObserver =
//                transferUtility.upload(
////                        "uploads/"+name+".jpg",
////                        user_email+"/user/"+uuid+".jpg",
//                        madeKey,
////                        "uploads/"+uuid+".jpg",
////                        new File("/path/to/file/localFile.txt"));
////                        new File(uri.getPath()));
////                        new File(getRealPathFromURI(uri)));
//                        new File(realPath));
////                        new File(realPath),
////                        CannedAccessControlList.PublicRead);
//                transferUtility.upload("androidprojectapp-userfiles-mobilehub-1711223959", madeKey, new File(realPath), CannedAccessControlList.PublicRead);
                transferUtility.upload("androidprojectapp-userfiles-mobilehub-1711223959", madeKey, new File(realPath));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
//                    Log.i("onstatechanged", "upload completed.");
//                    s3client.setObjectAcl("androidprojectapp-userfiles-mobilehub-1711223959", madeKey, CannedAccessControlList.PublicRead);

//                    sendRegisterHttp(user_email, "user", imageuuids);
                    threadNumber--;

                    if (threadNumber == 0) {
                        // some function. ->send to server
                        Log.i("s3", "s3 3 all finished");
                        sendRegisterHttp(user_email, "user", imageuuids);
                    }


                    Log.i("s3", uuid + "upload completed.");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

//    private void dispatchTakePictureIntent(){
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap)extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }

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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void doTakeAlbumAction() {    //앨범에서 이미지 가져오기
        //앨범 호출
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, PICK_FROM_ALBUM);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //multiple
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

//        startActivityForResult(intent, PICK_FROM_ALBUM);
        startActivityForResult(intent, PICK_FROM_ALBUM_MULTIPLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
//            case PICK_FROM_ALBUM:
//
//                mImageCaptureUri = data.getData();
//                imageView1.setImageURI(mImageCaptureUri);
//                 text1.setText(mImageCaptureUri.toString());
//                 uploadData(mImageCaptureUri);
//            break;

            case PICK_FROM_ALBUM_MULTIPLE:
                ClipData clipData = data.getClipData();
                int min = imageuris.length;
                if (min > clipData.getItemCount()) {
                    min = clipData.getItemCount();
                }
                for (int i = 0; i < min; i++) {
//                    for(int i =0; i<clipData.getItemCount(); i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    imageuris[i] = uri;
                    imageuuids[i] = UUID.randomUUID().toString();
                }
                imageView1.setImageURI(imageuris[0]);
                imageView2.setImageURI(imageuris[1]);
                imageView3.setImageURI(imageuris[2]);

//                 upload 주석처리해놨엉
//                uploadData(imageuris[0], String.valueOf(0001));
//                uploadData(imageuris[1], String.valueOf(0002));
//                uploadData(imageuris[2], String.valueOf(0003));

                threadNumber = 3;
                uploadData(imageuris[0], imageuuids[0]);
                uploadData(imageuris[1], imageuuids[1]);
                uploadData(imageuris[2], imageuuids[2]);

//                S3Async task = new S3Async();

//                task.execute();

//                text1.setText(imageuuids[0]);
//                text2.setText(imageuuids[1]);
//                text3.setText(imageuuids[2]);

//                sendRegisterHttp(user_email, "user", imageuuids);

                break;

        }
    }

//    private String getRealPathFromURI(Uri contentURI) {               //이미지하나였을땐 이걸로되섯는데
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
////            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
////            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }


    void sendRegisterHttp(final String email, final String register_type, final String[] uuids) {
        new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,
                        "{\"email\": \"" + email + "\", \"designation\": \"" + register_type + "\", " +
                                "\"uuid\" : [" +
                                "\"" + uuids[0].toString() + "\", \"" + uuids[1].toString() + "\", \"" + uuids[2].toString() + "\"]}");
                Log.d("sendRegisterHttp", "Body : " + body);
                Log.d("sendRegisterHttpString", "Body : " + "{\"email\": \"" + email + "\", \"designation\": \"" + register_type + "\", " +
                        "\"uuid\" : [" +
                        "\"" + uuids[0].toString() + "\", \"" + uuids[1].toString() + "\", \"" + uuids[2].toString() + "\"]}");
//                {
//                    "email" : "jh@mgmailc.om",
//                    "designation" : "user",
//                    "uuid" : [
//                            "uuid1lkjslkdfjsdlkfjsdklfjskldsjfkl",
//                                    "uuid1lkjslkdfjsdlkfjsdklfjskldsjfkl"
//                    "uuid1lkjslkdfjsdlkfjsdklfjskldsjfkl"
//                            ]
//                }

                Request request = new Request.Builder()
                        .url(getString(R.string.server_url_face_register))
                        .post(body)
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public class S3Async extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            uploadData(imageuris[0], imageuuids[0]);
            uploadData(imageuris[1], imageuuids[1]);
            uploadData(imageuris[2], imageuuids[2]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("PostExecute", "onpostexecuted");
            text1.setText(imageuuids[0]);
            text2.setText(imageuuids[1]);
            text3.setText(imageuuids[2]);

        }

    }
}