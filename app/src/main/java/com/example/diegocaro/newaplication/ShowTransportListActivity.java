package com.example.diegocaro.newaplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class ShowTransportListActivity extends AppCompatActivity {
    private TransportDbHelper db;
    private List<Transport> transportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transport_list);

        // init the helper and set ddbb
        db = new TransportDbHelper(this);
        db.onDowngrade(db.getWritableDatabase(), db.DATABASE_VERSION, db.DATABASE_VERSION);

        // create and manipulate Transport.
        loadList();
        createAndLoadListView();
    }

    /**
     * Create list of Transport
     */
    private void loadList() {
        // Inserting Transport
        db.addTransport(new Transport("Taxi 1", "3443434"));
        db.addTransport(new Transport("Taxi 2", "3443434"));
        db.addTransport(new Transport("Taxi 3", "3443434"));
        db.addTransport(new Transport("Taxi 4", "3443434"));
        db.addTransport(new Transport("Taxi 5", "3443434"));
        db.addTransport(new Transport("Remis 1", "3443434"));
        db.addTransport(new Transport("Remis 2", "3443434"));
        db.addTransport(new Transport("Remis 3", "3443434"));
        db.addTransport(new Transport("Remis 4", "3443434"));
        db.addTransport(new Transport("Remis 5", "3443434"));

        transportList = db.getAllTransport();
    }

    /**
     * Insert elements in ListView and asociate OnItemClick
     */
    private void createAndLoadListView() {
        final ListView mainListView;
        TransportAdapter transportAdapter;

        // search the component and insert the list
        mainListView = (ListView) findViewById(R.id.transportListView);
        transportAdapter = new TransportAdapter(this, transportList);
        mainListView.setAdapter(transportAdapter);

        // associed the event with the listbox.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Transport transport = (Transport) mainListView.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), transport.getName(), Toast.LENGTH_SHORT).show();

                // init INTENT to call the number selected.
                Uri phoneNumber = Uri.parse("tel:" + transport.getPhone());
                Intent callIntent = new Intent(Intent.ACTION_CALL, phoneNumber);
                startActivity(callIntent);
            }
        });
    }
}
