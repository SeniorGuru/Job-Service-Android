package com.wakeapp.gigsfinder.candidate.jobs.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.wakeapp.gigsfinder.candidate.jobs.adapters.Nokri_JobsAdapter;
import com.wakeapp.gigsfinder.candidate.jobs.models.Nokri_JobsModel;
import com.wakeapp.gigsfinder.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.wakeapp.gigsfinder.custom.MaterialProgressBar;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_SpinnerModel;
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
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

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
public class Nokri_AllJobsFragment extends Fragment implements View.OnClickListener, BillingProcessor.IBillingHandler, AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private Nokri_JobsAdapter adapter;
    private List<Nokri_JobsModel> modelList;
    private List<Nokri_JobsModel> modelLists;
    private TextView emptyTextView ,noOfJobs;
    //    View view;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private int nextPage = 1;
    private boolean hasNextPage = true;
    Nokri_CandidateDashboardActivity candidateDashboardActivity;
    private Button loadMoreButton;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    private int filterNextPage = 1;
    private boolean isFilterNetPage = false;
    ImageView imageViewCollapse;
    private String filterText = "";
    private ProgressBar progressBar;
    String job_title, e_distance, requestForm = "" ,categoryId= "";
    double e_lat, e_long;
    LinearLayout linearLayoutCollapse, linearLayoutCustom;
    NestedScrollView nestedScroll;
    private int pagenumber;
    LinearLayout textLinear;
    double longtitude , latitude ;
    public static String ALL_JOBS_SOURCE = "";
    private Nokri_DialogManager dialogManager;
    public static String myId;
    private Boolean spinnerTouched2 = false, checkRequest = false;
    private static String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkCXQyKE6qvZhg3oFxKvZrOL9efX/1v5qAw1IP7aT8xMH8bJrJjVJRHlpXxGJon0oaRbEb+TE49vqcT2JpP4imqJbAkYlU2S7pY/ffv6Satew8p+8fKE1ttjJOlZdU/IUP/1j5Gq75tZ7o/3ds/Hjyg2ZHGDXljME0Pg2KGpt+DXju82WVPTNhvhD331v9E8QugyAKpZH0GKJlbm8xdj7Lq3hQy+dDVEgq5/VKV9+hzNWVSbxFnXR22CCft5whthqvyMJYwQehJWSG50sI9XlBwLqDz2dOuYbyhwNY3Uv5j3CpBdsl/IbBa0NJT+Xzqu7//gbh+/+W02gpa3jkipkcwIDAQAB";
    private BillingProcessor bp;
    public static boolean purchased;
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

    public Nokri_AllJobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_nokri_all_jobs, container, false);


    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MainActivity", "Activity Result");
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            Log.i("MainActivity", "Activity Results");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        Log.i("MainActivity", "Destroy");
        if (bp != null) {
            Log.i("MainActivity", "bp release");
            bp.release();
        }
        super.onDestroy();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bp = new BillingProcessor(getContext(), LICENSE_KEY, this);
        bp.initialize();
        purchased = bp.isPurchased("com.basic.gigfinder");
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);
//-------------
        searchByTitleTextView = getView().findViewById(R.id.txt_search_by_title);
        footerTextView = getView().findViewById(R.id.footer_text);

        searchNow = getView().findViewById(R.id.search_now);
        searchNow.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        jobCategoryTextView = getView().findViewById(R.id.txt_job_caregory);
        countryTextView = getView().findViewById(R.id.txt_country);

        countrySpinner = getView().findViewById(R.id.spinner_country);
        jobCategorySpinner = getView().findViewById(R.id.spinner_job_category);

     //------------
        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkLoading = true;
            nokri_recreate_Submit_jobSearch();
        });
        noOfJobs=getView().findViewById(R.id.noofjobs);
        textLinear=getView().findViewById(R.id.txt_linear);
        noOfJobs.setTextColor(Color.BLACK);
