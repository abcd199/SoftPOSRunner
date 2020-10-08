package com.cpoc.softpostest;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int PAYMENT_REQUEST = 101;

    public static final int HEALTH_CHECK = 100;

    EditText etUserId,eTPass,etAmount;

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

        etAmount= findViewById(R.id.eTAmount);

        etUserId.setText("8233250524");

        eTPass.setText("1234");

        etAmount.setText("1");
//        getSupportActionBar().hide();
    }

    public void pay(View view) {
        if ((etAmount.getText().toString()!="")){

            int am = 0;

            try
            {
                // checking valid integer using parseInt() method
                am = Integer.parseInt(etAmount.getText().toString());

            }
            catch (NumberFormatException e)
            {
                Toast.makeText(this,"Not a valid integer",Toast.LENGTH_LONG).show();
                return;
            }

            if ((getUserId()!=null) && (getPassword()!=null) && (getUserId()!="") && (getPassword()!="")){
                Intent fmIntent = getPackageManager().getLaunchIntentForPackage("com.mosambee.mpos.softpos");
                fmIntent.setFlags(0);
                fmIntent.putExtra("amount", am+"");

                fmIntent.putExtra("userName", getUserId());

                fmIntent.putExtra("password", getPassword());

                fmIntent.setAction("com.mosambee.api");

                startActivityForResult(fmIntent, PAYMENT_REQUEST);
            }else{
                Toast.makeText(this,"Initialize API First",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Amount can not be black",Toast.LENGTH_LONG).show();
        }
    }


    public void payUsingMosambee(View view) {
        if ((etAmount.getText().toString()!="")){

            int am = 0;

            try
            {
                // checking valid integer using parseInt() method
                am = Integer.parseInt(etAmount.getText().toString());

            }
            catch (NumberFormatException e)
            {
                Toast.makeText(this,"Not a valid integer",Toast.LENGTH_LONG).show();
                return;
            }

            if ((getUserId()!=null) && (getPassword()!=null) && (getUserId()!="") && (getPassword()!="")){
//                Intent fmIntent = getPackageManager().getLaunchIntentForPackage("com.mosambee.mpos.cpoc");

                Intent fmIntent = new Intent();
                fmIntent.setComponent(new ComponentName("com.mosambee.mpos.cpoc", "com.activity.MerchantLauncherActivity"));


                fmIntent.setFlags(0);
                fmIntent.putExtra("amount", am+".00");

                fmIntent.putExtra("userName", getUserId());

                fmIntent.putExtra("password", getPassword());

                fmIntent.setAction("com.mosambee.api.app");

                startActivityForResult(fmIntent, PAYMENT_REQUEST);
            }else{
                Toast.makeText(this,"Initialize API First",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Amount can not be black",Toast.LENGTH_LONG).show();
        }
    }



    public void healthCheck(View view) {
        String INTENT_ACTION_HEALTH_CHECK = "health_check";

        Intent intent = new Intent();
        intent.setAction(INTENT_ACTION_HEALTH_CHECK);
        intent.addCategory("android.intent.category.DEFAULT");

        intent.setPackage("com.mosambee.mpos.softpos");
        startActivityForResult(intent, HEALTH_CHECK);
    }

    public void bTinitApi(View view) {

        if ((etUserId.getText().toString()!="") && (eTPass.getText().toString()!="")){
            setPassword(eTPass.getText().toString());
            setUserId(etUserId.getText().toString());
            Toast.makeText(this,"Initialise API",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==HEALTH_CHECK){

        Log.i("PASSED DATA", data.getStringExtra("response"));


            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage(Html.fromHtml(data.getStringExtra("response")))
                    .setNeutralButton("OK", null)
                    .show();
        }
    }

    /*    public void onTap(View view) {
        Intent fmIntent = getPackageManager().getLaunchIntentForPackage("com.mosambee.mpos.cpoc");
        fmIntent.setFlags(0);
        fmIntent.putExtra("amount", "120");

        fmIntent.putExtra("userName", "8233250524");

        fmIntent.putExtra("password", "1234");

        fmIntent.setAction("com.mosambee.api");

        startActivityForResult(fmIntent, PAYMENT_REQUEST);
    }*/
}