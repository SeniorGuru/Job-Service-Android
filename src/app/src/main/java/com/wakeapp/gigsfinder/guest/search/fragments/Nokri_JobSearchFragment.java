package com.wakeapp.gigsfinder.guest.search.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.wakeapp.gigsfinder.candidate.jobs.fragments.Nokri_AllJobsFragment;
import com.wakeapp.gigsfinder.custom.MaterialProgressBar;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_SpinnerModel;
import com.wakeapp.gigsfinder.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.wakeapp.gigsfinder.guest.search.models.Nokri_JobSearchModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.utils.Nokri_Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_JobSearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView searchByTitleTextView;
    private EditText searchEditText;
    private ImageButton searchImageButton;
    private Nokri_FontManager fontManager;
    private LinearLayout searchNow;
    View view;
    private TextView toolbarTitleTextView;
    private RelativeLayout filgersResetContainer;
    private TextView filtersTextView;
    private ImageButton closeImgeButton;
    private Button resetButton;
    List<View> allViewInstanceforCustom = new ArrayList<>();

    private TextView footerTextView;
    //private Nokri_GuestDashboardActivity guestDashboardActivity;


    private Nokri_SpinnerModel countrySpinnerModel, stateSpinnerModel, citySpinnerModel, townSpinnerModel;
    private TextView countryTextView, cityTextView, stateTextView, townTextView;

    private Spinner countrySpinner, stateSpinner, citySpinner, townSpinner;
    private String country = "", state = "", city = "", town = "";
    private MaterialProgressBar progressBar;


    private TextView jobCategoryTextView, jobQualificationTextView, jobTypeTextView, salaryCurrencyTextView, jobShiftTextView, jobLevelTextView, jobSkillsTextView;
    private TextView jobSubCategoryTextView1, jobSubCategoryTextView2, jobSubCategoryTextView3;
    private Spinner jobCategorySpinner, jobQualificationSpinner, jobTypeSpinner, salaryCurrenencySpinner, jobShiftSpinner, jobLevelSpinner, jobSkillsSpinner;
    private Spinner subCategorySinner1, subCategorySinner2, subCategorySinner3;
    private RelativeLayout stateContainer, cityContainer, townContainer;

    private Nokri_SpinnerModel jobCategorySpinnerModel, jobQualificationSpinnerModel, jobTypeSpinenrModel, salaryCurrerencySpinneModel, jobShiftSpinnerModel, jobLevelSpinnerModel, jobSkillsSpinnerModel;
    private Nokri_SpinnerModel subCategorySinner1Model1, subCategorySinnerModel2, subCategorySinnerModel3;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayout;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private String jobCategory = "", jobQualification = "", jobType = "", salaryCurrency = "", jobShift = "", jobLevel = "", jobSkills = "";
    private String subCategory1 = "", subCategory2 = "", subCategory3 = "";
    private ArrayList<String>jobTypeKeys = new ArrayList<>();
    private ArrayList<String>jobShiftKeys = new ArrayList<>();
    private String spinnerTitleText;
    private String[] values = new String[7];
    private TextView[] textViews = new TextView[7];
    private Spinner[] spinners = new Spinner[7];
    private Nokri_SpinnerModel[] spinnerModels = new Nokri_SpinnerModel[7];
    private RelativeLayout subCategoryContainer1, subCategoryContainer2, subCategoryContainer3;
    private Nokri_DialogManager dialogManager;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.VISIBLE);

    }

    public Nokri_JobSearchFragment() {

    }

    @Override
    public void onPause() {
        super.onPause();


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_job_search, container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        nokri_getJobSearchData();


        String toolbarTitle = "All Eventss";


        if (Nokri_SharedPrefManager.isAccountPublic(getContext())) {
            Nokri_GuestDashboardModel model = Nokri_SharedPrefManager.getGuestSettings(getContext());
            toolbarTitle = "All Events";
//            toolbarTitle = model.getExplore();
        } else if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {

            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
            toolbarTitle = "ALL Events";
//            toolbarTitle = model.getAdvancedSearch();
        } else if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {

            Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
            toolbarTitle ="All Events";
//            toolbarTitle = model.getExplore();

        }


        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(toolbarTitle);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.VISIBLE);

    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();
        searchByTitleTextView = getView().findViewById(R.id.txt_search_by_title);
        searchEditText = getView().findViewById(R.id.edittxt_search);
        searchImageButton = getView().findViewById(R.id.img_btn_search);
        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        footerTextView = getView().findViewById(R.id.footer_text);

        searchNow = getView().findViewById(R.id.search_now);


        searchNow.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        jobCategoryTextView = getView().findViewById(R.id.txt_job_caregory);
        jobQualificationTextView = getView().findViewById(R.id.txt_job_qualification);
        jobTypeTextView = getView().findViewById(R.id.txt_job_type);
        salaryCurrencyTextView = getView().findViewById(R.id.txt_salary_currency);
        jobShiftTextView = getView().findViewById(R.id.txt_job_shift);
        jobLevelTextView = getView().findViewById(R.id.txt_job_level);
        jobSkillsTextView = getView().findViewById(R.id.txt_job_skills);
