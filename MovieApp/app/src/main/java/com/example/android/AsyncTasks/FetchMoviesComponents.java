package com.example.android.AsyncTasks;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.Utilities.NetworkUtils;
import com.example.android.data.Movie;

import java.io.IOException;
import java.net.URL;

/*
*
* Async class for fetching a movies reviews and trailers
**
* */
public class FetchMoviesComponents extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchMoviesComponents.class.getSimpleName();
    private Movie movie;
    private String componentType;
    Context mcontext;

    public FetchMoviesComponents(Context context,Movie movie, String componentType){
        this.movie = movie;
        this.componentType = componentType;
        this.mcontext =context;
    }

    @Override
    protected String doInBackground(String... params) {

        if (params.length != 0) {
            return null;
        }
        try {
             URL requestURL= NetworkUtils.buildURL(mcontext,movie.getMovieId(),componentType);
             String response = NetworkUtils.getResponseFromHttpUrl(requestURL);
             return response;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result)
    {   if(componentType.equals("reviews")){
            movie.setReview(result);
        }else {
            movie.setTrailer(result);
        }

    }

}
