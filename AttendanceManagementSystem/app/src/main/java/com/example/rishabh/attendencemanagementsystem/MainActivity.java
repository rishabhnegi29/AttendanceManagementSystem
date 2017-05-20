package com.example.rishabh.attendencemanagementsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText u,p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        u=(EditText)findViewById(R.id.username);
        p=(EditText)findViewById(R.id.password);
    }
    public void login(View view)
    {
        String username = u.getText().toString();
        String password = p.getText().toString();
        String type = "login";
       // Toast.makeText(this,"welcome",Toast.LENGTH_LONG).show();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, username, password);

    }
}
