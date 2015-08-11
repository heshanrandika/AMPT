package com.ampt.bluetooth.Util;

import android.content.Context;
import android.widget.ImageView;

import com.ampt.bluetooth.AsyncTask.LoadImage;

import java.util.concurrent.ExecutionException;

/**
 * Created by malith on 8/11/15.
 */
public class ImageSetter {

    public static void setImage(Context context, ImageView iv,Long imageid ) {

        LoadImage li = new LoadImage(context);
        try {
            System.out.println("going to the image");
            iv.setImageBitmap(li.execute(imageid).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
