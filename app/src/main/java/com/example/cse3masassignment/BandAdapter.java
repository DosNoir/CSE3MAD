package com.example.cse3masassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BandAdapter extends ArrayAdapter<Band> {

    // Adapts bands from ArrayList to ListView

    private Context mContext;
    private List<Band> bandList;

    public BandAdapter(Context context, List<Band> list) {
        super(context, 0, list);
        mContext = context;
        bandList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        Band currentBand = bandList.get(position);

        TextView name = listItem.findViewById(R.id.listName);
        name.setText(currentBand.getBandName());

        TextView info = listItem.findViewById(R.id.listSub);
        info.setText(currentBand.getBandWebsite());

        return listItem;
    }
}