//       noOfJobs.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(noOfJobs, getActivity().getAssets());
        emptyTextView = getView().findViewById(R.id.txt_empty);
//        emptyTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView, getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        imageViewCollapse = getActivity().findViewById(R.id.collapse);
        linearLayoutCollapse = getView().findViewById(R.id.linearLayout);
        nestedScroll = getView().findViewById(R.id.scrollViewUp);
//        view = view.findViewById(R.id.line);
        Nokri_Utils.setRoundButtonColor(getContext(), loadMoreButton);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(loadMoreButton, getActivity().getAssets());
//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImage);
        if (ALL_JOBS_SOURCE.equals("")) {
            textLinear.setVisibility(View.GONE);
        }
        modelList = new ArrayList<>();
        modelLists = new ArrayList<>();

        progressBar = getView().findViewById(R.id.progress_bar);
        nextPage = 1;
        isFilterNetPage = false;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            job_title = bundle.getString("job_title", "");
            e_lat = bundle.getDouble("e_lat");
            e_long = bundle.getDouble("e_long");
            e_distance = bundle.getString("e_distance", "");
            pagenumber = bundle.getInt("nextPage");
            requestForm = bundle.getString("requestFrom", "");
            categoryId=bundle.getString("job_category","");
        }
        if (requestForm != null && requestForm.equals("Home")) {
            nokri_loadMores(true, filterText);
            Submit_jobSearch(true);


        } else {
            if (ALL_JOBS_SOURCE.equals(""))
                nokri_loadMore(true, filterText);

            else {
                nokri_filterJobsExternal(true);
            }
        }

        searchNow.setOnClickListener(this);
        nokri_getJobSearchData();
        loadMoreButton.setOnClickListener(this);
    }
    private void nokri_getJobSearchData() {

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


                            } else if (extra.getString("field_type_name").equals("job_post_btn")) {
                                footerTextView.setText("Filter Now");

                            } else if (extra.getString("field_type_name").equals("job_search_cat")) {
                            } else if (extra.getString("field_type_name").equals("page_title")) {
                            } else if (extra.getString("field_type_name").equals("job_position")) {

                                countryTextView.setText("select location");
                            } else if (extra.getString("field_type_name").equals("state")) {

//                                stateTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("city")) {

//                                cityTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("town")) {

//                                townTextView.setText(extra.getString("key"));
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
                            if(i == 0){
                            }


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
    public void nokri_recreate_Submit_jobSearch() {
        Nokri_AllJobsFragment nokri_allJobsFragment = new Nokri_AllJobsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("job_title", job_title);
        bundle.putString("job_category", categoryId);
        bundle.putDouble("e_lat", e_lat);
        bundle.putDouble("e_long", e_long);
        bundle.putString("e_distance", e_distance);
        bundle.putInt("page_number", pagenumber);
        bundle.putString("requestFrom", requestForm);
        Log.d("infoparamsofrefresh", bundle.toString());
        nokri_allJobsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, nokri_allJobsFragment);
        transaction.commit();
    }
    private void Submit_jobSearch(final boolean showAlert) {

        JsonObject params = new JsonObject();
        params.addProperty("page_number", nextPage);
        params.addProperty("job_title", job_title);
        params.addProperty("e_lat", e_lat);
        params.addProperty("e_long", e_long);
        params.addProperty("e_distance", e_distance);
//        params.addProperty("job_location", "275");
        if(!categoryId.isEmpty()) {
            params.addProperty("job_category", categoryId);
        }
        Log.d("infoparams", params.toString());
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addSocialHeaders());
            Log.d("params", params.toString());
        } else {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addHeaders());
            Log.d("elseparams", params.toString());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());

                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");
                        noOfJobs.setText(data.getString("no_txt"));
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                        }
//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapters();
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        Log.d("submit", companiesArray.toString());
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                model.setShowMenu(false);
                                JSONObject object = dataArray.getJSONObject(j);
                                String s = object.getString("field_type_name");
                                if ("job_id".equals(s)) {
                                    model.setJobId(object.getString("value"));
                                } else if ("company_id".equals(s)) {
                                    model.setCompanyId(object.getString("value"));
                                } else if ("job_name".equals(s)) {
                                    model.setJobTitle(object.getString("value"));
                                } else if ("company_name".equals(s)) {
                                    model.setJobDescription(object.getString("value"));
                                } else if ("job_salary".equals(s)) {
                                    model.setSalary(object.getString("value"));
                                } else if ("job_type".equals(s)) {
                                    model.setJobType(object.getString("value"));
                                } else if ("company_logo".equals(s)) {
                                    model.setCompanyLogo(object.getString("value"));
                                } else if ("job_location".equals(s)) {
                                    model.setAddress(object.getString("value"));
                                } else if ("job_time".equals(s)) {
                                    model.setTimeRemaining(object.getString("value"));
                                }
                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        setupAdapters();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                        if (showAlert)
                            dialogManager.hideAfterDelay();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    private void nokri_loadMore(final Boolean showAlert, String text) {
        Log.d("sdfaljdfklasdjfasl", "called");
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService;
        if (!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();
        if (!isFilterNetPage)
            params.addProperty("page_number", nextPage);
        else
            params.addProperty("page_number", filterNextPage);
        if (!text.equals("")) {
            params.addProperty("keyword", text);
        }

        Log.d("sdfaljdfklasdjfasl", params.toString());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getAllJobsAddMore(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getAllJobsAddMore(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");

                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));

                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapter();
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        Log.d("ablearry", companiesArray.toString());
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject object = dataArray.getJSONObject(j);

                                if (object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));

                                } else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));
                                if (j + 1 == dataArray.length()) {
                                    model.setShowMenu(false);
                                    modelList.add(model);
                                }
                            }

                        }
                        setupAdapter();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        //progressBar.setVisibility(View.GONE);
                        if (showAlert)
                            dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (showAlert) {

                    dialogManager.hideAfterDelay();
                }
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
            }
        });
    }

    private void nokri_loadMores(final Boolean showAlert, String text) {
        Log.d("sdfaljdfklasdjfasl", "called");
        RestService restService;
        if (!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();
        if (!isFilterNetPage)
            params.addProperty("page_number", nextPage);
        else
            params.addProperty("page_number", filterNextPage);
        if (!text.equals("")) {
            params.addProperty("keyword", text);
        }

        Log.d("sdfaljdfklasdjfasl", params.toString());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getAllJobsAddMore(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getAllJobsAddMore(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");



                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            return;
                        }
                        Log.d("ablearry", companiesArray.toString());
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject object = dataArray.getJSONObject(j);

                                if (object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));

                                } else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));
                                if (j + 1 == dataArray.length()) {
                                    model.setShowMenu(false);
                                    modelLists.add(model);
                                }
                            }

                        }
                    } catch (JSONException e) {
                        if (showAlert) {
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (showAlert) {

                }
            }
        });
    }

    public void nokri_filterJobsExternal(final boolean showAlert) {

        Nokri_JobSearchModel jobSearchModel = Nokri_SharedPrefManager.getJobSearchModel(getContext());

        JsonObject params = new JsonObject();
        params.addProperty("page_number", nextPage);


        String jobCategory = "";
        if (jobSearchModel.getSubCategory3().equals("") && jobSearchModel.getSubCategory2().equals("") && jobSearchModel.getSubCategory1().equals(""))
            jobCategory = jobSearchModel.getJobCategory();
        else if (jobSearchModel.getSubCategory2().equals("") && jobSearchModel.getSubCategory1().equals(""))
            jobCategory = jobSearchModel.getSubCategory3();

        else if (jobSearchModel.getSubCategory1().equals(""))
            jobCategory = jobSearchModel.getSubCategory2();
        else
            jobCategory = jobSearchModel.getSubCategory1();


        if (!jobSearchModel.getSearchNow().equals(""))
            params.addProperty("job_title", jobSearchModel.getSearchNow());
        if (!jobSearchModel.getJobCategory().equals(""))

            params.addProperty("job_category", jobCategory);
        if (!jobSearchModel.getJobQualification().equals(""))
            params.addProperty("job_qualifications", jobSearchModel.getJobQualification());
        if (!jobSearchModel.getJobType().equals(""))
            params.addProperty("job_type", jobSearchModel.getJobType());
        if (!jobSearchModel.getSalaryCurrency().equals(""))
            params.addProperty("job_currency", jobSearchModel.getSalaryCurrency());
        if (!jobSearchModel.getJobShift().equals(""))
            params.addProperty("job_shift", jobSearchModel.getJobShift());
        if (!jobSearchModel.getJobLevel().equals(""))
            params.addProperty("job_level", jobSearchModel.getJobLevel());
        if (!jobSearchModel.getJobSkills().equals(""))
            params.addProperty("job_skills", jobSearchModel.getJobSkills());
        if (jobSearchModel.getLocation() != null)
            if (!jobSearchModel.getLocation().trim().isEmpty())
                params.addProperty("ad_location", jobSearchModel.getLocation());


        Log.v("zzzzzzzzz", params.toString());
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addSocialHeaders());

        } else {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addHeaders());

        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextView.setText("");
