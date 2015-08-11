//package com.ampt.bluetooth;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.ampt.bluetooth.database.helper.DatabaseHelper;
//import com.ampt.bluetooth.database.model.DogsData;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
///**
// * Created by Heshanr on 4/16/2015.
// */
//public class AddOrEditDogProfile extends Activity {
//    DatabaseHelper daf = new DatabaseHelper(this);
//    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
//    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
//    public static final String EXTRAS_EDIT_OR_SAVE = "EDIT_OR_SAVE";
//    public static final String EXTRAS_SAVE = "TRUE";
//    public static final String EXTRAS_EDIT = "FALSE";
//    private String device_name;
//    private String device_address;
//    private boolean save;
//    private int id;
//    private String img;
//    private EditText name;
//    private EditText age;
//    private EditText goal;
//    private ImageView image;
//
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.add_dog_profile);
//
//        final Intent intent = getIntent();
//        device_name = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        device_address = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
//        save = Boolean.parseBoolean(intent.getStringExtra(EXTRAS_EDIT_OR_SAVE));
//
//        // Sets up UI references.
//
//        ((TextView) findViewById(R.id.lbl_device_name)).setText(device_name);
//        ((TextView) findViewById(R.id.lbl_device_address)).setText(device_address);
//        name  = (EditText) findViewById(R.id.dog_name_edit);
//        age   = (EditText) findViewById(R.id.edit_age_value);
//        goal   = (EditText) findViewById(R.id.dog_goal_edit);
//        image = (ImageView) findViewById(R.id.dog_image_edit);
//
//        Button saveBtn = (Button) findViewById(R.id.save_edit_btn);
//        Button cancelBtn = (Button) findViewById(R.id.cancel_btn);
//        Button RemoveBtn = (Button) findViewById(R.id.delete_btn);
//
//        if(save){
//            saveBtn.setText(R.string.save_btn);
//            RemoveBtn.setVisibility(View.INVISIBLE);
//            getActionBar().setTitle(R.string.title_save);
//        }else{
//            saveBtn.setText(R.string.update_btn);
//            RemoveBtn.setVisibility(View.VISIBLE);
//            getActionBar().setTitle(R.string.title_update);
//            getDataFromDB();
//        }
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveInDB();
//            }
//        });
//
//        Button imageBtn = (Button) findViewById(R.id.select_image);
//        imageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage();
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        RemoveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(AddOrEditDogProfile.this)
//                        .setMessage("Are you sure you want to exit?")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int ind) {
//                                daf.deleteDogProfile(id);
//                                daf.deleteActivityByDog(id);
//                                AddOrEditDogProfile.this.finish();
//                            }
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
//            }
//        });
//    }
//
//
//    private void saveInDB() {
//
//        DogsData saveData = new DogsData();
//        saveData.setName(name.getText().toString());
//        saveData.setGoal(goal.getText().toString());
//        saveData.setAge(Integer.parseInt(age.getText().toString()));
//        saveData.setImageID(img);
//        saveData.setDeviceName(device_name);
//        saveData.setDeviceAddress(device_address);
//        long done = 0;
//        if(save){
//            done = daf.addDog(saveData);
//        }else{
//            saveData.setId(id);
//            done = daf.updateDogProfile(saveData);
//        }
//        if(done>0){
//            finish();
//        }else{
//            Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//
//    private void getDataFromDB() {
//        DogsData updateData = daf.getDogProfileByAddress(device_address);
//        // Sets up UI
//        if(updateData != null){
//            name.setText(updateData.getName());
//            age.setText(updateData.getAge()+"");
//            goal.setText(updateData.getGoal());
//            img = updateData.getImageID();
//            if(img != null){
//                ByteArrayInputStream imageStream = new ByteArrayInputStream(img);
//                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//                image.setImageBitmap(theImage);
//            }
//            id = updateData.getId();
//        }
//
//    }
//
//    private void setImageArray(Bitmap bmp){
//        Bitmap resized = Bitmap.createScaledBitmap(bmp,(int)(bmp.getWidth()*0.5), (int)(bmp.getHeight()*0.6), true);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        img = stream.toByteArray();
//    }
//    private void selectImage() {
//
//        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddOrEditDogProfile.this);
//        builder.setTitle("Add Photo!");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo"))
//                {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                    startActivityForResult(intent, 1);
//                }
//                else if (options[item].equals("Choose from Gallery"))
//                {
//                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, 2);
//
//                }
//                else if (options[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 1) {
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            bitmapOptions);
//
//                    image.setImageBitmap(bitmap);
//                    setImageArray(bitmap);
//                    String path = android.os.Environment
//                            .getExternalStorageDirectory()
//                            + File.separator
//                            + "Phoenix" + File.separator + "default";
//                    f.delete();
//                    OutputStream outFile = null;
//                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    try {
//                        outFile = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
//                        outFile.flush();
//                        outFile.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (requestCode == 2) {
//
//                Uri selectedImage = data.getData();
//                String[] filePath = { MediaStore.Images.Media.DATA };
//                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                Log.w("path of image from gallery......******************.........", picturePath + "");
//                image.setImageBitmap(thumbnail);
//                setImageArray(thumbnail);
//            }
//        }
//    }
//}
