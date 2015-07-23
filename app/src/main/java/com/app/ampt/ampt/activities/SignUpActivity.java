package com.app.ampt.ampt.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ampt.ampt.R;
import com.app.ampt.ampt.bean.User;
import com.app.ampt.ampt.db.DBHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by malith on 7/23/15.
 */
public class SignUpActivity extends Activity {
    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        pattern = Pattern.compile(EMAIL_PATTERN);


        final EditText ename = (EditText) findViewById(R.id.signupActivity_name);
        final EditText elastname = (EditText) findViewById(R.id.signupActivity_lastname);
        final EditText eemail = (EditText) findViewById(R.id.signupActivity_email);
        final EditText epassword = (EditText) findViewById(R.id.signupActivity_password);
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

                DBHelper dbh = new DBHelper(SignUpActivity.this);
                User usr = dbh.getUser(email);
                if (null != usr) {
                    if (usr.getEmail().equals(email)) {
                        Toast.makeText(SignUpActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                long id = dbh.addContact(new User(name, lastname, email, password, ""));
                System.out.println("User is saved :" + id);
                Toast.makeText(SignUpActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
