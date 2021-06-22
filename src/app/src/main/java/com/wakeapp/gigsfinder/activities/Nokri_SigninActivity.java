package com.wakeapp.gigsfinder.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import com.rey.material.widget.LinearLayout;
import com.wakeapp.gigsfinder.LinkedIn.LinkedInBuilder;
import com.wakeapp.gigsfinder.LinkedIn.helpers.LinkedInUser;
import com.wakeapp.gigsfinder.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.wakeapp.gigsfinder.guest.dashboard.Nokri_GuestDashboardActivity;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.employeer.dashboard.Nokri_EmployeerDashboardActivity;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_LanguageSupport;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nokri_SigninActivity extends AppCompatActivity implements View.OnFocusChangeListener,View.OnClickListener {
    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordTextView, newHereTextView, signInTextView, singUpTextView;
    private Nokri_FontManager fontManager;
    private Button facebookButton, googleButton, signinButton,linkedinButton;
    private ImageView logoImageView;
    private View view;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOLGE_STATUS_CODE = 999;
    public static boolean IS_SOURCE_LOGIN = true;
    private String logged = "";
    private String selectedOption = "";
    private String[] accountTypes = new String[2];
    private boolean isCallbackFromLinkedin = false;

    private Nokri_DialogManager dialogManager;
    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_signin);
        //Nokri_Utils.generateKeyhash(this);
        Nokri_Utils.changeSystemBarColor(this);
        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));

        fontManager = new Nokri_FontManager();
        view=findViewById(R.id.viw1);
        view.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        emailEditText = findViewById(R.id.edittxt_email);
        passwordEditText = findViewById(R.id.edittxt_password);
        forgotPasswordTextView = findViewById(R.id.txt_forgotpassword);
        forgotPasswordTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        newHereTextView = findViewById(R.id.txt_newhere);
        signInTextView = findViewById(R.id.txt_signin);
        singUpTextView = findViewById(R.id.txt_singup);


        logoImageView = findViewById(R.id.img_logo);

        facebookButton = findViewById(R.id.btn_facebook);
        signinButton = findViewById(R.id.btn_singin);
        googleButton = findViewById(R.id.btn_google);
        linkedinButton = findViewById(R.id.btn_linkedin);

        Nokri_Utils.setRoundButtonColor(this,signinButton);
        setupFonts();
        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        signinButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        linkedinButton.setOnClickListener(this);
        logged = getIntent().getStringExtra("type");

        if (logged == null)
            oportunities_makeServerRequest();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("facebookSuccess", loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            // Nokri_ToastManager.showLongToast(Nokri_SigninActivity.this,"called");
//                           Log.d("facebook", String.valueOf(object.getJSONArray("public_profile")));
                            Log.v("facebookComplete", response.getRawResponse());
                            isCallbackFromLinkedin = false;
                            nokri_postSocialSignin(object.getString("email"), "social");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("cancelled","on cancel called");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("facebookError", error.getMessage() + error.getCause());
            }
        });

        if (logged != null && logged.equals("logged")) {
            if (Nokri_SharedPrefManager.isSocialLogin(this)) {
                nokri_postSocialSignin(Nokri_SharedPrefManager.getEmail(this), "social");
            } else
                nokri_postSignin(Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this));
        }


    }

    private void setupFonts() {


        fontManager.nokri_setOpenSenseFontTextView(forgotPasswordTextView, getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(signInTextView, getAssets());
        fontManager.nokri_setOpenSenseFontTextView(singUpTextView, getAssets());
        fontManager.nokri_setOpenSenseFontTextView(newHereTextView, getAssets());

        fontManager.nokri_setOpenSenseFontButton(facebookButton, getAssets());
        fontManager.nokri_setOpenSenseFontButton(googleButton, getAssets());
        fontManager.nokri_setOpenSenseFontButton(signinButton, getAssets());

        fontManager.nokri_setOpenSenseFontEditText(emailEditText, getAssets());
        fontManager.nokri_setOpenSenseFontEditText(passwordEditText, getAssets());

    }

    public void nokri_onClickSignUp(View view) {
        startActivity(new Intent(Nokri_SigninActivity.this, Nokri_SignupActivity.class));
        finish();
    }

    public void nokri_onClickForgotPassword(View view) {
        startActivity(new Intent(Nokri_SigninActivity.this, Nokri_ForgotPasswordActivity.class));
        finish();
    }

    public void nokri_onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onFocusChange(View view, boolean selected) {

        switch (view.getId()) {

            case R.id.edittxt_email:
                if (selected) {
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    passwordEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_password:
                if (selected) {
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    passwordEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                }
                break;

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOLGE_STATUS_CODE);

    }

    private void oportunities_makeServerRequest() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.getLoginSetting();
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if (responseObject.isSuccessful()) {
                    try {
                        Log.d("responce", responseObject.toString());

                        JSONObject respone = new JSONObject(responseObject.body().string());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");
                            if(!TextUtils.isEmpty(data.getString("logo")))
                                Picasso.with(Nokri_SigninActivity.this).load(data.getString("logo")).into(logoImageView);
                            emailEditText.setHint(data.getString("email_placeholder"));
                            passwordEditText.setHint(data.getString("password_placeholder"));
                            signinButton.setText(data.getString("form_btn"));
                         /*   facebookButton.setText(data.getString("facebook_btn"));
                            googleButton.setText(data.getString("google_btn"));
                            linkedinButton.setText(data.getString("linkedin_btn"));*/

                            forgotPasswordTextView.setText(data.getString("forgot_text"));



                            if(data.getString("google_switch").equals("0"))
                                googleButton.setVisibility(View.GONE);
                            else
                                googleButton.setVisibility(View.VISIBLE);


                            if(data.getString("facebook_switch").equals("0"))
                                facebookButton.setVisibility(View.GONE);

                            else
                                facebookButton.setVisibility(View.VISIBLE);

                            if(data.getString("linkedin_switch").equals("0"))
                                linkedinButton.setVisibility(View.GONE);
                            else
                                linkedinButton.setVisibility(View.VISIBLE);


                            if(googleButton.getVisibility() == View.GONE && linkedinButton.getVisibility() == View.GONE)
                            {DisplayMetrics metrics = getResources().getDisplayMetrics();
                                float dp = 35f;
                                float fpixels = metrics.density * dp;
                                int pixels = (int) (fpixels + 0.5f);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        pixels,pixels);

                                params.setMargins(0, 0, 0, 0);
                                facebookButton.setLayoutParams(params);
                            }
                            if(googleButton.getVisibility() == View.GONE && facebookButton.getVisibility() == View.GONE)
                            {DisplayMetrics metrics = getResources().getDisplayMetrics();
                                float dp = 35f;
                                float fpixels = metrics.density * dp;
                                int pixels = (int) (fpixels + 0.5f);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        pixels,pixels);

                                params.setMargins(0, 0, 0, 0);
                                linkedinButton.setLayoutParams(params);
                            }

                            String text1 = data.getString("register_text");
                            int firtPhraseLengts = text1.length();
                            String text2 = data.getString("register_text2");
                            String finalText = text1+" "+text2;
                            final SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

                            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor(Nokri_Config.APP_COLOR));
                            sb.setSpan(fcs, firtPhraseLengts, finalText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            singUpTextView.setText(sb);
                            //    Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                        }
                        else
                        {
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //    dialog.dismiss();
                dialogManager.hideAlertDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  dialog.showError();
                dialogManager.showError();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 2500);
            }
        });
    }

    private void nokri_postSignin(String email, final String password) {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("email", email);
        params.addProperty("pass", password);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, password, Nokri_SigninActivity.this);
        Call<ResponseBody> myCall = restService.postLogin(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());

                        if (respone.getBoolean("success")) {
                            Log.d("response", respone.getBoolean("success") + "error");
                            JSONObject data = respone.getJSONObject("data");
                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.savePassword(password, Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), Nokri_SigninActivity.this);
