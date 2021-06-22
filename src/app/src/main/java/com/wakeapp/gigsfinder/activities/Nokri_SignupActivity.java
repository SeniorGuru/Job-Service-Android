package com.wakeapp.gigsfinder.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
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
import com.wakeapp.gigsfinder.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.wakeapp.gigsfinder.employeer.dashboard.Nokri_EmployeerDashboardActivity;
import com.wakeapp.gigsfinder.guest.dashboard.Nokri_GuestDashboardActivity;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_LanguageSupport;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.wakeapp.gigsfinder.R;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class
Nokri_SignupActivity extends AppCompatActivity implements View.OnFocusChangeListener,BaseToggleSwitch.OnToggleSwitchChangeListener,View.OnClickListener {

    TextView singUpTextView,alreadyTextView,signInTextView,termsTextView;
    private Nokri_FontManager fontManager;
    private Button facebookButton,googleButton,signupButton;
    private EditText nameEditText,emailEditText,passwordEditText,phoneNoEditText;
    private ImageView logoImageView;
    private View view;
    private ToggleSwitch toogleSwitch;
    private AppCompatCheckBox checkBox;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOLGE_STATUS_CODE = 999;
    private ArrayList<String> list = new ArrayList();
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private String[] accountTypes = new String[2];
    private String selectedOption = "";
    private Nokri_DialogManager dialogManager;
    private String termsUrl;
    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Nokri_Utils.changeSystemBarColor(this);
        setContentView(R.layout.activity_nokri_signup);
        fontManager = new Nokri_FontManager();

        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));

        singUpTextView = findViewById(R.id.txt_signup);
        alreadyTextView = findViewById(R.id.txt_already);
        signInTextView = findViewById(R.id.txt_signin);
        signInTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        facebookButton = findViewById(R.id.btn_facebook);
        signupButton = findViewById(R.id.btn_singup);
        googleButton = findViewById(R.id.btn_google);
        view=findViewById(R.id.viw1);
        view.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        logoImageView = findViewById(R.id.img_logo);



        toogleSwitch = findViewById(R.id.toogle_switch);
        toogleSwitch.setActiveBgColor(Color.parseColor(Nokri_Config.APP_COLOR));
        termsTextView = findViewById(R.id.txt_terms);



        fontManager.nokri_setMonesrratSemiBioldFont(singUpTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(termsTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(alreadyTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(signInTextView,getAssets());
        fontManager.nokri_setOpenSenseFontButton(facebookButton,getAssets());
        fontManager.nokri_setOpenSenseFontButton(googleButton,getAssets());
        fontManager.nokri_setOpenSenseFontButton(signupButton,getAssets());


        nameEditText = findViewById(R.id.edittxt_name);
        emailEditText = findViewById(R.id.edittxt_email);
        passwordEditText = findViewById(R.id.edittxt_password);
        phoneNoEditText = findViewById(R.id.edittxt_phone_no);

        checkBox = findViewById(R.id.checkbox);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {

                        Color.RED //disabled
                        ,Color.parseColor(Nokri_Config.APP_COLOR) //enabled

                }
        );

        checkBox.setSupportButtonTintList(colorStateList);

        fontManager.nokri_setOpenSenseFontEditText(nameEditText,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(passwordEditText,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(phoneNoEditText,getAssets());


        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        phoneNoEditText.setOnFocusChangeListener(this);
        toogleSwitch.setOnToggleSwitchChangeListener(this);

        signupButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        termsTextView.setOnClickListener(this);

        Nokri_Utils.setRoundButtonColor(this,signupButton);
        makeCall();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            // Nokri_ToastManager.showLongToast(Nokri_SigninActivity.this,"called");
//                           Log.d("facebook", String.valueOf(object.getJSONArray("public_profile")));
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

            }

            @Override
            public void onError(FacebookException error) {
                Log.v("facebook", error.getMessage() + error.getCause());
            }
        });

    }
    public void nokri_onClickSignIn(View view){
        startActivity(new Intent(Nokri_SignupActivity.this,Nokri_SigninActivity.class));
        finish();
    }
    public void nokri_onClickBack(View view){
        onBackPressed();
    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()){
            case R.id.edittxt_name:
                if(selected){

                    nameEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    passwordEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    phoneNoEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_email:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    passwordEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    phoneNoEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_password:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    passwordEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    phoneNoEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_phone_no:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    passwordEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    phoneNoEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                }
                break;
        }
    }

    private void makeCall(){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall =  restService.getRegisterSettings();
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        if(respone.getBoolean("success")){

                            JSONObject data = respone.getJSONObject("data");
                            Log.d("info",data.getString("bg_color"));
                            //String backgroud = data.getString("bg_color");
                           // Toast.makeText(Nokri_SignupActivity.this,backgroud,Toast.LENGTH_LONG).show();
                            if(!TextUtils.isEmpty(data.getString("logo")))
                            Picasso.with(Nokri_SignupActivity.this).load(data.getString("logo")).into(logoImageView);

                           // container.setBackgroundColor(Color.parseColor(data.getString("bg_color")));

                            nameEditText.setHint(data.getString("name_placeholder"));
                            phoneNoEditText.setHint(data.getString("phone_placeholder"));
                            emailEditText.setHint(data.getString("email_placeholder"));
                            passwordEditText.setHint(data.getString("password_placeholder"));
                            signupButton.setText(data.getString("form_btn"));
                            facebookButton.setText(data.getString("facebook_btn"));
                            googleButton.setText(data.getString("google_btn"));
                            String loginText = data.getString("login_text");
                            alreadyTextView.setText(loginText.substring(0,loginText.indexOf('?')));
                            signInTextView.setText(loginText.substring(loginText.indexOf('?'),loginText.length()));
                            list.add(data.getString("switch_cand"));
                            list.add(data.getString("switch_emp"));
                            termsTextView.setText(data.getString("terms_text"));
                            termsUrl = data.getString("terms_link");
//                            termsTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            toogleSwitch.setLabels(list);

                        }
                    else
                        {
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                dialogManager.hideAlertDialog();}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                 dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,t.getMessage());        }

        });
    }

    private void nokri_postSignup(String name, final String email, final String phone, final String password, final int type){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();

            params.addProperty("name",name);
            params.addProperty("email",email);
            params.addProperty("phone",phone);
            params.addProperty("pass",password);
            params.addProperty("type",type);
            RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
            Call<ResponseBody>myCall = restService.postRegister(params);
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    if(responseObj.isSuccessful()){

                        try {
                            JSONObject respone = new JSONObject(responseObj.body().string());
                            if(respone.getBoolean("success"))
                            {

                                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));

                                nokri_postSignin(email,password,type);
                                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));

                            }
                                else {
                                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));
                                dialogManager.hideAlertDialog();
                            }
                            } catch (JSONException e) {
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showShortToast(Nokri_SignupActivity.this,e.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showShortToast(Nokri_SignupActivity.this,e.getMessage());

                            e.printStackTrace();
                        }
                    }

                //    Nokri_DialogManager.hideAlertDialog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("info",call.toString());
                    dialogManager.hideAlertDialog();
                    Nokri_ToastManager.showShortToast(Nokri_SignupActivity.this,t.getMessage());

