package com.ampt.bluetooth.activities.dialogs;

/**
 * Created by Heshanr on 10/11/2015.
 */

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ampt.bluetooth.AsyncTask.LoadImage;
import com.ampt.bluetooth.R;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.DogsData;

import java.util.concurrent.ExecutionException;


/**
 * Created by malith on 10/10/15.
 */
public class ViewDogDialog extends Dialog {
    private DogsData dogsData;
    private Context context;
    EditText name;
    EditText breed;
    EditText dob;
    EditText age;
    ImageView dogImage;
    RadioGroup radioSexGroup;
    RadioButton male;
    RadioButton female;

    public ViewDogDialog(final Context con, DogsData data) {
        super(con);
        this.dogsData = data;
        this.context = con;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.edit_dog_info_dialog_layout);
        this.setCancelable(false);

        dogImage = (ImageView) this.findViewById(R.id.edit_dog_info_dog_image);
        name = (EditText) this.findViewById(R.id.edit_dog_info_name_et);
        breed = (EditText) this.findViewById(R.id.edit_dog_info_breed_et);
        dob = (EditText) this.findViewById(R.id.edit_dog_info_dob_et);
        age = (EditText) this.findViewById(R.id.edit_dog_info_age_et);
        age.setEnabled(false);
        dob.setEnabled(false);
        breed.setEnabled(false);
        name.setEnabled(false);
        this.findViewById(R.id.edit_dog_info_save_and_exit_button).setVisibility(View.INVISIBLE);

        LoadImage li = new LoadImage(context);
        try {
            long imageID = Long.parseLong(dogsData.getImageID());
            dogImage.setImageBitmap((li.execute(imageID).get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        radioSexGroup = (RadioGroup) this.findViewById(R.id.edit_dog_info_Radio_group);
        male = (RadioButton) findViewById(R.id.edit_dog_info_radio_male);
        female = (RadioButton) findViewById(R.id.edit_dog_info_radio_female);
        male.setEnabled(false);
        female.setEnabled(false);
        radioSexGroup.setEnabled(false);

        if (dogsData.getGender().equals("Male")) {
            male.setChecked(true);
        } else {
            female.setChecked(true);
        }


        name.setText(dogsData.getName());
        breed.setText(dogsData.getBreed());
        age.setText(dogsData.getAge() + "");

        if (dogsData.getDob() == null || "".equals(dogsData.getDob())) {
            dob.setText("N/A");
        } else {
            dob.setText(dogsData.getDob());
        }


        this.findViewById(R.id.edit_dog_info_exit_without_saving).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



    }

}


