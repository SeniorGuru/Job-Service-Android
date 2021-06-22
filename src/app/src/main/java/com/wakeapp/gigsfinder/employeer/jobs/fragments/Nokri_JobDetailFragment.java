package com.wakeapp.gigsfinder.employeer.jobs.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_DescriptionModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_JobDetailFragment extends Fragment implements View.OnClickListener {
    private TextView jobTitleTextView,companyNameTextView,applyByTextView,dateTextView,shortDescriptonTextView,jobDescriptionTextView,nameTextView,urlTextView,addressTextView;
    private ImageButton bookmarkButton,applyNowButton;
    private WebView jobDescriptionDataTextView;
    private Nokri_FontManager fontManager;
    private RecyclerView shortDescrptionRecyclerView;
    private List<Nokri_DescriptionModel> shortDescriptionList;
    private Nokri_DescriptionRecyclerViewAdapter shortDescriptionAdapter;
    public static String JOB_ID;
    public static String COMPANY_ID;
    public static boolean purchased;
    private ArrayList<String>ids,names;
    private CircularImageView companyLogoImageView;
    private static final DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
    public static String CALLING_SOURCE = "";
    private RelativeLayout cardContaine4;
LinearLayout linearlayout;
View underline,underline2;
    private Dialog dialog;
    private boolean hasAppliedForJob = false;
    private boolean hasJobExpired = true;
    private String jobExpitedText ="";
    private RelativeLayout buttonContainer;
    private String popupTitle;
    private Nokri_DialogManager dialogManager;
    private TextView applyDate;
    Button bookmarkTextView;
    private TextView applyJobTextView;
    private ImageButton linkedInImageButton;
    private String isCandidate;
    private String onlyCandidateCanApplyMessage;
    private String alreadyAppliedForThisJob;
    private String loginFirst;
    private String onlyCandidateCanBookmark;
    private ImageView locationImageview;
    private ImageButton shareImageButton;
    private TextView shareTextView;
    private String jobUrl;
    public Nokri_JobDetailFragment() {
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
        return inflater.inflate(R.layout.fragment_nokri_job_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nokri_getJobDetail();
            }
        },100);

    }
    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();
        getView().findViewById(R.id.container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));


        locationImageview = getView().findViewById(R.id.img_location);
        locationImageview.setBackground(Nokri_Utils.getColoredXml(getContext(),R.drawable.location_icon));

//        shareImageButton = getView().findViewById(R.id.img_btn_share);
        shareTextView = getView().findViewById(R.id.txt_share);

        shortDescrptionRecyclerView = getView().findViewById(R.id.short_description_recyclerview);
        shortDescrptionRecyclerView.setNestedScrollingEnabled(false);


        buttonContainer = getView().findViewById(R.id.button_container);
//        buttonContainer.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

      /*  if(CALLING_SOURCE.equals("applied") || Nokri_SharedPrefManager.isAccountEmployeer(getContext()))
            buttonContainer.setVisibility(View.GONE);*/

        jobTitleTextView = getView().findViewById(R.id.txt_job_title);

        companyNameTextView = getView().findViewById(R.id.txt_company_name);
        applyByTextView = getView().findViewById(R.id.txt_apply_by);
        dateTextView = getView().findViewById(R.id.txt_date);
        shortDescriptonTextView = getView().findViewById(R.id.txt_short_description);
        jobDescriptionTextView = getView().findViewById(R.id.txt_job_description);
        jobDescriptionDataTextView = getView().findViewById(R.id.txt_job_description_data);
//        WebSettings webSettings = jobDescriptionDataTextView.getSettings();
//        webSettings.setDefaultFontSize(14);
//        webSettings.setSansSerifFontFamily("OpenSans.ttf");

        nameTextView = getView().findViewById(R.id.txt_name);
        urlTextView = getView().findViewById(R.id.txt_url);
        addressTextView = getView().findViewById(R.id.txt_address);

            bookmarkTextView = getView().findViewById(R.id.txt_bookmark);
        applyJobTextView = getView().findViewById(R.id.txt_apply_job);
