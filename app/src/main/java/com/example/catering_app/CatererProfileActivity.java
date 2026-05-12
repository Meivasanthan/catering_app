package com.example.catering_app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CatererProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caterer_profile);
        Toast.makeText(this, "Owner Profile - Coming Soon", Toast.LENGTH_SHORT).show();
    }
}