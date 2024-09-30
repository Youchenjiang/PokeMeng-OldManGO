package com.PokeMeng.OldManGO.medicined;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.PokeMeng.OldManGO.R;
import com.google.firebase.FirebaseApp;
import com.PokeMeng.OldManGO.medicined.ui.home.BlankFragment;
import com.google.firebase.FirebaseApp;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseApp.initializeApp(this);


        if (savedInstanceState == null) {
            // 加載 BlankFragment
            Fragment fragment = new BlankFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
    }
}
