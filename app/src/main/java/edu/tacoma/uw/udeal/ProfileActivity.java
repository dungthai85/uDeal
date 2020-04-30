package edu.tacoma.uw.udeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_toolbar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNav.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Person()).commit();
        }
        Fragment personfragment = new Person();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, personfragment).commit();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment personfragment = new Person();
//        fragmentTransaction.add(R.id.fragment_container, personfragment);
//        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            Intent h = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(h);
                            break;
                        case R.id.nav_camera:
                            Intent p = new Intent(ProfileActivity.this, PostActivity.class);
                            startActivity(p);
                            break;
                        case R.id.nav_inbox:
                            Intent i = new Intent(ProfileActivity.this, MessageInboxActivity.class);
                            startActivity(i);
                            break;
                        case R.id.nav_cart:
                            Intent c = new Intent(ProfileActivity.this, CartActivity.class);
                            startActivity(c);
                            break;
                        case R.id.nav_person:
                            break;
                    }
                    return false;
                }
            };
}