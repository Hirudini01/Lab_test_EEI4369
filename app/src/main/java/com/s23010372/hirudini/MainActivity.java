package com.s23010372.hirudini;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();

                    // Save both username and password
                    boolean isInserted = dbHelper.insertData(username, password);

                    if (isInserted) {
                        Toast.makeText(MainActivity.this, "Username and Password saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Save failed!", Toast.LENGTH_SHORT).show();
                    }

                    // Navigate to MapsActivity
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }
            }
        });
    }

    private boolean validateInput() {
        if (etUsername.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Username", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
