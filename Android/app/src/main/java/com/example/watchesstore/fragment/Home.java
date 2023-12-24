package com.example.watchesstore.fragment;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.watchesstore.R;
import com.example.watchesstore.ShowAllProduct;
import com.example.watchesstore.Signin;
import com.example.watchesstore.adapter.CategoryAdapter;
import com.example.watchesstore.adapter.ImageSlideHomeAdapter;
import com.example.watchesstore.adapter.ProductAdapter;
import com.example.watchesstore.models.Category;
import com.example.watchesstore.models.Photo;
import com.example.watchesstore.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class Home extends Fragment {

    private View mView;
    private ImageView imgAvatar;
    private TextView tvUserName, tvEmail;
    private Button btnSignOut;
    //dialog for loading
    ProgressDialog progressDialog;
    ConstraintLayout constraintLayout;
    //Home
    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private List<Photo> mListPhoto;
    private Handler mHandler=new Handler();
    private Runnable mRunnable;
    //Category recyclerview
    RecyclerView catRecyclerview;
    CategoryAdapter categoryAdapter;
    List<Category> categoryList;
    //Product Recyclerview
    RecyclerView productRecyclerView;
    ProductAdapter productAdapter;
    List<Product> productList;
    //viewAll button
    TextView catShowAll,popularShowAll;



    public Home(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_home,container,false);
        progressDialog=new ProgressDialog(getActivity());
        initUI();
        showUserInformation();
        initListener();
        //hide home layout
        constraintLayout.setVisibility(View.GONE);
        //news
        controlImageSlide();
        //control dialog
        progressDialog.setTitle("Welcome to my Store");
        progressDialog.setMessage("PLease wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //load info
        showCategory();
        showProduct();


        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                showUserInformation();
                // Do something with the result
            }
        });
        //Store position for resume
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable,2000);
    }

    private void initListener() {
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            //not logged login
            btnSignOut.setText("Sign In");
            Intent i=new Intent(getActivity(),Signin.class);

            startActivity(i);
        }
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(getActivity(),Signin.class);

                startActivity(i);


            }
        });
        catShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), ShowAllProduct.class);
                startActivity(i);
            }
        });
        popularShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), ShowAllProduct.class);
                startActivity(i);
            }
        });

    }

    private void initUI() {
        imgAvatar=mView.findViewById(R.id.imgAvatar);
        tvUserName=mView.findViewById(R.id.tvUserName);
        tvEmail=mView.findViewById(R.id.tvEmail);
        btnSignOut=mView.findViewById(R.id.btnSignOut);
        constraintLayout=mView.findViewById(R.id.mainHomeLayout);
        //see All
        catShowAll=mView.findViewById(R.id.category_see_all);
        popularShowAll=mView.findViewById(R.id.popular_see_all);
        //slideNewImages
        mRunnable=new Runnable() {
            @Override
            public void run() {
                if(mViewPager2.getCurrentItem()==mListPhoto.size()-1){
                    mViewPager2.setCurrentItem(0);
                }
                else{
                    mViewPager2.setCurrentItem(mViewPager2.getCurrentItem()+1);
                }
            }
        };
        mViewPager2=mView.findViewById(R.id.view_pager2);
        mCircleIndicator3=mView.findViewById(R.id.circle_indicator3);
        mListPhoto=getListPhoto();
        ImageSlideHomeAdapter adapter=new ImageSlideHomeAdapter(mListPhoto);
        mViewPager2.setAdapter(adapter);
        mCircleIndicator3.setViewPager(mViewPager2);
        //CatImage
        catRecyclerview=mView.findViewById(R.id.rec_category);
        catRecyclerview.setHasFixedSize(true);
        catRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        categoryList=new ArrayList<>(); //nguon
        categoryAdapter=new CategoryAdapter(getContext(),categoryList); //set nguon va man hinh hien thi cho adapter
        catRecyclerview.setAdapter(categoryAdapter);
        //Product
        productRecyclerView=mView.findViewById(R.id.recProduct);
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        productList=new ArrayList<>();
        productAdapter=new ProductAdapter(getContext(),productList);
        productRecyclerView.setAdapter(productAdapter);



    }

    private void controlImageSlide(){
        //control image Slider
        mHandler.postDelayed(mRunnable,2000);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable,2000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    private List<Photo> getListPhoto(){
        List<Photo> list =new ArrayList<>();
        list.add(new Photo(R.drawable.discount1));
        list.add(new Photo(R.drawable.discount2));
        list.add(new Photo(R.drawable.discount3));
        return list;
    }
    private void showCategory(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("CatImages");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Category cat=snapshot.getValue(Category.class);
                if(cat!=null){
                    categoryList.add(cat);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Category cat=snapshot.getValue(Category.class);
                if(cat ==null||categoryList==null||categoryList.isEmpty()){
                    return;
                }
                for (int i=0;i<categoryList.size();i++){
                    //dua vao name de thay doi image.
                    if(cat.getName()==categoryList.get(i).getName()){
                        categoryList.set(i,cat);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Category cat=snapshot.getValue(Category.class);
                if(cat ==null||categoryList==null||categoryList.isEmpty()){
                    return;
                }
                for (int i=0;i<categoryList.size();i++){
                    //dua vao name de thay doi image.
                    if(cat.getName()==categoryList.get(i).getName()){
                        categoryList.remove(i);
                        break;
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void showProduct(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Products");
        int count =0;
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product=snapshot.getValue(Product.class);
                if(product!=null){
                    if (productList.size()<5) {
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
                //show home layout and close dialog
                constraintLayout.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product=snapshot.getValue(Product.class);
                if(product ==null||productList==null||productList.isEmpty()){
                    return;
                }
                for (int i=0;i<productList.size();i++){
                    //dua vao name de thay doi image.
                    if(product.getId()==productList.get(i).getId()){
                        productList.set(i,product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Product product=snapshot.getValue(Product.class);
                if(product ==null||productList==null||productList.isEmpty()){
                    return;
                }
                for (int i=0;i<productList.size();i++){
                    //dua vao name de thay doi image.
                    if(product.getId()==productList.get(i).getId()){
                        productList.remove(i);
                        break;
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void showUserInformation(){

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            btnSignOut.setText("Sign In");
            tvEmail.setText("Please Log In");
            tvUserName.setText("For Better Experience");

        }
        else{
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            if(name==null){
                tvUserName.setText("Hello User");
            }
            else {
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(name);
            }

            tvEmail.setText(email);
            Glide.with(this).load(photoUrl).error(R.drawable.ic_light_user).into(imgAvatar);

        }
    }
}