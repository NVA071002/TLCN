package com.example.watchesstore.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.watchesstore.CheckOut;
import com.example.watchesstore.MenuSelection;
import com.example.watchesstore.MyCart;
import com.example.watchesstore.PurchasedHistory;
import com.example.watchesstore.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class UserProfile extends Fragment {

    private View mView;
    private ImageView imgAvatar;
    private EditText edUserName,edEmail,edPhoneNumber, edAddress;
    private Button btnUpdateUserProfile, btnHistory;
    private Uri seletedImage;
    private ProgressDialog dialog;


    private ActivityResultLauncher<Intent> mActivityResult=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK);
                    Intent i=result.getData();
                    if(i==null){
                        return;
                    }
                    seletedImage=i.getData();
                    imgAvatar.setImageURI(seletedImage);

                }
            });
    MenuSelection menuSelection=(MenuSelection) getActivity();
    public UserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_user_profile,container,false);
        initUI();
        setUserInformation();
        initListener();

        return mView;
    }

    private void initUI(){
        imgAvatar=(ImageView) mView.findViewById(R.id.imgAvatar);
        edUserName=mView.findViewById(R.id.edUserNameProfile);
        edEmail=mView.findViewById(R.id.edEmail);
        edAddress=mView.findViewById(R.id.edAddress);
        edPhoneNumber=mView.findViewById(R.id.edPhoneNumber);

        edEmail=mView.findViewById(R.id.edEmail);
        btnHistory=mView.findViewById(R.id.btnHistory);
        btnUpdateUserProfile=mView.findViewById(R.id.btnUpdateProfile);
        dialog=new ProgressDialog(getActivity());

    }
    private void initListener() {

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            //not logged login

        }
        else {
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                openClickRequestPermission(); dung update profile nhung da dung ImagePicker thay the.

                    ImagePicker.with(UserProfile.this)
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            //kiểm tra thử xem có ask permission trước không
                            //nếu không thì xem xét dùng lại hàm openClickRequestPermission
                            .createIntent(intent -> {
                                mActivityResult.launch(intent);
                                return null;
                            });

                }
            });
            btnUpdateUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickUpdateProfile();
                }
            });

            btnHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), PurchasedHistory.class);

                    startActivity(intent);
                }
            });
        }
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Toast.makeText(getActivity(), "Please Login first",
                    Toast.LENGTH_SHORT).show();        }
        dialog.show();
        //dieu chinh lai phan nay de hop ly khi nguoi dung chi click update ma khong
        //them bat cu thong tin gi
        if(seletedImage==null){
            Uri photoUrl = user.getPhotoUrl();
            seletedImage=photoUrl;
        }
        String userEmail=edEmail.getText().toString().trim();
        String updateUserName=edUserName.getText().toString().trim();
        String updatedAddress=edAddress.getText().toString().trim();
        String updatedPhone=edPhoneNumber.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(updateUserName)
                .setPhotoUri(seletedImage)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            updateUserInfoInFireStore(userEmail,updateUserName, updatedAddress, updatedPhone);
                            Toast.makeText(getActivity(), "Update Profile Success.",
                                    Toast.LENGTH_SHORT).show();
//                           //update fragment home( su dung FragmentResultListener)
                            Bundle result = new Bundle();
                            result.putString("bundleKey", "result");
                            getParentFragmentManager().setFragmentResult("requestKey", result);
                        }
                    }
                });
    }
    private void updateUserInfoInFireStore( String email, String userName, String address, String phoneNumber){
        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("userName", userName);
        userDetail.put("address", address);
        userDetail.put("phoneNumber", phoneNumber);

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Users").whereEqualTo("email",email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && !task.getResult().isEmpty()){
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentId  = documentSnapshot.getId();
                                fStore.collection("Users")
                                        .document(documentId)
                                        .update(userDetail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                    }
                });




    }

    private void setUserInformation() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user ==null){
            return;
        }
        else{
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("Users").document(user.getUid());


            docRef.addSnapshotListener( new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    edEmail.setText(value.getString("email"));
                    edUserName.setText(value.getString("userName"));
                    edPhoneNumber.setText(value.getString("phoneNumber"));
                    edAddress.setText(value.getString("address"));

                    Log.d("TEST",edUserName.getText().toString() );
                    Log.d("TEST",edEmail.getText().toString() );

                    System.out.println(edUserName.getText().toString());
                    System.out.println(edEmail.getText().toString());


                }
            });
//            edEmail.setText(user.getEmail());
//            edUserName.setText(user.getDisplayName());
            Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_light_user).into(imgAvatar);

        }
    }



//    public void setBitmapImage(Bitmap imageView){
//        imgAvatar.setImageBitmap(imageView);
//    }
//    private void openClickRequestPermission() {
//        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
//            openGallery();
//            return;
//        }
//        if(ActivityCompat.checkSelfPermission(getContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
//            openGallery();
//        }
//        else {
//            String [] permission={Manifest.permission.READ_EXTERNAL_STORAGE};
//            getActivity().requestPermissions(permission,MY_REQUEST_CODE);
//        }
//
//    }
//    public void openGallery() {
//        Intent i=new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);
//        mActivityResult.launch(Intent.createChooser(i,"Select Picture"));
//
//    }
}