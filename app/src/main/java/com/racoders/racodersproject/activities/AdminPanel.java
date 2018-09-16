
package com.racoders.racodersproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.ViewPagerAdapter;
import com.racoders.racodersproject.fragments.AddNews;
import com.racoders.racodersproject.fragments.AdminNews;
import com.racoders.racodersproject.fragments.AdminProfile;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class AdminPanel extends AppCompatActivity {
    private boolean isValid = false;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                AddNews.getmImage().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 0);
                    }
                });
            }else
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Crop.of(source_uri, destination_uri).asSquare().start(this);
                AddNews.getmImage().setImageURI(Crop.getOutput(data));

            }
            else if (requestCode == Crop.REQUEST_CROP){
                handle_crop(resultCode, data);
            }
        }
    }
    public void handle_crop(int code, Intent data){

        if(code == RESULT_OK){
            AddNews.getmImage().setImageURI(Crop.getOutput(data));
        }
        else if (code == Crop.RESULT_ERROR){
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setStatusBarColor(getResources().getColor(R.color.LightBlue));
            setTitle("Admin Panel");
        }
        adapter.addFragment(new AdminNews(), "");
        adapter.addFragment(new AddNews(), "");
        adapter.addFragment(new AdminProfile(), "");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);

        tabLayout.getTabAt(0).setIcon(R.drawable.tablayout_feed_icon_admin);
        tabLayout.getTabAt(1).setIcon(R.drawable.tablayout_map_icon_admin);
        tabLayout.getTabAt(2).setIcon(R.drawable.tablayout_person_icon_admin);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void signOut(View view){

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), toggleLoginActivity.class));
        finish();

    }

    public void toLocationsMap(View view){

        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        startActivity(new Intent(getApplicationContext(), AdminMap.class).putExtra("id", id));

    }
}
