package com.example.inspire_section.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.inspire_section.MainActivity;
import com.example.inspire_section.Model.InspireModelClass;
import com.example.inspire_section.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class UploadYourStoryActivity extends AppCompatActivity {
    VideoView videoView;
    Button choose_btn,upload_btn;
    Uri videoUrl;
    MediaController mediaController;
    final  int REQUEZT_Video_Code=101;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    EditText et_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_your_story);
        initss();
        videoView.setMediaController(mediaController);
        videoView.start();
        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent iotent=new Intent();
                                iotent.setType("video/*");
                                iotent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(iotent,REQUEZT_Video_Code);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent,REQUEZT_Video_Code);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoUrl!=null) {
                    PROCESSVIDEOUPLOAD();
                }
                else
                {
                    Toast.makeText(UploadYourStoryActivity.this, "please select data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public String Extension()
    {
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(videoUrl));
    }

    private void PROCESSVIDEOUPLOAD() {
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("media uploader");
        progressDialog.show();
        StorageReference uploadder=storageReference.child("uploadss/"+System.currentTimeMillis()+"."+Extension());
        uploadder.putFile(videoUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                InspireModelClass modelClass=new InspireModelClass(et_title.getText().toString(),uri.toString());
                                databaseReference.child(databaseReference.push().getKey()).setValue(modelClass)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(UploadYourStoryActivity.this, "sucessfully upload", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(UploadYourStoryActivity.this, "uploading failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });



                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float per=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("uploaded : "+(int)per+"%");

                    }
                });
    }

    private void initss() {
        videoView=(VideoView)findViewById(R.id.ins_video);
        choose_btn=(Button)findViewById(R.id.btn_choose_vedio);
        upload_btn=(Button)findViewById(R.id.btn_upload_vedio);
        et_title=(EditText)findViewById(R.id.et_inspire_title);
        mediaController=new MediaController(UploadYourStoryActivity.this);
        //storege type castig
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("uploadvideo");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEZT_Video_Code && resultCode==RESULT_OK)
        {
            videoUrl=data.getData();
            videoView.setVideoURI(videoUrl);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(UploadYourStoryActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}