//        applyJobTextView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        applyJobTextView.setBackground(Nokri_Utils.getColoredXml(getContext(),R.drawable.emailaddress));
        underline=getView().findViewById(R.id.line12);
        underline2=getView().findViewById(R.id.line13);
//        bookmarkButton = getView().findViewById(R.id.btn_bookmark);
//        applyNowButton = getView().findViewById(R.id.btn_applynow);
//        linkedInImageButton = getView().findViewById(R.id.img_btn_linkedin);

        if(Nokri_SharedPrefManager.isAccountEmployeer(getContext()))
        {
//            getView().findViewById(R.id.right).setVisibility(View.VISIBLE);
//            getView().findViewById(R.id.middle).setVisibility(View.GONE);
            getView().findViewById(R.id.button_container).setVisibility(View.GONE);
            getView().findViewById(R.id.left).setVisibility(View.GONE);

        }

        cardContaine4 = getView().findViewById(R.id.card_container4);
        companyLogoImageView = getView().findViewById(R.id.img_logo);
        shortDescriptionList = new ArrayList<>();


        shortDescriptionList = new ArrayList<>();


        ids = new ArrayList<>();
        names = new ArrayList<>();

        bookmarkTextView.setOnClickListener(this);
        applyJobTextView.setOnClickListener(this);
        cardContaine4.setOnClickListener(this);
        shareTextView.setOnClickListener(this);

    }




    private void setupShortDescriptionAdapter() {
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        shortDescriptionAdapter = new Nokri_DescriptionRecyclerViewAdapter(shortDescriptionList, getContext(),1);
        shortDescrptionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shortDescrptionRecyclerView.setItemAnimator(itemAnimator);
        shortDescrptionRecyclerView.setAdapter(shortDescriptionAdapter);
        shortDescriptionAdapter.notifyDataSetChanged();
    }




    private void nokri_setupFonts(){





        fontManager.nokri_setMonesrratSemiBioldFont(jobTitleTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(shortDescriptonTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(jobDescriptionTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(bookmarkTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(applyJobTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(shareTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(companyNameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(applyByTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(dateTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(urlTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView,getActivity().getAssets());

       /* fontManager.nokri_setOpenSenseFontButton(bookmarkButton,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(applyNowButton,getActivity().getAssets());
*/

    }



    private void nokri_getJobDetail(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("job_id",JOB_ID);
        RestService restService;
        if(Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService =  Nokri_ServiceGenerator.createService(RestService.class);
        else
            restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getJobDetail(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getJobDetail(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jobInfoArray  = data.getJSONArray("job_info");
                        JSONArray jobContentArray = data.getJSONArray("job_content");
                        JSONArray companyInfoArray = data.getJSONArray("comp_info");
                        JSONObject jobContentObject = null;
                        if(jobContentArray.length()>0)
                        {
                            jobContentObject = jobContentArray.getJSONObject(0);
                        }
                        JSONArray extrasArray = data.getJSONArray("extras");

                        for (int i =0;i<extrasArray.length();i++){
                            JSONObject extra = extrasArray.getJSONObject(i);
                            if(extra.getString("field_type_name").equals("job_apply"))
                                applyJobTextView.setText(extra.getString("key"));
                            if(extra.getString("field_type_name").equals("short_desc"))
                                shortDescriptonTextView.setText(extra.getString("key"));
                            if(extra.getString("field_type_name").equals("job_desc"))
                                jobDescriptionTextView.setText(extra.getString("key"));
                            if(extra.getString("field_type_name").equals("book_mark"))
                                bookmarkTextView.setText(extra.getString("key"));
                            if(extra.getString("field_type_name").equals("job_expired"))
                                jobExpitedText = extra.getString("key");
                            if(extra.getString("field_type_name").equals("page_title"))
                            {
//                                Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    toolbar.setTitle(extra.getString("key"));
//                                    toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//                                }
                                TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                toolbarTitleTextView.setText( extra.getString("key"));
                            }
                            if(extra.getString("field_type_name").equals("cand_apply"))
                                onlyCandidateCanApplyMessage = extra.getString("key");
                            if(extra.getString("field_type_name").equals("cand_bookmark"))
                                onlyCandidateCanBookmark = extra.getString("key");
                            if(extra.getString("field_type_name").equals("is_login"))
                                loginFirst = extra.getString("key");
                            if(extra.getString("field_type_name").equals("already_applied"))
                                alreadyAppliedForThisJob = extra.getString("key");
                            if(extra.getString("field_type_name").equals("share_job"))
                            {
                                shareTextView.setText(extra.getString("key"));
                                jobUrl = extra.getString("value");
                            }
                        }

                        for(int i =0;i<jobInfoArray.length();i++){
                            JSONObject jobInfoObject = jobInfoArray.getJSONObject(i);
                            if(jobInfoObject.getString("field_type_name").equals("job_title"))
                            {
                                jobTitleTextView.setText(jobInfoObject.getString("value"));
                                popupTitle = jobInfoObject.getString("value");
                            }
                            else
                            if(jobInfoObject.getString("field_type_name").equals("job_last"))
                            {
                                if(!hasJobExpired) {
                                    dateTextView.setText(jobInfoObject.getString("value"));
                                    applyByTextView.setText(jobInfoObject.getString("key"));
                                }
                            } else if(jobInfoObject.getString("field_type_name").equals("job_cat"))
                            {
                                companyNameTextView.setText(jobInfoObject.getString("value"));
                            } else if(jobInfoObject.getString("field_type_name").equals("is_candidate"))
                            {
                                isCandidate = jobInfoObject.getString("value");
                            } else if(jobInfoObject.getString("field_type_name").equals("job_apply")) {
                                if(Nokri_SharedPrefManager.isAccountCandidate(getContext())) {
                                    hasAppliedForJob = jobInfoObject.getBoolean("value");
                                }
                                continue;
                            } else if(jobInfoObject.getString("field_type_name").equals("job_expire")) {
                                hasJobExpired = jobInfoObject.getBoolean("value");
                                if(hasJobExpired){
                                    applyByTextView.setText(jobExpitedText);
                                    getView().findViewById(R.id.container).setBackgroundColor(getResources().getColor(R.color.google_red));
                                    dateTextView.setVisibility(View.GONE);
                                    buttonContainer.setVisibility(View.GONE);
                                }
                                continue;
                            }
                            else if(jobInfoObject.getString("field_type_name").equals("job_space") ||
                                    jobInfoObject.getString("field_type_name").equals("job_paid") ||
                                    jobInfoObject.getString("field_type_name").equals("job_equip") ||
                                    jobInfoObject.getString("field_type_name").equals("job_position") ||
                                    jobInfoObject.getString("field_type_name").equals("job_timeline") ||


                                    jobInfoObject.getString("field_type_name").equals("job_venue") ||
                                    jobInfoObject.getString("field_type_name").equals("job_capacity")
                            ) {
                                if(!purchased){
//                                    if(jobInfoObject.getString("field_type_name").equals("job_venue") || jobInfoObject.getString("field_type_name").equals("job_position")){

//                                    } else {
                                        Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                        model.setTitle(jobInfoObject.getString("key"));
                                        model.setDescription(jobInfoObject.getString("value"));
                                        shortDescriptionList.add(model);
//                                    }
                                } else{
                                    Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                    model.setTitle(jobInfoObject.getString("key"));
                                    model.setDescription(jobInfoObject.getString("value"));
                                    shortDescriptionList.add(model);
                                }

                            }
                        }

                        setupShortDescriptionAdapter();
                        jobDescriptionDataTextView.loadDataWithBaseURL(null, jobContentObject.getString("value"), "text/html", "utf-8", null);
//                        jobDescriptionDataTextView.loadData(jobContentObject.getString("value"), "text/html; charset=utf-8", null);
                        jobDescriptionDataTextView.getSettings().setAllowFileAccessFromFileURLs(false);
                        jobDescriptionDataTextView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
                        jobDescriptionDataTextView.getSettings().setJavaScriptEnabled(true);
                        jobDescriptionDataTextView.getSettings().setPluginState(WebSettings.PluginState.ON);
                        jobDescriptionDataTextView.getSettings().setDefaultFontSize(14);
                        jobDescriptionDataTextView.getSettings().setSansSerifFontFamily("OpenSans.ttf");

                        for(int i =0;i<companyInfoArray.length();i++){
                            JSONObject companyInfoObject = companyInfoArray.getJSONObject(i);
                            if(companyInfoObject.getString("field_type_name").equals("comp_img")){
                                if(!TextUtils.isEmpty(companyInfoObject.getString("value")))
                                    Picasso.with(getContext()).load(companyInfoObject.getString("value")).fit().centerCrop().into(companyLogoImageView);
                            }
                            else if(companyInfoObject.getString("field_type_name").equals("comp_name")){
                                nameTextView.setText(companyInfoObject.getString("value"));
                            }
                            else if(companyInfoObject.getString("field_type_name").equals("comp_web")){
                                urlTextView.setText(companyInfoObject.getString("value"));
                                urlTextView.setVisibility(View.GONE);
                            }
                            else if(companyInfoObject.getString("field_type_name").equals("company_adress")){
                                addressTextView.setText(companyInfoObject.getString("value"));
                                if(companyInfoObject.getString("value").trim().equals("") || companyInfoObject.getString("value").trim().isEmpty())
                                    locationImageview.setVisibility(View.GONE);
                            }


                             /*else{
                                 Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                 model.setDescription(companyInfoObject.getString("value"));
                                 model.setTitle(companyInfoObject.getString("key"));
                                 personalInfoList.add(model);
                             }
*/                         }
                        //                      setupPersonalInfoAdapter();






                        dialogManager.hideAlertDialog(); } catch (JSONException e) {
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    private void nokri_getPopupLabels(){
        JsonObject params = new JsonObject();
        params.addProperty("job_id",JOB_ID);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getApplyJobPopup(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getApplyJobPopup(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {ids = new ArrayList<>();
                        names  = new ArrayList<>();
                        String headingText = null,coverText = null;
                        String buttonText = null,selectResumeText= null;


                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        JSONArray infoArray = data.getJSONArray("info");
                        for(int i = 0;i<infoArray.length();i++){

                            JSONObject infoObject = infoArray.getJSONObject(i);
                            if(infoObject.getString("field_type_name").equals("job_apply"))
                            {
                                headingText = infoObject.getString("key");
                            }
                            else   if(infoObject.getString("field_type_name").equals("job_resume"))
                            {
                                selectResumeText = infoObject.getString("key");
                            }

                            else    if(infoObject.getString("field_type_name").equals("job_cvr"))
                            {
                                coverText = infoObject.getString("key");
                            }
                            else    if(infoObject.getString("field_type_name").equals("job_btn"))
                            {
                                buttonText = infoObject.getString("key");
                            }

                        }


                        JSONArray filterArray = data.getJSONArray("resumes");

                        for (int i = 0;i<filterArray.length();i++){
                            JSONObject valueObject =  filterArray.getJSONObject(i);
                            ids.add(valueObject.getString("key"));
                            names.add(valueObject.getString("value"));
                        }


                        nokri_showDialog(headingText,ids,names,coverText,buttonText,selectResumeText);
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


    private void nokri_applyJob(String resumeId, String coverLetter){
        JsonObject params = new JsonObject();
        params.addProperty("job_id",JOB_ID);
        params.addProperty("cand_apply_resume",resumeId);
        params.addProperty("cand_cover_letter",coverLetter);
        params.addProperty("cand_date",sdf.format(new Date()));
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postApplyJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postApplyJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {


                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(), "Successfully applied, please check your emails for a response");
                       // Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        if(dialog!=null && dialog.isShowing())
                            dialog.dismiss();
                        hasAppliedForJob = true;


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


    private void nokri_shareLink(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        // shareIntent.putExtra(Intent.EXTRA_TEXT, "www.google.com");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitleTextView.getText());
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitleTextView.getText());
        shareIntent.putExtra(Intent.EXTRA_TEXT, jobUrl);


        // Launch sharing dialog for image
        startActivity(Intent.createChooser(shareIntent, "Share Job"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_share:
                    nokri_shareLink();
                break;
            case R.id.txt_bookmark:
                 if(isCandidate.equals("1") && !Nokri_SharedPrefManager.isAccountPublic(getContext()))
                    nokri_bookmarkJob();
                else {

                    Nokri_ToastManager.showLongToast(getContext(), onlyCandidateCanBookmark);
                }
                break;
            case R.id.txt_apply_job:
                if (purchased == true) {
                    if (isCandidate.equals("1") && !Nokri_SharedPrefManager.isAccountPublic(getContext())) {
                        if (!hasAppliedForJob)
                            nokri_getPopupLabels();
                        else
                            Nokri_ToastManager.showLongToast(getContext(), alreadyAppliedForThisJob);
                    } else {
                        Nokri_ToastManager.showLongToast(getContext(), onlyCandidateCanBookmark);
                    }
                }else{
                    Toast.makeText(view.getContext(), "Please buy a package to apply", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.card_container4:
                android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = this.COMPANY_ID;
                Nokri_CompanyPublicProfileFragment.purchased = purchased;

                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

                break;
        }
    }






    private void nokri_bookmarkJob(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("job_id",JOB_ID);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.bookmarkJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.bookmarkJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {


                        JSONObject jsonObject = new JSONObject(responseObject.body().string());


                        Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));





                        dialogManager.hideAlertDialog(); } catch (JSONException e) {
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    public void nokri_showDialog(String headingText, final ArrayList<String>ids, ArrayList<String>names, String coverText, String buttonText, String selectResumeText){


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.apply_job_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final TextFieldBoxes textFieldBoxes = dialog.findViewById(R.id.text_field_boxes);
        textFieldBoxes.setLabelText(coverText);

        final RelativeLayout container = dialog.findViewById(R.id.container1);
        dialog.findViewById(R.id.heading).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        params.width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth()-100;
        container.setLayoutParams(params);
        EditText editText;
        textFieldBoxes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    textFieldBoxes.setPrimaryColor(Color.parseColor(Nokri_Config.APP_COLOR));
                else{
                    //  textFieldBoxes.setPrimaryColor(getResources().getColor(R.color.grey));
                }
            }
        });

        TextView headerTextView = dialog.findViewById(R.id.txt_header);
        headerTextView.setText(popupTitle);
        final ExtendedEditText coverLetterEditText = dialog.findViewById(R.id.edittxt_cover);

        Button applyNowButton = dialog.findViewById(R.id.btn_ápplynow);
        applyNowButton.setText(buttonText);
        Nokri_Utils.setRoundButtonColor(getContext(),applyNowButton);
        applyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( coverLetterEditText!=null && !coverLetterEditText.getText().toString().isEmpty())
                    nokri_applyJob("",coverLetterEditText.getText().toString());

                else
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);

            }
        });
        fontManager.nokri_setOpenSenseFontButton(applyNowButton,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(headerTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(coverLetterEditText,getActivity().getAssets());
        ImageView imageClose =  dialog.findViewById(R.id.img_close);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void nokri_shareJob(){

            Uri bmpUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
           bmpUri = nokri_getLocalBitmapUriPostLollipop(companyLogoImageView);

        }

            else
             bmpUri = getLocalBitmapUri(companyLogoImageView);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "www.google.com");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitleTextView.getText());

            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            // ...sharing failed, handle error
        }

    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    private Uri nokri_getLocalBitmapUriPostLollipop(ImageView imageView){

        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;

        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            return FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    return null;}
}

