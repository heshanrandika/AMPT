package com.ampt.bluetooth.AsyncTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.database.helper.DatabaseHelper;

/**
 * Created by malith on 8/4/15.
 */
public class LoadImage extends AsyncTask<Integer, Void, Bitmap> {
    private Context context;

    public LoadImage(Context con) {
        this.context = con;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        int id = params[0];
        DatabaseHelper dbh = new DatabaseHelper(context);
        byte[] imageBytes = dbh.getImage(id);
        if(null==imageBytes){
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.dog_profile);
        }
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }
}
