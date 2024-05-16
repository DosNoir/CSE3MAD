package com.example.cse3masassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewListAdapter extends ArrayAdapter<Review> {
    public ReviewListAdapter(@NonNull Context context, ArrayList<Review> data) {
        super(context, R.layout.list_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Review data = getItem(position);

        if(view==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView heading = view.findViewById(R.id.listName);
        TextView subHeading = view.findViewById(R.id.listSub);

        heading.setText(data.heading);
        subHeading.setText(data.body);
        return view;
    }
}
