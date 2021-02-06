package com.cpoc.softpostest;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    EditText etUserId, eTPass, etAmount, eTDesc;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String userId;
    String password;

    private ApiType apiType;

    public ApiType getApiType() {
        return apiType;
    }

    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }


    public void payPAYTM(View view) {

        setApiType(ApiType.PAYMENT_MERCHANT);

        new TestAsync().execute();

    }


    boolean isMosambeeExist = false;

    boolean isPaytmAppExist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosambee);

        etUserId = findViewById(R.id.eTuserId);

        eTPass = findViewById(R.id.eTPass);

        etAmount = findViewById(R.id.eTAmount);

        eTDesc = findViewById(R.id.eTDesc);

        etUserId.setText(Constants.USERNAME);

        eTPass.setText(Constants.PASSWORD);

        etAmount.setText("100");


        isPaytmAppExist = isPaytmAppExist();





    }


    public void healthCheckPaytm(View view) {
        setApiType(ApiType.HEALTH_MERCHANT);
        new TestAsync().execute();

    }


    public void bTinitApiPAYTM(View view) {

        setApiType(ApiType.LOGIN_MERCHANT);
        new TestAsync().execute();

    }


    public void getDetailsPaytm(View view) {
        setApiType(ApiType.DETAILS_MERCHANT);
        new TestAsync().execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this, "Got result", Toast.LENGTH_SHORT).show();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je;

        if (resultCode == -1) {
            return;
        }

        if (requestCode == Constants.ActivityLoginRequestCode) {

            String sessionId = data.getStringExtra("sessionId");
            String responseCode = data.getStringExtra("responseCode");
            String description = data.getStringExtra("description");

            Utils.setToken(MainActivity.this, sessionId);

            new AlertDialog.Builder(this)
                    .setTitle("Login")
                    .setMessage("Session Id : " + sessionId + "\n" + "Response Code : " + responseCode + "\n" + "Description : " + description)
                    .setPositiveButton("OK", null)
                    .show();

//            if (null != sessionId && "0".equals(responseCode)) {
//                setApiType(ApiType.PAYMENT);
//
//                new TestAsync().execute();
//            }


        } else if (requestCode == Constants.ActivityHealthCheckRequestCode) {
            String responseCode = data.getStringExtra("responseCode");
            String description = data.getStringExtra("description");
            String detailedAudit = data.getStringExtra("detailedAudit");

            try {
                je = jp.parse(detailedAudit);
                detailedAudit = gson.toJson(je);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage("Response Code : " + responseCode + "\n" + "Description : " + description + "\n" + "Detailed Audit : " + detailedAudit)
                    .setPositiveButton("OK", null)
                    .show();
        } else if (requestCode == Constants.ActivityPaymentRequestCode) {

            String response = data.getStringExtra("receiptResponse");
            String paymentDescription = data.getStringExtra("paymentDescription");
            String paymentResponseCode = data.getStringExtra("paymentResponseCode");
            String responseCode = data.getStringExtra("responseCode");

            String showResponse = response;

            new AlertDialog.Builder(this)
                    .setTitle("Receipt")
                    .setMessage("ResponseCode : " + responseCode + "\n" +
                            "PaymentResponseCode : " + paymentResponseCode + "\n" +
                            "ReceiptResponse : " + showResponse + "\n" + "PaymentDescription : " + paymentDescription)
                    .setPositiveButton("OK", null)
                    .show();
        } else if (requestCode == Constants.ActivityDetailsRequestCode) {

            String appVersion = data.getStringExtra("appVersion");
            String backendVersion = data.getStringExtra("backendVersion");
            String kcv = data.getStringExtra("kcv");
            String description = data.getStringExtra("description");

            new AlertDialog.Builder(this)
                    .setTitle("App Details")
                    .setMessage("App Version : " + appVersion + "\n" +
                            "Backend Version : " + backendVersion + "\n" +
                            "Application KCV : " + kcv + "\n" +
                            "Description : " + description
                    )
                    .setPositiveButton("OK", null)
                    .show();
        } else if (requestCode == Constants.ActivityLogsRequestCode) {

            String description = data.getStringExtra("description");

            new AlertDialog.Builder(this)
                    .setTitle("Details")
                    .setMessage(
                            "Description : " + description
                    )
                    .setPositiveButton("OK", null)
                    .show();
        } else if (requestCode == Constants.ActivityLastReceiptRequestCode) {

            String response = data.getStringExtra("receiptResponse");
            String paymentDescription = data.getStringExtra("paymentDescription");
            String paymentResponseCode = data.getStringExtra("paymentResponseCode");
            String responseCode = data.getStringExtra("responseCode");

            String showResponse = response;

            new AlertDialog.Builder(this)
                    .setTitle("Receipt")
                    .setMessage("ResponseCode : " + responseCode + "\n" +
                            "PaymentResponseCode : " + paymentResponseCode + "\n" +
                            "ReceiptResponse : " + showResponse + "\n" + "PaymentDescription : " + paymentDescription)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }


    boolean isPaytmAppExist() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {


            if (packageInfo.packageName.contains(Constants.SOFTPOS_PACKAGE_NAME_MERCHANT)) {
                return true;
            }
        }
        return false;
    }

    enum ApiType {LOGIN_MERCHANT, HEALTH_MERCHANT, PAYMENT, PAYMENT_MERCHANT, DETAILS_MERCHANT, LASTRECEIPT}

    class TestAsync extends AsyncTask<Void, Integer, Boolean> {
        String TAG = getClass().getSimpleName();

        protected Boolean doInBackground(Void... arg0) {

//            final PackageManager pm = getPackageManager();
//            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//
//            for (ApplicationInfo packageInfo : packages) {
//                Log.d(TAG, "Installed package :" + packageInfo.packageName);
//                Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
//                Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
//
//                if (packageInfo.packageName.contains(Constants.SOFTPOS_PACKAGE_NAME_MOSAMBEE)) {
//                    return true;
//                }
//            }
//
//            return false;


            return true;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Message")
                        .setMessage("This Application require Mosambee SoftPOS in a Device")
                        .setPositiveButton("OK", null)
                        .show();

            } else {
                if (getApiType().equals(ApiType.PAYMENT_MERCHANT)) {

                    if ((etAmount.getText().toString() != "")) {

                        int am = 0;

                        try {
                            am = Integer.parseInt(etAmount.getText().toString());

                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Not a valid integer", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent intent = new Intent();
                        intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MERCHANT);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("amount", String.format("%d", am));
                        mBundle.putString("sessionId", Utils.getToken(MainActivity.this));
                        mBundle.putString("mobNo", "8424834651");
                        mBundle.putString("description", eTDesc.getText().toString());

                        intent.putExtras(mBundle);
                        intent.setAction(Constants.SOFTPOS_PAYMENT_ACTION);
                        startActivityForResult(intent, Constants.ActivityPaymentRequestCode);
                    } else {
                        Toast.makeText(MainActivity.this, "Amount can not be black", Toast.LENGTH_LONG).show();
                    }
                } else if (getApiType().equals(ApiType.LOGIN_MERCHANT)) {

                    if ((etUserId.getText().toString() != "") && (eTPass.getText().toString() != "")) {
                        setPassword(eTPass.getText().toString());
                        setUserId(etUserId.getText().toString());

                        Intent intent = new Intent();
                        Bundle mBundle = new Bundle();
                        mBundle.putString("userName", getUserId());
                        mBundle.putString("password", getPassword());
                        mBundle.putString("key", "1234");
                        intent.putExtras(mBundle);
                        intent.setAction(Constants.SOFTPOS_INIT_ACTION);
                        intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MERCHANT);
                        startActivityForResult(intent, Constants.ActivityLoginRequestCode);


                    }
                } else if (getApiType().equals(ApiType.HEALTH_MERCHANT)) {

                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setAction(Constants.SOFTPOS_HEALTHCHECK_ACTION);
                    intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MERCHANT);
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionId", Utils.getToken(MainActivity.this));
                    intent.putExtras(bundle);
                    startActivityForResult(intent, Constants.ActivityHealthCheckRequestCode);
                } else if (getApiType().equals(ApiType.DETAILS_MERCHANT)) {

                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setAction(Constants.SOFTPOS_DETAILS_ACTION);
                    intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MERCHANT);
                    startActivityForResult(intent, Constants.ActivityDetailsRequestCode);
                } else if (getApiType().equals(ApiType.LASTRECEIPT)) {

                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setAction(Constants.SOFTPOS_LAST_TRANSACTION_ACTION);
                    intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MERCHANT);

                    startActivityForResult(intent, Constants.ActivityLastReceiptRequestCode);
                }
            }
        }

    }
}
