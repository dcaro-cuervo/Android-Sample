package com.example.diegocaro.newaplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego.caro on 17/03/2016.
 */
public class TransportAdapter extends BaseAdapter {
    private Context _context;
    List<Transport> transportList;

    public TransportAdapter(Context context, List<Transport> transports) {
        this._context = context;
        this.transportList = transports;
    }

    @Override
    public int getCount() {
        return transportList.size();
    }

    @Override
    public Transport getItem(int position) {
        return transportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_adapter_transport, parent, false);
        }

        TextView txtTName = (TextView) convertView.findViewById(R.id.txtTransportName);
        TextView txtTPhone = (TextView) convertView.findViewById(R.id.txtTransportPhone);

        Transport transport = transportList.get(position);

        txtTName.setText(transport.getName());
        txtTPhone.setText(transport.getPhone());

        return convertView;
    }
}