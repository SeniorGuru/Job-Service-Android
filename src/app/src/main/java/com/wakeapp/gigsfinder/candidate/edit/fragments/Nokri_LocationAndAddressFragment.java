package com.wakeapp.gigsfinder.candidate.edit.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_SpinnerModel;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.custom.MaterialProgressBar;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_LocationAndAddressFragment extends Fragment implements OnMapReadyCallback, OnFocusChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener,GoogleMap.OnMyLocationChangeListener,AdapterView.OnItemClickListener,TextWatcher {

    private TextView locationAndAddressTextView, setLocationTextView, latitudeTextView, longitudeTextView;
    private MapView map;
    private EditText latitudeEditText, longitudeEditText;
    private Button saveLocatiosButton;
    private Nokri_FontManager fontManager;
    private static double LATITUDE, LONGITUDE;
    private GoogleMap googleMap;

    private Nokri_DialogManager dialogManager;
    private TextView selectTextView, countryTextView, cityTextView, stateTextView, townTextView;

    private Spinner countrySpinner, stateSpinner, citySpinner, townSpinner;
    private String country = "", state = "", city = "", town = "";
    private Nokri_SpinnerModel countrySpinnerModel, stateSpinnerModel, citySpinnerModel, townSpinnerModel;

    private MaterialProgressBar progressBar;


    private AutoCompleteTextView autoCompleteTextView;
    private PlacesClient placesClient;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();

    }

    public Nokri_LocationAndAddressFragment() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();
        nokri_getLocationAndAddress();

    }


    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(locationAndAddressTextView, getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(setLocationTextView, getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(latitudeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(longitudeTextView, getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(countryTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(cityTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(stateTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(townTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(latitudeEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(longitudeEditText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontButton(saveLocatiosButton, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(autoCompleteTextView, getActivity().getAssets());


    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();

        locationAndAddressTextView = getView().findViewById(R.id.txt_location_and_address);
        setLocationTextView = getView().findViewById(R.id.txt_set_location);

        latitudeTextView = getView().findViewById(R.id.txt_latitude);
        longitudeTextView = getView().findViewById(R.id.txt_longitude);

        latitudeEditText = getView().findViewById(R.id.edittxt_latitude);
        longitudeEditText = getView().findViewById(R.id.edittxt_longitude);


        autoCompleteTextView = getView().findViewById(R.id.autoCompleteTextView);

        saveLocatiosButton = getView().findViewById(R.id.btn_savelocations);

        Nokri_Utils.setEditBorderButton(getContext(), saveLocatiosButton);

        selectTextView = getView().findViewById(R.id.txt_select_country);
        countryTextView = getView().findViewById(R.id.txt_country);
        cityTextView = getView().findViewById(R.id.txt_city);
        stateTextView = getView().findViewById(R.id.txt_state);
        townTextView = getView().findViewById(R.id.txt_town);

        countrySpinner = getView().findViewById(R.id.spinner_country);
        stateSpinner = getView().findViewById(R.id.spinner_state);
        citySpinner = getView().findViewById(R.id.spinner_city);
        townSpinner = getView().findViewById(R.id.spinner_town);
        progressBar = getView().findViewById(R.id.progress);



        latitudeEditText.setOnFocusChangeListener(this);
        longitudeEditText.setOnFocusChangeListener(this);
        placesClient = Places.createClient(getContext());
        autoCompleteTextView.setOnFocusChangeListener(this);
        autoCompleteTextView.addTextChangedListener(this);
        autoCompleteTextView.setOnItemClickListener(this);
        saveLocatiosButton.setOnClickListener(this);




    }

    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.autoCompleteTextView:
                if (selected) {
                    autoCompleteTextView.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    longitudeTextView.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_latitude:
                if (selected) {
                    autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));
                    latitudeEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    longitudeTextView.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_longitude:
                if (selected) {

                    autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));
                    latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    longitudeTextView.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));

                }
                break;


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_location_and_address, container, false);
        map = view.findViewById(R.id.map_fragment);
        map.onCreate(savedInstanceState);

        map.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.getMapAsync(this);
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMyLocationChangeListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Nokri_ToastManager.showLongToast(getContext(),"This Feature Requires Permission");
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                latitudeEditText.setText(LATITUDE+"");
                longitudeEditText.setText(LONGITUDE+"");
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                     autoCompleteTextView.setText(address);
                    setMapLocation();
                } catch (IOException e) {
                    Nokri_ToastManager.showLongToast(getContext(),"Something Went Wrong");
                }
                catch (Exception ex){
                    Nokri_ToastManager.showLongToast(getContext(),"Something Went Wrong");
                }



                return false;
            }
        });

    }
    private void setMapLocation(){
        if(googleMap!=null)
        {   googleMap.clear();

            LatLng location = new LatLng(LATITUDE, LONGITUDE);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(location));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }


    @Override
    public void onClick(View view) {
        Nokri_Utils.checkEditTextForError(autoCompleteTextView);
        Nokri_Utils.checkEditTextForError(latitudeEditText);
        Nokri_Utils.checkEditTextForError(longitudeEditText);
        if (!autoCompleteTextView.getText().toString().trim().isEmpty()&&!latitudeEditText.getText().toString().trim().isEmpty() && !longitudeEditText.getText().toString().trim().isEmpty())
    nokri_postLocationAndAddress();

    else Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
    }



    private void nokri_postLocationAndAddress(){

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        nokri_setValues();

        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(country);
        if(!city.isEmpty())
            jsonArray.add(city);

            if(!state.isEmpty())
                jsonArray.add(state);

            if(!town.isEmpty())
                jsonArray.add(town);

        jsonObject.add("cand_custom_loc",jsonArray);
        jsonObject.addProperty("cand_lat",latitudeEditText.getText().toString());
        jsonObject.addProperty("cand_long",longitudeEditText.getText().toString());
        jsonObject.addProperty("cand_loc",autoCompleteTextView.getText().toString());






        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
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
                        //    nokri_getLocationAndAddress();
                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
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
            myCall = restService.getCandidateLocation(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateLocation( Nokri_RequestHeaderManager.addHeaders());
        }
        //Call<ResponseBody> myCall = restService.getCandidateLocation(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {
                    try {//Log.d("tessssst",responseObject.body().string());
                        JSONObject response = new JSONObject(responseObject.body().string());

                        if(response.getBoolean("success")){
                            JSONArray dataArray = response.getJSONArray("data");
                            JSONObject extras = response.getJSONObject("extras");
                            saveLocatiosButton.setText(extras.getString("btn_txt"));
                            locationAndAddressTextView.setText(extras.getString("page_title")+":");

                            countryTextView.setText(extras.getString("country_txt"));
                            cityTextView.setText(extras.getString("city_txt"));
                            townTextView.setText(extras.getString("town_txt"));
                            stateTextView.setText(extras.getString("state_txt"));

                            for(int i=0;i<dataArray.length();i++) {

                                JSONObject data = dataArray.getJSONObject(i);


                                if (data.getString("field_type_name").equals("cand_lat")) {
                                    latitudeTextView.setText(data.getString("key"));
                                    latitudeEditText.setText(data.getString("value"));
                                    latitudeEditText.setHint(data.getString("key"));
                                    try {
                                        LATITUDE = Double.parseDouble(data.getString("value"));

                                    }
                                    catch (NumberFormatException e){
                                        LATITUDE = 0;
                                    }
                                    } else if (data.getString("field_type_name").equals("cand_long")) {
                                     longitudeTextView.setText(data.getString("key"));
                                    longitudeEditText.setText(data.getString("value"));
                                    longitudeEditText.setHint(data.getString("key"));

                                    try {
                                        LONGITUDE = Double.parseDouble(data.getString("value"));
                                    }
                                    catch (NumberFormatException e){
                                        LONGITUDE = 0;
                                    }
                                } else if (data.getString("field_type_name").equals("cand_loc")) {
                                    setLocationTextView.setText(data.getString("key"));
                                    autoCompleteTextView.setText(data.getString("value"));
                                    autoCompleteTextView.setHint(data.getString("key"));

                                }



                        else if (data.getString("field_type_name").equals("cand_custom_loc")) {
                                    selectTextView.setText(data.getString("key"));
                                    countrySpinnerModel = nokri_populateSpinner(countrySpinner, data.getJSONArray("value"));


                                }

                            }

                            dialogManager.hideAlertDialog();
                            setMapLocation();}

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



    private void nokri_getCountryCityState(String id, final String tag){

     /*   dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());*/
        progressBar.setVisibility(View.VISIBLE);
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id",id);

        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

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
    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray){
        int index = 0;
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();

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
        if(getContext()!= null && model!=null && spinner!=null && model.getNames()!=null){

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,model.getNames()));
            nokri_setSpinnerSelection(spinner, index);
        }
        spinner.setOnItemSelectedListener(this);
        return model;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_country :
                if(countrySpinnerModel!= null && countrySpinnerModel.getHasChild().get(position))
                {

                    stateTextView.setVisibility(View.VISIBLE);
                    stateSpinner.setVisibility(View.VISIBLE);
                    nokri_getCountryCityState(countrySpinnerModel.getIds().get(position),"state");
                }
                else {

                    stateTextView.setVisibility(View.GONE);
                    stateSpinner.setVisibility(View.GONE);
                    citySpinner.setVisibility(View.GONE);
                    cityTextView.setVisibility(View.GONE);
                    townSpinner.setVisibility(View.GONE);
                    townTextView.setVisibility(View.GONE);

                }
                break;
            case R.id.spinner_state :
                if(stateSpinnerModel!= null && stateSpinnerModel.getHasChild().get(position))
                {

                    cityTextView.setVisibility(View.VISIBLE);
                    citySpinner.setVisibility(View.VISIBLE);
                    nokri_getCountryCityState(stateSpinnerModel.getIds().get(position),"city");
                }
                else {


                    citySpinner.setVisibility(View.GONE);
                    cityTextView.setVisibility(View.GONE);
                    townSpinner.setVisibility(View.GONE);
                    townTextView.setVisibility(View.GONE);

                }
                break;
            case R.id.spinner_city :
                if(citySpinnerModel!= null && citySpinnerModel.getHasChild().get(position))
                {

                    townTextView.setVisibility(View.VISIBLE);
                    townSpinner.setVisibility(View.VISIBLE);
                    nokri_getCountryCityState(citySpinnerModel.getIds().get(position),"town");
                }
                else {



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

    private void nokri_setValues(){


        if(countrySpinner.getAdapter()!=null){
            if(countrySpinnerModel.getIds()!=null && countrySpinnerModel.getIds().size()>0)
                country   = countrySpinnerModel.getIds().get(countrySpinner.getSelectedItemPosition());
        }

        if(stateSpinner.getAdapter()!=null && stateSpinner.getVisibility() == View.VISIBLE){
            if(stateSpinnerModel.getIds()!=null && stateSpinnerModel.getIds().size()>0)
                state = stateSpinnerModel.getIds().get(stateSpinner.getSelectedItemPosition());
        }
        else state = "";

        if(citySpinner.getAdapter()!=null && citySpinner.getVisibility() == View.VISIBLE){
            if(citySpinnerModel.getIds()!=null && citySpinnerModel.getIds().size()>0)
                city = citySpinnerModel.getIds().get(citySpinner.getSelectedItemPosition());
        }
        else city = "";
        if(townSpinner.getAdapter()!=null && townSpinner.getVisibility() == View.VISIBLE){
            if(townSpinnerModel.getIds()!=null && townSpinnerModel.getIds().size()>0)
                town   = townSpinnerModel.getIds().get(townSpinner.getSelectedItemPosition());
        }
        else town="";


    }
    private void nokri_setSpinnerSelection(Spinner spinner, int index){



        spinner.setSelection(index);



    }



    @Override
    public void onMyLocationChange(Location location) {
        if(location!=null){
          LATITUDE = location.getLatitude();
          LONGITUDE = location.getLongitude();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String placeId = ids.get(position);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);


        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i("Places", "Place found: " + place.getLatLng().latitude + " "+place.getLatLng().longitude);
            LATITUDE = place.getLatLng().latitude;
            LONGITUDE = place.getLatLng().longitude;
            latitudeEditText.setText(place.getLatLng().latitude+"");
            longitudeEditText.setText(place.getLatLng().longitude+"");
            setMapLocation();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();

                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        nokri_manageAutoComplete(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void nokri_manageAutoComplete(String query){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());
            }
            String[] data = places.toArray(new String[places.size()]);  // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_dropdown_item_1line, data);
            autoCompleteTextView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }



}
