package com.wakeapp.gigsfinder.employeer.jobs.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.activities.Nokri_ImagePreview;
import com.wakeapp.gigsfinder.candidate.profile.model.Nokri_MyProfileModel;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_DescriptionModel;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.edit.fragments.Nokri_CandidateEditProfileFragment;
import com.wakeapp.gigsfinder.candidate.profile.adapter.Nokri_PortfolioAdapter;
import com.wakeapp.gigsfinder.candidate.profile.model.Nokri_PortfolioModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_PublicProfileFragment extends Fragment implements View.OnClickListener,Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener, View.OnFocusChangeListener {

    public static String USER_ID;
    private RecyclerView recyclerView1,portfolioRecyclerview;

    private List<Nokri_DescriptionModel> modelList;
    private List<Nokri_MyProfileModel>modelList2,modelList3,modelList4;
    private TextView aboutMeTextView,aboutMeDataTextView,nameTextView,jobTextView,portfolioTextView,contactTextView;
    private Nokri_FontManager fontManager;
    private CircularImageView profileImageView;
    private ImageView facebookImageView,twitterImageView,googlePlusImageView,linkedinImageView;
    private List<Nokri_PortfolioModel>potfolionModelList;
    private TextView portfolioGoneTextView;

    private Nokri_DialogManager dialogManager;
    String facebook ;
    String twitter ;
    String linkedIn ;
    String googlePlus;
    private TextView youtubeTextView,youtubeGoneTextView, audioTextView, audioGoneTextView;
    private LinearLayout btn_container;
    private YouTubePlayer YPlayer;
    private EditText nameEditText,emailEditText,subjectEditText,messageEditText,buttonEditText;
    private Button messageButton;
    private String receiverId,reciverName,receiverEimail;

    // Audio Part Begin
    private Button btn;
    /**
     * help to toggle between play and pause.
     */
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    /**
     * remain false till media is not completed, inside OnCompletionListener make it true.
     */
    private boolean intialStage = true;
    private String audio_url = "";
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setUpFonts();
        nokri_getProfile();

        btn = getView().findViewById(R.id.btn_play);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        btn.setOnClickListener(pausePlay);
    }
    public Nokri_PublicProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_public_profile, container, false);
    }
    private  void nokri_setupDescriptionRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView1.setLayoutManager(layoutManager);

        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setAdapter(new Nokri_DescriptionRecyclerViewAdapter(modelList,getContext(),0,this));

    }
    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();

        recyclerView1 = getView().findViewById(R.id.recyclerview1);
        portfolioRecyclerview = getView().findViewById(R.id.recyclerview_portfolio);
        audioTextView = getView().findViewById(R.id.txt_audio);
        audioGoneTextView = getView().findViewById(R.id.txt_no_audio);
        btn_container = getView().findViewById(R.id.btn_container);
        youtubeTextView = getView().findViewById(R.id.txt_youttube);
        youtubeGoneTextView = getView().findViewById(R.id.txt_no_youtube);


        recyclerView1.setNestedScrollingEnabled(false);
        portfolioRecyclerview.setNestedScrollingEnabled(false);

        aboutMeTextView = getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView = getView().findViewById(R.id.txt_about_me_data);
        nameTextView = getView().findViewById(R.id.txt_name);
        jobTextView = getView().findViewById(R.id.txt_job);
        portfolioTextView = getView().findViewById(R.id.txt_portfolio);
        portfolioGoneTextView = getView().findViewById(R.id.txt_no_porfolio);
        contactTextView = getView().findViewById(R.id.txt_contact);

        profileImageView = getView().findViewById(R.id.img_logo);

        facebookImageView = getView().findViewById(R.id.img_facebook);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        googlePlusImageView = getView().findViewById(R.id.img_gooogle_plus);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);

        messageButton = getView().findViewById(R.id.btn_message);

        Nokri_Utils.setRoundButtonColor(getContext(),messageButton);

        nameEditText = getView().findViewById(R.id.edittxt_name);
        emailEditText = getView().findViewById(R.id.edittxt_email);
        subjectEditText = getView().findViewById(R.id.edittxt_subject);
        messageEditText = getView().findViewById(R.id.edittxt_message);

        modelList = new ArrayList<>();
        modelList2 = new ArrayList<>();
        modelList3 = new ArrayList<>();
        modelList4 = new ArrayList<>();
        potfolionModelList = new ArrayList<>();

        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        subjectEditText.setOnFocusChangeListener(this);
        messageEditText.setOnFocusChangeListener(this);

        facebookImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        googlePlusImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
        messageButton.setOnClickListener(this);
    }
    private void nokri_getProfile(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("user_id",USER_ID);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidatePublicProfile(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidatePublicProfile(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        String aboutEmpty= null;
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        if(jsonObject.getBoolean("success")==false)
                        {
                            getView().findViewById(R.id.main_container).setVisibility(View.GONE);
                            getView().findViewById(R.id.private_container).setVisibility(View.VISIBLE);
                            TextView messageTextView = getView().findViewById(R.id.txt_message);
                            messageTextView.setText(jsonObject.getString("message"));
                            dialogManager.hideAlertDialog();
                            if(!jsonObject.getString("message").trim().isEmpty())
                            {
                                Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));
                            }
                                return;
                        }


                        Nokri_Globals.EDIT_MESSAGE = jsonObject.getString("message");

                        JSONObject data = jsonObject.getJSONObject("data");

                        JSONObject usereConatct = data.getJSONObject("user_contact");
                        contactTextView.setText(usereConatct.getString("receiver_name"));
                        nameEditText.setHint(usereConatct.getString("sender_name"));
                        emailEditText.setHint(usereConatct.getString("sender_email"));
                        subjectEditText.setHint(usereConatct.getString("sender_subject"));
                        messageEditText.setHint(usereConatct.getString("sender_message"));



                        receiverId = usereConatct.getString("receiver_id");
                        reciverName = usereConatct.getString("receiver_name");
                        receiverEimail = usereConatct.getString("receiver_email");

                        messageButton.setText(usereConatct.getString("btn_txt"));
                        JSONObject toolbarExtra = data.getJSONObject("extra");

                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(toolbarExtra.getString("key"));

                        JSONObject basicInfo = data.getJSONObject("basic_ifo");
                        JSONArray extrasArray = basicInfo.getJSONArray("extra");
                        for (int i =0;i<extrasArray.length();i++){
                            JSONObject extra = extrasArray.getJSONObject(i);
                            if(extra.getString("field_type_name").equals("cand_about"))
                                aboutEmpty = extra.getString("value");
                        }

                        if(!TextUtils.isEmpty(basicInfo.getString("profile_img")))
                        Picasso.with(getContext()).load(basicInfo.getString("profile_img")).fit().centerCrop().into(profileImageView);

                        JSONObject socialObject = basicInfo.getJSONObject("social");
                        if(socialObject.getBoolean("is_show")) {
                            facebook = socialObject.getString("facebook");
                            twitter = socialObject.getString("twitter");
                            linkedIn = socialObject.getString("linkedin");
                            googlePlus = socialObject.getString("google_plus");

                            if (facebook.trim().isEmpty())
                                facebookImageView.setVisibility(View.GONE);
                            if (twitter.trim().isEmpty())
                                twitterImageView.setVisibility(View.GONE);
                            if (linkedIn.trim().isEmpty())
                                linkedinImageView.setVisibility(View.GONE);
                            if (googlePlus.trim().isEmpty())
                                googlePlusImageView.setVisibility(View.GONE);
                        }
                        else
                            getView().findViewById(R.id.socail_container).setVisibility(View.GONE);
                        JSONArray infoArray = basicInfo.getJSONArray("info");

                        for(int i=0;i<infoArray.length();i++)
                        {
                            Log.v("tagzzzzzz","inside info Array");
                            JSONObject response = infoArray.getJSONObject(i);
                            Log.v("tagzzzzzz","inside after info Array");
                            if(response.getString("field_type_name").equals("cand_name"))
                                nameTextView.setText(response.getString("value"));
                            else
                            if(response.getString("field_type_name").equals("cand_hand"))
                                jobTextView.setText(response.getString("value"));
                            else
                            if(response.getString("field_type_name").equals("about_me"))
                            { aboutMeTextView.setText(response.getString("key"));
                                String text = Nokri_Utils.stripHtml(response.getString("value")).toString();
                                if(text.isEmpty())
                                    aboutMeDataTextView.setText(aboutEmpty);
                                else
                                    aboutMeDataTextView.setText(Nokri_Utils.stripHtml(response.getString("value")));}
                            else if(!response.getString("field_type_name").equals("your_dashbord")&&
                                    !response.getString("field_type_name").equals("loc")&&
                                    !response.getString("field_type_name").equals("cand_long")&&
                                    !response.getString("field_type_name").equals("cand_lat")&&
                                    !response.getString("field_type_name").equals("set_profile"))
                            {
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(response.getString("key"));
                                model.setDescription(response.getString("value"));
                                modelList.add(model);
                            }
                        }
                        nokri_setupDescriptionRecyclerView();

                        //---------------------------------Portfolio------------------------------------> Start

                        JSONObject portfolioObject = data.getJSONObject("portfolio");
                        JSONArray portfolioArray = portfolioObject.getJSONArray("img");
                        JSONArray portfolioExtra = portfolioObject.getJSONArray("extra");

                        for(int i =0;i<portfolioExtra.length();i++){
                            JSONObject object = portfolioExtra.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_label"))
                            {
                                portfolioTextView.setText(object.getString("value"));
                            }
                            if(object.getString("field_type_name").equals("not_added"))
                            {   portfolioGoneTextView.setText(object.getString("value"));

                            }
                            if(object.getString("field_type_name").equals("video_url"))
                            {   youtubeTextView.setText(object.getString("key").trim());
                                if(!object.getBoolean("is_required")){
                                    youtubeGoneTextView.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    nokri_initYoutube(object.getString("value"));
                                }
                            }
                            if(object.getString("field_type_name").equals("audio_url"))
                            {
                                audioTextView.setText(object.getString("key").trim());
                                if(!object.getBoolean("is_required")){
                                    audioGoneTextView.setVisibility(View.VISIBLE);
                                    btn_container.setVisibility(View.GONE);
                                }
                                else
                                {
                                    audio_url = Nokri_Config.UPLOAD_ADDRESS + object.getString("value");
                                }
                            }
                            if(object.getString("field_type_name").equals("no_video_url"))
                            {
                                youtubeGoneTextView.setText(object.getString("value").trim());
                            }
                        }
                        if(portfolioArray.length()<=0)
                        {
                            portfolioGoneTextView.setVisibility(View.VISIBLE);
                            dialogManager.hideAlertDialog();
                            return;
                        }
                        else
                            portfolioGoneTextView.setVisibility(View.GONE);

                        for(int i=0;i<portfolioArray.length();i++)
                        {
                            JSONObject object = portfolioArray.getJSONObject(i);
                            Nokri_PortfolioModel model = new Nokri_PortfolioModel();
                            model.setUrl(object.getString("value"));
                            potfolionModelList.add(model);
                        }
                        nokri_setupPortfolioRecyclerview();

                        //---------------------------------Portfolio------------------------------------> End

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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_setUpFonts(){
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(contactTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(portfolioGoneTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(nameEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(subjectEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(messageEditText,getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(audioTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(audioGoneTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(youtubeTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(youtubeGoneTextView,getActivity().getAssets());

    }
    private void nokri_setupPortfolioRecyclerview(){
        portfolioRecyclerview.setLayoutManager(new GridLayoutManager(getContext(),4));
        portfolioRecyclerview.setItemAnimator(new DefaultItemAnimator());


        portfolioRecyclerview.setAdapter(new Nokri_PortfolioAdapter(potfolionModelList, getContext(), new Nokri_PortfolioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_PortfolioModel item, int position) {
                Intent intent = new Intent(getActivity(), Nokri_ImagePreview.class);
                //  Nokri_ImagePreview.INDEX = position;
                intent.putStringArrayListExtra(Nokri_ImagePreview.EXTRA_NAME, nokri_getImagesList(potfolionModelList,position));


                startActivity(intent);
            }
        }));
    }
    private ArrayList<String> nokri_getImagesList(List<Nokri_PortfolioModel> models, int position){
        ArrayList<String>images = new ArrayList<>();
        for(int i =0;i<models.size();i++) {
            images.add(models.get(i).getUrl());

        }
        Collections.swap(images, 0, position);
        return images;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_facebook:
                Nokri_Utils.opeInBrowser(getContext(),facebook);
                break;
            case R.id.img_twitter:
                Nokri_Utils.opeInBrowser(getContext(),twitter);
                break;
            case R.id.img_gooogle_plus:
                Nokri_Utils.opeInBrowser(getContext(),googlePlus);
                break;
            case R.id.img_linkedin:
                Nokri_Utils.opeInBrowser(getContext(),linkedIn);
                break;
            case R.id.btn_message:
                nokri_sendMessage();
                break;
        }
    }
    private void nokri_sendMessage(){

        if(!Nokri_Utils.isValidEmail(emailEditText.getText().toString()))
        {
            emailEditText.setError("!");

            Nokri_ToastManager.showLongToast(getContext(),Nokri_Globals.INVALID_EMAIL);

            return;

        }

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());




        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("sender_name",nameEditText.getText().toString());
        jsonObject.addProperty("sender_email",emailEditText.getText().toString());
        jsonObject.addProperty("sender_subject",subjectEditText.getText().toString());
        jsonObject.addProperty("sender_message",messageEditText.getText().toString());
        jsonObject.addProperty("receiver_id",receiverId);
        jsonObject.addProperty("receiver_name",reciverName);
        jsonObject.addProperty("receiver_email",receiverEimail);


        Log.v("tagzzzzzz",jsonObject.toString());



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postContactUS(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postContactUS(jsonObject, Nokri_RequestHeaderManager.addHeaders());
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
                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                }
                else {
                    Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
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
    public void onItemClick(Nokri_DescriptionModel item) {
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CandidateEditProfileFragment()).commit();
    }

    private void nokri_initYoutube(final String url){
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(getResources().getString(R.string.google_api_credentials_for_youtube), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.setFullscreen(false);
                    YPlayer.cueVideo(url);


                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {


            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.edittxt_name:
                if(hasFocus)
                {
                    nameEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_email:

                if(hasFocus)
                {
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            case R.id.edittxt_subject:

                if(hasFocus)
                {
                    subjectEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_message:
                if(hasFocus)
                {
                    messageEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            default:
                break;
        }
    }

    private OnClickListener pausePlay = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub

            if (!playPause) {
//                btn.setBackgroundResource(R.drawable.ic_pause);
                btn.setText("Pause");
                if (intialStage)
                    new Player()
                            .execute(audio_url);
                else {
                    if (!mediaPlayer.isPlaying())
                        mediaPlayer.start();
                }
                playPause = true;
            } else {
//                btn.setBackgroundResource(R.drawable.ic_play);
                btn.setText("Play");
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                playPause = false;
            }
        }
    };

    /**
     * preparing mediaplayer will take sometime to buffer the content so prepare it inside the background thread and starting it on UI thread.
     * @author piyush
     *
     */

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {
                mediaPlayer.setDataSource(audio_url);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        playPause=false;
//                        btn.setBackgroundResource(R.drawable.ic_play);
                        btn.setText("Play");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Log.d("Prepared----", "//" + result);
            mediaPlayer.start();

            intialStage = false;
        }

        public Player() {
            progress = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Loading...");
            this.progress.show();

        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
