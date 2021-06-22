package com.wakeapp.gigsfinder.employeer.jobs.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_InactiveJobsAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_InactiveJobsModel;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;

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


public class Nokri_InactiveJobsFragment extends Fragment implements Nokri_PopupManager.ConfirmInterface {
    private RecyclerView recyclerView;
    private Nokri_InactiveJobsAdapter adapter;
    private List<Nokri_InactiveJobsModel> modelList;

    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private nokri_pagerCallback listener;
    private String id;
    private Nokri_PopupManager popupManager;
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

    protected void onUserVisibleChanged(boolean visible) {
        if(getContext()!=null)
        {
            if(visible){
             nokri_getInactiveJobs();

                }

            }
        }




    public Nokri_InactiveJobsFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
       // nokri_getInactiveJobs();
        popupManager = new Nokri_PopupManager(getContext(),this);
        if(Nokri_Globals.IS_RTL_ENABLED)
            nokri_getInactiveJobs();
    }

    private void nokri_initialize(){

        recyclerView = getView().findViewById(R.id.recyclerview);
        emptyTextView = getView().findViewById(R.id.txt_empty);
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());

//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImage);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_inactive_jobs, container, false);
    }
    private void nokri_getInactiveJobs(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getInActiveJobs(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getInActiveJobs( Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {modelList = new ArrayList<>();

                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");

                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if(companiesArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            dialogManager.hideAlertDialog();
                            setupAdapter();
                            return;
                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_InactiveJobsModel model = new Nokri_InactiveJobsModel();
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_title"))
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

    private void setupAdapter() {

        adapter = new Nokri_InactiveJobsAdapter(modelList, getContext(), new Nokri_InactiveJobsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_InactiveJobsModel item) {

            }

            @Override
            public void menuItemSelected(Nokri_InactiveJobsModel model, MenuItem item) {

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
            public void onActive(Nokri_InactiveJobsModel model) {
            nokri_activeJobs(model.getJobId());
            }

            @Override
            public void onDeleteClick(Nokri_InactiveJobsModel model) {



            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void nokri_activeJobs(String id){

        JsonObject params = new JsonObject();
        params.addProperty("job_id",id);

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.activeJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.activeJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));

                            nokri_getInactiveJobs();
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

    @Override
    public void onConfirmClick(Dialog dialog) {
        nokri_deleteJob();
        dialog.dismiss();

    }


    public interface nokri_pagerCallback {
        void onCallback(Nokri_InactiveJobsModel model);
        void onViewJobClick(Nokri_InactiveJobsModel model);
        void onEditJobClick(Nokri_InactiveJobsModel model);
    }
    public void nokri_setListener(nokri_pagerCallback listener){
        this.listener = listener;
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
                                nokri_getInactiveJobs();

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
