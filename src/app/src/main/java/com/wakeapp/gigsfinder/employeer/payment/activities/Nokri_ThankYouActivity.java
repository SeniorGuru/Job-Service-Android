package com.wakeapp.gigsfinder.employeer.payment.activities;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_LanguageSupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nokri_ThankYouActivity extends AppCompatActivity implements View.OnClickListener {
    WebView webView;
    private Nokri_DialogManager dialogManager;
    ImageView imageView;
    Button contineBuyPackage;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_thank_you);
        imageView = (ImageView) findViewById(R.id.closeIcon);
        contineBuyPackage = (Button) findViewById(R.id.contineBuyPackage);
        contineBuyPackage.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        contineBuyPackage.setText(Nokri_Globals.Continue_buy_Package);
        contineBuyPackage.setTextColor(Color.WHITE);
        imageView.setOnClickListener(this);
        contineBuyPackage.setOnClickListener(this);
        logo = findViewById(R.id.img_logo);
//        Picasso.with(this).load(R.drawable.logo).into(logo);
        webView = findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_SharedPrefManager.getAppColor(this)));
        }

        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
        nokri_getPackages();
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    private void nokri_getPackages() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this), this);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(this)) {
            myCall = restService.getThankYou(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getThankYou(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        webView.loadDataWithBaseURL(null, data.getString("data"), "text/html", "utf-8", null);

                        dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }

                } else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeIcon:
                finish();
                break;
            case R.id.contineBuyPackage:
                finish();
                break;
        }
    }
}
