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

public class Clearance extends AppCompatActivity {
    private static final int CODE_POST_REQUEST = 1025;
    int logStatus;
    EditText etPlateNo;
    TextView txtEngNo, txtChasisNo, txtPlateNo, txtStatus;
    Button btnGetClearance, btnLogout;
    ProgressDialog progressDialog;
    Button btnScanPlateNo;
    String plateNo;
    //    String engNo, chasisNo, plateNo;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clearance);

        SharedPreferences sp = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences sp2 = getApplication().getSharedPreferences("plate", MODE_PRIVATE);

        if(sp.contains("logStatus")) {
            logStatus = sp.getInt("logStatus", 0);
        }

        if(sp2.contains("plateNo")) {
            plateNo = sp2.getString("plateNo", null);
        }

        if(logStatus == 0){
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        etPlateNo = findViewById(R.id.etPlateNo);
        txtEngNo = findViewById(R.id.txtEngNo);
        txtChasisNo = findViewById(R.id.txtChasisNo);
        txtPlateNo = findViewById(R.id.txtPlateNo);
        btnLogout = findViewById(R.id.btnLogout);
        txtStatus = findViewById(R.id.txtStatus);
        btnScanPlateNo = findViewById(R.id.btnScanPlateNo);
        btnGetClearance = findViewById(R.id.btnGetClearance);
        progressDialog = new ProgressDialog(this);

        btnGetClearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClearance();
            }
        });

        btnScanPlateNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OpenCamera.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getApplication().getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.putInt("logStatus",0);
                editor.commit();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        if(plateNo != null){
            etPlateNo.setText(plateNo);
            getClearance();
        }

    }

    public void getClearance(){
        String plateNo = etPlateNo.getText().toString().trim();
        Toast.makeText(getApplicationContext(),plateNo,Toast.LENGTH_SHORT).show();
        if(plateNo.isEmpty()){
            etPlateNo.setError("Please enter the plate number!");
            etPlateNo.requestFocus();
        }else{
            HashMap<String, String> params = new HashMap<>();
            params.put("plateNo", plateNo);
            Clearance.PerformNetworkRequest request = new Clearance.PerformNetworkRequest("http://3.1.244.230/mobile/getClearance", params, CODE_POST_REQUEST);
            request.execute();

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
            progressDialog.setMessage("Getting Clearance Please wait...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try {
                JSONObject object2 = new JSONObject(s);
                if (!object2.getBoolean("error2")) {
                    getJSONObject(object2.getJSONArray("record"));
                    SharedPreferences sp = getApplication().getSharedPreferences("plate", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.commit();
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

    private void getJSONObject(JSONArray record) throws JSONException {
        for (int i = 0; i < record.length(); i++) {
            JSONObject obj = record.getJSONObject(i);
            txtEngNo.setText(obj.getString("engineNumber"));
            txtChasisNo.setText(obj.getString("chassisNumber"));
            txtPlateNo.setText(obj.getString("plateNo"));
            status = obj.getInt("clearanceStatus");
        }

        if(status == 1){
            txtStatus.setText("New");
        }else if(status == 2){
            txtStatus.setText("Motor Vehicle Inspection");
        }else if(status == 3){
            txtStatus.setText("Macro-etching Inspections");
        }else if(status == 4){
            txtStatus.setText("Final Processing of Records");
        }else if(status == 5){
            txtStatus.setText("Pending for Approval");
        }else if(status == 6){
            txtStatus.setText("Cleared");
        }else if(status == 7){
            txtStatus.setText("Declined Clearance");
        }else if(status == 8){
            txtStatus.setText("Request for Nationwide Alarm Application");
        }else if(status == 9){
            txtStatus.setText("Waiting for Notary for Nationwide Alarm Application");
        }else if(status == 10){
            txtStatus.setText("Waiting for Notary for Nationwide Alarm Application");
        }else if(status == 11){
            txtStatus.setText(" Pending Approval of Memorandum for Nationwide Alarm Application");
        }else if(status == 12){
            txtStatus.setText("Pending Approval of Memorandum for Nationwide Alarm Application");
        }else if(status == 13){
            txtStatus.setText("Pending Approval of Memorandum for Nationwide Alarm Application");
        }else if(status == 14){
            txtStatus.setText("Pending Approval of Certificate for Nationwide Alarm Application");
        }else if(status == 15){
            txtStatus.setText("Pending Approval of Certificate for Nationwide Alarm Application");
        }else if(status == 16){
            txtStatus.setText("Pending Approval of Certificate for Nationwide Alarm Application");
        }else if(status == 17){
            txtStatus.setText("With Alarm");
        }else if(status == 18){
            txtStatus.setText("Cleared : Declined Nationwide Alarm Application");
        }else if(status == 19){
            txtStatus.setText("Request for Lifting of Alarm Application");
        }else if(status == 20){
            txtStatus.setText("Waiting for Notary for Lifting of Alarm Application");
        }else if(status == 21){
            txtStatus.setText("Pending Requirements for Lifting of Alarm Application");
        }else if(status == 22){
            txtStatus.setText("Investigator : Pending Approval of Memorandum for Lifting of Alarm Application");
        }else if(status == 23){
            txtStatus.setText("Deputy Chief : Pending Approval of Memorandum for Lifting of Alarm Application");
        }else if(status == 24){
            txtStatus.setText("Officer in Charge : Pending Approval of Memorandum for Lifting of Alarm Application");
        }else if(status == 25){
            txtStatus.setText("Officer in Charge : Pending Approval of Certificate for Lifting of Alarm Application");
        }
    }
}
