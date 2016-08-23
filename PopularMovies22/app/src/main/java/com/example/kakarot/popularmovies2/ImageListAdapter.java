package com.example.kakarot.popularmovies2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by kakarot on 8/23/2016.
 */
public class ImageListAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater inflater;

    private String[] imageUrls;

    public ImageListAdapter(Context context, String[] imageUrls) {
        super(context, R.layout.grid_item_movie, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
        }

        Picasso
                .with(context)
                .load(imageUrls[position])
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }

}
