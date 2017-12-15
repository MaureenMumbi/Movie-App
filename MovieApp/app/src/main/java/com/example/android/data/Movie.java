package com.example.android.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mauryn on 10/10/2017.
 */

public class Movie implements Parcelable{
    public String title;
    public String overview;
    public String releasedate;
    public String vote_average;
    public String posterpath;
    public int ID;
    public String review;
    public String trailer;


//    private int movieId;
//    private String title;
//    private String poster;
//    private String overview;
//    private String voteAverage;
//    private String releaseDate;
//    private String movieReviews;
//    private String moviePreviews = "";

    public Movie(int movieId, String title, String poster, String overview,
                 String voteAverage, String releaseDate){
        this.ID = movieId;
        this.title = title;
        this.posterpath = poster;
        this.overview = overview;
        this.vote_average = voteAverage;
        this.releasedate = releaseDate;
    }

    public int getMovieId() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return posterpath;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return releasedate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String reviews) {
        review = reviews;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String movieTrailers) {
        trailer = movieTrailers;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);
        out.writeString(title);
        out.writeString(posterpath);
        out.writeString(overview);
        out.writeString(vote_average);
        out.writeString(releasedate);
        out.writeString(review);
        out.writeString(trailer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel in) {
        ID = in.readInt();
        title = in.readString();
        posterpath = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        releasedate = in.readString();
        review = in.readString();
        trailer = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}






//    protected Movie(Parcel in) {
//        title = in.readString();
//        overview = in.readString();
//        releasedate = in.readString();
//        vote_average = in.readDouble();
//        posterpath = in.readString();
//        ID = in.readInt();
//        review = in.readString();
//        trailer = in.readString();
//    }
//
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(title);
//        dest.writeString(overview);
//        dest.writeString(releasedate);
//        dest.writeDouble(vote_average);
//        dest.writeString(posterpath);
//        dest.writeInt(ID);
//        dest.writeString(review);
//        dest.writeString(trailer);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
//        @Override
//        public Movie createFromParcel(Parcel in) {
//            return new Movie(in);
//        }
//
//        @Override
//        public Movie[] newArray(int size) {
//            return new Movie[size];
//        }
//    };
//
//    public int getId() {
//        return ID;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getImage() {
//        return posterpath;
//    }
//
//
//
//    public String getOverview() {
//        return overview;
//    }
//
//    public double getRating() {
//        return vote_average;
//    }
//
//    public String getDate() {
//        return releasedate;
//    }
//
//
//}
