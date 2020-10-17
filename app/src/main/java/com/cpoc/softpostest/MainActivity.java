package com.cpoc.softpostest;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    EditText etUserId, eTPass, etAmount;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosambee);

        etUserId = findViewById(R.id.eTuserId);

        eTPass = findViewById(R.id.eTPass);

        etAmount = findViewById(R.id.eTAmount);

        etUserId.setText(Constants.USERNAME);

        eTPass.setText(Constants.PASSWORD);

        etAmount.setText("1");

    }

    public void pay(View view) {

        boolean isInstalled = Utils.isPackageInstalled(Constants.SOFTPOS_PACKAGE_NAME, getPackageManager());

        if (!isInstalled) {

            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage("This Application require Mosambee SoftPOS in a Device")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        if ((etAmount.getText().toString() != "")) {

            int am = 0;

            try {
                am = Integer.parseInt(etAmount.getText().toString());

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Not a valid integer", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent();
            intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME);
            Bundle mBundle = new Bundle();
            mBundle.putString("amount", String.format("%d", am));
            mBundle.putString("sessionId", Utils.getToken(this));
            mBundle.putString("mobNo", "8424834651");
            mBundle.putString("description", "description");
            intent.putExtras(mBundle);
            intent.setAction(Constants.SOFTPOS_PAYMENT_ACTION);
            startActivityForResult(intent, Constants.ActivityPaymentRequestCode);
        } else {
            Toast.makeText(this, "Amount can not be black", Toast.LENGTH_LONG).show();
        }
    }


    public void healthCheck(View view) {
        boolean isInstalled = Utils.isPackageInstalled(Constants.SOFTPOS_PACKAGE_NAME, getPackageManager());

        if (!isInstalled) {

            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage("This Application require Mosambee SoftPOS in a Device")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        Intent intent = new Intent();
        intent.setAction(Constants.SOFTPOS_HEALTHCHECK_ACTION);
        intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString("sessionId", Utils.getToken(this));
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ActivityHealthCheckRequestCode);
    }

    public void bTinitApi(View view) {
        boolean isInstalled = Utils.isPackageInstalled(Constants.SOFTPOS_PACKAGE_NAME, getPackageManager());

        if (!isInstalled) {

            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage("This Application require Mosambee SoftPOS in a Device")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        if ((etUserId.getText().toString() != "") && (eTPass.getText().toString() != "")) {
            setPassword(eTPass.getText().toString());
            setUserId(etUserId.getText().toString());

            Intent intent = new Intent();
            Bundle mBundle = new Bundle();
            mBundle.putString("userName", getUserId());
            mBundle.putString("password", getPassword());
            intent.putExtras(mBundle);
            intent.setAction(Constants.SOFTPOS_INIT_ACTION);
            intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME);
            startActivityForResult(intent, Constants.ActivityLoginRequestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je;

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
            try {
                je = jp.parse(response);
                showResponse = gson.toJson(je);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Receipt")
                    .setMessage("ResponseCode : " + responseCode + "\n" +
                            "PaymentResponseCode : " + paymentResponseCode + "\n" +
                            "ReceiptResponse : " + showResponse + "\n" + "PaymentDescription : " + paymentDescription)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }


}