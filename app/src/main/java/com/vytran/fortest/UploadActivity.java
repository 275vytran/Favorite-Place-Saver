package com.vytran.fortest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class UploadActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    public Uri selected;
    ImageView imageView;
    EditText commentText;
    EditText nameText;
    EditText typeText;
    Button mapButton;
    FirebaseDatabase database;
    DatabaseReference myRef, myPrivateRef;
    FirebaseAuth mAuth;
    Upload upload;
    String folder_uuid;
    String pathToFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageView = findViewById(R.id.imageView);
        nameText = findViewById(R.id.nameText);
        commentText = findViewById(R.id.commentText);
        typeText = findViewById(R.id.typeText);
        mapButton = findViewById(R.id.mapButton);


        //To keep data when user navigate to map activity
        Intent intent = getIntent();
        nameText.setText(intent.getStringExtra("locationName"));
        typeText.setText(intent.getStringExtra("locationType"));
        commentText.setText(intent.getStringExtra("userComment"));

        if (intent.getStringExtra("address") == null)
            mapButton.setText("CHOOSE LOCATION");
        else
            mapButton.setText(intent.getStringExtra("address"));

        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads"); //the name of outside folder
        //Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("uploads");
        myPrivateRef = database.getReference("private_uploads");
        mAuth = FirebaseAuth.getInstance();
    }

    //Get the extension of file such as .jpg or .png
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void upload(View v) {

        if (selected != null) {
            String uuid = UUID.randomUUID().toString();
            String imageName = uuid + "." + getFileExtension(selected);
            final StorageReference storageReference = mStorageRef.child(imageName);

            storageReference.putFile(selected)
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return storageReference.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUrl = task.getResult().toString();
                                //Write to Firebase Database
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userEmail = user.getEmail();
                                String userComment = commentText.getText().toString();
                                String locationName = nameText.getText().toString();
                                String locationType = typeText.getText().toString();

                                Intent intent = getIntent();
                                String locationAddress = intent.getStringExtra("address");
                                String userLatitude = intent.getStringExtra("latitude");
                                String userLongitude = intent.getStringExtra("longitude");

                                if (userLatitude == null || userLongitude == null) {
                                    Toast.makeText(UploadActivity.this, "Please choose and save location", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                //Create unique Id to store block of email-comment-url of image
                                folder_uuid = UUID.randomUUID().toString();
                                upload = new Upload(folder_uuid, userEmail, locationName, locationType, locationAddress, userComment, downloadUrl, userLatitude, userLongitude);
                                //Upload on realtime database
                                new AlertDialog.Builder(UploadActivity.this)
                                        .setIcon(R.drawable.icon)
                                        .setTitle("Choose Modes")
                                        .setCancelable(true)
                                        .setMessage("- Public: Save and share on news feed\n- Private: Only save to list")
                                        .setPositiveButton("PUBLIC", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                myRef.child(folder_uuid).setValue(upload);
                                                Toast.makeText(UploadActivity.this, "Post is shared!", Toast.LENGTH_LONG).show();
                                                Intent myIntent = new Intent(UploadActivity.this, FeedActivity.class);
                                                startActivity(myIntent);
                                            }
                                        })
                                        .setNegativeButton("PRIVATE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                myPrivateRef.child(folder_uuid).setValue(upload);
                                                Toast.makeText(UploadActivity.this, "Post is saved to your list!", Toast.LENGTH_LONG).show();
                                                Intent myIntent = new Intent(UploadActivity.this, ListActivity.class);
                                                startActivity(myIntent);
                                            }
                                        })
                                        .show();

                            }
                        }
                    });
        }
        else {
            Toast.makeText(UploadActivity.this, "No file selected!", Toast.LENGTH_LONG).show();
        }

    }

    public void chooseLocation(View v) {
        Intent intent = new Intent(UploadActivity.this, MapsActivity.class);
        String locationName = nameText.getText().toString();

        if (!locationName.trim().equals(""))
            intent.putExtra("location_name", nameText.getText().toString());
        else
            intent.putExtra("location_name", "Unknown");

        intent.putExtra("location_type", typeText.getText().toString());
        intent.putExtra("user_comment", commentText.getText().toString());

        startActivity(intent);
        Toast.makeText(UploadActivity.this, "Long click on map to select the location", Toast.LENGTH_LONG).show();
    }

    public void cancel(View v) {
        Intent intent = new Intent(UploadActivity.this, FeedActivity.class);
        startActivity(intent);
    }

    public void selectImage(View v) {

        /*if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            //If user already has permission
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }*/

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setTitle("Choose Image From:")
                .setCancelable(true)
                .setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Check permission - if user has not have permission yet
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                        else {
                            //If user already has permission
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    }
                })
                .setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Check permission
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                        }
                        else {
                            pictureTakeAction();
                        }
                    }
                })
                .show();

    }

    //Handle requestCode

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //grant permission for user
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pictureTakeAction();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //pick image and pass URI of selected image to "selected"
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            //selected = data.getData();
            selected = data.getData();
            //Use picasso to show image
            Picasso.get().load(selected).into(imageView);
        }

        if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            selected = Uri.fromFile(new File(pathToFile));
            Picasso.get().load(selected).into(imageView);
            //Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            //imageView.setImageBitmap(bitmap);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pictureTakeAction() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(UploadActivity.this,
                        "com.vytran.fortest.fileprovider", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePic, 3);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
}
