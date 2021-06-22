package com.wakeapp.gigsfinder.employeer.edit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_SpinnerModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class Nokri_LocationAndMapFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private TextView setLocationTextView;
    private Button saveLocatiosButton;
    private Nokri_FontManager fontManager;

    private Spinner locationSpinner;
    private Nokri_SpinnerModel locationSpinnerModel;
    private Nokri_DialogManager dialogManager;
    private int selectedId = 0;
    private ArrayList<String>locationSpinnerIds;

    @Override
    public void onPause() {
        super.onPause();
        dialogManager.hideAlertDialog();
    }


    public Nokri_LocationAndMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();
        nokri_getLocationAndAddress();

    }
    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(setLocationTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(saveLocatiosButton,getActivity().getAssets());
    }
    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();

        setLocationTextView = getView().findViewById(R.id.txt_set_location);
        saveLocatiosButton = getView().findViewById(R.id.btn_savelocations);
        locationSpinner = getView().findViewById(R.id.spinner_country);
        saveLocatiosButton.setOnClickListener(this);
        locationSpinnerIds = new ArrayList<>();
        Nokri_Utils.setEditBorderButton(getContext(),saveLocatiosButton);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nokri_location_and_map, container, false);
        return view;
    }

    @Override
    public void onClick(View view) {
        String location = locationSpinnerIds.get(locationSpinner.getSelectedItemPosition());
        if (locationSpinner.getAdapter() != null) {
            if (locationSpinnerIds.size() > 0) {
                location = locationSpinnerIds.get(locationSpinner.getSelectedItemPosition());
                nokri_postLocationAndAddress(location);
                return;
            }
        }
        Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
    }

    private void nokri_postLocationAndAddress(String location){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("emp_loc", location);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postEmployeerLocation(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postEmployeerLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response",responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            nokri_getLocationAndAddress();
                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        } else {
                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));

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
                    dialogManager.showCustom(responseObject.code()+"");
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

    private void nokri_getLocationAndAddress(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerLocation(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getEmployeerLocation( Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if(response.getBoolean("success")){
                            JSONArray dataArray = response.getJSONArray("data");
                            for(int i=0;i<dataArray.length();i++) {
                                JSONObject data = dataArray.getJSONObject(i);
                                if(data.getString("field_type_name").equals("cand_level")){
                                    int selectedIndex = 0;
                                    setLocationTextView.setText(data.getString("key"));
                                    JSONArray array = data.getJSONArray("value");
                                    ArrayList<String>countryList = new ArrayList<>();

                                    for(int j =0;j<array.length();j++){
                                        JSONObject object = array.getJSONObject(j);
                                        if(object.getBoolean("selected")){
                                            selectedIndex = j;
                                        }
                                        countryList.add(object.getString("value"));
                                        locationSpinnerIds.add(object.getString("key"));
                                    }
                                    locationSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,countryList));
                                    locationSpinner.setSelection(selectedIndex);
                                } else if (data.getString("field_type_name").equals(("save"))) {
                                    saveLocatiosButton.setText(data.getString("value"));
                                }
                            }
                            dialogManager.hideAlertDialog();
                        }
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
