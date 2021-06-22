package com.wakeapp.gigsfinder.candidate.follow.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.wakeapp.gigsfinder.candidate.follow.adapters.Nokri_FollowingAdapter;
import com.wakeapp.gigsfinder.candidate.follow.models.Nokri_FollowingModel;
import com.wakeapp.gigsfinder.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
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
public class Nokri_FollowingFragment extends Fragment implements Nokri_PopupManager.ConfirmInterface,View.OnClickListener{

    private RecyclerView recyclerView;

    private List<Nokri_FollowingModel> modelList;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;

    private String id;
    private Nokri_PopupManager popupManager;
    private Nokri_FollowingAdapter adapter;
    private Nokri_DialogManager dialogManager;
    private ProgressBar progressBar;
    private int nextPage=1;
    private boolean hasNextPage = true;
    private Button loadMoreButton;
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_FollowingFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_following, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modelList = new ArrayList<>();
        popupManager = new Nokri_PopupManager(getContext(),this);
        emptyTextView = getView().findViewById(R.id.txt_empty);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImage);
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getCompany());
        nextPage = 1;
        nokri_loadMore(true);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);
    }

    private void nokri_loadMore(final boolean showAlert) {

        if(showAlert) {
            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        JsonObject params = new JsonObject();

        params.addProperty("page_number",nextPage);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFollowedCompaniesLoadMore(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getFollowedCompaniesLoadMore( params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        emptyTextView.setText("");

                        JSONObject response = new JSONObject(responseObject.body().string());

                        JSONObject pagination = response.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

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
                        JSONArray companiesArray = data.getJSONArray("comapnies");
                        if(companiesArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);

                            setupAdapter();
                            if(showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_FollowingModel model = new Nokri_FollowingModel();
                            model.setUnfollow(data.getString("btn_text"));
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setCompanyName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_adress"))
                                    model.setCompanyAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("open_position"))
                                    model.setTotalPositons(object.getString("key")+" "+object.getString("value"));
                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        setupAdapter();
                        if(!hasNextPage){
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if(showAlert)
                            dialogManager.hideAlertDialog();


                    } catch (JSONException e) {
                        if(showAlert){
                            dialogManager .showCustom(e.getMessage());
                            dialogManager .hideAfterDelay();}
                        e.printStackTrace();
                    } catch (IOException e) {
                        if(showAlert){
                            dialogManager .showCustom(e.getMessage());
                            dialogManager .hideAfterDelay();}
                        e.printStackTrace();
                    }

                }
                else {
                    if(showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                if(showAlert)
                    dialogManager .hideAfterDelay();
            }
        });

    }

    private void nokri_getFollowedCompanies(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getFollowedCompanies( Nokri_RequestHeaderManager.addHeaders());
        }
       // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {modelList = new ArrayList<>();
                        emptyTextView.setText("");

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        JSONArray companiesArray = data.getJSONArray("comapnies");
                        if(companiesArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            setupAdapter();
                            dialogManager.hideAlertDialog();
                            return;
                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_FollowingModel model = new Nokri_FollowingModel();
                            model.setUnfollow(data.getString("btn_text"));
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setCompanyName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_adress"))
                                    model.setCompanyAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("open_position"))
                                    model.setTotalPositons(object.getString("key")+" "+object.getString("value"));
                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        setupAdapter();
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


    private void nokri_unFollow() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("company_id",id);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postDeleteFollowedCompanies(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postDeleteFollowedCompanies(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //Call<ResponseBody> myCall = restService.postDeleteFollowedCompanies(params,Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            recyclerView.removeAllViews();
                           nextPage = 1;
                           modelList.clear();
                           nokri_loadMore(true);
                            // nokri_getFollowedCompanies();
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

    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_FollowingAdapter(modelList, getActivity().getApplicationContext(), new Nokri_FollowingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_FollowingModel item) {
                android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void onDeleteClick(Nokri_FollowingModel item) {
                id = item.getCompanyId();
                popupManager.nokri_showDeletePopup();
            }
        });
    recyclerView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    }

    @Override
    public void onConfirmClick(Dialog dialog) {
        nokri_unFollow();
        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        loadMoreButton.setVisibility(View.GONE);
        if (hasNextPage)
            nokri_loadMore(false);
    }
}
