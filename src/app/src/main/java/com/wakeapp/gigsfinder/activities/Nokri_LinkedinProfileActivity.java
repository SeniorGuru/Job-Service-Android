package com.wakeapp.gigsfinder.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.wakeapp.gigsfinder.employeer.dashboard.Nokri_EmployeerDashboardActivity;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class Nokri_LinkedinProfileActivity extends Activity {
    private Nokri_DialogManager dialogManager;
    private static final String PROFILE_URL = "https://api.linkedin.com/v2/me";
    private static final String EMAIL_URL = "https://api.linkedin.com/v2/emailAddress";


    private static final String OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";
    private String selectedOption = "";
    private String[] accountTypes = new String[2];
    private ProgressDialog pd;
    public static boolean IS_SOURCE_LOGIN = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Request basic profile of the user
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
        String accessToken = preferences.getString("accessToken", null);
        if(accessToken!=null){
            String profileUrl = getProfileUrl(accessToken);
            new GetProfileRequestAsyncTask().execute(profileUrl);
            new GetProfileRequestAsyncTask().execute(getEmailUrl(accessToken));

        }
    }

    private static final String getProfileUrl(String accessToken){
        return PROFILE_URL
                +QUESTION_MARK
                +OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;
    }
    private static final String getEmailUrl(String accessToken){
        return EMAIL_URL
                +QUESTION_MARK
                +OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;
    }
    private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(Nokri_LinkedinProfileActivity.this, "", Nokri_LinkedinProfileActivity.this.getString(R.string.loading),true);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            if(urls.length>0){
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("x-li-format", "json");
                try{
                    HttpResponse response = httpClient.execute(httpget);
                    if(response!=null){
                        //If status is OK 200
                        if(response.getStatusLine().getStatusCode()==200){
                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object
                            return new JSONObject(result);
                        }
                    }
                }catch(IOException e){
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject data){
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            if(data!=null){

                try {
                  //  String welcomeTextString = String.format(data.getString("firstName"),data.getString("lastName"),data.getString("headline"));
                    if(IS_SOURCE_LOGIN) {
                        Nokri_SharedPrefManager.saveEmail(data.getString("emailAddress"), Nokri_LinkedinProfileActivity.this);
                        Nokri_SharedPrefManager.saveName(data.getString("firstName") + " " + data.getString("lastName"), Nokri_LinkedinProfileActivity.this);
                        Nokri_SharedPrefManager.saveHeadline(data.getString("headline"), Nokri_LinkedinProfileActivity.this);
                        Nokri_SharedPrefManager.saveLinkedinPublicProfile(data.getString("publicProfileUrl"), Nokri_LinkedinProfileActivity.this);
                        Nokri_SharedPrefManager.saveProfileImage(data.getString("pictureUrls"), Nokri_LinkedinProfileActivity.this);

                        nokri_postSocialSignin(data.getString("emailAddress"), "social");
                    }
                    else {
                        if(Nokri_SharedPrefManager.isAccountPublic(Nokri_LinkedinProfileActivity.this)) {
                            Nokri_SharedPrefManager.saveEmail(data.getString("emailAddress"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("firstName") + " " + data.getString("lastName"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveHeadline(data.getString("headline"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("pictureUrls"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveLinkedinPublicProfile(data.getString("publicProfileUrl"), Nokri_LinkedinProfileActivity.this);

                            nokri_postSocialSigninFromApplyJob(data.getString("emailAddress"),"social");
                        }
                        else {
                            Nokri_SharedPrefManager.saveLinkedinPublicProfile(data.getString("publicProfileUrl"), Nokri_LinkedinProfileActivity.this);
                            nokri_applyjobLinkedIn();
                        }
                    }



                } catch (JSONException e) {
                    Log.e("Authorize","Error Parsing json "+e.getLocalizedMessage());
                }
            }
        }


    };

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
                                Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,"Employeers cannot apply for job");
                                dialogManager.hideAlertDialog();
                                Nokri_SharedPrefManager.saveLoginType(type, Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.savePassword("pass", Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_LinkedinProfileActivity.this);
                                dialogManager.hideAlertDialog();

                                Intent intent = new Intent(Nokri_LinkedinProfileActivity.this, Nokri_EmployeerDashboardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                return;
                            }
                            else{
                                Nokri_SharedPrefManager.saveLoginType(type, Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.savePassword("pass", Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_LinkedinProfileActivity.this);
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_LinkedinProfileActivity.this);
                                nokri_postAccountType("0");

                            }


                        } else {
                            Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,respone.getString("message"));
                            dialogManager.hideAlertDialog();
                            Nokri_LinkedinProfileActivity.super.onBackPressed();
                            }
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();

                        e.printStackTrace();
                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();

                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,t.getMessage());
            }
        });
    }

    private void nokri_applyjobLinkedIn() {

        dialogManager = new Nokri_DialogManager();
        JsonObject object = new JsonObject();


        object.addProperty("job_id", Nokri_JobDetailFragment.JOB_ID);
        object.addProperty("url", Nokri_SharedPrefManager.getLinkedInPublicProfile(this));

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this),this);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(this)) {
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

                            Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveLoginType("social", Nokri_LinkedinProfileActivity.this);
                            Intent intent = new Intent(Nokri_LinkedinProfileActivity.this, Nokri_CandidateDashboardActivity.class);
                            intent.putExtra("linkedin",true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Nokri_ToastManager.showLongToast(Nokri_LinkedinProfileActivity.this,response.getString("message"));
                            dialogManager.hideAlertDialog();
                        } else {
                            /*Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveLoginType("social", Nokri_LinkedinProfileActivity.this);
                            Intent intent = new Intent(Nokri_LinkedinProfileActivity.this, Nokri_CandidateDashboardActivity.class);
                            intent.putExtra("linkedin",true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();*/
                            Nokri_ToastManager.showLongToast(Nokri_LinkedinProfileActivity.this,response.getString("message"));

                            dialogManager.hideAlertDialog();
                            Nokri_LinkedinProfileActivity.super.onBackPressed();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,responseObject.message());
                    dialogManager.hideAlertDialog();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,t.getMessage());
                dialogManager.hideAlertDialog();

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

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, email, Nokri_LinkedinProfileActivity.this);
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

                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveLoginType(type, Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.savePassword("pass", Nokri_LinkedinProfileActivity.this);

                            Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_LinkedinProfileActivity.this);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), Nokri_LinkedinProfileActivity.this);
                            // Default Sigin in type for testing
                            //     if(isCallbackFromLinkedin)


