package com.example.kakarot.popularmovies2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MoviesDB extends Fragment {
    private ImageListAdapter mForecastAdapter;

    public MoviesDB() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
       // inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchMoviesTask moviesTask=new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(getString(R.string.pref_order_key),
                getString(R.string.pref_order_default));
        moviesTask.execute(order);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_movies_db, container, false);

      //  Movie one=new Movie();
       // one.setPoster_path("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg");


        Movie[] data={
        };
       List<Movie> weekForecast = new ArrayList<Movie>(Arrays.asList(data));

        mForecastAdapter =
                new ImageListAdapter(getActivity(),weekForecast);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
       gridView.setAdapter(mForecastAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    Movie movie = (Movie) mForecastAdapter.getItem(position);

                    String overview = movie.getOverview();
                    String posterpath = "http://image.tmdb.org/t/p/w342/" + movie.getPoster_path();
                    String title = movie.getTitle();
                    String releasedate = movie.getRelease_date();
                    int voteavg = movie.getVote_average();
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("OVERVIEW", overview);
                    extras.putString("POSTERPATH", posterpath);
                    extras.putString("TITLE", title);
                    extras.putString("RELEASEDATE", releasedate);
                    extras.putInt("VOTEAVG", voteavg);
                    intent.putExtras(extras);
                    startActivity(intent);
                }catch (ClassCastException c){}
        }});





        return rootView;
    }













    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();





        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Movie> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
           // final String OWM_LIST = "results";
           // final String OWM_DESCRIPTION = "poster_path";

           // JSONObject moviesJson = new JSONObject(moviesJsonStr);
          //  JSONArray moviesArray = moviesJson.getJSONArray(OWM_LIST);


            JSONObject mainObject = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = mainObject.getJSONArray("results");
            Movie[] resultStrs = new Movie[resultsArray.length()];
           // String[] posterpath=new String[resultsArray.length()];
            for (int i = 0; i < resultsArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                JSONObject indexObject = resultsArray.getJSONObject(i);
                Movie indexMovie = new Movie();
                indexMovie.setBackdrop_path("http://image.tmdb.org/t/p/w342/"+indexObject.getString("backdrop_path"));
                indexMovie.setId(indexObject.getInt("id"));
                indexMovie.setOriginal_title(indexObject.getString("original_title"));
                indexMovie.setOverview(indexObject.getString("overview"));
                indexMovie.setRelease_date(indexObject.getString("release_date"));
                indexMovie.setPoster_path("http://image.tmdb.org/t/p/w342/"+indexObject.getString("poster_path"));
                indexMovie.setPopularity(indexObject.getDouble("popularity"));
                indexMovie.setTitle(indexObject.getString("title"));
                indexMovie.setVote_average(indexObject.getInt("vote_average"));
                indexMovie.setVote_count(indexObject.getInt("vote_count"));

                resultStrs[i]=indexMovie; // Add each item to the list
               // posterpath[i]="http://image.tmdb.org/t/p/w185"+indexMovie.getBackdrop_path();

            }
            List<Movie> weekForecast2 = new ArrayList<>(Arrays.asList(resultStrs));
            for (Movie s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return weekForecast2;

        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+params[0]+"?";

                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "8bbd2f5a1f269e6fdbec51aabd92b747")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            super.onPostExecute(result);
            //  String[] result = new String[0];
            if (result != null) {
              mForecastAdapter.clear();
                for (Movie dayForecastStr : result) {
                    Log.v(LOG_TAG, "backdrop " + dayForecastStr);
                    mForecastAdapter.add(dayForecastStr);
                }
                mForecastAdapter.add(result);
            }
        }
    }


}
