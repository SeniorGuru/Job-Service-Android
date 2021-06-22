package com.wakeapp.gigsfinder.guest.search.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.custom.MaterialProgressBar;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_SpinnerModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.guest.search.models.Nokri_CandidateSearchModel;
import com.wakeapp.gigsfinder.guest.search.models.Nokri_ShowFilteredCandidatesFragment;
import com.wakeapp.gigsfinder.utils.Nokri_Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CandidateSearchFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private TextView countryTextView,cityTextView,stateTextView,townTextView;

    private Spinner countrySpinner,stateSpinner,citySpinner,townSpinner;
    private String country = "",state = "",city ="",town = "";
    private String type = "",available = "";
    private Nokri_SpinnerModel countrySpinnerModel,stateSpinnerModel,citySpinnerModel,townSpinnerModel;
    private TextView toolbarTitleTextView;
    private Spinner typeSpinner, availableSpinner;

    private TextView[] textViews = new TextView[2];
    private Spinner[] spinners = new Spinner[2];
    private Nokri_SpinnerModel[] spinnerModels  = new Nokri_SpinnerModel[2];

    private MaterialProgressBar progressBar;
    private Nokri_DialogManager dialogManager;
    private CardView stateContainer,cityContainer,townContainer;
    private TextView typeTextView,availableTextView;
    private TextView searchByTitleTextView,footerTextView;
    private EditText searchEditText;
    private String [] values = new String[4];
    private ImageButton searchImageButton;
    private LinearLayout searchNow;
    private String spinnerTitleText;
    public Nokri_CandidateSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_candidate_search, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        nokri_getCandidateSeachFilters();
    }

    private void nokri_initialize(){

        countryTextView = getView().findViewById(R.id.txt_country);
        cityTextView    = getView().findViewById(R.id.txt_city);
        stateTextView   = getView().findViewById(R.id.txt_state);
        townTextView    = getView().findViewById(R.id.txt_town);

        typeTextView    = getView().findViewById(R.id.txt_type);
        availableTextView   = getView().findViewById(R.id.txt_avail);

        searchByTitleTextView = getView().findViewById(R.id.txt_search_by_title);
        footerTextView = getView().findViewById(R.id.footer_text);

        countrySpinner          = getView().findViewById(R.id.spinner_country);
        stateSpinner            = getView().findViewById(R.id.spinner_state);
        citySpinner             = getView().findViewById(R.id.spinner_city);
        townSpinner             = getView().findViewById(R.id.spinner_town);
        progressBar             = getView().findViewById(R.id.progress);

        typeSpinner             = getView().findViewById(R.id.spinner_type);
        availableSpinner        = getView().findViewById(R.id.spinner_avail);

        toolbarTitleTextView     = getActivity().findViewById(R.id.toolbar_title);

        spinners[0] = typeSpinner;
        spinners[1] = availableSpinner;
        textViews[0] = typeTextView;
        textViews[1] = availableTextView;

        searchEditText = getView().findViewById(R.id.edittxt_search);


        stateContainer = getView().findViewById(R.id.state_container);
        cityContainer = getView().findViewById(R.id.city_container);
        townContainer = getView().findViewById(R.id.town_container);


        searchImageButton = getView().findViewById(R.id.img_btn_search);
        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        searchNow = getView().findViewById(R.id.search_now);
        searchNow.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        searchImageButton.setOnClickListener(this);

        searchNow.setOnClickListener(this);


    }
    private void nokri_setupFonts(){

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setOpenSenseFontTextView(countryTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(cityTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(stateTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(townTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(typeTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(availableTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(searchByTitleTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(footerTextView,getActivity().getAssets());
    }

    private void nokri_getCandidateSeachFilters(){
          dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
      //  toolbarTitleTextView.setText("Advanced Search");
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateSearchFilters(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateSearchFilters( Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONArray extras = response.getJSONArray("extra");

                        for(int i=0;i<extras.length();i++){

                            JSONObject extra = extras.getJSONObject(i);
                            if(extra.getString("field_type_name").equals("cand_search_title"))
                            {
                            toolbarTitleTextView.setText(extra.getString("key"));
                            }
                            else if(extra.getString("field_type_name").equals("cand_search_now"))
                            {
                                searchByTitleTextView.setText( extra.getString("key"));
                                footerTextView.setText( extra.getString("key"));
                            }
                            else if(extra.getString("field_type_name").equals("cand_search_name"))
                            {

                                searchEditText.setHint(extra.getString("key"));
                            }

                            else if(extra.getString("field_type_name").equals("country"))
                            {

                                countryTextView.setText(extra.getString("key"));
                            }
                            else if(extra.getString("field_type_name").equals("state"))
                            {

                                stateTextView.setText(extra.getString("key"));
                            }
                            else if(extra.getString("field_type_name").equals("city"))
                            {

                                cityTextView.setText(extra.getString("key"));
                            }
                            else if(extra.getString("field_type_name").equals("town"))
                            {

                                townTextView.setText(extra.getString("key"));
                            }

                        }

                        JSONArray searchFields = response.getJSONObject("data").getJSONArray("search_fields");
                        spinnerTitleText = searchFields.getJSONObject(0).getString("column");
                        for(int i=0;i<searchFields.length();i++){

                            JSONObject filterObject = searchFields.getJSONObject(i);
                            JSONArray filters =  filterObject.getJSONArray("value");


                            if(filterObject.getString("field_type_name").equals("cand_location"))
                            {
                                countryTextView.setText(filterObject.getString("key"));
                                countrySpinnerModel = nokri_populateSpinner(countrySpinner, filterObject.getJSONArray("value"));
                            } else if(filterObject.getString("field_type_name").equals("cand_type"))
                            {
                                spinnerModels [i] =   nokri_populateSpinner(spinners[i],filters);
                                textViews[i].setText(filterObject.getString("key"));
                            } else if(filterObject.getString("field_type_name").equals("cand_experience"))
                            {
                                spinnerModels [i] =   nokri_populateSpinner(spinners[i],filters);
                                textViews[i].setText(filterObject.getString("key"));
                            }
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





    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray){
        int index = 0;
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();
        model.getNames().add(spinnerTitleText);
        model.getIds().add(spinnerTitleText);
        model.getHasChild().add(false);
        for(int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(getContext()!=null && model!=null && spinner!=null && model.getNames()!=null){

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,model.getNames(),true));
            nokri_setSpinnerSelection(spinner, index);
        }
        spinner.setOnItemSelectedListener(this);
        return model;
    }

    private void nokri_setSpinnerSelection(Spinner spinner, int index){



        spinner.setSelection(index);
        Log.d("itemzzz","called"+ index + " "+spinner.getAdapter().getItem(index).toString());


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_country :
                if(countrySpinnerModel!= null && countrySpinnerModel.getHasChild().get(position))
                {
                    stateContainer.setVisibility(View.VISIBLE);
                    stateTextView.setVisibility(View.VISIBLE);
                    stateSpinner.setVisibility(View.VISIBLE);
                    nokri_getCountryCityState(countrySpinnerModel.getIds().get(position),"state");
                }
                else {

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
                break;
            case R.id.spinner_state :
                if(stateSpinnerModel!= null && stateSpinnerModel.getHasChild().get(position))
                {
                    cityContainer.setVisibility(View.VISIBLE);
                    cityTextView.setVisibility(View.VISIBLE);
                    citySpinner.setVisibility(View.VISIBLE);
                    nokri_getCountryCityState(stateSpinnerModel.getIds().get(position),"city");
                }
                else {

                    cityContainer.setVisibility(View.GONE);
                    citySpinner.setVisibility(View.GONE);
                    cityTextView.setVisibility(View.GONE);
                    townContainer.setVisibility(View.GONE);
                    townSpinner.setVisibility(View.GONE);
                    townTextView.setVisibility(View.GONE);

                }
                break;
            case R.id.spinner_city :
                if(citySpinnerModel!= null && citySpinnerModel.getHasChild().get(position))
                {
                    townContainer.setVisibility(View.VISIBLE);
                    townTextView.setVisibility(View.VISIBLE);
                    townSpinner.setVisibility(View.VISIBLE);
                    nokri_getCountryCityState(citySpinnerModel.getIds().get(position),"town");
                }
                else {


                    townContainer.setVisibility(View.GONE);
                    townSpinner.setVisibility(View.GONE);
                    townTextView.setVisibility(View.GONE);

                }

                break;
            case R.id.spinner_town :
                break;



        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void nokri_getCountryCityState(String id, final String tag){

     /*   dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());*/
        progressBar.setVisibility(View.VISIBLE);
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id",id);

        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
                        Log.v("response",responseObject.message());

                        switch (tag)
                        {
                            case "state":
                                stateSpinnerModel =  nokri_populateSpinner(stateSpinner,response);
                                break;
                            case "city":
                                citySpinnerModel =  nokri_populateSpinner(citySpinner,response);
                                break;
                            case "town":
                                townSpinnerModel =  nokri_populateSpinner(townSpinner,response);
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
                }
                else {
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

    private void nokri_PostSearchCandidate(boolean searchOnlyName){

        nokri_setValues();
        JsonObject params = new JsonObject();
        Nokri_CandidateSearchModel candidateSearchModel = new Nokri_CandidateSearchModel();
        if(searchOnlyName)
            candidateSearchModel.setSearchOnly(true);
        else
            candidateSearchModel.setSearchOnly(false);

        candidateSearchModel.setTitle(searchEditText.getText().toString());
        params.addProperty("cand_location", country);
       String location;
        location = country;
        if (!state.isEmpty())
            location = state;

        if (!city.isEmpty())
            location = city;

        if (!town.isEmpty())
            location = town;

        candidateSearchModel.setLocation(location);
        candidateSearchModel.setType(type);
        candidateSearchModel.setAvailable(available);

        Nokri_SharedPrefManager.saveCandidateSearchModel(candidateSearchModel,getContext());
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment showFilteredCandidatesFragment = new Nokri_ShowFilteredCandidatesFragment();
        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),showFilteredCandidatesFragment).addToBackStack(null).commit();
    }

    private void nokri_setValues(){

        for(int i=0;i<spinners.length;i++) {
            if(spinners[i].getSelectedItemPosition() ==0)
                values[i] = "";
            else {
                values[i] = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

            }
        }


        type = values[0];
        available = values[1];

        if(countrySpinner.getAdapter()!=null){
            if(countrySpinnerModel.getIds()!=null && countrySpinnerModel.getIds().size()>0 && countrySpinner.getSelectedItemPosition()!=0)
                country   = countrySpinnerModel.getIds().get(countrySpinner.getSelectedItemPosition());
        }
        else
            country = "";

        if(stateSpinner.getAdapter()!=null && stateSpinner.getVisibility() == View.VISIBLE && stateSpinner.getSelectedItemPosition()!=0){
            if(stateSpinnerModel.getIds()!=null && stateSpinnerModel.getIds().size()>0)
                state = stateSpinnerModel.getIds().get(stateSpinner.getSelectedItemPosition());
        }
        else state = "";

        if(citySpinner.getAdapter()!=null && citySpinner.getVisibility() == View.VISIBLE && citySpinner.getSelectedItemPosition()!=0){
            if(citySpinnerModel.getIds()!=null && citySpinnerModel.getIds().size()>0)
                city = citySpinnerModel.getIds().get(citySpinner.getSelectedItemPosition());
        }
        else city = "";
        if(townSpinner.getAdapter()!=null && townSpinner.getVisibility() == View.VISIBLE && townSpinner.getSelectedItemPosition()!=0){
            if(townSpinnerModel.getIds()!=null && townSpinnerModel.getIds().size()>0)
                town   = townSpinnerModel.getIds().get(townSpinner.getSelectedItemPosition());
        }
        else town="";


    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.img_btn_search:
            nokri_PostSearchCandidate(true);
            break;
        case R.id.search_now:
            nokri_PostSearchCandidate(false);
            break;
    }

    }
}
