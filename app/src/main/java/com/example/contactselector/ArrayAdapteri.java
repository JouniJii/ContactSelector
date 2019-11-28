package com.example.contactselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ArrayAdapteri extends ArrayAdapter<listObject> {

    private LayoutInflater inflater;
    private ArrayList<listObject> items;
    private int item_layout;

    public ArrayAdapteri(@NonNull Context context, int resource, @NonNull ArrayList<listObject> objects) {
        super(context, resource, objects);

        this.item_layout = resource;
        this.items = objects;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    static class ViewHolder {
        TextView textView;
        CheckBox checkBox;

        public ViewHolder(View view) {
            this.textView = view.findViewById(R.id.textView);
            this.checkBox = view.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(item_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        listObject item = items.get(position);
        viewHolder.textView.setText(item.name);

        viewHolder.checkBox.setChecked(item.checked);
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.get(position).checked = items.get(position).checked ? false : true;
            }
        });

        return convertView;
    }
}
