package com.wakeapp.gigsfinder.employeer.payment.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.employeer.payment.adapters.Nokri_PackageDetailAdapter;
import com.wakeapp.gigsfinder.employeer.payment.models.Nokri_PackageDetailModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.utils.Nokri_Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_PackageDetailFragment extends Fragment {

private TextView titleTextView,srTextView,nameTextView,deatilTextView,packageExpiryTextView,expiryDateTextView,emptyTextView;
private ArrayList<Nokri_PackageDetailModel>modelList;
    private RecyclerView recyclerView;
private Nokri_PackageDetailAdapter adapter;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private Nokri_DialogManager dialogManager;
    public Nokri_PackageDetailFragment() {
        // Required empty public constructor
    }
    private void setupRecyclerview() {

        //populuateListWithDummyData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_PackageDetailAdapter(modelList,getContext());
        recyclerView.setAdapter(adapter);

        dialogManager.hideAlertDialog();
        }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = view.findViewById(R.id.txt_title);
        srTextView = view.findViewById(R.id.txt_sr);
        nameTextView = view.findViewById(R.id.txt_name);
        deatilTextView = view.findViewById(R.id.txt_detail);
        recyclerView = view.findViewById(R.id.recyclerview);
        packageExpiryTextView = view.findViewById(R.id.txt_package_expiry);
        expiryDateTextView = view.findViewById(R.id.txt_package_expiry_date);
        emptyTextView = getView().findViewById(R.id.txt_empty);
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImage);

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setMonesrratSemiBioldFont(titleTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(srTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(deatilTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(packageExpiryTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(expiryDateTextView,getActivity().getAssets());


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        nokri_getPackageDetailList();
    }

    private void nokri_getPackageDetailList(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getPackageDetail(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getPackageDetail( Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {

                    try {
                        modelList = new ArrayList<>();
                        JSONObject response = new JSONObject(responseObject.body().string());

                        if(response.getBoolean("success")){
                            JSONObject data = response.getJSONObject("data");
                            emptyTextView.setText(response.getString("message"));
                            TextView toolbarTitle = getActivity().findViewById(R.id.toolbar_title);
                            toolbarTitle.setText(data.getString("page_title"));
                            titleTextView.setText(data.getString("page_title"));
                            expiryDateTextView.setText(data.getString("expiry"));
                            JSONObject info = data.getJSONObject("info");
                            srTextView.setText(info.getString("number"));
                            nameTextView.setText(info.getString("name"));
                            deatilTextView.setText(info.getString("details"));
                            packageExpiryTextView.setText(info.getString("expiry"));


                            JSONArray dataArray = data.getJSONArray("packages");
                            if(dataArray.length()<=0)
                            {   messageContainer.setVisibility(View.VISIBLE);
                                dialogManager.hideAlertDialog();
                                packageExpiryTextView.setVisibility(View.GONE);
                                expiryDateTextView.setVisibility(View.GONE);
                                return;
                            }
                            else
                                messageContainer.setVisibility(View.GONE);



                            for(int i=0;i<dataArray.length();i++) {
                                JSONObject jsonObject = dataArray.getJSONObject(i);

                                Nokri_PackageDetailModel model = new Nokri_PackageDetailModel();
                                model.setSrNum(i+1+"");
                                model.setTitle(jsonObject.getString("name"));
                                model.setPackageDetail(jsonObject.getString("no_of_jobs"));

                                modelList.add(model);

                            }
                            if(data.getJSONObject("resumes").getBoolean("is_req")){
                                    JSONObject resumeObject = data.getJSONObject("resumes");
                                    Nokri_PackageDetailModel resumeModel = new Nokri_PackageDetailModel();
                                    resumeModel.setSrNum(dataArray.length()+1+"");
                                    resumeModel.setTitle(resumeObject.getString("title"));
                                    resumeModel.setPackageDetail(resumeObject.getString("nos"));
                                    modelList.add(resumeModel);
                            }
                            setupRecyclerview();}
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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_package_detail, container, false);
    }




}
