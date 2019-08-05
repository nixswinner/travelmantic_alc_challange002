package com.nix.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nix.travelmantics.model.TravelDetails;

public class AdminActivity extends AppCompatActivity {

    private EditText edtName,edtCost,edtDesc;
    private Context context;

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String image_url=null;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("travelmantics");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("travelmantics_photos");
        mMessagesDatabaseReference.keepSynced(true);

        context = AdminActivity.this;
        edtCost = findViewById(R.id.edt_cost);
        edtName = findViewById(R.id.edt_name);
        edtDesc = findViewById(R.id.edt_desc);

        imageView = findViewById(R.id.imageView);

        Button selectImage = findViewById(R.id.btn_select_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

            }
        });

    }

    public void save(){
        if (!edtName.getText().toString().isEmpty()){
            if (!edtCost.getText().toString().isEmpty()){
                if (!edtDesc.getText().toString().isEmpty()){

                   if (image_url !=null){
                       //save
                       TravelDetails travelDetails = new TravelDetails(edtName.getText().toString(),
                               edtCost.getText().toString(),edtDesc.getText().toString(),image_url);
                       mMessagesDatabaseReference.push().setValue(travelDetails);
                       finish();
                       Toast.makeText(context,"Saved Successfully",
                               Toast.LENGTH_SHORT).show();
                   }else {
                       Toast.makeText(context,"Kindly select image",
                               Toast.LENGTH_SHORT).show();
                   }
                }else {
                    edtDesc.setError("Provide Description");
                }
            }else {
                edtName.setError("Provide Cost");
            }
        }else {
            edtName.setError("Provide Name");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_CANCELED){
                //Toast.makeText(this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri).addOnSuccessListener((Activity) context, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("Uplaod", "onSuccess: "+uri );
                           //show image
                            image_url = String.valueOf(uri);

                            Glide.with(context)
                                    .load(image_url)
                                    .centerCrop()
                                    .into(imageView);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
         if (id == R.id.action_save) {
            save();
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(AdminActivity.this,UserActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
