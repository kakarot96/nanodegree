package com.example.kakarot.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    TextView textView;String overview;String posterpath;ImageView imageView;TextView title,releasedate;String titl,release;
    int vote;TextView voteavg;String votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textView=(TextView)findViewById(R.id.textView);
        imageView=(ImageView)findViewById(R.id.imageView);
        title=(TextView)findViewById(R.id.title);
        releasedate=(TextView)findViewById(R.id.releasedate);
        voteavg=(TextView)findViewById(R.id.vote);
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        overview=extras.getString("OVERVIEW");
        posterpath=extras.getString("POSTERPATH");
        titl=extras.getString("TITLE");
        release=extras.getString("RELEASEDATE");
        vote=extras.getInt("VOTEAVG");
        votes="User Rating: "+vote;

        textView.setText(overview);
        Picasso.with(getApplicationContext()).load(posterpath).into(imageView);
        title.setText(titl);
        releasedate.setText(release);
        voteavg.setText(votes);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