/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },2500);
*/

                    Toast.makeText(Nokri_SignupActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                }


                  });
    }

    private void nokri_postSignin(String email, final String password, final int type){


        JsonObject params = new JsonObject();
        params.addProperty("email",email);
        params.addProperty("pass",password);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class,email,password,Nokri_SignupActivity.this);
        Call<ResponseBody> myCall = restService.postLogin(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("response", respone.toString());
                        if(respone.getBoolean("success")){
                            JSONObject data = respone.getJSONObject("data");
                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"),Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.savePassword(password,Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.saveId(data.getString("id"),Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"),Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"),Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"),Nokri_SignupActivity.this);

                            if(type==0){
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SignupActivity.this);
                            startActivity(new Intent(Nokri_SignupActivity.this,Nokri_CandidateDashboardActivity.class));
                            finish();}
                            else
                                if(type ==1){
                                    Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SignupActivity.this);
                                    startActivity(new Intent(Nokri_SignupActivity.this,Nokri_EmployeerDashboardActivity.class));
                                    finish();

                                }
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));

                            dialogManager.hideAlertDialog();}
                            else
                        {     dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));


                        }
                    } catch (IOException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,e.getMessage());

                        e.printStackTrace();
                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,e.getMessage());

                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,t.getMessage());

            }
        });
    }


    @Override
    public void onToggleSwitchChangeListener(int position, boolean isChecked) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_singup:
           if(checkBox.isChecked()){
                String name = null,email = null,phone = null,password = null;
                int type;
                if(!nameEditText.getText().toString().trim().isEmpty())
                    name = nameEditText.getText().toString();
                if(!emailEditText.getText().toString().trim().isEmpty()) {
                    email = emailEditText.getText().toString();
                }
                if(!phoneNoEditText.getText().toString().trim().isEmpty())
                phone = phoneNoEditText.getText().toString();
                if(!passwordEditText.getText().toString().trim().isEmpty())
                    password = passwordEditText.getText().toString();
                 type =   toogleSwitch.getCheckedTogglePosition();

                 Nokri_Utils.checkEditTextForError(nameEditText);
               Nokri_Utils.checkEditTextForError(emailEditText);
               Nokri_Utils.checkEditTextForError(phoneNoEditText);
               Nokri_Utils.checkEditTextForError(passwordEditText);

                if(name!=null && email!=null && phone !=null && password!=null) {
                    if(Nokri_Utils.isValidEmail(email))
                    nokri_postSignup(name, email, phone, password, type);
                    else {
                        Nokri_ToastManager.showShortToast(this, Nokri_Globals.INVALID_EMAIL);
                         emailEditText.setError("!");
                    }
                    }
                else
                    Nokri_ToastManager.showShortToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);


           }
                 else
               Nokri_ToastManager.showShortToast(this, Nokri_Globals.TERMS_AND_SERVICES);
                break;

            case R.id.btn_google:
                signIn();
                break;
            case R.id.btn_facebook:
                loginManager.logInWithReadPermissions(Nokri_SignupActivity.this, Arrays.asList("email", "public_profile"));
                break;
            case R.id.txt_terms:
                Nokri_Utils.opeInBrowser(Nokri_SignupActivity.this,termsUrl);
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOLGE_STATUS_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOLGE_STATUS_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            nokri_postSocialSignin(account.getEmail(), "social");
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void nokri_postSocialSignin(String email, final String type) {
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

                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), getBaseContext());
                            Nokri_SharedPrefManager.saveLoginType(type, getBaseContext());
                            Nokri_SharedPrefManager.savePassword("pass", getBaseContext());

                            Nokri_SharedPrefManager.saveId(data.getString("id"), getBaseContext());
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), getBaseContext());
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), getBaseContext());
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), getBaseContext());

                            // Default Sigin in type for testing

