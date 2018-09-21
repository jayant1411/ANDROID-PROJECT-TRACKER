package com.example.dell.tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdminRegActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference adminDbRef;
    private Button BtnRegister;
    private EditText etAname;
    private EditText etAmail;
    private EditText etApassword;
    private TextView tvAsignin;
    private ProgressDialog progressDialog;
    FirebaseDatabase adminDatabase;
    // private ProgressBar progressBaR;
    private FirebaseAuth adminFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reg);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        adminFirebaseAuth = FirebaseAuth.getInstance(); // working for firebase authentication
        progressDialog = new ProgressDialog(this); // show progress Dialog

        etAname = (EditText)findViewById(R.id.ida_name);
        etAmail = (EditText)findViewById(R.id.ida_mail);
        etApassword = (EditText)findViewById(R.id.ida_password);
        tvAsignin = (TextView)findViewById(R.id.ida_text);
        BtnRegister = (Button)findViewById(R.id.eta_btnreg);
        adminDbRef = FirebaseDatabase.getInstance().getReference("Admin");

        BtnRegister.setOnClickListener(this);
        tvAsignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == BtnRegister){
            registerAdmin();
        }
        if(v == tvAsignin){
            startActivity(new Intent(AdminRegActivity.this,AdminLoginActivity.class));
        }
    }

    private void registerAdmin() {
        String aName = etAname.getText().toString();
        String aMail = etAmail.getText().toString();
        String aPassword = etApassword.getText().toString();

        if(TextUtils.isEmpty(aName)){
            Toast.makeText(this,"Please insert Name ",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(aMail)){
            //email is empty
            Toast.makeText(this,"Please insert email id ",Toast.LENGTH_SHORT).show();
            // stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(aPassword)){
            Toast.makeText(this,"please insert password ",Toast.LENGTH_SHORT).show();
            return;
        }
        if(aPassword.length()<6) {
            Toast.makeText(this, "please insert strong and minimum 6 digit password !", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show(); // here show progress message

        adminFirebaseAuth.createUserWithEmailAndPassword(aMail,aPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.cancel();
                if(task.isSuccessful()){
                    //user is registered successful
                    // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                    adminDatabase = FirebaseDatabase.getInstance();
                    adminDbRef = adminDatabase.getReference("Admin");
                    appendAdmin(adminDbRef);
                    saveAdminPassword();
                    Toast.makeText(AdminRegActivity.this,"Registration Successful :) ",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminRegActivity.this ,AdminLoginActivity.class));

                }
                else {

                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveAdminPassword() {
        String state;
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File Root = Environment.getExternalStorageDirectory();
            File Dir = new File(Root.getAbsolutePath() + "/myAppFile");
            if (Dir.exists()) {
                Dir.mkdir();
            }
            File file = new File(Dir, "adminPassword.txt");
            String password = etAname.getText().toString();
            password += " : ";
            password += etApassword.getText().toString();
            password += "\n";
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                //fileOutputStream = openFileOutput(file,MODE_APPEND);
                fileOutputStream.write(password.getBytes());
                fileOutputStream.close();
                Toast.makeText(getApplicationContext(), "Message saved", Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void appendAdmin(DatabaseReference adminDbRef) {
        String email = etAmail.getText().toString();
        String name = etAname.getText().toString();
        String key = adminDbRef.push().getKey();
        Admin admin = new Admin(name,email);
        adminDbRef.child(key).setValue(admin);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}