//                            Log.d("profile_img", data.getJSONObject("profile_img").getString("img"));
                            Nokri_SharedPrefManager.saveLoginType(null, Nokri_SigninActivity.this);
                            if (data.getString("user_type").equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SigninActivity.this);

                                startActivity(new Intent(Nokri_SigninActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if(data.getString("user_type").equals("1")){
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);

                                startActivity(new Intent(Nokri_SigninActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            }
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));

                        } else {
                            Log.d("response", responseObject.toString() + "error");
                            dialogManager.hideAfterDelay();
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                        }
                    } catch (IOException e) {
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,e.getMessage());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        dialogManager.hideAfterDelay();
                        Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,e.getMessage());
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,t.getMessage());
            }
        });
    }


    private void nokri_postSocialSignin(String email, final String type) {
        Log.d("socail", email + type);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("type", type);
        params.addProperty("email", email);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, email, Nokri_SigninActivity.this);
//        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.postSocialLogin(params, Nokri_RequestHeaderManager.addSocialHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("socail", respone.toString());
                        if (respone.getBoolean("success")) {

                            JSONObject data = respone.getJSONObject("data");

                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.saveLoginType(type, Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.savePassword("pass", Nokri_SigninActivity.this);

                            Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SigninActivity.this);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), Nokri_SigninActivity.this);
                            // Default Sigin in type for testing
                            //     if(isCallbackFromLinkedin)