//---------------------------------------------------------------------
                            if (data.getString("acount_type").equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (data.getString("acount_type").equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            } else {

                                nokri_showAccountTypePopup();
                            }
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));
                            dialogManager.hideAlertDialog();
                        } else {
                            Log.d("socail", responseObject.toString() + "error");
                            dialogManager.showError();
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));
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
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,t.getMessage());
            }
        });
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


                            final Dialog dialog = new Dialog(Nokri_SignupActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.account_type_popup);
                            dialog.findViewById(R.id.title_text_container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            final TextView titleTextView = dialog.findViewById(R.id.txt_title);
                            TextView headingTextView = dialog.findViewById(R.id.txt_heading);
                            final ToggleSwitch toggleSwitch = dialog.findViewById(R.id.toogle_switch);
                            toggleSwitch.setActiveBgColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            list.add(data.getString("btn_cand"));
                            list.add(data.getString("btn_emp"));
                            headingTextView.setText(data.getString("desc"));
                            toggleSwitch.setLabels(list);
                            Button okButton = dialog.findViewById(R.id.btn_ok);
                            okButton.setText(data.getString("continue"));
                            Nokri_Utils.setRoundButtonColor(Nokri_SignupActivity.this,okButton);
                            accountTypes[0] = data.getString("btn_cand");
                            accountTypes[1] = data.getString("btn_emp");
                            titleTextView.setText( accountTypes[0]);
                            Nokri_FontManager innerFontManager = new Nokri_FontManager();
                            innerFontManager.nokri_setMonesrratSemiBioldFont(titleTextView,getAssets());
                            innerFontManager.nokri_setOpenSenseFontTextView(headingTextView,getAssets());
                            innerFontManager.nokri_setOpenSenseFontButton(okButton,getAssets());

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
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,respone.getString("message"));
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
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,t.getMessage());
            }
        });
    }
    private void nokri_postAccountTypePopup(final String type) {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("user_type", type);
        params.addProperty("user_id", Nokri_SharedPrefManager.getId(this));
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), "password", Nokri_SignupActivity.this);
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
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (type.equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_EmployeerDashboardActivity.class));

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
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this,t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this,Nokri_GuestDashboardActivity.class));
        finish();
    }
}