//        horizontalScrollView=getView().findViewById(R.id.horiradio);
        jobSubCategoryTextView1 = getView().findViewById(R.id.txt_sub_category1);
        jobSubCategoryTextView2 = getView().findViewById(R.id.txt_sub_category2);
        jobSubCategoryTextView3 = getView().findViewById(R.id.txt_sub_category3);


        countryTextView = getView().findViewById(R.id.txt_country);
        cityTextView = getView().findViewById(R.id.txt_city);
        stateTextView = getView().findViewById(R.id.txt_state);
        townTextView = getView().findViewById(R.id.txt_town);

        countrySpinner = getView().findViewById(R.id.spinner_country);
        stateSpinner = getView().findViewById(R.id.spinner_state);
        citySpinner = getView().findViewById(R.id.spinner_city);
        townSpinner = getView().findViewById(R.id.spinner_town);
        progressBar = getView().findViewById(R.id.progress);


        stateContainer = getView().findViewById(R.id.state_container);
        cityContainer = getView().findViewById(R.id.city_container);
        townContainer = getView().findViewById(R.id.town_container);

        textViews[0] = jobCategoryTextView;
        textViews[1] = jobQualificationTextView;
        textViews[2] = jobTypeTextView;
        textViews[3] = salaryCurrencyTextView;
        textViews[4] = jobShiftTextView;
        textViews[5] = jobLevelTextView;
        textViews[6] = jobSkillsTextView;

        jobCategorySpinner = getView().findViewById(R.id.spinner_job_category);
        jobQualificationSpinner = getView().findViewById(R.id.spinner_job_qualificaion);
        jobTypeSpinner = getView().findViewById(R.id.spinner_job_type);
        salaryCurrenencySpinner = getView().findViewById(R.id.spinner_salary_currency);
        jobShiftSpinner = getView().findViewById(R.id.spinner_job_shift);
        jobLevelSpinner = getView().findViewById(R.id.spinner_job_level);
        jobSkillsSpinner = getView().findViewById(R.id.spinner_job_skills);


        subCategorySinner1 = getView().findViewById(R.id.spinner_sub_category1);
        subCategorySinner2 = getView().findViewById(R.id.spinner_sub_category2);
        subCategorySinner3 = getView().findViewById(R.id.spinner_sub_category3);


        spinners[0] = jobCategorySpinner;
        spinners[1] = jobQualificationSpinner;
        spinners[2] = jobTypeSpinner;
        spinners[3] = salaryCurrenencySpinner;
        spinners[4] = jobShiftSpinner;
        spinners[5] = jobLevelSpinner;
        spinners[6] = jobSkillsSpinner;


