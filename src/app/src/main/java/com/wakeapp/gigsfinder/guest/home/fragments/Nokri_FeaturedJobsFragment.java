package com.wakeapp.gigsfinder.guest.home.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.candidate.jobs.adapters.Nokri_JobsAdapter;
import com.wakeapp.gigsfinder.candidate.jobs.models.Nokri_JobsModel;
import com.wakeapp.gigsfinder.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.wakeapp.gigsfinder.guest.blog.adapters.Nokri_BlogGridAdapter;
import com.wakeapp.gigsfinder.guest.blog.models.Nokri_BlogGridModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

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


public class Nokri_FeaturedJobsFragment extends Fragment implements View.OnClickListener{

    private RecyclerView middleRecyclerView;
    private RecyclerView bottomRecyclerView;
    private List<Nokri_JobsModel> modelList;
    private List<Nokri_BlogGridModel>blogGridModelList;
    private Nokri_JobsAdapter adapter;
    private Nokri_BlogGridAdapter blogGridAdapter;
    private TextView emptyTextViewMiddle;
    private TextView emptyTextViewBottom;
    private ImageView messageImageMiddle;
    private ImageView messageImageBottom;
    private LinearLayout messageContainerMiddle;
    private LinearLayout messageContainerBotton;

    private int nextPage=1;
    private int blogGridNextPage =1;
    private boolean hasNextPage = true;
    private boolean blogGridHasNextPage = true;
    private Button loadMoreButtonMiddle;
    private Button loadMoreButtonBottom;
    private ProgressBar progressBarMiddle;
    private ProgressBar progressBarBottom;
    private nokri_pagerCallback pagerCallback;
    private RelativeLayout secondCntainer;
    public static String CALLING_SOURCE="";
    public static boolean CALLED_FROM_DASHBOARD = false;
    private TextView blogTitleTextView;
    private Nokri_DialogManager dialogManager;
    public void nokri_setPagerCallback(nokri_pagerCallback pagerCallback){
        this.pagerCallback = pagerCallback;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nokri_initialize(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextPage = 1;
                nokri_getFeaturedJobs(true);
                if(!CALLING_SOURCE.equals("")) {
                    blogGridNextPage = 1;
                    nokri_loadBlogGrid();
                }
            }
        },100);


    }




    @Override
    public void onResume() {
        super.onResume();
        try {
            Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        }
        catch (Exception e){}


    }

    public Nokri_FeaturedJobsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_featured_jobs, container, false);
    }





    private void nokri_loadBlogGrid() {
        RestService restService;
        if(!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        else
            restService =  Nokri_ServiceGenerator.createService(RestService.class);

        JsonObject params = new JsonObject();
        params.addProperty("page_number",blogGridNextPage);
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getHomeBlog(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getHomeBlog(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {

                        emptyTextViewBottom.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject extra = response.getJSONObject("extra");
                        blogTitleTextView.setText(extra.getString("page_title"));
                        loadMoreButtonBottom.setText(extra.getString("load_more"));
                        JSONObject data = response.getJSONObject("data");
                        JSONObject pagination = data.getJSONObject("pagination");

                        blogGridNextPage = pagination.getInt("next_page");

                        blogGridHasNextPage = pagination.getBoolean("has_next_page");
                        if(!blogGridHasNextPage){
                            loadMoreButtonBottom.setVisibility(View.GONE);
                            progressBarBottom.setVisibility(View.GONE);
                        }
                        else {
                            loadMoreButtonBottom.setVisibility(View.VISIBLE);
                            progressBarBottom.setVisibility(View.VISIBLE);
                        }
                        JSONArray postArray = data.getJSONArray("post");

                        if(postArray.length() == 0){
                            messageContainerBotton.setVisibility(View.VISIBLE);
                            emptyTextViewBottom.setText(response.getString("message"));
                            progressBarBottom.setVisibility(View.GONE);
                            loadMoreButtonBottom.setVisibility(View.GONE);
                            dialogManager.hideAlertDialog();
                            setupBlogAdapter();
                            return;
                        }
                        else
                            messageContainerBotton.setVisibility(View.GONE);
                        for(int i = 0;i<postArray.length();i++){
                            JSONObject object = postArray.getJSONObject(i);
                            Nokri_BlogGridModel model = new Nokri_BlogGridModel();
                            model.setHtmlResponse(false);
                            model.setId(object.getString("post_id"));
                            model.setHeadingText(object.getString("title"));
                            model.setParagraphText(object.getString("excerpt"));
                            model.setDateText(object.getString("date"));
                            model.setHeaderImage(object.getString("image"));
                            model.setHasImage(object.getBoolean("has_image"));
                            model.setCommentsText(extra.getString("comment_title")+" "+object.getString("comments"));
                            blogGridModelList.add(model);

                             /*   if(j+1==dataArray.length())
                                    modelList.add(model);*/

                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupBlogAdapter();
                        if(!blogGridHasNextPage){

                            progressBarBottom.setVisibility(View.GONE);
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

    private void nokri_initialize(View view){
        emptyTextViewMiddle = view.findViewById(R.id.txt_empty);
//        view.findViewById(R.id.line1).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        loadMoreButtonBottom = view.findViewById(R.id.btn_load_more2);
        progressBarBottom = view.findViewById(R.id.progress_bar2);

        loadMoreButtonBottom.setOnClickListener(this);

        if(CALLING_SOURCE.equals("")){
            secondCntainer = view.findViewById(R.id.blog);
            secondCntainer.setVisibility(View.GONE);
        }
        else
        { secondCntainer = view.findViewById(R.id.blog);
            secondCntainer.setVisibility(View.VISIBLE);
            emptyTextViewBottom = view.findViewById(R.id.txt_empty2);
            messageImageBottom = view.findViewById(R.id.img_message2);
            messageContainerBotton = view.findViewById(R.id.msg_container2);
            new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextViewBottom,getActivity().getAssets());
            blogTitleTextView = view.findViewById(R.id.txt_blog_title);
            new Nokri_FontManager().nokri_setOpenSenseFontTextView(blogTitleTextView,getActivity().getAssets());
            Picasso.with(getContext()).load(R.drawable.logo).into(messageImageBottom);

            blogGridModelList = new ArrayList<>();
            bottomRecyclerView = view.findViewById(R.id.recyclerview2);
            bottomRecyclerView.setNestedScrollingEnabled(false);
        }
        messageImageMiddle = view.findViewById(R.id.img_message);

        messageContainerMiddle = view.findViewById(R.id.msg_container);

        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextViewMiddle,getActivity().getAssets());


//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImageMiddle);

        loadMoreButtonMiddle = view.findViewById(R.id.btn_load_more);
        progressBarMiddle = view.findViewById(R.id.progress_bar);
        loadMoreButtonMiddle.setOnClickListener(this);

        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButtonBottom);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButtonMiddle);

        modelList = new ArrayList<>();

        middleRecyclerView = view.findViewById(R.id.recyclerview);

        middleRecyclerView.setNestedScrollingEnabled(false);

    }

    public interface nokri_pagerCallback {

        void onJobClick(Nokri_JobsModel model);
        void onCompanyClick(Nokri_JobsModel model);
        void onIconClick(Nokri_JobsModel model);
        void loadThisFragmentExternally();
        void onBlogItemClicked(Nokri_BlogGridModel blogGridModel);
        void onClickBlogLoadMode();
    }

    private void setupBlogAdapter(){
        bottomRecyclerView = getView().findViewById(R.id.recyclerview2);

        blogGridAdapter = new Nokri_BlogGridAdapter(blogGridModelList,getContext(), new Nokri_BlogGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_BlogGridModel item) {
                if(pagerCallback!=null)
                    pagerCallback.onBlogItemClicked(item);
            }
        });
        blogGridAdapter.setMultiLine(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        bottomRecyclerView.setLayoutManager(layoutManager);

        bottomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomRecyclerView.setAdapter(blogGridAdapter);


    }


    private void nokri_setupAdapter(){
        adapter = new Nokri_JobsAdapter(modelList,getContext(), new Nokri_JobsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Nokri_JobsModel item) {




            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {
                if(CALLING_SOURCE.equals(""))
                {

                    android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                    Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
                    fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

                }
                else
                    pagerCallback.onCompanyClick(item); }

            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item) {

            }
        });
        adapter.setOnImageClickListener(new Nokri_JobsAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Nokri_JobsModel model) {

                if(CALLING_SOURCE.equals(""))
                {

                    android.support.v4.app.FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                    Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();

                    fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

                }
                else
                    pagerCallback.onIconClick(model);

            }
        });
        adapter.setOnJobClickListener(new Nokri_JobsAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {

                if(CALLING_SOURCE.equals(""))
                {     android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                    Nokri_JobDetailFragment.CALLING_SOURCE = "";
                    Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                    Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();

                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();


                }
                else
                    pagerCallback.onJobClick(model); }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        middleRecyclerView.setLayoutManager(layoutManager);

        middleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        middleRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void nokri_getFeaturedJobs(final Boolean showAlert){
        if(showAlert)
        {
            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();

        params.addProperty("page_number",nextPage);


        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFeaturedJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getFeaturedJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getAppliedJobs(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if(responseObject.isSuccessful()){
                    try {

                        emptyTextViewMiddle.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");

                        if(CALLED_FROM_DASHBOARD) {
                            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                            toolbarTitleTextView.setText(data.getString("tab_title"));

                        }
                        if(!hasNextPage){
                            loadMoreButtonMiddle.setVisibility(View.GONE);
                            progressBarMiddle.setVisibility(View.GONE);
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
                        }
                        else {
                            loadMoreButtonMiddle.setVisibility(View.VISIBLE);
                            progressBarMiddle.setVisibility(View.VISIBLE);
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.VISIBLE);
                                }

                        JSONArray jobsArray = data.getJSONArray("jobs");

                        if(jobsArray.length() == 0){
                            messageContainerMiddle.setVisibility(View.VISIBLE);
                            emptyTextViewMiddle.setText(response.getString("message"));
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
                            progressBarMiddle.setVisibility(View.GONE);
                            loadMoreButtonMiddle.setVisibility(View.GONE);
                            nokri_setupAdapter();
                            if(showAlert && CALLING_SOURCE.equals(""))
                                dialogManager.hideAlertDialog();
                            return;

                        }
                        else
                            messageContainerMiddle.setVisibility(View.GONE);
                        for(int i = 0;i<jobsArray.length();i++)
                        {
                            JSONArray dataArray =  jobsArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();
                            for(int j =0;j<dataArray.length();j++)
                            {model.setShowMenu(false);
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                if(object.getString("field_type_name").equals("company_id"))
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
                                }
                                else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));

                                if(j+1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        nokri_setupAdapter();
                        if(!hasNextPage){

                            progressBarMiddle.setVisibility(View.GONE);
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
                        }
                        if(showAlert &&  CALLING_SOURCE.equals(""))
                            dialogManager.hideAfterDelay();

                    } catch (IOException e) {
                        if(showAlert){
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();}
                        e.printStackTrace();
                    } catch (JSONException e) {
                        if(showAlert){
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();}

                        e.printStackTrace();

                    }

                }
                else {if(showAlert) {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(showAlert){
                    Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                    dialogManager.hideAfterDelay();}
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_load_more) {
            loadMoreButtonMiddle.setVisibility(View.GONE);
            if (hasNextPage && CALLING_SOURCE.equals("")) {

                nokri_getFeaturedJobs(false);

            } else
                pagerCallback.loadThisFragmentExternally();
        } else if (i == R.id.btn_load_more2) {
            if (pagerCallback != null)
                pagerCallback.onClickBlogLoadMode();
        }
    }
}

