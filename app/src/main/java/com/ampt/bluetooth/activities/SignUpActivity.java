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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ampt.bluetooth.R;
import com.ampt.bluetooth.bean.User;
import com.ampt.bluetooth.database.helper.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by malith on 7/23/15.
 */
public class SignUpActivity extends Activity {
    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    ImageView img_logo;
    EditText ename;
    EditText elastname;
    EditText eemail;
    EditText epassword;
    byte[] imageData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        pattern = Pattern.compile(EMAIL_PATTERN);


        ename = (EditText) findViewById(R.id.signupActivity_name);
        elastname = (EditText) findViewById(R.id.signupActivity_lastname);
        eemail = (EditText) findViewById(R.id.signupActivity_email);
        epassword = (EditText) findViewById(R.id.signupActivity_password);

        img_logo = (ImageView) findViewById(R.id.SignUpActivityProfilePic);
        img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        Button create = (Button) findViewById(R.id.signupActivity_create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ename.getText().toString();
                String lastname = elastname.getText().toString();
                String email = eemail.getText().toString();
                String password = epassword.getText().toString();
                if (null == name || name.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == lastname || lastname.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Last name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == email || email.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Email can't be empty ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    matcher = pattern.matcher(email);
                    boolean isMatched = matcher.matches();
                    if (!isMatched) {
                        Toast.makeText(SignUpActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }
                }
                if (null == password || password.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbh = new DatabaseHelper(SignUpActivity.this);
                User usr = dbh.getUser(email);
                if (null != usr) {
                    if (usr.getEmail().equals(email)) {
                        Toast.makeText(SignUpActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                long imageId = -1;
                if (null != imageData) {
                    imageId = dbh.saveImage(imageData);
                }
                User user=new User(name, lastname, email, password, imageId);
                long id = dbh.addUser(user);
                System.out.println("User is saved : " + id +" and user image is saved : "+imageId);
                Toast.makeText(SignUpActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                if (id > 0) {
                    finish();
                    EventBus bus = EventBus.getDefault();
                    bus.post(user);
                    startActivity(new Intent(SignUpActivity.this, TabbedMainActivity.class));
                }
            }
        });
    }

    private void startDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
                img_logo.setImageBitmap(thumbnail);
                imageData = bytes.toByteArray();
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
                img_logo.setImageBitmap(thumbnail);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageData = stream.toByteArray();
                System.out.println(imageData.length);
            }
        }
    }


}
