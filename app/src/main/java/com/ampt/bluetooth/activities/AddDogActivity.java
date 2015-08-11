package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.Util.CustomToast;
import com.ampt.bluetooth.database.helper.DatabaseHelper;
import com.ampt.bluetooth.database.model.DogsData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by malith on 8/6/15.
 */
public class AddDogActivity extends Activity {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private byte[] dogImageData = null;
    private ImageView iv_dog_image;
    private EditText nameOfDog;
    private EditText age;
    private Spinner gender;
    private EditText dateOfBirth;
    private EditText breed;
    private EditText goal;
    private EditText deviceName;
    private EditText deviceID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_dog);
        iv_dog_image = (ImageView) findViewById(R.id.add_new_dog_activity_dog_imageView);
        nameOfDog = (EditText) findViewById(R.id.add_new_dog_activity_dog_name);
        age = (EditText) findViewById(R.id.add_new_dog_activity_age);
        gender = (Spinner) findViewById(R.id.add_new_dog_activity_sppinder_gender);
        dateOfBirth = (EditText) findViewById(R.id.add_new_dog_activity_date_of_birth);
        breed = (EditText) findViewById(R.id.add_new_dog_activity_breed);
        goal = (EditText) findViewById(R.id.add_new_dog_activity_goal);
        deviceName = (EditText) findViewById(R.id.add_new_dog_activity_device_name);
        deviceID = (EditText) findViewById(R.id.add_new_dog_activity_device_id);


        iv_dog_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        List<String> list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(dataAdapter);


        Button save = (Button) findViewById(R.id.add_new_dog_activity_save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String _nameOfDog = nameOfDog.getText().toString();
                String _age = age.getText().toString();
                String _gender = String.valueOf(gender.getSelectedItem());
                String _dateOfBirth = dateOfBirth.getText().toString();
                String _breed = breed.getText().toString();
                String _goal = goal.getText().toString();
                String _deviceName = deviceName.getText().toString();
                String _deviceId = deviceID.getText().toString();


                if (null == dogImageData) {
                    CustomToast.showToast(AddDogActivity.this, "Please Select an Image", R.layout.error_toast_layout);
                    return;
                }
                if (null == _nameOfDog || _nameOfDog.equals("")) {
                    CustomToast.showToast(AddDogActivity.this, "Please Enter name of the dog", R.layout.error_toast_layout);
                    return;
                }
                if (null == _age || _age.equals("")) {
                    CustomToast.showToast(AddDogActivity.this, "Please set age of the dog", R.layout.error_toast_layout);
                    return;
                }
                if (null == _breed || _breed.equals("")) {
                    CustomToast.showToast(AddDogActivity.this, "Please set breed of the dog", R.layout.error_toast_layout);
                    return;
                }

                DatabaseHelper dbh = new DatabaseHelper(AddDogActivity.this);
                long imageID = dbh.saveImage(dogImageData);
                if (imageID < 0) {
                    CustomToast.showToast(AddDogActivity.this, "Something went wrong,please try again ", R.layout.error_toast_layout);
                    return;
                }
                DogsData dog = new DogsData();
                dog.setName(_nameOfDog);
                dog.setImageID(String.valueOf(imageID));
                dog.setGender(_gender);
                dog.setDob(_dateOfBirth == null ? "Unknown" : _dateOfBirth);
                dog.setBreed(_breed == null ? "Unknown" : _breed);
                dog.setAge(Integer.parseInt(_age));
                dog.setDeviceAddress(_deviceId == null ? "Unknown" : _deviceId);
                dog.setDeviceName(_deviceName == null ? "Unknown" : _deviceName);
                dog.setGoal(_goal == null ? "Unknown" : _goal);
                long dogRowId = dbh.addDog(dog);
                if (dogRowId > 0) {
                    CustomToast.showToast(AddDogActivity.this, "Saved Successfully ", R.layout.message_toast_layout);
                    Intent i = getIntent(); //gets the intent that called this intent
                    setResult(123, i);
                    finish();
                } else {
                    CustomToast.showToast(AddDogActivity.this, "Something went wrong,please try again ", R.layout.error_toast_layout);
                }


            }
        });

    }


    private void startDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddDogActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            GALLERY_PICTURE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap thumbnail = null;
            if (requestCode == CAMERA_REQUEST) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iv_dog_image.setImageBitmap(thumbnail);
                dogImageData = bytes.toByteArray();
            } else if (requestCode == GALLERY_PICTURE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);
                iv_dog_image.setImageBitmap(thumbnail);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                dogImageData = stream.toByteArray();
                System.out.println(dogImageData.length);
            }
        }
    }
}
