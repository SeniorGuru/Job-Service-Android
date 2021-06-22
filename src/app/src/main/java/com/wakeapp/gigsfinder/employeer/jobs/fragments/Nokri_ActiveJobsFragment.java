package com.wakeapp.gigsfinder.employeer.jobs.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_ActiveJobsAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_ActiveJobsModel;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
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
public class Nokri_ActiveJobsFragment extends Fragment implements AdapterView.OnItemSelectedListener,Nokri_PopupManager.ConfirmInterface,View.OnClickListener {

    private RecyclerView recyclerView;
    private Nokri_ActiveJobsAdapter adapter;
    private List<Nokri_ActiveJobsModel> modelList;
    private TextView emptyTextView;
    private ImageView messageImage;
    private ImageView alertImage;
    private LinearLayout messageContainer;
    private  int counter = 0;
    private Spinner spinner;
    private ArrayList<String>spinnerNmaes;
    private ArrayList<String>spinnerIds;
    private nokri_pagerCallback listener;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private Nokri_PopupManager popupManager;
    private String id;
    private ProgressBar progressBar;
    private int maxNumOfPages,currentPage,nextPage=1,increment,currentNoOfJobs;
    private boolean hasNextPage,loading = true;
    private Button loadMoreButton;
    private boolean isCallFromFilters = false;
    private  boolean isVisibleCalled = false;
    private Nokri_DialogManager dialogManager;


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

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewCreated = false;
        mUserSeen = false;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Override this if you want to get savedInstanceState.
        mViewCreated = true;
        tryViewCreatedFirstSight();
        alertImage = getView().findViewById(R.id.alert_message);

    }
    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }


    protected void onViewCreatedFirstSight(View view) {

    }

    protected void onUserFirstSight() {

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nokri_initialize();

        isCallFromFilters = false;


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    modelList.clear();
                    counter = 0;
                    nextPage = 1;
                    currentPage = 1;

                    if(!isVisibleCalled)
                    nokri_loadMore(true);
                }
            }, 100);
        }



    protected void onUserVisibleChanged(boolean visible) {
            if(getContext()!=null)
            {
                if(visible){
                    isVisibleCalled = true;

                    modelList.clear();
                    if(adapter!=null)
                        adapter.notifyDataSetChanged();
                    counter = 0;
                    nextPage = 1;
                    currentPage = 1;
                    nokri_loadMore(true);
                }
            }
    }

    @Override
    public void onConfirmClick(Dialog dialog) {
        nokri_deleteJob();
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        loadMoreButton.setVisibility(View.GONE);
        if(!isCallFromFilters) {
            if (hasNextPage)
                nokri_loadMore(false);
        }

    else
        {
            if (hasNextPage)
                nokri_filterActiveJobs(spinnerIds.get(spinner.getSelectedItemPosition()),false);
        }
    }


    public interface nokri_pagerCallback {
        void onCallback(Nokri_ActiveJobsModel model);
        void onViewJobClick(Nokri_ActiveJobsModel model);
        void onEditJobClick(Nokri_ActiveJobsModel model);
    }

    public Nokri_ActiveJobsFragment() {
        // Required empty public constructor
    }




    private void nokri_filterActiveJobs(String id, final boolean showAlert){
        if(showAlert)
        {

            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }

        JsonObject params = new JsonObject();
        params.addProperty("job_class",id);
        params.addProperty("page_number",nextPage);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.filterActiveJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.filterActiveJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {modelList = new ArrayList<>();

                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());

                        JSONObject pagination = response.getJSONObject("pagination");
                        maxNumOfPages = pagination.getInt("max_num_pages");
                        currentPage = pagination.getInt("current_page");
                        nextPage = pagination.getInt("next_page");
                        increment = pagination.getInt("increment");
                        currentNoOfJobs = pagination.getInt("current_no_of_ads");
                        hasNextPage = pagination.getBoolean("has_next_page");
                        if(!hasNextPage){
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        JSONObject data = response.getJSONObject("data");

//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));





                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if(companiesArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            if(showAlert)
                                dialogManager.hideAlertDialog();
                            setupAdapter();
                            return;
                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_ActiveJobsModel model = new Nokri_ActiveJobsModel();
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("job_id")){
                                    model.setJobId(object.getString("value"));
                                }

                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_expiry"))
                                {  model.setJobExpireDate(object.getString("value"));
                                    model.setJobExpire(object.getString("key"));
                                }
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("inactive_job"))
                                    model.setInavtiveText(object.getString("key"));

                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupAdapter();
                        if(!hasNextPage){

                            progressBar.setVisibility(View.GONE);
                        }
                        if(showAlert)
                            dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        if(showAlert){
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAlertDialog();}
                        e.printStackTrace();
                    } catch (IOException e) {
                        if(showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAlertDialog();
                        }
                        e.printStackTrace();

                    }
                }
                else {
                    if(showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAlertDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAlertDialog();
            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();
        isVisibleCalled = false;

    }

    private void nokri_initialize(){

        recyclerView = getView().findViewById(R.id.recyclerview);
        emptyTextView = getView().findViewById(R.id.txt_empty);
//        emptyTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(loadMoreButton,getActivity().getAssets());
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        spinner = getView().findViewById(R.id.spinner);
//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImage);
        spinner.setOnItemSelectedListener(this);
        popupManager = new Nokri_PopupManager(getContext(),this);
        modelList = new ArrayList<>();

        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void nokri_loadMore(final Boolean showAlert) {
        if(showAlert)
        {

            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        JsonObject params = new JsonObject();
        params.addProperty("page_number",nextPage);
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getActiveJobsAddMore(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getActiveJobsAddMore(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        spinnerNmaes = new ArrayList<>();
                        spinnerIds = new ArrayList<>();
                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");
                        maxNumOfPages = pagination.getInt("max_num_pages");
                        currentPage = pagination.getInt("current_page");
                        nextPage = pagination.getInt("next_page");

                        increment = pagination.getInt("increment");
                        currentNoOfJobs = pagination.getInt("current_no_of_ads");
                        hasNextPage = pagination.getBoolean("has_next_page");
                        if(!hasNextPage){
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        JSONObject data = response.getJSONObject("data");


//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONObject jobFilterObject = data.getJSONObject("job_filter");
                        JSONArray valueArray = jobFilterObject.getJSONArray("value");

                        for (int i = 0;i<valueArray.length();i++){
                            JSONObject valueObject =  valueArray.getJSONObject(i);
                            spinnerNmaes.add(valueObject.getString("value"));
                            spinnerIds.add(valueObject.getString("key"));
                        }
                        if(spinner.getAdapter() == null)
                            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,spinnerNmaes));

                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if(companiesArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                           if(showAlert)
                               dialogManager.hideAlertDialog();
                            setupAdapter();
                            return;
                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_ActiveJobsModel model = new Nokri_ActiveJobsModel();
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_expiry"))
                                {  model.setJobExpireDate(object.getString("value"));
                                    model.setJobExpire(object.getString("key"));
                                }
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("inactive_job"))
                                    model.setInavtiveText(object.getString("key"));

                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupAdapter();
                        if(!hasNextPage){

                            progressBar.setVisibility(View.GONE);
                        }
                        if(showAlert)
                            dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        if(showAlert){
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAlertDialog();}
                        e.printStackTrace();
                    } catch (IOException e) {
                        if(showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAlertDialog();
                        }
                        e.printStackTrace();

                        }

                }
                else {
                    if(showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAlertDialog();
                    }
                    }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAlertDialog();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_active_jobs, container, false);
    }

    public void nokri_setListener(nokri_pagerCallback listener){
        this.listener = listener;
    }

    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_ActiveJobsAdapter(modelList, getContext(), new Nokri_ActiveJobsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_ActiveJobsModel item) {

            }

            @Override
            public void menuItemSelected(Nokri_ActiveJobsModel model, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_resume_received:
                            listener.onCallback(model);
                            break;
                        case R.id.menu_delete:
                            id  = model.getJobId();
                            popupManager.nokri_showDeletePopup();
                            break;
                        case R.id.menu_edit:
                        listener.onEditJobClick(model);
                            break;
                        case R.id.menu_view_job:
                            listener.onViewJobClick(model);
                            break;
                            default:
                                break;
                    }
            }

            @Override
            public void onInActive(Nokri_ActiveJobsModel model) {
                nokri_inactiveJobs(model.getJobId());
            }

            @Override
            public void onDeleteClick(Nokri_ActiveJobsModel model) {



            }
        });

        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(counter!=0) {

            nextPage = 1;
            isCallFromFilters = true;
            Log.d("actives", id);
            nokri_filterActiveJobs(spinnerIds.get(i),true);

        }
        counter++;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void nokri_inactiveJobs(String id){

        JsonObject params = new JsonObject();
        params.addProperty("job_id",id);


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.inActiveJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.inActiveJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        if(spinnerIds!=null && !spinnerIds.isEmpty()) {

                            if(counter>1) {
                                isCallFromFilters = true;
                                nextPage = 1;
                                nokri_filterActiveJobs(spinnerIds.get(spinner.getSelectedItemPosition()), true);
                            }
                            else {
                                isCallFromFilters = false;
                                currentPage =1;
                                nextPage=1;
                                modelList.clear();
                                nokri_loadMore(true);
                            }
                        }

                        dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }

                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAlertDialog();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAlertDialog();
            }
        });

    }



    private void nokri_deleteJob(){

        if(id!=null && !id.isEmpty()){



            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
            JsonObject params = new JsonObject();
            params.addProperty("job_id",id);

            RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

            Call<ResponseBody> myCall;
            if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.postDeleteJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else

            {
                myCall = restService.postDeleteJob(params, Nokri_RequestHeaderManager.addHeaders());
            }
            //  Call<ResponseBody> myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addHeaders());

            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                    if(responseObject.isSuccessful()){

                        try {
                            JSONObject response = new JSONObject(responseObject.body().string());
                            if (response.getBoolean("success")) {
                                dialogManager.hideAlertDialog();
                                popupManager.nokri_showSuccessPopup(response.getString("message"));
                                if(counter>1) {
                                    isCallFromFilters = true;
                                    nokri_filterActiveJobs(spinnerIds.get(spinner.getSelectedItemPosition()), true);

                                }
                                    else {
                                    isCallFromFilters = false;
                                    modelList.clear();
                                    nextPage = 1;
                                    currentPage = 1;
                                   nokri_loadMore(true);

                                }
                            } else {
                                dialogManager.showCustom(responseObject.message());

                                dialogManager.hideAlertDialog();
                            }

                        } catch (JSONException e) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAlertDialog();
                            e.printStackTrace();
                        } catch (IOException e) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAlertDialog();
                            e.printStackTrace();
                        }
                    }

                    else {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAlertDialog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialogManager.showCustom(t.getMessage());
                    dialogManager.hideAlertDialog();
                }
            });



        }



    }

}
