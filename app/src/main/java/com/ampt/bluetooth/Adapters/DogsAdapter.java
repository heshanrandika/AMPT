package com.ampt.bluetooth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ampt.bluetooth.AsyncTask.LoadImage;
import com.ampt.bluetooth.R;
import com.ampt.bluetooth.database.model.DogsData;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by malith on 8/6/15.
 */
public class DogsAdapter extends ArrayAdapter<DogsData> {

    private Context context;
    private int layout;
    private List<DogsData> dogs;

    public DogsAdapter(Context con, int resource, List<DogsData> dogsDataList) {
        super(con, resource, dogsDataList);
        context = con;
        layout = resource;
        dogs = dogsDataList;

    }

    @Override
    public DogsData getItem(int position) {
        return dogs.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.dogName = (TextView) convertView.findViewById(R.id.dog_row_dogs_name);
            viewHolder.dogImage = (ImageView) convertView.findViewById(R.id.dogs_row_dogs_image);
            viewHolder.info = (ImageButton) convertView.findViewById(R.id.dog_row_info_image_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DogsData item = getItem(position);
        if (item != null) {
            viewHolder.dogName.setText(item.getName());
            long imageID = Long.parseLong(item.getImageID());
            LoadImage li = new LoadImage(context);
            try {
                viewHolder.dogImage.setImageBitmap((li.execute(imageID).get()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView dogName;
        private ImageView dogImage;
        private ImageButton info;
    }
}

