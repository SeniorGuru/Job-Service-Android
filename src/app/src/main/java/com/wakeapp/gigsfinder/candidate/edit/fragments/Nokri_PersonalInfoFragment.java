package com.wakeapp.gigsfinder.candidate.edit.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.guest.dashboard.Nokri_GuestDashboardActivity;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.custom.ProgressRequestBody;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_UploadProgressDialolque;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_PathUtils;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.wakeapp.gigsfinder.utils.models.Nokri_EditTextModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_PersonalInfoFragment extends Fragment implements View.OnFocusChangeListener,View.OnClickListener,DatePickerDialog.OnDateSetListener,View.OnTouchListener,ProgressRequestBody.UploadCallbacks,AdapterView.OnItemSelectedListener, Nokri_PopupManager.ConfirmInterface{

    private static final int PICK_IMAGE_PROFILE = 200;
    private  TextView nameDataTextView,headlineDataTextView,nameTextView,phoneTextView,emailTextView,headlineTextView,dateTextView,profileImageTextView,aboutTextView,statusTextView,changePasswordTextView,deleteAccountTextView;;
    private EditText nameEditText,phoneEditText,emailEditText,headlineEditText,dateEditText,pofileImaeEditText;
    private RichEditor aboutEditText;
    private Button saveProfileButton;
    private ImageView boldImageView,italicImageView,underlineImageView,numberBulletsImageView,listBulletsImageView;
    private Nokri_FontManager fontManager;
    private CircularImageView profileImageView;
    private Calendar calendar;
    private ArrayList<String>idList;
    private ArrayList<Nokri_EditTextModel>editTextModels;
    private Nokri_UploadProgressDialolque progressDialolque;

    private LinearLayout textarea;
    private LinearLayout container;

    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private Nokri_DialogManager dialogManager;

    private Spinner levelSpinner,typeSpinner,experienceSpinner,statusSpinner;
    private TextView levelTextView,typeTextView,experienceTextView;
    private ArrayList<String>levelSpinnerIds,typeSpinnerIds,experienceSpinnerIds,statusSpinnerIds;
    private Nokri_PopupManager popupManager;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }
    protected void onUserFirstSight() {

    }

    protected void onUserVisibleChanged(boolean visible) {
        if(getContext()!=null)
        {
            if(visible){


            }

        }
    }

    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }
    protected void onViewCreatedFirstSight(View view) {

    }


    public Nokri_PersonalInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nokri_initialize();
        nokri_setDataFromSharedref();
        nokri_getCandidatePersonalInfo();
        nokri_setFonts();
        nokri_setListeners();


    }





    private void nokri_setListeners() {

        pofileImaeEditText.setOnFocusChangeListener(this);
        dateEditText.setOnFocusChangeListener(this);
        nameEditText.setOnFocusChangeListener(this);
        phoneEditText.setOnFocusChangeListener(this);

        headlineEditText.setOnFocusChangeListener(this);
        saveProfileButton.setOnClickListener(this);
        pofileImaeEditText.setOnTouchListener(this);

        dateEditText.setOnTouchListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_personal_info, container, false);
    }

    private void nokri_initialize(){
        editTextModels = new ArrayList<>();
        fontManager = new Nokri_FontManager();
        nameDataTextView = getView().findViewById(R.id.txt_name_data);
        headlineDataTextView = getView().findViewById(R.id.txt_headline_data);
        calendar = Calendar.getInstance();
        nameTextView = getView().findViewById(R.id.txt_name);
        phoneTextView  = getView().findViewById(R.id.txt_phone);
        saveProfileButton = getView().findViewById(R.id.btn_saveprofile);
        Nokri_Utils.setEditBorderButton(getContext(),saveProfileButton);
        emailTextView = getView().findViewById(R.id.txt_email);
        headlineTextView = getView().findViewById(R.id.txt_headline);
        dateTextView = getView().findViewById(R.id.txt_date);
        statusTextView = getView().findViewById(R.id.txt_status);
        profileImageTextView = getView().findViewById(R.id.txt_profile_image);

        aboutTextView = getView().findViewById(R.id.txt_about);
        container = getView().findViewById(R.id.container1);
        changePasswordTextView = getView().findViewById(R.id.txt_change_password);
        deleteAccountTextView = getView().findViewById(R.id.txt_delete_account);
        changePasswordTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        if(Nokri_SharedPrefManager.isSocialLogin(getContext()))
            changePasswordTextView.setVisibility(View.GONE);

        nameEditText = getView().findViewById(R.id.edittxt_name);
        phoneEditText = getView().findViewById(R.id.edittxt_phone);
        emailEditText = getView().findViewById(R.id.edittxt_email);
        headlineEditText = getView().findViewById(R.id.edittxt_headline);
        dateEditText = getView().findViewById(R.id.edittxt_date);

        pofileImaeEditText = getView().findViewById(R.id.edittxt_profile_image);

        aboutEditText = getView().findViewById(R.id.edittxt_descripton);

        textarea = getView().findViewById(R.id.textarea);

        aboutEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.rich_text_size));

        aboutEditText.setBackground(getActivity().getResources().getDrawable(R.drawable.rectangle));
        aboutEditText.setTextColor(Color.RED);
        profileImageView = getView().findViewById(R.id.img_profile);

        boldImageView= getView().findViewById(R.id.img_bold);
        italicImageView= getView().findViewById(R.id.img_italic);
        underlineImageView= getView().findViewById(R.id.img_underline);
        numberBulletsImageView= getView().findViewById(R.id.img_num_bullets);
        listBulletsImageView= getView().findViewById(R.id.img_list_bullets);


        boldImageView.setOnClickListener(this);
        italicImageView.setOnClickListener(this);
        underlineImageView.setOnClickListener(this);
        numberBulletsImageView.setOnClickListener(this);
        listBulletsImageView.setOnClickListener(this);
        changePasswordTextView.setOnClickListener(this);
        deleteAccountTextView.setOnClickListener(this);

        typeSpinner = getView().findViewById(R.id.spinner_type);
        levelSpinner = getView().findViewById(R.id.spinner_level);
        experienceSpinner = getView().findViewById(R.id.spinner_experience);
        typeTextView = getView().findViewById(R.id.txt_type);
        levelTextView = getView().findViewById(R.id.txt_level);
        experienceTextView = getView().findViewById(R.id.txt_experience);
        statusSpinner = getView().findViewById(R.id.spinner_status);
        typeSpinnerIds  = new ArrayList<>();
        levelSpinnerIds = new ArrayList<>();
        experienceSpinnerIds = new ArrayList<>();
        statusSpinnerIds = new ArrayList<>();
        aboutEditText.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        aboutEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));


    }
    private void nokri_setFonts(){
        fontManager.nokri_setMonesrratSemiBioldFont(nameDataTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(headlineDataTextView,getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(phoneTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(emailTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(headlineTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(dateTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(levelTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(typeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(experienceTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(profileImageTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(statusTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(aboutTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(changePasswordTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(deleteAccountTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(nameEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(phoneEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(headlineEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(dateEditText,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(pofileImaeEditText,getActivity().getAssets());

        //

        fontManager.nokri_setOpenSenseFontButton(saveProfileButton,getActivity().getAssets());

    }
    private void nokri_setDataFromSharedref(){

        if(Nokri_SharedPrefManager.getProfileImage(getContext())!=null && !Nokri_SharedPrefManager.getProfileImage(getContext()).trim().isEmpty())
            Picasso.with(getContext()).load(Nokri_SharedPrefManager.getProfileImage(getContext())).into(profileImageView);
        String name = Nokri_SharedPrefManager.getName(getContext());
        if(name!=null)
        {
            nameDataTextView.setText(name);
            nameEditText.setHint(name);
        }
        String headLine = Nokri_SharedPrefManager.getHeadline(getContext());
        if(headLine!=null)
        {
            headlineEditText.setHint(headLine);
            headlineDataTextView.setText(headLine);
        }

        textarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutEditText.focusEditor();
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(container.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        });
    }

    private void uploadProfileImageRequest(String absolutePath){
        progressDialolque = new Nokri_UploadProgressDialolque(getContext());
        progressDialolque.showUploadDialogue();



        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody (file,this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("logo_img",file.getName(),requestBody);
        RestService restService =  Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        final Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadProfileImage(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else

        {
            myCall = restService.postUploadProfileImage(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }
        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        //  Call<ResponseBody> myCall  = restService.postUploadProfileImage(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if(responseObject.isSuccessful()){

                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        if(jsonObject.getBoolean("success")){
                            Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));
                            JSONObject data = jsonObject.getJSONObject("data");
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("logo_img"),getContext());
                            Log.v("uploaded profile",data.getString("logo_img"));
                            if(!TextUtils.isEmpty(data.getString("logo_img")))
                            Picasso.with(getContext()).load(data.getString("logo_img")).into(profileImageView);
                               /* if(!jsonObject.getBoolean("data")){
                                    Log.v("Cover Upload","failed");
                                }
                                else
                                {

                                }*/
                            if(!TextUtils.isEmpty(data.getString("logo_img"))) {
                                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                View headerView = navigationView.getHeaderView(0);
                                CircularImageView profileImage = headerView.findViewById(R.id.img_profile);

                                Picasso.with(getContext()).load(data.getString("logo_img")).fit().centerCrop().into(profileImage);
                            }


                            Log.v("Profile Upload",jsonObject.get("data").toString());

                    progressDialolque.handleSuccessScenerion();
                        }
                        else
                        {Log.v("Profile Upload",jsonObject.get("data").toString());
                            Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));
                            progressDialolque.handleFailedScenerio();
                        }
                    } catch (JSONException e) {
                        progressDialolque.handleFailedScenerio(e.getMessage());
                        Log.v("Profile Upload",e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        progressDialolque.handleFailedScenerio();
                        e.printStackTrace();
                        Log.v("Profile Upload",e.getMessage());}
                }
                dialogManager.hideAlertDialog();  }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                progressDialolque.handleFailedScenerio(t.getMessage());
                Log.v("Profile Upload",t.getMessage()); }
        });
    }


    private void nokri_getCandidatePersonalInfo(){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidatePersonalinfo(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidatePersonalinfo(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if(response.getBoolean("success")){

                            JSONArray extrasArray = response.getJSONArray("extras");

                            for(int i=0;i<extrasArray.length();i++){
                                JSONObject extraObject = extrasArray.getJSONObject(i);
                                if(extraObject.getString("field_type_name").equals("section_name"))
                                {
                                    nameTextView.setText(extraObject.getString("value")+":");
                                }
                                else
                                if(extraObject.getString("field_type_name").equals("btn_name"))
                                {
                                    saveProfileButton.setText(extraObject.getString("value"));
                                }
                                if(extraObject.getString("field_type_name").equals("change_pasword"))
                                {
                                    changePasswordTextView.setText(extraObject.getString("value"));

                                }
                                if(extraObject.getString("field_type_name").equals("del_acount"))
                                {
                                    deleteAccountTextView.setText(extraObject.getString("value"));

                                }

                            }

                            JSONArray dataJsonArray = response.getJSONArray("data");
                            for(int i=0;i<dataJsonArray.length();i++){
                                Nokri_EditTextModel editTextModel;
                                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                                if(jsonObject.getString("field_type_name").equals("cand_name")){
                                    nameEditText.setText(jsonObject.getString("value"));
                                    nameTextView.setText(jsonObject.getString("key"));
                                    nameEditText.setHint(jsonObject.getString("key"));
                                    nameDataTextView.setText(jsonObject.getString("value"));
                                    Nokri_SharedPrefManager.saveName(jsonObject.getString("value"),getContext());
                                    editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(nameEditText);
                                    editTextModel.setRequired(jsonObject);
                                }
                                else  if(jsonObject.getString("field_type_name").equals("cand_phone")){
                                    phoneEditText.setText(jsonObject.getString("value"));
                                    phoneTextView.setText(jsonObject.getString("key"));
                                    phoneEditText.setHint(jsonObject.getString("key"));

                                    editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(phoneEditText);
                                    editTextModel.setRequired(jsonObject);

                                }

                                else  if(jsonObject.getString("field_type_name").equals("")){
                                    emailEditText.setText(jsonObject.getString("value"));
                                    emailTextView.setText(jsonObject.getString("key"));
                                    emailEditText.setHint(jsonObject.getString("key"));

                                    editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(emailEditText);
                                    editTextModel.setRequired(jsonObject);
                                }
                                else  if(jsonObject.getString("field_type_name").equals("cand_headline")){
                                    headlineEditText.setText(jsonObject.getString("value"));
                                    headlineDataTextView.setText(jsonObject.getString("value"));
                                    headlineTextView.setText(jsonObject.getString("key"));
                                    headlineEditText.setHint(jsonObject.getString("key"));

                                    editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(headlineEditText);
                                    editTextModel.setRequired(jsonObject);
                                }
                                else  if(jsonObject.getString("field_type_name").equals("cand_dob")){
                                    dateEditText.setText(jsonObject.getString("value"));
                                    dateTextView.setText(jsonObject.getString("key"));
                                    dateEditText.setHint(jsonObject.getString("key"));

                                    editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(dateEditText);
                                    editTextModel.setRequired(jsonObject);

                                }
                                else  if(jsonObject.getString("field_type_name").equals("cand_dp")){

                                    profileImageTextView.setText(jsonObject.getString("key"));
                                    pofileImaeEditText.setHint(jsonObject.getString("key"));

                                    editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(pofileImaeEditText);
                                    editTextModel.setRequired(jsonObject);
                                }
                                else  if(jsonObject.getString("field_type_name").equals("cand_cvr")){




                                    editTextModel = new Nokri_EditTextModel();

                                    editTextModel.setRequired(jsonObject);
                                }

                                else  if(jsonObject.getString("field_type_name").equals("cand_level")){
                                    int selectedIndex = 0;
                                    levelTextView.setText(jsonObject.getString("key"));
                                    JSONArray array = jsonObject.getJSONArray("value");
                                    ArrayList<String>educationList = new ArrayList<>();

                                    for(int j =0;j<array.length();j++){
                                        JSONObject object = array.getJSONObject(j);
                                        if(object.getBoolean("selected")){
                                            selectedIndex = j;
                                        }
                                        educationList.add(object.getString("value"));
                                        levelSpinnerIds.add(object.getString("key"));
                                    }
                                    levelSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));
                                    levelSpinner.setSelection(selectedIndex);
                                }

                                else  if(jsonObject.getString("field_type_name").equals("cand_type")){
                                    int selectedIndex = 0;
                                    typeTextView.setText(jsonObject.getString("key"));
                                    JSONArray array = jsonObject.getJSONArray("value");
                                    ArrayList<String>educationList = new ArrayList<>();

                                    for(int j =0;j<array.length();j++){
                                        JSONObject object = array.getJSONObject(j);
                                        if(object.getBoolean("selected")){
                                            selectedIndex = j;
                                        }
                                        educationList.add(object.getString("value"));
                                        typeSpinnerIds.add(object.getString("key"));
                                    }
                                    typeSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));
                                    typeSpinner.setSelection(selectedIndex);
                                }

                                else  if(jsonObject.getString("field_type_name").equals("cand_experience")){
                                    int selectedIndex = 0;
                                    experienceTextView.setText(jsonObject.getString("key"));
                                    JSONArray array = jsonObject.getJSONArray("value");
                                    ArrayList<String>educationList = new ArrayList<>();

                                    for(int j =0;j<array.length();j++){
                                        JSONObject object = array.getJSONObject(j);
                                        if(object.getBoolean("selected")){
                                            selectedIndex = j;
                                        }
                                        educationList.add(object.getString("value"));
                                        experienceSpinnerIds.add(object.getString("key"));
                                    }
                                    experienceSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));
                                    experienceSpinner.setSelection(selectedIndex);
                                }


                                else  if(jsonObject.getString("field_type_name").equals("cand_prof_stat")){


                                    if(!jsonObject.getBoolean("is_required"))
                                    {
                                        statusTextView.setVisibility(View.GONE);
                                        statusSpinner.setVisibility(View.GONE);
                                    }


                                    int selectedIndex = 0;
                                    statusTextView.setText(jsonObject.getString("key"));
                                    JSONArray array = jsonObject.getJSONArray("value");
                                    ArrayList<String>statusList = new ArrayList<>();

                                    for(int j =0;j<array.length();j++){
                                        JSONObject object = array.getJSONObject(j);
                                        if(object.getBoolean("selected")){
                                            selectedIndex = j;
                                        }
                                        statusList.add(object.getString("value"));
                                        statusSpinnerIds.add(object.getString("key"));
                                    }
                                    statusSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,statusList));
                                    statusSpinner.setSelection(selectedIndex);
                                }


                                else  if(jsonObject.getString("field_type_name").equals("cand_intro")){
                                    aboutEditText.setHtml(jsonObject.getString("value"));
                                    aboutEditText.setPlaceholder(jsonObject.getString("key"));

                                    aboutTextView.setText(jsonObject.getString("key"));

                                  /*  editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(nameEditText);
                                    editTextModel.setRequired(jsonObject);*/

                                }
                            }
                            dialogManager.hideAlertDialog();
                        }
                        else
                        {
                            dialogManager.showCustom(response.getString("message"));
                            dialogManager.hideAfterDelay();
                        }
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }


                }
                else{
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }
    private void nokri_postPersonalInfo(String name, String phone, String headline, String dob, String type, String level, String intro, String experience,String status){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cand_name",name);
        jsonObject.addProperty("cand_phone",phone);
        jsonObject.addProperty("cand_headline",headline);
        jsonObject.addProperty("cand_dob",dob);
        jsonObject.addProperty("cand_type",type);
        jsonObject.addProperty("cand_level",level);
        jsonObject.addProperty("cand_intro",intro);
        jsonObject.addProperty("cand_experience",experience);
        jsonObject.addProperty("cand_prof_stat",status);
        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidatePersonalInfo(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postCandidatePersonalInfo(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        // Call<ResponseBody> myCall = restService.postCandidatePersonalInfo(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response",responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            nokri_getCandidatePersonalInfo();
                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
                        }

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                }
                else {
                    dialogManager.showCustom(responseObject.code()+"");
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == PICK_IMAGE_PROFILE && data!=null)
            uploadProfileImageRequest(Nokri_PathUtils.getPath(getContext(),data.getData()));
    }


    private void getDataFromEditText(){

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String headline = headlineEditText.getText().toString();
        String dateOfBirth = dateEditText.getText().toString();

        String type = typeSpinnerIds.get(typeSpinner.getSelectedItemPosition());
        String level = levelSpinnerIds.get(levelSpinner.getSelectedItemPosition());
        String about = aboutEditText.getHtml();
        String experience = experienceSpinnerIds.get(experienceSpinner.getSelectedItemPosition());
        String status = statusSpinnerIds.get(statusSpinner.getSelectedItemPosition());
        Nokri_Utils.checkEditTextForError(nameEditText);
        Nokri_Utils.checkEditTextForError(phoneEditText);
        Nokri_Utils.checkEditTextForError(headlineEditText);
        Nokri_Utils.checkEditTextForError(dateEditText);

        if(aboutEditText!=null && !about.isEmpty()){

            getView().findViewById(R.id.line).setBackgroundColor( getResources().getColor(R.color.gray));
            if(!name.trim().isEmpty()&&!phone.trim().isEmpty()&&!headline.trim().isEmpty()&&!dateOfBirth.trim().isEmpty()&&!about.trim().isEmpty()&&!status.trim().isEmpty()){
                nokri_postPersonalInfo(name,phone,headline,dateOfBirth,type,level,about,experience,status);
            }
            else
            {
                Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            }
        }
        else
        {getView().findViewById(R.id.line).setBackgroundColor(Color.RED);
            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);


}

    }


    private void selectImageFromGallery(int flag) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_PROFILE);
    }


    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()){
            case R.id.edittxt_name:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));




                }
                break;
            case R.id.edittxt_phone:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                }
                break;
            case R.id.edittxt_email:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                }
                break;
            case R.id.edittxt_headline:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                }
                break;
            case R.id.edittxt_date:
                if(selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    //new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                break;

            case R.id.edittxt_profile_image:
                if(selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));

                    //  selectImageFromGallery(1);
                }
                break;


            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_bold:
                aboutEditText.setBold();
                 break;
            case R.id.img_italic:
                aboutEditText.setItalic();
                break;
            case R.id.img_underline:
                aboutEditText.setUnderline();
                break;
            case R.id.img_num_bullets:
                aboutEditText.setNumbers();
                break;
            case R.id.img_list_bullets:
                aboutEditText.setBullets();
                break;
            case R.id.btn_saveprofile:
                getDataFromEditText();
                break;
            case R.id.txt_change_password:
                nokri_getResetPassword();
                break;
            case R.id.txt_delete_account:
                popupManager = new Nokri_PopupManager(getContext(),this);
                popupManager.nokri_showDeletePopup();

                break;

            default:
                break;
        }
    }



    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.edittxt_profile_image:

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    selectImageFromGallery(1);

                break;

            case R.id.edittxt_date:
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
        }

        return false;
    }

    @Override
    public void onProgressUpdate(int percentage) {

progressDialolque.updateProgress(percentage);
    }

    @Override
    public void onError() {
progressDialolque.handleFailedScenerio();
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onConfirmClick(Dialog dialog) {

        nokri_deleteAccont();
        dialog.dismiss();
    }

    private void nokri_deleteAccont(){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();

        params.addProperty("user_id", Nokri_SharedPrefManager.getId(getContext()));


        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.deleteAccount(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.deleteAccount(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getAppliedJobs(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if(responseObject.isSuccessful()){
                    try {


                        JSONObject response = new JSONObject(responseObject.body().string());
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        Nokri_SharedPrefManager.invalidate(getContext());
                        Intent intent = new Intent(getActivity(), Nokri_GuestDashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);


                    } catch (IOException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (JSONException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();

                    }

                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    private void nokri_getResetPassword(){
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());


        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getResetPassword(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getResetPassword(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if(jsonObject.getBoolean("success")){

                            JSONObject data = jsonObject.getJSONObject("data");

                            showDeleteDialog(data.getString("logo"),data.getString("old_password"),data.getString("new_password"),data.getString("confirm_password"),data.getString("ok"),data.getString("cancel"));


                        }

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();

                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }
    private void showDeleteDialog(String url,String oldPasswordText,String newPasswordText,String confirmPasswodText,String okButtonText,String cancelButtonText){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_reset_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText oldPasswordEditText = dialog.findViewById(R.id.edittxt_old_password);
        final EditText newPasswordEditText = dialog.findViewById(R.id.edittxt_new_password);
        final EditText confirmPasswordEditText = dialog.findViewById(R.id.edittxt_confirm_password);
        CircularImageView logo = dialog.findViewById(R.id.logo);

        Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(logo);
        oldPasswordEditText.setHint(oldPasswordText);
        newPasswordEditText.setHint(newPasswordText);
        confirmPasswordEditText.setHint(confirmPasswodText);


        Button okButton = dialog.findViewById(R.id.btn_ok);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);
        okButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        cancelButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        okButton.setText(okButtonText);
        cancelButton.setText(cancelButtonText);

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setOpenSenseFontEditText(oldPasswordEditText,getContext().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(newPasswordEditText,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(confirmPasswordEditText,getContext().getAssets());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                nokri_postResetPassword(oldPassword,newPassword,confirmPassword);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

        //  dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int)getContext().getResources().getDimension(R.dimen.reset_password_height));
        dialogManager.hideAlertDialog();
    }


    private void nokri_postResetPassword(String oldPassword, final String newPassword, String confirmPassword){

        JsonObject params = new JsonObject();
        params.addProperty("old_password",oldPassword);
        params.addProperty("new_password",newPassword);
        params.addProperty("confirm_password",confirmPassword);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postResetPassword(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postResetPassword(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        if(response.getBoolean("success")) {
                            Nokri_SharedPrefManager.savePassword(newPassword,getContext());


                        }
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
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

                }
                else {
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

}

