package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    private static final int CODE_POST_REQUEST = 1025;
    int logStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences sp = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        if(sp.contains("logStatus")) {
            logStatus = sp.getInt("logStatus", 0);
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);

        if(logStatus == 1){
            startActivity(new Intent(getApplicationContext(), Clearance.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }

    public void login(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if(username.isEmpty()){
            etUsername.setError("Please Enter your Email address");
            etUsername.requestFocus();
        }
        if (password.isEmpty()) {
            etPassword.setError("Please Enter your password");
            etPassword.requestFocus();
        }

        if(!username.isEmpty() && !password.isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            Login.PerformNetworkRequest request = new Login.PerformNetworkRequest("http://3.1.244.230/mobile/loginAdmin", params, CODE_POST_REQUEST);
            request.execute();

            progressDialog.setMessage("Logging in Please wait...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

    }

    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Logging in Account Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try {
                JSONObject object2 = new JSONObject(s);
                if (!object2.getBoolean("error2")) {
                    SharedPreferences sp = getApplication().getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("logStatus",1);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),object2.getString("message2"),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Clearance.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),object2.getString("message2"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);
            return null;
        }
    }

}
