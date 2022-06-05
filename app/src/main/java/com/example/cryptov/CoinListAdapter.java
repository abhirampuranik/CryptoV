package com.example.cryptov;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class CoinListAdapter extends ArrayAdapter<coinStructure> {

    private static final String TAG = "CoinListAdapter";
    Context mcontext;
    int mresource;

    public CoinListAdapter(Context context, int resource, ArrayList<coinStructure> objects){
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String value = getItem(position).getValue();
        String change = getItem(position).getChange();

        coinStructure coin_structure = new coinStructure(name, value, change);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.NameOfCoin);
        TextView tvValue = (TextView) convertView.findViewById(R.id.ValueOfCoin);
        TextView tvChange = (TextView) convertView.findViewById(R.id.ChangeOfValue);
        tvName.setHighlightColor(Color.parseColor("#ffffff"));
        if(Double.parseDouble(change) < 0)
        {
            tvValue.setTextColor(Color.parseColor("#eb0e0e"));
            tvChange.setTextColor(Color.parseColor("#eb0e0e"));
            tvChange.setText("▼"+'(' + change+"%" + ')');
        }else if (Double.parseDouble(change) > 0){
            tvValue.setTextColor(Color.parseColor("#36a832"));
            tvChange.setTextColor(Color.parseColor("#36a832"));
            tvChange.setText("▲"+'(' + change+"%" + ')');

        }else {
            tvValue.setTextColor(Color.parseColor("#a6a4a1"));
            tvChange.setTextColor(Color.parseColor("#a6a4a1"));
            tvChange.setText('(' + change+"%" + ')');
        }

        tvName.setText(name);
        tvValue.setText(value);


        return convertView;


    }
}
