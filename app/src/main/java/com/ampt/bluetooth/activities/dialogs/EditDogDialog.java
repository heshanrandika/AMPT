package com.ampt.bluetooth.activities.dialogs;

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
public class EditDogDialog extends Dialog {
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

    public EditDogDialog(final Context con, DogsData data) {
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
        this.findViewById(R.id.edit_dog_info_save_and_exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(context);
                String newName = name.getText().toString();
                String newBreed = breed.getText().toString();
                String newDoB = dob.getText().toString();
                String newAge = age.getText().toString();

                String newGender = "";
                boolean isMaleSelected = male.isChecked();
                if (isMaleSelected) {
                    newGender = "Male";
                } else {
                    newGender = "Female";
                }
                if (null == newName || newName.equals("")) {
                    Toast.makeText(context, "Name is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == newAge || newAge.equals("")) {
                    Toast.makeText(context, "Age is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == newBreed || newBreed.equals("")) {
                    Toast.makeText(context, "Breed is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == newDoB || newDoB.equals("")) {
                    Toast.makeText(context, "DoB is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                DogsData dd = new DogsData();
                dd.setName(newName);
                dd.setAge(Integer.parseInt(newAge));
                dd.setBreed(newBreed);
                dd.setDob(newDoB);
                dd.setImageID(dogsData.getImageID());
                dd.setGender(newGender);
                dd.setId(dogsData.getId());
                dd.setCreatedAt(dogsData.getCreatedAt());
                dd.setGoal(dogsData.getGoal());
                dd.setGoalPlay(dogsData.getGoalPlay());
                dd.setGoalWalk(dogsData.getGoalWalk());
                dd.setStatus(dogsData.isStatus());
                if (dd.getName().equals(dogsData.getName()) && dd.getDob().equals(dogsData.getDob())
                        && dd.getGender().equals(dogsData.getGender()) && dd.getBreed().equals(dogsData.getBreed()) && dd.getAge() == dogsData.getAge()) {
                    Toast.makeText(context, "No Changes to Update", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                db.updateDogProfile(dd);
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

    }

}