//
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");
//
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
                        noOfJobs.setText(data.getString("no_txt"));

                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else
                            loadMoreButton.setVisibility(View.VISIBLE);

//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);

                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapter();
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);

                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                model.setShowMenu(false);
                                JSONObject object = dataArray.getJSONObject(j);
                                String s = object.getString("field_type_name");
                                if ("job_id".equals(s)) {
                                    model.setJobId(object.getString("value"));
                                } else if ("company_id".equals(s)) {
                                    model.setCompanyId(object.getString("value"));
                                } else if ("job_name".equals(s)) {
                                    model.setJobTitle(object.getString("value"));
                                } else if ("company_name".equals(s)) {
                                    model.setJobDescription(object.getString("value"));
                                } else if ("job_salary".equals(s)) {
                                    model.setSalary(object.getString("value"));
                                } else if ("job_type".equals(s)) {
                                    model.setJobType(object.getString("value"));
                                } else if ("company_logo".equals(s)) {
                                    model.setCompanyLogo(object.getString("value"));
                                } else if ("job_location".equals(s)) {
                                    model.setAddress(object.getString("value"));
                                } else if ("job_time".equals(s)) {
                                    model.setTimeRemaining(object.getString("value"));
                                }
                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        setupAdapter();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        //progressBar.setVisibility(View.GONE);
                        if (showAlert)
                            dialogManager.hideAfterDelay();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        Log.d("modelist", modelList.toString());
        adapter = new Nokri_JobsAdapter(modelList, getContext(), new Nokri_JobsAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(Nokri_JobsModel item) {

            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {
                android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_view_job:
                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "";
                        Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                        Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                        Nokri_JobDetailFragment.purchased = purchased;
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
                        break;
//                    case R.id.menu_view_company_profile:
//                        android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
//                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
//                        Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
//
//                        Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
//                        fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
//                        break;
                }

            }
        });
        adapter.setOnJobClickListener(new Nokri_JobsAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
            }
        });
        adapter.setOnImageClickListener(new Nokri_JobsAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Nokri_JobsModel model) {
                android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
//                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
//
//                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
//                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();

            }
        });
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
     /*   recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems =((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("stufffffffff", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data

                            if(hasNextPage) {
                                progressBar.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                }
            }
        });*/
        adapter.notifyDataSetChanged();


        //   Nokri_DialogManager.hideAfterDelay();
    }
    private void setupAdapters() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        Log.d("modelist", modelList.toString());
        for(int i = 0; i < modelLists.size(); i++){
            for(int j = 0; j < modelList.size(); j++){
                if(modelLists.get(i).getJobId().equals(modelList.get(j).getJobId())){
                    modelList.get(j).setAddress(modelLists.get(i).getAddress());
                }
            }
        }
        adapter = new Nokri_JobsAdapter(modelList, getContext(), new Nokri_JobsAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(Nokri_JobsModel item) {

            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {
                android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_view_job:
                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "";
                        Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                        Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                        Nokri_JobDetailFragment.purchased = purchased;
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
                        break;
//                    case R.id.menu_view_company_profile:
//                        android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
//                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
//                        Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
//
//                        Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
//                        fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
//                        break;
                }

            }
        });
        adapter.setOnJobClickListener(new Nokri_JobsAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
            }
        });
        adapter.setOnImageClickListener(new Nokri_JobsAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Nokri_JobsModel model) {
                android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
//                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
//
//                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
//                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();

            }
        });
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
     /*   recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems =((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("stufffffffff", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data

                            if(hasNextPage) {
                                progressBar.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                }
            }
        });*/
        adapter.notifyDataSetChanged();


        //   Nokri_DialogManager.hideAfterDelay();
    }
    private void nokri_filterJobs(String text) {
        JsonObject params = new JsonObject();
        params.addProperty("keyword", text);

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.filterAllJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.filterAllJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        modelList = new ArrayList<>();

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");

                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
                        noOfJobs.setText(data.getString("no_txt"));

                        JSONArray jobsArray = data.getJSONArray("jobs");
                        if (jobsArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            dialogManager.hideAlertDialog();
                            setupAdapter();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        for (int i = 0; i < jobsArray.length(); i++) {
                            JSONArray dataArray = jobsArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();
                            for (int j = 0; j < dataArray.length(); j++) {
                                model.setShowMenu(false);
                                JSONObject object = dataArray.getJSONObject(j);
                                if (object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));

                                }
                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        setupAdapter();

                        //   Log.d("Pointz",modelList.toString());
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }
    private void nokri_setValues() {


        if (countrySpinner.getAdapter() != null) {
            if (countrySpinnerModel.getIds() != null && countrySpinnerModel.getIds().size() > 0 && countrySpinner.getSelectedItemPosition() != 0)
                country = countrySpinnerModel.getIds().get(countrySpinner.getSelectedItemPosition());
        } else
            country = "";


    }
    private void nokri_postSearch() {

        nokri_setValues();

        String location;
        location = country;
        List<Nokri_JobsModel> temp = new ArrayList<>();
        if(countrySpinner.getSelectedItemPosition() == 0) return;
        for(int i = 0; i < modelList.size(); i++){
            if(modelList.get(i).getAddress().equals(countrySpinnerModel.getNames().get(countrySpinner.getSelectedItemPosition()))){
                temp.add(modelList.get(i));
            }
        }
        adapter.updateList(temp);
        Log.d("modellissst", countrySpinner.getSelectedItemPosition()+ "");
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
            case R.id.btn_load_more:
                loadMoreButton.setVisibility(View.GONE);
                if (hasNextPage) {
                    if (requestForm.equals("Home")) {
                        Submit_jobSearch(false);
                    } else {
                        if (ALL_JOBS_SOURCE.equals(""))
                            nokri_loadMore(false, filterText);
                        else
                            nokri_filterJobsExternal(false);

                    }
                }
                break;

        }


    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Log.i("MainActivity", "Product purchased");
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.i("MainActivity", "Purchase History Restored");
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
        Log.i("MainActivity", "Billing initialized");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = parent.getId();
        if (i == R.id.spinner_country) {
            if (countrySpinnerModel != null && countrySpinnerModel.getHasChild().get(position)) {
            } else {

            }
        }  else if (i == R.id.spinner_job_category) {
            if (spinnerModels[0] != null && spinnerModels[0].getHasChild().get(position)) {

            } else {

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
