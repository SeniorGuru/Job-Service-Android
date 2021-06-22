package com.wakeapp.gigsfinder.candidate.dashboard.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.activities.Nokri_ImagePreview;
import com.wakeapp.gigsfinder.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.wakeapp.gigsfinder.candidate.profile.adapter.Nokri_PortfolioAdapter;
import com.wakeapp.gigsfinder.candidate.profile.model.Nokri_PortfolioModel;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_DescriptionModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.edit.fragments.Nokri_CandidateEditProfileFragment;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
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
public class Nokri_CandidateDashboardFragment extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView1,portfolioRecyclerview;
    private Nokri_DescriptionRecyclerViewAdapter adapter1;

    private List<Nokri_DescriptionModel> modelList;
    private Nokri_FontManager fontManager;
    private TextView nameTextView,addressTextView;
    private TextView yourDashboardTextView,aboutMeTextView,aboutMeDataTextView,portfolioTextView,portfolioGoneTextView;
    private CircularImageView profileImage;
    private String facebook,twitter,linkedin,googlePlus;
    private ImageView facebookImageViwe,twitterImageView,linkedinImageView,googleplusImageView;
    private Nokri_DialogManager dialogManager;
    FrameLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    private TextView youtubeTextView,youtubeGoneTextView, audioTextView, audioGoneTextView;
    private YouTubePlayer YPlayer;
    private List<Nokri_PortfolioModel>potfolionModelList;

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
    public Nokri_CandidateDashboardFragment() {
        // Required empty public constructor
    }


    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();
        recyclerView1 = getView().findViewById(R.id.recyclerview);
        recyclerView1.setNestedScrollingEnabled(false);
        profileImage =  getView().findViewById(R.id.img_profile);
        linearLayout=getView().findViewById(R.id.background_circle);

        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.saa);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.MULTIPLY));

        nameTextView =  getView().findViewById(R.id.txt_name);
        addressTextView =  getView().findViewById(R.id.txt_address);
        yourDashboardTextView =  getView().findViewById(R.id.txt_your_dashboard);
        aboutMeTextView =  getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView =  getView().findViewById(R.id.txt_about_me_data);
        portfolioTextView = getView().findViewById(R.id.txt_portfolio);
        portfolioGoneTextView = getView().findViewById(R.id.txt_no_porfolio);
        audioTextView = getView().findViewById(R.id.txt_audio);
        audioGoneTextView = getView().findViewById(R.id.txt_no_audio);
        youtubeTextView = getView().findViewById(R.id.txt_youttube);
        youtubeGoneTextView = getView().findViewById(R.id.txt_no_youtube);

        facebookImageViwe = getView().findViewById(R.id.img_facebook);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);
        googleplusImageView = getView().findViewById(R.id.img_gooogle_plus);

        facebookImageViwe.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
        googleplusImageView.setOnClickListener(this);
        modelList = new ArrayList<>();

        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(model.getDashboard());

        potfolionModelList = new ArrayList<>();
        portfolioRecyclerview = getView().findViewById(R.id.recyclerview_portfolio);
        portfolioRecyclerview.setNestedScrollingEnabled(false);
    }

    private void nokri_setupFonts(){
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(aboutMeDataTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(yourDashboardTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(portfolioGoneTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(youtubeTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(youtubeGoneTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(audioTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(audioGoneTextView,getActivity().getAssets());
     }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkLoading = true;
            recreate_nokri_getCandidateDasboard();
        });
        nokri_getPortfolio();

        btn = getView().findViewById(R.id.btn_play);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        btn.setOnClickListener(pausePlay);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        nokri_getCandidateDasboard();
    }

    private void recreate_nokri_getCandidateDasboard(){
        Nokri_CandidateDashboardFragment nokri_candidateDashboardFragment=new Nokri_CandidateDashboardFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, nokri_candidateDashboardFragment);
        transaction.commit();
    }

    private void nokri_getCandidateDasboard(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
             myCall = restService.getCandidateDashboard(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
             myCall = restService.getCandidateDashboard(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        String aboutEmptyText = "";
                        if(jsonObject.getBoolean("success")){
                            Nokri_Globals.EDIT_MESSAGE  = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONObject profile = data.getJSONObject("profile");
                            JSONObject againData = profile.getJSONObject("data");
                            JSONObject socialObject = data.getJSONObject("social_icons");

                            facebook = socialObject.getString("facebook");
                            twitter = socialObject.getString("twitter");
                            linkedin = socialObject.getString("linkedin");
                            googlePlus = socialObject.getString("google_plus");

                            if(facebook.trim().isEmpty())
                                    facebookImageViwe.setVisibility(View.GONE);
                            if(twitter.trim().isEmpty())
                                twitterImageView.setVisibility(View.GONE);
                            if(linkedin.trim().isEmpty())
                                linkedinImageView.setVisibility(View.GONE);
                            if(googlePlus.trim().isEmpty())
                                googleplusImageView.setVisibility(View.GONE);

                            JSONArray extraArray = againData.getJSONArray("extra");
                            for(int i=0;i<extraArray.length();i++){
                                if(extraArray.getJSONObject(i).getString("field_type_name").equals("cand_about")){
                                        aboutEmptyText = extraArray.getJSONObject(i).getString("value");
                                }
                            }

                            JSONArray jsonArray = againData.getJSONArray("info");
                            if(!TextUtils.isEmpty(data.getString("cand_dp")))
                                Picasso.with(getContext()).load(data.getString("cand_dp")).into(profileImage);

                            Nokri_SharedPrefManager.saveProfileImage(data.getString("cand_dp"),getContext());
                            NavigationView navigationView =  getActivity().findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0); // 0-index header
                            ImageView profileImageView = headerView.findViewById(R.id.img_profile);

                            if(!TextUtils.isEmpty(data.getString("cand_dp")))
                                Picasso.with(getContext()).load(data.getString("cand_dp")).into(profileImageView);

                            Nokri_SharedPrefManager.saveCoverImage(data.getString("cand_cover"),getContext());
                            for(int i = 0; i<jsonArray.length();i++){
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                if(jsonData.getString("field_type_name").equals("cand_name"))
                                {
                                    nameTextView.setText(jsonData.getString("value"));
                                    continue;
                                }

                                if(jsonData.getString("field_type_name").equals("cand_adress")){
                                    addressTextView.setText(jsonData.getString("value"));
                                    Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                    model.setTitle(jsonData.getString("key"));
                                    model.setDescription(jsonData.getString("value"));
                                    modelList.add(model);
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("about_me"))
                                {
                                    aboutMeTextView.setText(jsonData.getString("key"));
                                    if(!jsonData.getString("value").trim().isEmpty())
                                        aboutMeDataTextView.setText(Nokri_Utils.stripHtml(jsonData.getString("value")));
                                    else
                                        aboutMeDataTextView.setText(aboutEmptyText);
                                    Nokri_SharedPrefManager.saveAbout(jsonData.getString("value"),getContext());
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("loc")){
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("set_profile")){
                                   continue;
                                }
                                if(jsonData.getString("field_type_name").equals("cand_lat")){
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("cand_long")){
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("your_dashbord")){
                                    yourDashboardTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if(jsonData.getString("key").equals("dp")||jsonData.getString("key").equals("cover"))
                                    continue;
                                Log.d("kkkkkkkk", jsonData.getString("key"));
                                Log.d("vvvvvvvv", jsonData.getString("value"));
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(jsonData.getString("key"));
                                model.setDescription(jsonData.getString("value"));
                                if(jsonData.getString("field_type_name").equals("cand_dob"))
                                    Nokri_SharedPrefManager.saveDateOfBirth(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("last_esdu"))
                                    Nokri_SharedPrefManager.saveLastEducation(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("cand_hand"))
                                    Nokri_SharedPrefManager.saveHeadline(jsonData.getString("value"),getContext());

                                modelList.add(model);
                            }
                            Log.v("array",jsonArray.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    adapter1 = new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, new Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Nokri_DescriptionModel item) {
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            RelativeLayout placeholder = getActivity().findViewById(R.id.fragment_placeholder);
                            fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CandidateEditProfileFragment()).commit();
                        }
                    });

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView1.setLayoutManager(layoutManager);
                    recyclerView1.setItemAnimator(new DefaultItemAnimator());
                    recyclerView1.setAdapter(adapter1);
                    dialogManager.hideAlertDialog();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            };
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_candidate_dashboard, container, false);
        return view;
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
            case R.id.img_linkedin:
                Nokri_Utils.opeInBrowser(getContext(),linkedin);
                break;
            case R.id.img_gooogle_plus:
                Nokri_Utils.opeInBrowser(getContext(),googlePlus);
                break;
        }
    }


    private void nokri_getPortfolio(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidatePortfolio(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidatePortfolio( Nokri_RequestHeaderManager.addHeaders());
        }
        //   Call<ResponseBody> myCall = service.getCandidatePortfolio(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        JSONArray dataArray = dataObject.getJSONArray("img");
                        JSONArray extras = dataObject.getJSONArray("extra");
                        for(int i =0;i<extras.length();i++){
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_label"))
                            {
                                portfolioTextView.setText(object.getString("value").trim());
                            }
                            if(object.getString("field_type_name").equals("not_added"))
                            {
                                portfolioGoneTextView.setText(object.getString("value").trim());
                            }

                            if(object.getString("field_type_name").equals("video_url"))
                            {
                                youtubeTextView.setText(object.getString("key").trim());
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
                                    btn.setVisibility(View.GONE);
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

                            if(object.getString("field_type_name").equals("no_audio_url"))
                            {
                                audioGoneTextView.setText(object.getString("value").trim());
                            }
                        }
                        if(dataArray.length()<=0)
                        {
                            portfolioGoneTextView.setVisibility(View.VISIBLE);
                            dialogManager.hideAlertDialog();
                            return;
                        }
                        else
                            portfolioGoneTextView.setVisibility(View.GONE);

                        for(int i=0;i<dataArray.length();i++)
                        {
                            JSONObject object = dataArray.getJSONObject(i);
                            Nokri_PortfolioModel model = new Nokri_PortfolioModel();
                            model.setUrl(object.getString("value"));
                            potfolionModelList.add(model);
                        }
                        nokri_setupPortfolioRecyclerview();
                        dialogManager.hideAlertDialog(); }
                    catch (JSONException e) {
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
                    YPlayer.setShowFullscreenButton(false);
                    YPlayer.cueVideo(url);

                    //YPlayer.play();

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {


            }
        });
    }

    private View.OnClickListener pausePlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub

            if (!playPause) {
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
}
