package com.ampt.bluetooth.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.ampt.bluetooth.R;
import com.ampt.bluetooth.bean.User;
import com.ampt.bluetooth.database.helper.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by malith on 7/23/15.
 */
public class SignInActivity extends Activity {
    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);
        pattern = Pattern.compile(EMAIL_PATTERN);

        final EditText eemail = (EditText) findViewById(R.id.signInActivity_email);
        eemail.setText("h@g.com");

        final EditText epassword = (EditText) findViewById(R.id.signInActivity_password);
        epassword.setText("qwe");
        Button signin = (Button) findViewById(R.id.signInActivity_signin_Button);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eemail.getText().toString();
                String password = epassword.getText().toString();

                if (null == email || email.equals("")) {
                    Toast.makeText(SignInActivity.this, "Email can't be empty ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    matcher = pattern.matcher(email);
                    boolean isMatched = matcher.matches();
                    if (!isMatched) {
                        Toast.makeText(SignInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }
                }

                if (null == password || password.equals("")) {
                    Toast.makeText(SignInActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseHelper dbh = new DatabaseHelper(SignInActivity.this);
                User usr = dbh.getUser(email);
                if (null != usr) {
                    if (usr.getEmail().equals(email) && password.equals(usr.getPassword())) {
                        Toast.makeText(SignInActivity.this, "SignIn Success", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(SignInActivity.this, TabbedMainActivity.class));
                    } else {
                        Toast.makeText(SignInActivity.this, "Email or Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

}