//---------------------------------------------------------------------
                            if (data.getString("acount_type").equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SigninActivity.this);
                                startActivity(new Intent(Nokri_SigninActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (data.getString("acount_type").equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                startActivity(new Intent(Nokri_SigninActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            } else {

                                nokri_showAccountTypePopup();
                            }
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));

                            dialogManager.hideAlertDialog();
                            /*startActivity(new Intent(Nokri_SigninActivity.this, Nokri_CandidateDashboardActivity.class));
                            finish();*/
                        } else {
                            Log.d("socail", responseObject.toString() + "error");
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                            dialogManager.showError();
                            dialogManager.hideAlertDialog();
                        }
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
//                        Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        //  Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAlertDialog();

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_singin:
                isCallbackFromLinkedin = false;
                String email = null, password = null;
                if (!emailEditText.getText().toString().isEmpty())
                    email = emailEditText.getText().toString();
                if (!passwordEditText.getText().toString().isEmpty())
                    password = passwordEditText.getText().toString();

                if (email != null && password != null) {
                    nokri_postSignin(email, password);
                } else {
                    Toast.makeText(Nokri_SigninActivity.this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_facebook:

                isCallbackFromLinkedin = false;
                loginManager.logInWithReadPermissions(Nokri_SigninActivity.this, Arrays.asList("email", "public_profile"));

                break;
            case R.id.btn_google:
                signIn();
                isCallbackFromLinkedin = false;
                break;
            case R.id.btn_linkedin:
                LinkedInBuilder.getInstance(Nokri_SigninActivity.this)
                        .setClientID(Nokri_Config.LINKEDIN_CLIENT_ID)
                        .setClientSecret(Nokri_Config.LINKEDIN_CLIENT_SECRET)
                        .setRedirectURI(Nokri_Config.LINKEDIN_REDIRECT_URL)
                        .authenticate(25);
                break;
            default:
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(!isCallbackFromLinkedin)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOLGE_STATUS_CODE) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            isCallbackFromLinkedin = false;
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(isCallbackFromLinkedin){


        }
        if (requestCode == 25 && data != null) {
            if (resultCode == RESULT_OK) {
                //Successfully signed in
                LinkedInUser user = data.getParcelableExtra("social_login");

                //acessing user info
                Log.i("LinkedInLogin", user.getId());
                Log.i("LinkedInLogin", user.getFirstName());
                Log.i("LinkedInLogin", user.getLastName());
                Log.i("LinkedInLogin", user.getAccessToken());
                Log.i("LinkedInLogin", user.getProfileUrl());
                Log.i("LinkedInLogin", user.getEmail());

                //  String welcomeTextString = String.format(data.getString("firstName"),data.getString("lastName"),data.getString("headline"));
                if(IS_SOURCE_LOGIN) {
                    Nokri_SharedPrefManager.saveEmail(user.getEmail(),Nokri_SigninActivity.this);
                    Nokri_SharedPrefManager.saveName(user.getFirstName()+ " " + user.getLastName(), Nokri_SigninActivity.this);
                    Nokri_SharedPrefManager.saveLinkedinPublicProfile(user.getProfileUrl(), Nokri_SigninActivity.this);
                    nokri_postSocialSignin(Nokri_SharedPrefManager.getEmail(this), "social");
                }
                else {
                    if(Nokri_SharedPrefManager.isAccountPublic(Nokri_SigninActivity.this)) {
                        Nokri_SharedPrefManager.saveEmail(data.getStringExtra("emailAddress"),Nokri_SigninActivity.this);

//                            Nokri_SharedPrefManager.saveEmail(data.getString("emailAddress"), Nokri_SigninActivity.this);
                        Nokri_SharedPrefManager.saveName(data.getStringExtra("firstName") + " " + data.getStringExtra("lastName"), Nokri_SigninActivity.this);
                        Nokri_SharedPrefManager.saveHeadline(data.getStringExtra("headline"), Nokri_SigninActivity.this);
                        Nokri_SharedPrefManager.saveProfileImage(data.getStringExtra("pictureUrls"), Nokri_SigninActivity.this);
                        Nokri_SharedPrefManager.saveLinkedinPublicProfile(data.getStringExtra("publicProfileUrl"), Nokri_SigninActivity.this);

                        nokri_postSocialSigninFromApplyJob(data.getStringExtra("emailAddress"),"social");
                    }
                    else {
                        Nokri_SharedPrefManager.saveLinkedinPublicProfile(data.getStringExtra("publicProfileUrl"), Nokri_SigninActivity.this);
//                        nokri_applyjobLinkedIn(this);
                    }
                }




            } else {

                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                    //Handle : user denied access to account

                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_FAILED) {

                    //Handle : Error in API : see logcat output for details
                    Log.e("LINKEDIN ERROR", data.getStringExtra("err_message"));
                }
            }
        }


    }

    private void nokri_postSocialSigninFromApplyJob(String email, final String type) {
        Log.d("socail", email + type);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("type", type);
        params.addProperty("email", email);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, email, this);