//        radioGroup[0]=jobTypeSpinenrModel;

        spinnerModels[0] = jobCategorySpinnerModel;
        spinnerModels[1] = jobQualificationSpinnerModel;
        spinnerModels[2] = jobTypeSpinenrModel;
        spinnerModels[3] = salaryCurrerencySpinneModel;
        spinnerModels[4] = jobShiftSpinnerModel;
        spinnerModels[5] = jobLevelSpinnerModel;
        spinnerModels[6] = jobSkillsSpinnerModel;


        values[0] = jobCategory;
        values[1] = jobQualification;
        values[2] = jobType;
        values[3] = salaryCurrency;
        values[4] = jobShift;
        values[5] = jobLevel;
        values[6] = jobSkills;


        subCategoryContainer1 = getView().findViewById(R.id.sub_category_container1);
        subCategoryContainer2 = getView().findViewById(R.id.sub_category_container2);
        subCategoryContainer3 = getView().findViewById(R.id.sub_category_container3);


        searchImageButton.setOnClickListener(this);
        searchNow.setOnClickListener(this);

        toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        filgersResetContainer = getActivity().findViewById(R.id.filter_reset_container);
        filtersTextView = getActivity().findViewById(R.id.txt_filters);
        resetButton = getActivity().findViewById(R.id.btn_reset);
        closeImgeButton = getActivity().findViewById(R.id.img_btn_cross);

        //  getActivity().findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);

        closeImgeButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);


        // filgersResetContainer.setVisibility(View.VISIBLE);
    }


    private void nokri_setupFonts() {

        fontManager.nokri_setOpenSenseFontTextView(countryTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(cityTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(stateTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(townTextView, getActivity().getAssets());


        fontManager.nokri_setOpenSenseFontTextView(searchByTitleTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(searchEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(footerTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView1, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView2, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView3, getActivity().getAssets());

        for (int i = 0; i < textViews.length; i++)
            fontManager.nokri_setOpenSenseFontTextView(textViews[i], getActivity().getAssets());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_btn_search:
                nokri_postSearch();
                break;
            case R.id.search_now:
                nokri_postSearch();
                break;
            case R.id.btn_reset:
                break;
            case R.id.img_btn_cross:
                getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
                filgersResetContainer.setVisibility(View.GONE);
                break;

        }

    }

    private void nokri_postSearch() {
        if (toolbarTitleTextView != null)
            toolbarTitleTextView.setText("Search Results");

        nokri_setValues();

        String location;
        location = country;
        if (!state.isEmpty())
            location = state;

        if (!city.isEmpty())
            location = city;

        if (!town.isEmpty())
            location = town;


        Nokri_JobSearchModel model = new Nokri_JobSearchModel();
        model.setSearchNow(searchEditText.getText().toString());
        model.setJobCategory(jobCategory);
        model.setJobQualification(jobQualification);
        model.setJobType(jobType);
        model.setSalaryCurrency(salaryCurrency);
        model.setJobShift(jobShift);
        model.setJobLevel(jobLevel);
        model.setJobSkills(jobSkills);
        model.setSubCategory1(subCategory1);
        model.setSubCategory2(subCategory2);
        model.setSubCategory3(subCategory3);
        model.setLocation(location);
        Log.d("tesssssssssssssssst",model.toString());
        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "external";
        Nokri_SharedPrefManager.saveJobSearchModel(model, getContext());
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment allJobsFragment = new Nokri_AllJobsFragment();
        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), allJobsFragment).addToBackStack(null).commit();

    }


    private void nokri_getJobSearchData() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFilters(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getFilters(Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                JsonObject optionsObj = null;
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONArray extraArray = response.getJSONArray("extra");

                        for (int i = 0; i < extraArray.length(); i++) {
                            JSONObject extra = extraArray.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("job_title")) {
                                searchByTitleTextView.setText(extra.getString("key"));
                                searchEditText.setHint(extra.getString("value"));


                            } else if (extra.getString("field_type_name").equals("job_post_btn")) {
                                footerTextView.setText(extra.getString("key"));
                                searchByTitleTextView.setText(extra.getString("key"));

                            } else if (extra.getString("field_type_name").equals("job_search_cat")) {
                                jobSubCategoryTextView1.setText(extra.getString("key"));
                                jobSubCategoryTextView2.setText(extra.getString("key"));
                                jobSubCategoryTextView3.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("page_title")) {
                                toolbarTitleTextView.setText("All Events");
                            } else if (extra.getString("field_type_name").equals("job_position")) {

                                countryTextView.setText("select location");
                            } else if (extra.getString("field_type_name").equals("state")) {

                                stateTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("city")) {

                                cityTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("town")) {

                                townTextView.setText(extra.getString("key"));
                            }


                        }

                        JSONObject data = response.getJSONObject("data");
                        JSONArray searchFieldsArray = data.getJSONArray("search_fields");
                        spinnerTitleText = searchFieldsArray.getJSONObject(0).getString("column");
                        for (int i = 0; i < searchFieldsArray.length(); i++) {

                            JSONObject filterObject = searchFieldsArray.getJSONObject(i);
                            JSONArray filters = filterObject.getJSONArray("value");
                            if (filterObject.getString("field_type_name").equals("job_location")) {
                                countryTextView.setText(filterObject.getString("key"));
                                countrySpinnerModel = nokri_populateSpinner(countrySpinner, filterObject.getJSONArray("value"));
                                continue;
                            }

                            if (filterObject.getString("field_type_name").equals("job_type")) {

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                params.topMargin = 25;
                                params.bottomMargin = 25;
                                params.rightMargin = 25;
                                params.leftMargin = 25;

                                JSONArray jobTypeValueArray = filterObject.getJSONArray("value");
                                radioGroup = getView().findViewById(R.id.radioGroup);
                                radioGroup.setOrientation(RadioGroup.HORIZONTAL);

                                for (int j = 0; j < jobTypeValueArray.length(); j++) {
                                    JSONObject jobTypeJson = jobTypeValueArray.getJSONObject(j);

                                    RadioButton radioButton = new RadioButton(getActivity());
                                    radioButton.setButtonDrawable(null);
                                    radioButton.setTextSize(12);
//                                                radioButton.setPadding(15,15,15,15);
                                    radioButton.setPadding(30, 12, 30, 12);
                                    radioButton.setTextColor(Color.LTGRAY);
                                    radioButton.setBackgroundResource(R.drawable.radiobuttonbg);
//                                    radioButton.setTextColor(ContextCompat.getColorStateList(getContext(), Color.parseColor(Nokri_Config.APP_COLOR)));
                                    radioButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                    radioButton.setText(jobTypeJson.getString("value"));
                                    jobTypeKeys.add(jobTypeJson.getString("key"));
                                    radioGroup.addView(radioButton, params);
//                                                radioButton.setLayoutParams(params);

//                                        radioButton.setText(jobTypeJson.getString("key"));

                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                             RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                                             int index = radioGroup.indexOfChild(radioButton);
                                             jobType = jobTypeKeys.get(index);
//                                            Nokri_ToastManager.showShortToast(getContext(),jobType);
                                        }
                                    });
                                }

                            }
                            if (filterObject.getString("field_type_name").equals("job_shift")) {
                                RelativeLayout.LayoutParams params22 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                params22.topMargin = 25;
                                params22.bottomMargin = 25;
                                params22.rightMargin = 25;
                                params22.leftMargin = 25;

                                JSONArray jobShiftValueArray = filterObject.getJSONArray("value");
                                radioGroup = getView().findViewById(R.id.radioGroup1);
                                radioGroup.setOrientation(RadioGroup.HORIZONTAL);

                                for (int k = 0; k < jobShiftValueArray.length(); k++) {
                                    JSONObject jobShiftJson = jobShiftValueArray.getJSONObject(k);

                                    RadioButton radioButton1 = new RadioButton(getActivity());
                                    radioButton1.setButtonDrawable(null);
                                    radioButton1.setTextSize(12);
                                    radioButton1.setPadding(30, 12, 30, 12);
                                    radioButton1.setTextColor(Color.LTGRAY);
                                    radioButton1.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                    radioButton1.setBackgroundResource(R.drawable.radiobuttonbg);
                                    radioButton1.setText(jobShiftJson.getString("value"));
                                    jobShiftKeys.add(jobShiftJson.getString("key"));
                                    radioGroup.addView(radioButton1, params22);
//                                                radioButton.setLayoutParams(params);

                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                                            int index = radioGroup.indexOfChild(radioButton);
                                            jobShift = jobShiftKeys.get(index);
//                                            Nokri_ToastManager.showShortToast(getContext(),jobShift);
                                        }
                                    });                                }

                            } else {
//                                dialogManager.showCustom(responseObject.message());
//                                dialogManager.hideAfterDelay();
                            }


                            textViews[i].setText(filterObject.getString("key"));
                            spinnerModels[i] = nokri_populateSpinner(spinners[i], filters);


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

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray) {

        Nokri_SpinnerModel model = new Nokri_SpinnerModel();
        model.getNames().add(spinnerTitleText);
        model.getIds().add(spinnerTitleText);
        model.getHasChild().add(false);
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames(), true));
            spinner.setOnItemSelectedListener(this);
        }

        return model;
    }

    private void nokri_setValues() {


        for (int i = 0; i < spinners.length; i++) {
            if (spinners[i].getSelectedItemPosition() == 0)
                values[i] = "";
            else {
                values[i] = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

            }
        }

        jobCategory = values[0];
        jobQualification = values[1];
        //jobType = values[2];
        salaryCurrency = values[3];
//        jobShift = values[4];
        jobLevel = values[5];
        jobSkills = values[6];

        if (subCategorySinner1.getVisibility() == View.VISIBLE) {
            if (subCategorySinner1.getSelectedItemPosition() > 0) {
                subCategory1 = subCategorySinner1Model1.getIds().get(subCategorySinner1.getSelectedItemPosition());
            } else
                subCategory1 = "";
        }

        if (subCategorySinner2.getVisibility() == View.VISIBLE) {
            if (subCategorySinner2.getSelectedItemPosition() > 0) {
                subCategory2 = subCategorySinnerModel2.getIds().get(subCategorySinner2.getSelectedItemPosition());
                subCategory1 = "";
            } else
                subCategory2 = "";
        }
        if (subCategorySinner3.getVisibility() == View.VISIBLE) {
            if (subCategorySinner3.getSelectedItemPosition() > 0) {
                subCategory3 = subCategorySinnerModel3.getIds().get(subCategorySinner3.getSelectedItemPosition());
                subCategory1 = "";
                subCategory2 = "";
            } else
                subCategory3 = "";
        }

        if (countrySpinner.getAdapter() != null) {
            if (countrySpinnerModel.getIds() != null && countrySpinnerModel.getIds().size() > 0 && countrySpinner.getSelectedItemPosition() != 0)
                country = countrySpinnerModel.getIds().get(countrySpinner.getSelectedItemPosition());
        } else
            country = "";

        if (stateSpinner.getAdapter() != null && stateSpinner.getVisibility() == View.VISIBLE && stateSpinner.getSelectedItemPosition() != 0) {
            if (stateSpinnerModel.getIds() != null && stateSpinnerModel.getIds().size() > 0)
                state = stateSpinnerModel.getIds().get(stateSpinner.getSelectedItemPosition());
        } else state = "";

        if (citySpinner.getAdapter() != null && citySpinner.getVisibility() == View.VISIBLE && citySpinner.getSelectedItemPosition() != 0) {
            if (citySpinnerModel.getIds() != null && citySpinnerModel.getIds().size() > 0)
                city = citySpinnerModel.getIds().get(citySpinner.getSelectedItemPosition());
        } else city = "";
        if (townSpinner.getAdapter() != null && townSpinner.getVisibility() == View.VISIBLE && townSpinner.getSelectedItemPosition() != 0) {
            if (townSpinnerModel.getIds() != null && townSpinnerModel.getIds().size() > 0)
                town = townSpinnerModel.getIds().get(townSpinner.getSelectedItemPosition());
        } else town = "";


    }

    private void nokri_getCountryCityState(String id, final String tag) {

     /*   dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());*/
        progressBar.setVisibility(View.VISIBLE);
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id", id);

        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
//                        Log.v("response", responseObject.message());

                        switch (tag) {
                            case "state":
                                stateSpinnerModel = nokri_populateSpinner(stateSpinner, response);
                                break;
                            case "city":
                                citySpinnerModel = nokri_populateSpinner(citySpinner, response);
                                break;
                            case "town":
                                townSpinnerModel = nokri_populateSpinner(townSpinner, response);
                                break;

                        }


                        //     dialogManager.hideAlertDialog();
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                       /* dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    } catch (IOException e) {
                       /* dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();

                    }
                } else {
                 /*   dialogManager.showCustom(responseObject.code()+"");
                    dialogManager.hideAfterDelay();*/
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            /*    dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();*/
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = parent.getId();
        if (i == R.id.spinner_country) {
            if (countrySpinnerModel != null && countrySpinnerModel.getHasChild().get(position)) {
                stateContainer.setVisibility(View.VISIBLE);
                stateTextView.setVisibility(View.VISIBLE);
                stateSpinner.setVisibility(View.VISIBLE);
                nokri_getCountryCityState(countrySpinnerModel.getIds().get(position), "state");
            } else {

                stateContainer.setVisibility(View.GONE);
                stateTextView.setVisibility(View.GONE);
                stateSpinner.setVisibility(View.GONE);
                cityContainer.setVisibility(View.GONE);
                citySpinner.setVisibility(View.GONE);
                cityTextView.setVisibility(View.GONE);
                townContainer.setVisibility(View.GONE);
                townSpinner.setVisibility(View.GONE);
                townTextView.setVisibility(View.GONE);

            }
        } else if (i == R.id.spinner_state) {
            if (stateSpinnerModel != null && stateSpinnerModel.getHasChild().get(position)) {
                cityContainer.setVisibility(View.VISIBLE);
                cityTextView.setVisibility(View.VISIBLE);
                citySpinner.setVisibility(View.VISIBLE);
                nokri_getCountryCityState(stateSpinnerModel.getIds().get(position), "city");
            } else {

                cityContainer.setVisibility(View.GONE);
                citySpinner.setVisibility(View.GONE);
                cityTextView.setVisibility(View.GONE);
                townContainer.setVisibility(View.GONE);
                townSpinner.setVisibility(View.GONE);
                townTextView.setVisibility(View.GONE);

            }
        } else if (i == R.id.spinner_city) {
            if (citySpinnerModel != null && citySpinnerModel.getHasChild().get(position)) {
                townContainer.setVisibility(View.VISIBLE);
                townTextView.setVisibility(View.VISIBLE);
                townSpinner.setVisibility(View.VISIBLE);
                nokri_getCountryCityState(citySpinnerModel.getIds().get(position), "town");
            } else {


                townContainer.setVisibility(View.GONE);
                townSpinner.setVisibility(View.GONE);
                townTextView.setVisibility(View.GONE);

            }
        } else if (i == R.id.spinner_town) {
        } else if (i == R.id.spinner_job_category) {
            if (spinnerModels[0] != null && spinnerModels[0].getHasChild().get(position)) {

                subCategorySinner1.setVisibility(View.VISIBLE);
                jobSubCategoryTextView1.setVisibility(View.VISIBLE);
                subCategoryContainer1.setVisibility(View.VISIBLE);
                nokri_getSubFields(spinnerModels[0].getIds().get(position), "cat1");
            } else {

                subCategorySinner1.setVisibility(View.GONE);
                jobSubCategoryTextView1.setVisibility(View.GONE);
                subCategoryContainer1.setVisibility(View.GONE);
                subCategorySinner2.setVisibility(View.GONE);
                jobSubCategoryTextView2.setVisibility(View.GONE);
                subCategoryContainer2.setVisibility(View.GONE);
                subCategorySinner3.setVisibility(View.GONE);
                jobSubCategoryTextView3.setVisibility(View.GONE);
                subCategoryContainer3.setVisibility(View.GONE);
            }
        } else if (i == R.id.spinner_sub_category1) {
            if (subCategorySinner1Model1 != null && subCategorySinner1Model1.getHasChild().get(position)) {
                subCategoryContainer2.setVisibility(View.VISIBLE);
                subCategorySinner2.setVisibility(View.VISIBLE);
                jobSubCategoryTextView2.setVisibility(View.VISIBLE);
                nokri_getSubFields(subCategorySinner1Model1.getIds().get(position), "cat2");
            } else {
                subCategoryContainer2.setVisibility(View.GONE);
                subCategorySinner2.setVisibility(View.GONE);
                jobSubCategoryTextView2.setVisibility(View.GONE);
                subCategorySinner3.setVisibility(View.GONE);
                jobSubCategoryTextView3.setVisibility(View.GONE);
                subCategoryContainer3.setVisibility(View.GONE);
            }
        } else if (i == R.id.spinner_sub_category2) {
            if (subCategorySinnerModel2 != null && subCategorySinnerModel2.getHasChild().get(position)) {

                subCategorySinner3.setVisibility(View.VISIBLE);
                jobSubCategoryTextView3.setVisibility(View.VISIBLE);
                subCategoryContainer3.setVisibility(View.VISIBLE);
                nokri_getSubFields(subCategorySinnerModel2.getIds().get(position), "cat3");
            } else {

                subCategorySinner3.setVisibility(View.GONE);
                jobSubCategoryTextView3.setVisibility(View.GONE);
                subCategoryContainer3.setVisibility(View.GONE);

            }
        }










         /*   case R.id.spinner_sub_category3 :
                if(subCategorySinnerModel3!= null && subCategorySinnerModel3.getHasChild().get(position))
                {


                    nokri_getSubFields(subCategorySinnerModel3.getIds().get(position),"cat4");
                }

                break;*/
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void nokri_getSubFields(String id, final String tag) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cat_id", id);

        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
//                        Log.v("response", responseObject.message());

                        switch (tag) {
                            case "cat1":
                                subCategorySinner1Model1 = nokri_populateSpinner(subCategorySinner1, response);
                                break;
                            case "cat2":
                                subCategorySinnerModel2 = nokri_populateSpinner(subCategorySinner2, response);
                                break;
                            case "cat3":
                                subCategorySinnerModel3 = nokri_populateSpinner(subCategorySinner3, response);
                                break;

                        }


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
                    dialogManager.showCustom(responseObject.code() + "");
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }

}
