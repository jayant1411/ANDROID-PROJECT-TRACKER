package com.example.dell.tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdminLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button adminLogin;
    private EditText adminEmail;
    private EditText adminPassword;
    private TextView adminSignup,forPassword;
    ProgressDialog progressDialog;
    FirebaseAuth AdminfirebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if (AdminfirebaseAuth.getCurrentUser() != null) {

            progressDialog.setMessage("checking user logging info");
            progressDialog.show(); // here show progress messag
            String admin = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            FirebaseDatabase userLocDatabase = FirebaseDatabase.getInstance();
            DatabaseReference userLocDbRef = userLocDatabase.getReference("Admin");
            Toast.makeText(AdminLoginActivity.this, "First " + admin, Toast.LENGTH_LONG).show();

            Query query = userLocDbRef.orderByChild("adminEmail").equalTo(admin);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists()){
                        progressDialog.hide();
                        Toast.makeText(AdminLoginActivity.this,"First,logOut from User",Toast.LENGTH_LONG).show();
                        finish();
                        // startActivity(new Intent(MapsActivity.this,loginActivity.class));
                    }
                    else {
                        progressDialog.hide();
                        Toast.makeText(AdminLoginActivity.this,"Already Log in :>",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AdminLoginActivity.this,userlistActivity.class));
                    } }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        progressDialog = new ProgressDialog(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        adminLogin = (Button)findViewById(R.id.ad_login);
        adminEmail = (EditText)findViewById(R.id.ad_logemail);
        adminPassword = (EditText)findViewById(R.id.ad_password);
        adminSignup = (TextView)findViewById(R.id.ad_signup);
        forPassword = (TextView)findViewById(R.id.forPassword);
        AdminfirebaseAuth = FirebaseAuth.getInstance();

        forPassword.setOnClickListener(this);
        adminLogin.setOnClickListener(this);
        adminSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == adminLogin){
            loginAdmin();
        }
        if(v == adminSignup){
            finish();
            Intent intent = new Intent(AdminLoginActivity.this,AdminRegActivity.class);
            startActivity(intent);
        }
        if(v == forPassword){
            File Root = Environment.getExternalStorageDirectory();
            File Dir = new File(Root.getAbsolutePath()+"/myAppFile");
            File file = new File(Dir, "adminPassword.txt");

            // retrive password
            String message;

            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();
                progressDialog.setMessage("fetching Password...");
                progressDialog.show(); // here show progress message
                while ((message = bufferedReader.readLine()) != null){
                    stringBuffer.append("\n");
                    stringBuffer.append(message);
                }
                // message = bufferedReader.readLine();
                String password = String.valueOf(stringBuffer);

                progressDialog.hide();
                Toast.makeText(this,"Password : "+password,Toast.LENGTH_LONG).show();

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void loginAdmin() {
        final String emailAdmin = adminEmail.getText().toString();
        String passwordAdmin = adminPassword.getText().toString();

        if(TextUtils.isEmpty(emailAdmin)){
            Toast.makeText(this, "insert your registered email id", Toast.LENGTH_SHORT).show();
            return; }
        if(TextUtils.isEmpty(passwordAdmin)){
            Toast.makeText(this,"insert password",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(" Loging Admin ");
        progressDialog.show(); // here show progress message

        AdminfirebaseAuth.signInWithEmailAndPassword(emailAdmin,passwordAdmin).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.hide();
                if (task.isSuccessful()) {
                    // finish();
                    // check weather the given email id is of admin or not
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    Query query = rootRef.child("Admin").orderByChild("adminEmail").equalTo(emailAdmin);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists())
                            {
                                Toast.makeText(getApplicationContext(),"please... use Admin Email ID",Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                //startActivity(new Intent(AdminLoginActivity.this,AdminLoginActivity.class));
                                finish();
                            }
                            else{
                                // userlistActivity.AdminMail(emailAdmin);  // send current admin email id who try to add users
                                Toast.makeText(AdminLoginActivity.this, "login Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminLoginActivity.this, userlistActivity.class));
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}
