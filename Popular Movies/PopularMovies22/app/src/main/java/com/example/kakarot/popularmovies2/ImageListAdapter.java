package com.example.kakarot.popularmovies2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kakarot on 8/23/2016.
 */
public class ImageListAdapter extends ArrayAdapter {

    private Context context;
    private String back;
    private LayoutInflater inflater;

    private List<Movie> imageUrls;

    public ImageListAdapter(Context context, List<Movie> imageUrls) {
        super(context, R.layout.grid_item_movie, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }


    public String getItemID(int position) {

        back=imageUrls.get(position).getPoster_path();
        return  back;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
        }

        try{
            Picasso
                    .with(context)
                    .load(getItemID(position))
                    .fit() // will explain later
                    .into((ImageView) convertView);
        }
        catch(ClassCastException c){

        }

        return convertView;
    }

}
