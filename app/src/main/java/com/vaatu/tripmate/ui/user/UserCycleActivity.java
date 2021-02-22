package com.vaatu.tripmate.ui.user;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vaatu.tripmate.R;
//import com.vaatu.tripmate.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;


public class UserCycleActivity extends AppCompatActivity {

    //a doua activitate care nu face nimic, doar o folosim ca sa legam cele doua fragmenete, login si singup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cycle);

    }

}
