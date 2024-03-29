package com.example.handymanadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfile extends AppCompatActivity {

    private TextInputLayout Userfullname, Useremailprofile, UserphoneNumberprofile, UserOccupationprofile, UserAboutprofile;
    private Button Saveprofile, Deleteprofile;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference userDbRef;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog progressDialog;
    private String uid;
    private static final String TAG = "EditProfile";
    private StorageTask mStorageTask;
    private StorageReference mStorageReference;
    private Uri uri;
    public static final int GALLERY_REQUEST = 100;
    String getImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Userfullname = findViewById(R.id.Txtprofilefullname);
        Useremailprofile = findViewById(R.id.TxtprofileEmail);
        UserphoneNumberprofile = findViewById(R.id.Txtprofilephonenumber);
        UserOccupationprofile = findViewById(R.id.TxtprofileOccupation);
        UserOccupationprofile.setEnabled(false);
        UserAboutprofile = findViewById(R.id.TxtprofileAbout);
        Saveprofile = findViewById(R.id.btnprofilesSave);
        Deleteprofile = findViewById(R.id.btnprofileDelete);
        profileImage = findViewById(R.id.profileimage);
        progressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Saveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Saveuserdetails();
            }

        });

        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();
        userDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userDbRef.keepSynced(true);
        mStorageReference = FirebaseStorage.getInstance().getReference("userPhotos");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        retrieveuserdetails();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(16, 16)
                        .start(EditProfile.this);

            }
        });

    }

    private void Saveuserdetails() {
        String ufullname = Userfullname.getEditText().getText().toString();
        String unumber = UserphoneNumberprofile.getEditText().getText().toString();
        String uoccupation = UserOccupationprofile.getEditText().getText().toString();
        String uabout = UserAboutprofile.getEditText().getText().toString();

        if ( ufullname.isEmpty() || unumber.isEmpty()|| uabout.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }


        HashMap<String, Object> userDetails = new HashMap<>();
        //userDetails.put("firstName", ufirstname);
       // userDetails.put("lastName", ulastname);
        userDetails.put("mobileNumber", unumber);
        userDetails.put("occupation", uoccupation);
        userDetails.put("about", uabout);
        userDetails.put("fullName", ufullname);

        progressDialog.setMessage("please wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mDatabaseReference.updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this, "Profile Successfully changed", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void retrieveuserdetails() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();
        userDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userDbRef.keepSynced(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String showfullname = (String) dataSnapshot.child("fullName").getValue();
                    String showEmail = (String) dataSnapshot.child("email").getValue();
                    String showNumber = (String) dataSnapshot.child("mobileNumber").getValue();
                    String showImage = (String) dataSnapshot.child("thumbImage").getValue();
                    String showOcc = (String) dataSnapshot.child("occupation").getValue();
                    String showAbout = (String) dataSnapshot.child("about").getValue();


                    //display the values into the required fields
                    Userfullname.getEditText().setText(showfullname);
                    Useremailprofile.getEditText().setText(showEmail);
                    UserOccupationprofile.getEditText().setText(showOcc);
                    UserphoneNumberprofile.getEditText().setText(String.valueOf(showNumber));
                    UserAboutprofile.getEditText().setText(showAbout);
                    Glide.with(getApplicationContext()).load(showImage).into(profileImage);
                } else {
                    Glide.with(getApplicationContext()).load(R.drawable.defaultavatar).into(profileImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(EditProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                uri = result.getUri();

                Glide.with(EditProfile.this).load(uri).into(profileImage);
                Log.i(TAG, "onActivityResult: " + uri);
                uploadFile();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void uploadFile() {
        if (uri != null) {
            progressDialog.setMessage("please wait ...");
            progressDialog.setCancelable(false);
            progressDialog.show();


            final File thumb_imageFile = new File(uri.getPath());

            //  compress image file to bitmap surrounding with try catch
            byte[] thumbBytes = new byte[0];
            try {
                Bitmap thumb_imageBitmap = new Compressor(this)
                        .setMaxHeight(130)
                        .setMaxWidth(13)
                        .setQuality(100)
                        .compressToBitmap(thumb_imageFile);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                thumbBytes = byteArrayOutputStream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //                file path for the image
            final StorageReference fileReference = mStorageReference.child(uid + "." + uri.getLastPathSegment());
            //                path for thumb_imageFile
//                creates another folder called thumb_images in the root directory which is the profile_images
            Log.i(TAG, "uploadFile: " + uri);
            fileReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        //throw task.getException();
                        Log.d(TAG, "then: " + task.getException().getMessage());

                    }
                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downLoadUri = task.getResult();
                        assert downLoadUri != null;
                        getImageUri = downLoadUri.toString();


                        Map<String, Object> updateThumb = new HashMap<>();
                        updateThumb.put("image", getImageUri);
                        updateThumb.put("thumbImage", getImageUri);

                        mDatabaseReference.updateChildren(updateThumb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast toast = Toast.makeText(EditProfile.this, "Successfully changed", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    Log.d(TAG, "onComplete: Image uploaded");


                                } else {
                                    progressDialog.dismiss();
                                    Toast toast = Toast.makeText(EditProfile.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    Log.d(TAG, "onComplete: Image uploading failed");
                                }

                            }

                        });

                    }

                }
            });
        }
    }
}
