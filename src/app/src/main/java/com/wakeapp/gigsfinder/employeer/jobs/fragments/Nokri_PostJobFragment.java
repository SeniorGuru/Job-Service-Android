package com.wakeapp.gigsfinder.employeer.jobs.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.custom.MaterialProgressBar;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_SpinnerModel;
import com.wakeapp.gigsfinder.employeer.payment.fragments.Nokri_PricingTableFragment;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Nokri_PostJobFragment extends Fragment implements View.OnFocusChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener, TimePickerDialog.OnTimeSetListener {
    private RichEditor jobDetailsEditor;
    private String jobDetails;
    private ImageView boldImageView, italicImageView, underlineImageView, numberBulletsImageView, listBulletsImageView;
    private Nokri_FontManager fontManager;

    String calledFrom;
    Boolean categorySelected, PaidSelected, positionSelected;

    private EditText jobTitleEditText, applicatiponDeadlineEditText, timelineEditText, spaceEditText, equipEditText, venueEditText, capacityEditText;
    private String title, jobCategory, deadline, isPaid, space, equip, position, timeline, venue, capacity;
    private String jobId;
    private Nokri_SpinnerModel paidSpinnerModel, jobCategorySpinnerModel, positionSpinnerModel;
    private Button publishJobButton;
    private TextView jobTitleTextView, jobDescriptionTextView, spaceTextView, equipTextView, venueTextView, capacityTextView;
    private TextView applicationDeadlineTextView, timelineTextView;
    private TextView paidTextView, jobCategoryTextView, positionTextView;

    private Calendar calendar;
    private  Spinner paidSpinner, jobCategorySpinner, positionSpinner;

    private Nokri_DialogManager dialogManager;
    private MaterialProgressBar progressBar;

    public static String POST_JOB_CALLING_SOURCE = "";
    public static String POST_JOB_ID = "";

    public static boolean purchased;

    private int index = 0;

    @SuppressLint("ValidFragment")
    public Nokri_PostJobFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_post_job, container, false);
        if (getArguments() != null) {
            calledFrom = getArguments().getString("calledFrom");

            if (calledFrom.equals("edit")) {
				categorySelected = true;
                PaidSelected = true;
                positionSelected = true;
            } else {
				categorySelected = false;
                PaidSelected = false;
                positionSelected = false;
            }
        }
        if (calledFrom == null) {
            calledFrom = "new";
			categorySelected = true;
            PaidSelected = true;
            positionSelected = true;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        if (POST_JOB_CALLING_SOURCE.equals("external"))
            nokri_getUpdatePostJob(POST_JOB_ID);
        else {
            nokri_getPostJob();
            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

            toolbarTitleTextView.setText(model.getPostJob());
        }
    }

    /*    @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);




        }*/
    private void nokri_setupFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(jobTitleTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(jobCategoryTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(positionTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(jobDescriptionTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(applicationDeadlineTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(timelineTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(spaceTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(paidTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(equipTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(venueTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(capacityTextView, getActivity().getAssets());


        fontManager.nokri_setOpenSenseFontEditText(jobTitleEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(venueEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(capacityEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(applicatiponDeadlineEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(spaceEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(equipEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(publishJobButton, getActivity().getAssets());
    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();

        jobTitleTextView = getView().findViewById(R.id.txt_job_title);
        jobCategoryTextView = getView().findViewById(R.id.txt_category);
        positionTextView = getView().findViewById(R.id.txt_position);
        jobDescriptionTextView = getView().findViewById(R.id.txt_job_description);
        applicationDeadlineTextView = getView().findViewById(R.id.txt_application_deadline);
        timelineTextView = getView().findViewById(R.id.txt_application_timeline);
        spaceTextView = getView().findViewById(R.id.txt_available_spaces);
        equipTextView = getView().findViewById(R.id.txt_equip_available);
        paidTextView = getView().findViewById(R.id.txt_is_paid);
        venueTextView = getView().findViewById(R.id.txt_job_venue);
        capacityTextView = getView().findViewById(R.id.txt_job_capacity);

        jobTitleEditText = getView().findViewById(R.id.edittxt_job_title);
        applicatiponDeadlineEditText = getView().findViewById(R.id.edittxt_appication_deadline);
        timelineEditText = getView().findViewById(R.id.edittxt_appication_timeline);
        spaceEditText = getView().findViewById(R.id.edittxt_available_spaces);
        equipEditText = getView().findViewById(R.id.edittxt_equip_available);
        venueEditText = getView().findViewById(R.id.edittxt_job_venue);
        capacityEditText = getView().findViewById(R.id.edittxt_job_capacity);

        jobDetailsEditor = getView().findViewById(R.id.edittxt_descripton);

        jobDetailsEditor.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        jobDetailsEditor.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));

        jobCategorySpinner = getView().findViewById(R.id.spinner_category);
        paidSpinner = getView().findViewById(R.id.spinner_is_paid);
        positionSpinner = getView().findViewById(R.id.spinner_position);

        progressBar = getView().findViewById(R.id.progress);

        publishJobButton = getView().findViewById(R.id.btn_publish_job);

        Nokri_Utils.setRoundButtonColor(getContext(), publishJobButton);

        boldImageView = getView().findViewById(R.id.img_bold);
        italicImageView = getView().findViewById(R.id.img_italic);
        underlineImageView = getView().findViewById(R.id.img_underline);
        numberBulletsImageView = getView().findViewById(R.id.img_num_bullets);
        listBulletsImageView = getView().findViewById(R.id.img_list_bullets);


        calendar = Calendar.getInstance();

        boldImageView.setOnClickListener(this);
        italicImageView.setOnClickListener(this);
        underlineImageView.setOnClickListener(this);
        numberBulletsImageView.setOnClickListener(this);
        listBulletsImageView.setOnClickListener(this);

        publishJobButton.setOnClickListener(this);
        applicatiponDeadlineEditText.setOnTouchListener(this);
        timelineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);

                        int secondString = 0;
                        if (selectedMinute == 00) {

                            timelineEditText.setText( selectedHour + ":" + selectedMinute + secondString);
                        } else if (selectedHour < 10) {
                            timelineEditText.setText( selectedHour + ":" + secondString + selectedMinute);

                        } else {
                            timelineEditText.setText(selectedHour + ":" + selectedMinute);
                        }


                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        applicatiponDeadlineEditText.setOnFocusChangeListener(this);
        jobTitleEditText.setOnFocusChangeListener(this);
        spaceEditText.setOnFocusChangeListener(this);
        equipEditText.setOnFocusChangeListener(this);

        LinearLayout textarea = getView().findViewById(R.id.textarea);
        final LinearLayout container = getView().findViewById(R.id.container1);
        textarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobDetailsEditor.focusEditor();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(container.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bold:
                jobDetailsEditor.setBold();
                break;
            case R.id.img_italic:
                jobDetailsEditor.setItalic();
                break;
            case R.id.img_underline:
                jobDetailsEditor.setUnderline();
                break;
            case R.id.img_num_bullets:
                jobDetailsEditor.setNumbers();
                break;
            case R.id.img_list_bullets:
                jobDetailsEditor.setBullets();
                break;

            case R.id.btn_publish_job:
                if (purchased == true) {

                    if (nokri_areFieldsEmpty()) {
                        Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                        if (jobDetailsEditor.getHtml() == null || jobDetailsEditor.getHtml().trim().isEmpty())
                            getView().findViewById(R.id.line).setBackgroundColor(Color.RED);

                        else
                            getView().findViewById(R.id.line).setBackgroundColor(getResources().getColor(R.color.gray));
                    } else {
                        nokri_setValues();
                        nokri_postJob();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Please buy a package to post event", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.edittxt_job_title:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                spaceEditText.setHintTextColor(getResources().getColor(R.color.grey));
                equipEditText.setHintTextColor(getResources().getColor(R.color.grey));
                break;

            case R.id.edittxt_appication_deadline:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                spaceEditText.setHintTextColor(getResources().getColor(R.color.grey));
                equipEditText.setHintTextColor(getResources().getColor(R.color.grey));
                break;
            case R.id.edittxt_available_spaces:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                spaceEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                equipEditText.setHintTextColor(getResources().getColor(R.color.grey));
            case R.id.edittxt_equip_available:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                spaceEditText.setHintTextColor(getResources().getColor(R.color.grey));
                equipEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
            default:
                break;


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        int id = adapterView.getId();
        if (id == R.id.spinner_is_paid) {
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray) {
        int preSelectedIndex = 0;
        Log.d("qqqqqqqqqqqqq", jsonArray.toString());
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                if (jsonObject.has("has_child"))
                    model.getHasChild().add(jsonObject.getBoolean("has_child"));
                else
                    model.getHasChild().add(false);

                if (POST_JOB_CALLING_SOURCE.equals("external")) {
                    if (jsonObject.getBoolean("selected")) {
                        preSelectedIndex = i;
                    }
                }
            } catch (JSONException e) {
                  e.printStackTrace();
            }
        }

        if (getContext() != null && model != null && spinner != null && model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames()));
            if (POST_JOB_CALLING_SOURCE.equals("external")) {
                Log.d("index", String.valueOf(index));

                nokri_setSpinnerSelection(spinner, preSelectedIndex);

            }
        }
        spinner.setOnItemSelectedListener(this);
        return model;
    }


    private void nokri_getPostJob() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject data = new JSONObject(responseObject.body().string());
                        if (data.getBoolean("success")) {
                            JSONObject response = data.getJSONObject("data");
                            jobId = response.getString("job_id");
                            Log.d("tagggggggg", jobId);
                            jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
                            jobTitleEditText.setHint(response.getJSONObject("job_title").getString("key"));
                            jobCategoryTextView.setText(response.getJSONObject("job_category").getString("key"));
                            jobCategorySpinnerModel = nokri_populateSpinner(jobCategorySpinner, response.getJSONObject("job_category").getJSONArray("value"));
                            jobDescriptionTextView.setText(response.getJSONObject("job_description").getString("key"));
                            jobDetailsEditor.setPlaceholder(response.getJSONObject("job_description").getString("key"));
                            applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                            timelineTextView.setText(response.getJSONObject("job_timeline").getString("key"));
                            spaceTextView.setText(response.getJSONObject("job_space").getString("key"));
                            spaceEditText.setHint(response.getJSONObject("job_space").getString("key"));
                            paidTextView.setText(response.getJSONObject("job_paid").getString("key"));
                            positionTextView.setText(response.getJSONObject("job_position").getString("key"));
                            positionSpinnerModel = nokri_populateSpinner(positionSpinner, response.getJSONObject("job_position").getJSONArray("value"));
                            JSONObject paid = new JSONObject();
                            JSONObject unpaid = new JSONObject();
                            JSONArray ja = new JSONArray();
                            paid.put("key", "0");
                            paid.put("value", "paid");
                            ja.put(paid);
                            unpaid.put("key", "1");
                            unpaid.put("value", "unpaid");
                            ja.put(unpaid);
                            paidSpinnerModel = nokri_populateSpinner(paidSpinner, ja);
                            equipTextView.setText(response.getJSONObject("job_equip").getString("key"));
                            equipEditText.setHint(response.getJSONObject("job_equip").getString("key"));
                            venueTextView.setText(response.getJSONObject("job_venue").getString("key"));
                            venueEditText.setHint(response.getJSONObject("job_venue").getString("key"));
                            capacityTextView.setText(response.getJSONObject("job_capacity").getString("key"));
                            capacityEditText.setHint(response.getJSONObject("job_capacity").getString("key"));
                            publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));
                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), data.getString("message"));
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment pricingTableFragment = new Nokri_PricingTableFragment();

                            fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), pricingTableFragment).commit();

                        }
                        dialogManager.hideAfterDelay();
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

    private void nokri_getUpdatePostJob(String id) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        JsonObject params = new JsonObject();
        params.addProperty("is_update", id);
        params.addProperty("job_id", id);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.editPostJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.editPostJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    //  Log.d("Info responseObject", responseObject.toString());
                    try {
                        JSONObject data = new JSONObject(responseObject.body().string());

                        JSONObject response = data.getJSONObject("data");
                        TextView toolbarTitleTextvView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextvView.setText(response.getJSONObject("job_page_title").getString("key"));
                        jobId = response.getString("job_id");
                        jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
                        jobTitleEditText.setHint(response.getJSONObject("job_title").getString("key"));
                        jobTitleEditText.setText(response.getJSONObject("job_title").getString("value"));

                        jobCategoryTextView.setText(response.getJSONObject("job_category").getString("key"));
                        jobCategorySpinnerModel = nokri_populateSpinner(jobCategorySpinner, response.getJSONObject("job_category").getJSONArray("value"));

                        jobDescriptionTextView.setText(response.getJSONObject("job_description").getString("key"));
                        jobDetailsEditor.setPlaceholder(response.getJSONObject("job_description").getString("key"));
                        jobDetailsEditor.setHtml(response.getJSONObject("job_description").getString("value"));


                        applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                        applicatiponDeadlineEditText.setText(response.getJSONObject("job_deadline").getString("value"));

                        timelineTextView.setText(response.getJSONObject("job_timeline").getString("key"));
                        timelineEditText.setText(response.getJSONObject("job_timeline").getString("value"));

                        spaceTextView.setText(response.getJSONObject("job_space").getString("key"));
                        spaceEditText.setHint(response.getJSONObject("job_space").getString("key"));
                        spaceEditText.setText(response.getJSONObject("job_space").getString("value"));

                        equipTextView.setText(response.getJSONObject("job_equip").getString("key"));
                        equipEditText.setHint(response.getJSONObject("job_equip").getString("key"));
                        equipEditText.setText(response.getJSONObject("job_equip").getString("value"));

                        venueTextView.setText(response.getJSONObject("job_venue").getString("key"));
                        venueEditText.setHint(response.getJSONObject("job_venue").getString("key"));
                        venueEditText.setText(response.getJSONObject("job_venue").getString("value"));

                        capacityTextView.setText(response.getJSONObject("job_capacity").getString("key"));
                        capacityEditText.setHint(response.getJSONObject("job_capacity").getString("key"));
                        capacityEditText.setText(response.getJSONObject("job_capacity").getString("value"));

                        paidTextView.setText(response.getJSONObject("job_paid").getString("key"));
                        JSONObject paid = new JSONObject();
                        JSONObject unpaid = new JSONObject();
                        JSONArray ja = new JSONArray();
                        paid.put("key", "0");
                        paid.put("value", "paid");
                        unpaid.put("key", "1");
                        unpaid.put("value", "unpaid");
                        if (response.getJSONObject("job_paid").getString("value").equals("paid")) {
                            paid.put("selected", true);
                            unpaid.put("selected", false);
                        } else {
                            paid.put("selected", false);
                            unpaid.put("selected", true);
                        }
                        ja.put(paid);
                        ja.put(unpaid);
                        paidSpinnerModel = nokri_populateSpinner(paidSpinner, ja);
                        positionTextView.setText(response.getJSONObject("job_position").getString("key"));
                        positionSpinnerModel = nokri_populateSpinner(positionSpinner, response.getJSONObject("job_position").getJSONArray("value"));
                        publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));
                        dialogManager.hideAfterDelay();
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
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        applicatiponDeadlineEditText.setText(sdf.format(calendar.getTime()));
    }


    private void nokri_postJob() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();

        if (POST_JOB_CALLING_SOURCE.equals("external")) {
            jsonObject.addProperty("job_id", POST_JOB_ID);
            jsonObject.addProperty("is_update", POST_JOB_ID);
        } else
            jsonObject.addProperty("is_update", jobId);

        jsonObject.addProperty("job_description", jobDetails);
        jsonObject.addProperty("job_cat", jobCategory);
        jsonObject.addProperty("job_title", title);
        jsonObject.addProperty("job_date", deadline);
        jsonObject.addProperty("job_position", position);
        jsonObject.addProperty("job_timeline", timeline);
        jsonObject.addProperty("job_venue", venue);
        jsonObject.addProperty("job_capacity", capacity);
        jsonObject.addProperty("job_space", space);
        jsonObject.addProperty("job_equip", equip);
        if ( isPaid == "0" ) {
            jsonObject.addProperty("job_paid", "paid");
        } else {
            jsonObject.addProperty("job_paid", "unpaid");
        }

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postJob(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postJob(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        dialogManager.hideAlertDialog();

                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "applied";
                        Nokri_JobDetailFragment.JOB_ID = jobId;
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).commit();
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
                    dialogManager.showCustom(responseObject.code() + "");
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

    private boolean nokri_areFieldsEmpty() {
        Nokri_Utils.checkEditTextForError(jobTitleEditText);
        Nokri_Utils.checkEditTextForError(spaceEditText);
        Nokri_Utils.checkEditTextForError(equipEditText);
        Nokri_Utils.checkEditTextForError(applicatiponDeadlineEditText);
        Nokri_Utils.checkEditTextForError(venueEditText);
        Nokri_Utils.checkEditTextForError(capacityEditText);
        Nokri_Utils.checkEditTextForError(timelineEditText);

        if (jobTitleEditText.getText().toString().trim().isEmpty() ||
                applicatiponDeadlineEditText.getText().toString().trim().isEmpty() ||
                jobDetailsEditor.getHtml() == null ||
                jobDetailsEditor.getHtml().trim().isEmpty() ||
                spaceEditText.getText().toString().trim().isEmpty() ||
                equipEditText.getText().toString().trim().isEmpty() ||
                venueEditText.getText().toString().trim().isEmpty() ||
                capacityEditText.getText().toString().trim().isEmpty() ||
                timelineEditText.getText().toString().trim().isEmpty()
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void nokri_setValues() {
        jobDetails = jobDetailsEditor.getHtml();
        title = jobTitleEditText.getText().toString();
        deadline = applicatiponDeadlineEditText.getText().toString();
        space = spaceEditText.getText().toString();
        equip = equipEditText.getText().toString();
        timeline = timelineEditText.getText().toString();
        venue = venueEditText.getText().toString();
        capacity = capacityEditText.getText().toString();
        if (jobCategorySpinner.getAdapter() != null) {
            if (jobCategorySpinnerModel.getIds() != null && jobCategorySpinnerModel.getIds().size() > 0)
                jobCategory = jobCategorySpinnerModel.getIds().get(jobCategorySpinner.getSelectedItemPosition());
        }

        if (positionSpinner.getAdapter() != null) {
            if (positionSpinnerModel.getIds() != null && positionSpinnerModel.getIds().size() > 0)
                position = positionSpinnerModel.getIds().get(positionSpinner.getSelectedItemPosition());
        }

        if (paidSpinner.getAdapter() != null) {
            if (paidSpinnerModel.getIds() != null && paidSpinnerModel.getIds().size() > 0)
                isPaid = paidSpinnerModel.getIds().get(paidSpinner.getSelectedItemPosition());
        }
    }


    private void nokri_setSpinnerSelection(Spinner spinner, int index) {
        Log.e("abc123", spinner.getSelectedItemPosition() + " " + index);

        spinner.setSelection(index);
//          Log.d("itemzzz", "called" + index + " " + spinner.getAdapter().getItem(index).toString());


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        return false;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    }
}
