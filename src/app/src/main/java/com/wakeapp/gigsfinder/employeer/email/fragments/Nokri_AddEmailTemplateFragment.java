package com.wakeapp.gigsfinder.employeer.email.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_JobsFragment;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_AddEmailTemplateFragment extends Fragment implements View.OnClickListener,View.OnFocusChangeListener{
//MMMMM d, yyyy
    private TextView nameTextView,emailTextView,aboutTextView,titleTextView,forTextView;
    private EditText nameEditText,emailEditText;
    private RichEditor aboutEditText;
    private ImageView boldImageView,italicImageView,underlineImageView,numberBulletsImageView,listBulletsImageView;
    private Nokri_FontManager fontManager;
    private Button saveTemplateButton,addMoreButton;
    private static final DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
    private Spinner spinner;
    private ArrayList<String>names,ids;
    private Class fragmentClass;
    private Nokri_DialogManager dialogManager;
    private Fragment fragment;
    public Nokri_AddEmailTemplateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_add_email_template, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentClass = Nokri_JobsFragment.class;
        nokri_initialize();
        nokri_setFonts();
        if(!Nokri_EditEmailTemplate.IS_Update)
        nokri_getEmployeerEmailLabels();

        else{
          nokri_setDataFromPost(Nokri_EditEmailTemplate.ID);
        }
    }

    private void nokri_initialize(){
        fontManager = new Nokri_FontManager();
        getView().findViewById(R.id.container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        nameTextView = getView().findViewById(R.id.txt_name);
        emailTextView = getView().findViewById(R.id.txt_email);
        aboutTextView = getView().findViewById(R.id.txt_about);
        titleTextView = getView().findViewById(R.id.txt_title);
        forTextView  = getView().findViewById(R.id.txt_for);

        nameEditText = getView().findViewById(R.id.edittxt_name);
        emailEditText = getView().findViewById(R.id.edittxt_email);
        aboutEditText = getView().findViewById(R.id.edittxt_about);
        aboutEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.rich_text_size));
        aboutEditText.setBackground(getActivity().getResources().getDrawable(R.drawable.rectangle));



        boldImageView= getView().findViewById(R.id.img_bold);
        italicImageView= getView().findViewById(R.id.img_italic);
        underlineImageView= getView().findViewById(R.id.img_underline);
        numberBulletsImageView= getView().findViewById(R.id.img_num_bullets);
        listBulletsImageView= getView().findViewById(R.id.img_list_bullets);

        saveTemplateButton = getView().findViewById(R.id.btn_savetemplate);
        Nokri_Utils.setEditBorderButton(getContext(),saveTemplateButton);
        addMoreButton = getView().findViewById(R.id.btn_add_more);

        spinner = getView().findViewById(R.id.spinner);

        names = new ArrayList<>();
        ids = new ArrayList<>();

        boldImageView.setOnClickListener(this);
        italicImageView.setOnClickListener(this);
        underlineImageView.setOnClickListener(this);
        numberBulletsImageView.setOnClickListener(this);
        listBulletsImageView.setOnClickListener(this);


        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);

        saveTemplateButton.setOnClickListener(this);
        addMoreButton.setOnClickListener(this);

    }


    private void nokri_setFonts(){

        fontManager.nokri_setMonesrratSemiBioldFont(titleTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(emailTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(aboutTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(forTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(nameEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getActivity().getAssets());




        fontManager.nokri_setOpenSenseFontButton(saveTemplateButton,getActivity().getAssets());
       // fontManager.nokri_setOpenSenseFontButton(addMoreButton,getActivity().getAssets());

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
            case R.id.btn_savetemplate:
                Nokri_Utils.checkEditTextForError(nameEditText);
                Nokri_Utils.checkEditTextForError(emailEditText);
                if(aboutEditText!=null && aboutEditText.getHtml()!=null){

                    if(!nameEditText.getText().toString().trim().isEmpty() && nameEditText.getText().toString().trim()!=null && !emailEditText.getText().toString().trim().isEmpty() && emailEditText.getText().toString().trim()!=null && ! aboutEditText.getHtml().toString().isEmpty())
                    {   if(!Nokri_EditEmailTemplate.IS_Update)
                        nokri_postEmailTemplate();
                        else
                            nokri_postUpdateEmailTemplate(Nokri_EditEmailTemplate.ID);

                    }
                    else
                    {                    aboutEditText.setEditorFontColor(Color.RED);
                        Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    }
                }
                else
                {aboutEditText.setEditorFontColor(getResources().getColor(R.color.gray));
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                }

                break;
            case  R.id.btn_add_more:
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment editEmailTemplate = new Nokri_EditEmailTemplate();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),editEmailTemplate).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()){
            case R.id.edittxt_name:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_email:
                if(selected){
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                }
                break;
            default:
                break;
        }
    }

    private void nokri_getEmployeerEmailLabels(){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerEmailLabels(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getEmployeerEmailLabels(Nokri_RequestHeaderManager.addHeaders());
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
                                if(extraObject.getString("field_type_name").equals("add"))
                                {
                                    titleTextView.setText(extraObject.getString("value"));

                                }

                                else   if(extraObject.getString("field_type_name").equals("save"))
                                {
                                    saveTemplateButton.setText(extraObject.getString("value"));

                                }
                                else
                                if(extraObject.getString("field_type_name").equals("view"))
                                {
                                    addMoreButton.setText(extraObject.getString("value"));
                                }
                                if(extraObject.getString("field_type_name").equals("page_title"))
                                {
                                   TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                   toolbarTitleTextView.setText(extraObject.getString("value"));
                                }

                            }

                            JSONArray dataJsonArray = response.getJSONArray("data");
                            for(int i=0;i<dataJsonArray.length();i++){
                                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                                if(jsonObject.getString("field_type_name").equals("email_temp_name")){
                                    nameEditText.setText(jsonObject.getString("value"));
                                    nameTextView.setText(jsonObject.getString("key"));
                                    nameEditText.setHint(jsonObject.getString("key"));
                                }


                                else  if(jsonObject.getString("field_type_name").equals("email_temp_subject")){
                                    emailEditText.setText(jsonObject.getString("value"));
                                    emailTextView.setText(jsonObject.getString("key"));
                                    emailEditText.setHint(jsonObject.getString("key"));
                                }
                                else  if(jsonObject.getString("field_type_name").equals("email_temp_details")){


                                    aboutTextView.setText(jsonObject.getString("key"));
                                    aboutEditText.setPlaceholder(jsonObject.getString("key"));
                                    aboutEditText.setHtml(jsonObject.getString("value"));
                                }

                                else  if(jsonObject.getString("field_type_name").equals("email_temp_for")){
                                    forTextView.setText(jsonObject.getString("key"));
                                    JSONArray spinnerArray = jsonObject.getJSONArray("value");
                                    for(int j=0;j<spinnerArray.length();j++){

                                        names.add(spinnerArray.getJSONObject(j).getString("key"));
                                        ids.add(spinnerArray.getJSONObject(j).getString("value"));

                                    }
                                }




                            }

                            if(spinner.getAdapter() == null)
                            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,names));
                            dialogManager .hideAlertDialog();

                        }
                        else
                        {
                           Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                            dialogManager .hideAfterDelay();
                        }
                    } catch (JSONException e) {

                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();
                    }


                }
                else{
                    Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
                    dialogManager .hideAfterDelay();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager .hideAfterDelay();
            }
        });
    }


    private void nokri_postEmailTemplate(){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email_temp_name",nameEditText.getText().toString());
        jsonObject.addProperty("email_temp_subject",emailEditText.getText().toString());
        jsonObject.addProperty("email_temp_details",aboutEditText.getHtml());
        jsonObject.addProperty("email_temp_date",sdf.format(new Date()));
        jsonObject.addProperty("email_temp_for",ids.get(spinner.getSelectedItemPosition()));


        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postEmailTeplate(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postEmailTeplate(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response",responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager .hideAlertDialog();
                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                            nameEditText.setText("");
                            emailEditText.setText("");

                        } else {
                            dialogManager .showCustom(response.getString("message"));

                            dialogManager .hideAfterDelay();
                        }

                        if (fragmentClass != null) {
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (fragment != null) {
                                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();
                            }

                            fragmentClass = null;
                        }
                    } catch (JSONException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();

                    }
                }
                else {
                    dialogManager .showCustom(responseObject.code()+"");
                    dialogManager .hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager .showCustom(t.getMessage());
                dialogManager .hideAfterDelay();

            }
        });
    }


    private void nokri_postUpdateEmailTemplate(final String id){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("template_id",id);
        jsonObject.addProperty("email_temp_name",nameEditText.getText().toString());
        jsonObject.addProperty("email_temp_subject",emailEditText.getText().toString());
        jsonObject.addProperty("email_temp_details",aboutEditText.getHtml());
        jsonObject.addProperty("email_temp_date",sdf.format(new Date()));
        jsonObject.addProperty("email_temp_for",ids.get(spinner.getSelectedItemPosition()));


        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postEmailTeplate(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postEmailTeplate(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response",responseObject.message());
                        if (response.getBoolean("success")) {
                           Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                            dialogManager .hideAlertDialog();

                            nokri_setDataFromPost(id);

                            if (fragmentClass != null) {
                                try {
                                    fragment = (Fragment) fragmentClass.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (fragment != null) {
                                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();
                                }

                                fragmentClass = null;
                            }
                        } else {

                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                            dialogManager .hideAfterDelay();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                        dialogManager .hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();

                    }
                }
                else {
                    Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
                    dialogManager .hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager .hideAfterDelay();

            }
        });
    }


    private void nokri_setDataFromPost(String id){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("template_id",id);




        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postEmailTeplate(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postEmailTeplate(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response",responseObject.message());
                        if (response.getBoolean("success")) {


                            JSONArray extrasArray = response.getJSONArray("extras");

                            for(int i=0;i<extrasArray.length();i++){
                                JSONObject extraObject = extrasArray.getJSONObject(i);
                                if(extraObject.getString("field_type_name").equals("add"))
                                {
                                    titleTextView.setText(extraObject.getString("value"));

                                }

                                else   if(extraObject.getString("field_type_name").equals("save"))
                                {
                                    saveTemplateButton.setText(extraObject.getString("value"));

                                }
                                else
                                if(extraObject.getString("field_type_name").equals("view"))
                                {
                                    addMoreButton.setText(extraObject.getString("value"));
                                }
                                else
                                if(extraObject.getString("field_type_name").equals("page_title"))
                                {
                                    TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                    toolbarTitleTextView.setText(extraObject.getString("value"));
                                }

                            }

                            JSONArray dataJsonArray = response.getJSONArray("data");
                            for(int i=0;i<dataJsonArray.length();i++){
                                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                                if(jsonObject.getString("field_type_name").equals("email_temp_name")){
                                    nameEditText.setText(jsonObject.getString("value"));
                                    nameTextView.setText(jsonObject.getString("key"));
                                    nameEditText.setHint(jsonObject.getString("key"));
                                }


                                else  if(jsonObject.getString("field_type_name").equals("email_temp_subject")){
                                    emailEditText.setText(jsonObject.getString("value"));
                                    emailTextView.setText(jsonObject.getString("key"));
                                    emailEditText.setHint(jsonObject.getString("key"));
                                }
                                else  if(jsonObject.getString("field_type_name").equals("email_temp_details")){


                                    aboutTextView.setText(jsonObject.getString("key"));
                                    aboutEditText.setPlaceholder(jsonObject.getString("key"));
                                    aboutEditText.setHtml(jsonObject.getString("value"));
                                }

                                else  if(jsonObject.getString("field_type_name").equals("email_temp_for")){
                                    forTextView.setText(jsonObject.getString("key"));
                                    JSONArray spinnerArray = jsonObject.getJSONArray("value");
                                    for(int j=0;j<spinnerArray.length();j++){

                                        names.add(spinnerArray.getJSONObject(j).getString("key"));
                                        ids.add(spinnerArray.getJSONObject(j).getString("value"));

                                    }
                                }




                            }

                            if(spinner.getAdapter() == null)
                                spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,names));
                            dialogManager .hideAlertDialog();


                        } else {
                            dialogManager .showCustom(response.getString("message"));

                            dialogManager .hideAfterDelay();
                        }

                    } catch (JSONException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();

                    }
                }
                else {
                    dialogManager .showCustom(responseObject.code()+"");
                    dialogManager .hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager .showCustom(t.getMessage());
                dialogManager .hideAfterDelay();

            }
        });
    }


}
