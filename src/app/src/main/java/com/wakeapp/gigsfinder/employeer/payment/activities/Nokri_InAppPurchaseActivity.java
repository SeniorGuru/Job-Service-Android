package com.wakeapp.gigsfinder.employeer.payment.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nokri_InAppPurchaseActivity extends   AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkCXQyKE6qvZhg3oFxKvZrOL9efX/1v5qAw1IP7aT8xMH8bJrJjVJRHlpXxGJon0oaRbEb+TE49vqcT2JpP4imqJbAkYlU2S7pY/ffv6Satew8p+8fKE1ttjJOlZdU/IUP/1j5Gq75tZ7o/3ds/Hjyg2ZHGDXljME0Pg2KGpt+DXju82WVPTNhvhD331v9E8QugyAKpZH0GKJlbm8xdj7Lq3hQy+dDVEgq5/VKV9+hzNWVSbxFnXR22CCft5whthqvyMJYwQehJWSG50sI9XlBwLqDz2dOuYbyhwNY3Uv5j3CpBdsl/IbBa0NJT+Xzqu7//gbh+/+W02gpa3jkipkcwIDAQAB";
    // PUT YOUR MERCHANT KEY HERE;
// put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
// if filled library will provide protection against Freedom alike Play Market simulators
    private BillingProcessor bp;
    String porductId, packageId, packageType, activityName, billing_error, no_market = "", one_time = "";
    RestService restService;

    private Nokri_DialogManager dialogManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_in_app_purchase);
        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            porductId = getIntent().getStringExtra("porductId");
            activityName = getIntent().getStringExtra("activityName");
            billing_error = getIntent().getStringExtra("billing_error");
            no_market = getIntent().getStringExtra("no_market");
            one_time = getIntent().getStringExtra("one_time");
            LICENSE_KEY = getIntent().getStringExtra("LICENSE_KEY");

        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this),this);

        setTitle(activityName);
        bp = new BillingProcessor(this, LICENSE_KEY, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BillingProcessor.isIabServiceAvailable(Nokri_InAppPurchaseActivity.this)) {
                    if (bp.isOneTimePurchaseSupported()) {
                        bp.consumePurchase(porductId);
                        bp.purchase(Nokri_InAppPurchaseActivity.this, porductId);
                    } else {
                        if (one_time.equals(""))
                            Toast.makeText(Nokri_InAppPurchaseActivity.this, "One Time Purchase not Supported on your Device.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Nokri_InAppPurchaseActivity.this, one_time, Toast.LENGTH_SHORT).show();
                    }
                } else if (no_market.equals(""))
                    Toast.makeText(Nokri_InAppPurchaseActivity.this, "Play Market app is not installed.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Nokri_InAppPurchaseActivity.this, no_market, Toast.LENGTH_SHORT).show();
            }
        }, 200);

    }

    private void adforest_Checkout() {



        JsonObject params = new JsonObject();
        params.addProperty("package_id", packageId);
        params.addProperty("payment_from", packageType);
        Log.d("info Send Checkout", params.toString());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(this)) {
            myCall = restService.postPackages(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postPackages(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                try {

                    if (responseObj.isSuccessful()) {
                        Log.d("info Checkout Resp", "" + responseObj.toString());

                        JSONObject response = new JSONObject(responseObj.body().string());
                        Log.d("info Checkout object", "" + response.toString());
                        if (response.getBoolean("success")) {
                            //  settingsMain.setPaymentCompletedMessage(response.get("message").toString());
                            adforest_getDataForThankYou();
                        } else {
                            Toast.makeText(Nokri_InAppPurchaseActivity.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            dialogManager.hideAlertDialog();
                        }
                    }

                } catch (JSONException e) {
                    dialogManager.hideAlertDialog();
                    e.printStackTrace();
                } catch (IOException e) {
                    dialogManager.hideAlertDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof TimeoutException) {
                    //    Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    dialogManager.hideAlertDialog();
                }
                if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                    //     Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    dialogManager.hideAlertDialog();
                }
                if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                    Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                    dialogManager.hideAlertDialog();
                } else {
                    dialogManager.hideAlertDialog();
                    Log.d("info Checkout err", String.valueOf(t));
                    Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            }
        });

    }


    public void adforest_getDataForThankYou() {
        Intent intent = new Intent(this, Nokri_ThankYouActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        Log.d("info purchase", "onProductPurchased: " + productId + details.toString());
        bp.consumePurchase(porductId);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        adforest_Checkout();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        showToast(billing_error);
        finish();
    }

    @Override
    public void onBillingInitialized() {
        Log.d("info purchase", "onBillingInitialized");

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("info string ", resultCode + "" + requestCode);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}

