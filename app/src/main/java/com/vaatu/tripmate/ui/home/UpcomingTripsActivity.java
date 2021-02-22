package com.vaatu.tripmate.ui.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vaatu.tripmate.R;
import com.vaatu.tripmate.data.remote.network.FirebaseDB;
import com.vaatu.tripmate.ui.home.addButtonActivity.AddBtnActivity;
import com.vaatu.tripmate.ui.user.SignUp;
import com.vaatu.tripmate.ui.user.UserCycleActivity;
import com.vaatu.tripmate.utils.TripModel;

//a treia activtate care tin in ea navigation drawerul (meniul) si fragmentele aferente acestuia (homeFragment, historyFragment)
public class UpcomingTripsActivity extends AppCompatActivity   {
    android.app.AlertDialog alert;
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDB fbdb;
    public static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_trips);


        Intent i = getIntent();
        String username = i.getStringExtra(SignUp.username);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //creaza o instanta de firebase si ia userul curent care e logat
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //Overlay Permission
        checkPermission();

        fbdb = FirebaseDB.getInstance();
        fbdb.saveUserToFirebase(currentUser.getEmail(), username);


        fab = findViewById(R.id.fab);

        //seteaza onclick pt butonul de + (fab)
        //porneste o noua activitate si asteaza un rezultat de la ea
        fab.setOnClickListener(view -> {
            Intent i1 = new Intent(UpcomingTripsActivity.this, AddBtnActivity.class);
            startActivityForResult(i1, 55);
        });




        // initializeaza meniul (cod generat)
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        //ia numele si emailul si le afisaza in meniu (navigation drawer)
        //todo de investigat de ce nu afisaza numele
        View vv = navigationView.getHeaderView(0);
        TextView userEmailTextView = vv.findViewById(R.id.userEmail);
        userEmailTextView.setText(currentUser.getEmail());
        TextView userNameTextView = vv.findViewById(R.id.userName);
        userNameTextView.setText(currentUser.getDisplayName());


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_sync, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            if (menuItem.getItemId() == R.id.nav_sync) {

                //todo de investigat cum functioneaza sync
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);

                        if (connected) {
                            Toast.makeText(UpcomingTripsActivity.this, "You are Connected and Data Updated", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(UpcomingTripsActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                return true;
            } else if (menuItem.getItemId() == R.id.nav_logout) {
                //delogare cand apesi pe logout
                signOut();
                //todo de investigat cum functioneaza lock_mode
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                Intent mainIntent = new Intent(UpcomingTripsActivity.this, UserCycleActivity.class);
                startActivity(mainIntent);
                finish();

                return true;
            } else if (menuItem.getItemId() == R.id.nav_home) {
                //Navigation here

                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_history) {

                //se duce in framnetul pentru history
                fab.hide();
                navController.navigate(R.id.action_HomeFragment_to_History);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                return true;
            }

            return true;
        });
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.upcoming_trips, menu);
//
//        return true;
//    }

    @Override
    protected void onResume() {
        super.onResume();

        fab.show();

    }

    //cod generat pentru navigation drawer (meniu)
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //dupa ce ne intoarcem din AddBtnActivity aici asteptam sa primim un TripModel ca sa il putem aduga in baza de date
        switch (requestCode) {
            //facem switch dupa codul rezultat din activitatea AddBtnActivity, daca e cod 55 luam tripModel primit din intent si il adaugam in firebase
            case (55): {
                if (resultCode == Activity.RESULT_OK) {
                    TripModel newtrip = (TripModel) data.getSerializableExtra("NEWTRIP");
                    if (newtrip != null) {
                        fbdb.saveTripToDatabase(newtrip);
                    } else {
                        //Toast.makeText(this, "Something wend wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            //todo de investigat
            case  RESULT_OK: {
                if (checkPermission()) {

                } else {
                    reqPermission();
                }
            }
        }
    }

    //face signout si currentu user devine null
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }


    //todo de investigat
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                reqPermission();
                return false;
            }
            else {
                return true;
            }
        }else{
            return true;
        }

    }


    //todo de investigat
    private void reqPermission(){
        final android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Screen overlay detected");
        alertBuilder.setMessage("Enable 'Draw over other apps' in your system setting.");
        alertBuilder.setPositiveButton("OPEN SETTINGS", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,RESULT_OK);
        });
        alert = alertBuilder.create();
        alert.show();
    }
}