//---------------------------------------------------------------------
                            if (data.getString("acount_type").equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_LinkedinProfileActivity.this);
                                startActivity(new Intent(Nokri_LinkedinProfileActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (data.getString("acount_type").equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_LinkedinProfileActivity.this);
                                startActivity(new Intent(Nokri_LinkedinProfileActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            } else {

                                nokri_showAccountTypePopup();
                            }
                            Nokri_ToastManager.showLongToast(Nokri_LinkedinProfileActivity.this,respone.getString("message"));

                            dialogManager.hideAlertDialog();
                            /*startActivity(new Intent(Nokri_SigninActivity.this, Nokri_CandidateDashboardActivity.class));
                            finish();*/
                        } else {
                            Log.d("socail", responseObject.toString() + "error");
                            Nokri_ToastManager.showLongToast(Nokri_LinkedinProfileActivity.this,respone.getString("message"));
                            dialogManager.hideAlertDialog();
                            Nokri_LinkedinProfileActivity.super.onBackPressed();
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


                            final Dialog dialog = new Dialog(Nokri_LinkedinProfileActivity.this);
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
                            Nokri_Utils.setRoundButtonColor(Nokri_LinkedinProfileActivity.this,okButton);
                            okButton.setText(data.getString("continue"));
                            accountTypes[0] = data.getString("btn_cand");
                            accountTypes[1] = data.getString("btn_emp");
                            titleTextView.setText( accountTypes[0]);
                            Nokri_FontManager innerFontManager = new Nokri_FontManager();
                            innerFontManager.nokri_setMonesrratSemiBioldFont(titleTextView,getAssets());
                            innerFontManager.nokri_setOpenSenseFontTextView(headingTextView,getAssets());
                            innerFontManager.nokri_setOpenSenseFontButton(okButton,getAssets());

                            Nokri_Utils.setRoundButtonColor(Nokri_LinkedinProfileActivity.this,okButton);

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
                            Nokri_ToastManager.showLongToast(Nokri_LinkedinProfileActivity.this,respone.getString("message"));
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
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), "password", Nokri_LinkedinProfileActivity.this);
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
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_LinkedinProfileActivity.this);
                                startActivity(new Intent(Nokri_LinkedinProfileActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (type.equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_LinkedinProfileActivity.this);
                                startActivity(new Intent(Nokri_LinkedinProfileActivity.this, Nokri_EmployeerDashboardActivity.class));

                                finish();
                            }
                            dialogManager.hideAfterDelay();
                        } else {
                            Log.d("response", responseObject.toString() + "error");
                            Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,respone.getString("message"));
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

    private void nokri_postAccountType(final String type) {

        JsonObject params = new JsonObject();
        params.addProperty("user_type", type);
        params.addProperty("user_id", Nokri_SharedPrefManager.getId(this));
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), "password", Nokri_LinkedinProfileActivity.this);
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
                                nokri_applyjobLinkedIn();
                            }

                        } else {
                            Log.d("response", responseObject.toString() + "error");
                            Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,respone.getString("message"));
                            dialogManager.hideAlertDialog();
                            Nokri_LinkedinProfileActivity.super.onBackPressed();
                        }
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,e.getMessage());
                        e.printStackTrace();
                        dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,e.getMessage());
                        dialogManager.hideAlertDialog();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(Nokri_LinkedinProfileActivity.this,t.getMessage());
                dialogManager.hideAlertDialog();
            }
        });
    }


}