//        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.postSocialLogin(params, Nokri_RequestHeaderManager.addSocialHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("socail", respone.toString());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");




                            Log.d("chussss",data.getString("acount_type"));

                            if (data.getString("acount_type").trim().equals("1")) {
                                Log.d("chussss","inside");
                                Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,"Employeers cannot apply for job");
                                dialogManager.hideAlertDialog();
                                Nokri_SharedPrefManager.saveLoginType(type, Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.savePassword("pass", Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                dialogManager.hideAlertDialog();

                                Intent intent = new Intent(Nokri_SigninActivity.this, Nokri_EmployeerDashboardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                return;
                            }
                            else{
                                Nokri_SharedPrefManager.saveLoginType(type, Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.savePassword("pass", Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SigninActivity.this);
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SigninActivity.this);
                                nokri_postAccountType("0");

                            }


                        } else {
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                            dialogManager.hideAlertDialog();
                            Nokri_SigninActivity.super.onBackPressed();
                        }
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();

                        e.printStackTrace();
                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();

                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,t.getMessage());
            }
        });
    }
    public static void nokri_applyjobLinkedIn(Context context) {

        Nokri_DialogManager dialogManager = new Nokri_DialogManager();
        JsonObject object = new JsonObject();


        object.addProperty("job_id", Nokri_JobDetailFragment.JOB_ID);
        object.addProperty("url", Nokri_SharedPrefManager.getLinkedInPublicProfile(context));
        Log.d("url",Nokri_SharedPrefManager.getLinkedInPublicProfile(context).toString());

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(context), Nokri_SharedPrefManager.getPassword(context),context);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(context)) {
            myCall = restService.applyJobLinkedin(object, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.applyJobLinkedin(object, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateSkills(object, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {

                            Nokri_SharedPrefManager.saveAccountType("candidate", context);
                            Nokri_SharedPrefManager.saveLoginType("social", context);
                            Intent intent = new Intent(context, Nokri_CandidateDashboardActivity.class);
                            intent.putExtra("linkedin",true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

                            Nokri_ToastManager.showLongToast(context,response.getString("message"));
                            dialogManager.hideAlertDialog();
                        } else {
                            Nokri_ToastManager.showLongToast(context,response.getString("message"));

                            dialogManager.hideAlertDialog();

                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(context,e.getMessage());
                        dialogManager.hideAlertDialog();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(context,e.getMessage());
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showShortToast(context,responseObject.message());
                    dialogManager.hideAlertDialog();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(context,t.getMessage());
                dialogManager.hideAlertDialog();

            }
        });
    }

    private void nokri_postAccountType(final String type) {

        JsonObject params = new JsonObject();
        params.addProperty("user_type", type);
        params.addProperty("user_id", Nokri_SharedPrefManager.getId(this));
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), "password", Nokri_SigninActivity.this);
        Call<ResponseBody> myCall = restService.postAccountTypleSelector(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("response", respone.toString());
                        if (respone.getBoolean("success")) {

                            if (type.equals("0")) {
                                dialogManager.hideAlertDialog();
//                                nokri_applyjobLinkedIn(Nokri_SigninActivity.this);
                            }

                        } else {
                            Log.d("response", responseObject.toString() + "error");
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                            dialogManager.hideAlertDialog();
                            Nokri_SigninActivity.super.onBackPressed();
                        }
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,e.getMessage());
                        e.printStackTrace();
                        dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,t.getMessage());
                dialogManager.hideAlertDialog();
            }
        });
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            nokri_postSocialSignin(account.getEmail(), "social");
        } catch (ApiException e) {
            Log.w("Whoopsie", "signInResult:failed code=" + e.getStatusCode());

            e.printStackTrace();
        }
    }


    private void nokri_showAccountTypePopup() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.getAccoutTypeSelector(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if (responseObject.isSuccessful()) {
                    try {
                        Log.d("responce", responseObject.toString());

                        JSONObject respone = new JSONObject(responseObject.body().string());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");


                            ArrayList<String> list = new ArrayList();


                            final Dialog dialog = new Dialog(Nokri_SigninActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.account_type_popup);
                            final TextView titleTextView = dialog.findViewById(R.id.txt_title);
                            TextView headingTextView = dialog.findViewById(R.id.txt_heading);
                            final ToggleSwitch toggleSwitch = dialog.findViewById(R.id.toogle_switch);
                            dialog.findViewById(R.id.title_text_container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            list.add(data.getString("btn_cand"));
                            list.add(data.getString("btn_emp"));
                            headingTextView.setText(data.getString("desc"));
                            toggleSwitch.setLabels(list);
                            toggleSwitch.setActiveBgColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            Button okButton = dialog.findViewById(R.id.btn_ok);
                            Nokri_Utils.setRoundButtonColor(Nokri_SigninActivity.this,okButton);
                            okButton.setText(data.getString("continue"));
                            accountTypes[0] = data.getString("btn_cand");
                            accountTypes[1] = data.getString("btn_emp");
                            titleTextView.setText( accountTypes[0]);
                            Nokri_FontManager innerFontManager = new Nokri_FontManager();
                            innerFontManager.nokri_setMonesrratSemiBioldFont(titleTextView,getAssets());
                            innerFontManager.nokri_setOpenSenseFontTextView(headingTextView,getAssets());
                            innerFontManager.nokri_setOpenSenseFontButton(okButton,getAssets());

                            Nokri_Utils.setRoundButtonColor(Nokri_SigninActivity.this,okButton);

                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    dialogManager.hideAlertDialog();
                                }
                            });
                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedOption =     toggleSwitch.getCheckedTogglePosition()+"";
                                    nokri_postAccountTypePopup(selectedOption);
                                    dialog.dismiss();

                                }
                            });
                            toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
                                @Override
                                public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                                    switch (position)
                                    {
                                        case 0:
                                            titleTextView.setText( accountTypes[position]);
                                            break;
                                        case 1:
                                            titleTextView.setText( accountTypes[position]);
                                            break;

                                    }
                                }
                            });
                            dialog.show();



                        }
                        else
                        {
                            Nokri_ToastManager.showShortToast(Nokri_SigninActivity.this,respone.getString("message"));
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                dialogManager.hideAlertDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  dialog.showError();
                dialogManager.showError();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 2500);
            }
        });
    }
    private void nokri_postAccountTypePopup(final String type) {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("user_type", type);
        params.addProperty("user_id", Nokri_SharedPrefManager.getId(this));
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), "password", Nokri_SigninActivity.this);
        Call<ResponseBody> myCall = restService.postAccountTypleSelector(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("response", respone.toString());
                        if (respone.getBoolean("success")) {

                            if (type.equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SigninActivity.this);
                                startActivity(new Intent(Nokri_SigninActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (type.equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                startActivity(new Intent(Nokri_SigninActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            }
                            dialogManager.hideAfterDelay();
                        } else {
                            Log.d("response", responseObject.toString() + "error");
                            dialogManager.showCustom(respone.getString("message"));
                            dialogManager.hideAlertDialog();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showError();
                dialogManager.hideAlertDialog();
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,Nokri_GuestDashboardActivity.class));
        finish();
    }
}




