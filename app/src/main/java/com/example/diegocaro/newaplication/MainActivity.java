package com.example.diegocaro.newaplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.newApplication.MESSAGE";
    private Button btnLastLocation, btnShowTransportAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLastLocation = (Button) findViewById(R.id.btnShowLastLocation);
        btnShowTransportAvailable = (Button) findViewById(R.id.btnShowTransportAvailable);
        final Context context = this;

        btnLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowLastPositionActivity.class);
                startActivity(intent);
            }
        });
        btnShowTransportAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowTransportListActivity.class);
                startActivity(intent);
            }
        });
    }
}
