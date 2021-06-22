package com.wakeapp.gigsfinder.employeer.dashboard.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_DescriptionModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.wakeapp.gigsfinder.employeer.edit.fragments.Nokri_CompanyEditProfileFragment;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_EmployeerDashboardFragment extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView1;
    private Nokri_DescriptionRecyclerViewAdapter adapter1;

    private List<Nokri_DescriptionModel> modelList;
    private Nokri_FontManager fontManager;

    private TextView nameTextView,addressTextView;
    private TextView yourDashboardTextView,aboutMeTextView,aboutMeDataTextView;
    private CircularImageView profileImage;

    private String facebook,twitter,linkedin,googleplus;
    private ImageView facebookImageButton,twitterImageButton,linkedinImageButton,googlePlusImageButton;
    private Nokri_DialogManager dialogManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    public Nokri_EmployeerDashboardFragment() {
        // Required empty public constructor
    }


    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();
        recyclerView1 = getView().findViewById(R.id.recyclerview);
        profileImage =  getView().findViewById(R.id.img_profile);



        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.saa);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.MULTIPLY));


        nameTextView =  getView().findViewById(R.id.txt_name);
        addressTextView =  getView().findViewById(R.id.txt_address);
        yourDashboardTextView =  getView().findViewById(R.id.txt_your_dashboard);
        aboutMeTextView =  getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView =  getView().findViewById(R.id.txt_about_me_data);


        facebookImageButton = getView().findViewById(R.id.img_btn_facebook);
        twitterImageButton = getView().findViewById(R.id.img_btn_twitter);
        linkedinImageButton = getView().findViewById(R.id.img_btn_linkedin);
        googlePlusImageButton = getView().findViewById(R.id.img_btn_goole_plus);


        facebookImageButton.setOnClickListener(this);
        twitterImageButton.setOnClickListener(this);
        linkedinImageButton.setOnClickListener(this);
        googlePlusImageButton.setOnClickListener(this);

        modelList = new ArrayList<>();

        recyclerView1.setNestedScrollingEnabled(false);
    }

    private void setSkills(JSONArray jsonArray,String skillsEmpty){
        List<String>tagsList = new ArrayList<>();
        if(jsonArray.length()<=0)
        {
		  String []tag = {skillsEmpty};
        }
        for(int i = 0;i<jsonArray.length();i++)
        {
            try {
                JSONObject tagObject = jsonArray.getJSONObject(i);
                tagsList.add(tagObject.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String tags [] = new String[tagsList.size()];
    }

    private void nokri_setupFonts(){





        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView,getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(aboutMeDataTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(yourDashboardTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
     }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        getEmployeerDashboard();
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getDashboard());
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
            recreate_nokri_getEmployeerDashboard();

        });
    }
private void recreate_nokri_getEmployeerDashboard(){

    Nokri_EmployeerDashboardFragment nokri_employeerDashboardFragment= new Nokri_EmployeerDashboardFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.fragment_placeholder, nokri_employeerDashboardFragment);
    transaction.commit();
}
    private void getEmployeerDashboard(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());


        Call<ResponseBody> myCall;

        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerDashboard(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getEmployeerDashboard(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        String defaultAboutText = null,skillsEmpty = null;
                    //    Log.d("errrrrrrrr",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if(jsonObject.getBoolean("success")){

                            Nokri_Globals.EDIT_MESSAGE  = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");

                            JSONObject social = data.getJSONObject("social");
                            facebook = social.getString("facebook");
                            twitter = social.getString("twitter");
                            linkedin = social.getString("linkedin");
                            googleplus = social.getString("google_plus");
                            if(facebook.trim().isEmpty())
                                facebookImageButton.setVisibility(View.GONE);
                            if(twitter.trim().isEmpty())
                                twitterImageButton.setVisibility(View.GONE);
                            if(linkedin.trim().isEmpty())
                                linkedinImageButton.setVisibility(View.GONE);
                            if(googleplus.trim().isEmpty())
                                googlePlusImageButton.setVisibility(View.GONE);

                            JSONArray extrasArray = data.getJSONArray("extra");
                            for(int i=0;i<extrasArray.length();i++){
                                JSONObject extra = extrasArray.getJSONObject(i);
                                if(extra.getString("field_type_name").equals("emp_about")){
                                    defaultAboutText = extra.getString("value");
                                }
                                    else if(extra.getString("field_type_name").equals("emp_not_skills")) {
                                    skillsEmpty = extra.getString("value");
                                    }
                                else if(extra.getString("field_type_name").equals("emp_skills")) {

                                }

                                }

                            JSONArray skillsArray = data.getJSONArray("skills");
                            setSkills(skillsArray, skillsEmpty);

                            JSONArray jsonArray = data.getJSONArray("info");

                            JSONObject dpJsonObject = data.getJSONObject("profile_img");
                            JSONObject coverJsonObject = data.getJSONObject("cvr_img");



                            Log.v("backgroudn",coverJsonObject.getString("img"));
                            if(!TextUtils.isEmpty(dpJsonObject.getString("img")))
                            Picasso.with(getContext()).load(dpJsonObject.getString("img")).into(profileImage);
                            Nokri_SharedPrefManager.saveProfileImage(dpJsonObject.getString("img"),getContext());
                            NavigationView navigationView =  getActivity().findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0);
                           CircularImageView dashboardImage = headerView.findViewById(R.id.img_profile);
                            if(!TextUtils.isEmpty(Nokri_SharedPrefManager.getProfileImage(getContext())))
                           Picasso.with(getContext()).load(Nokri_SharedPrefManager.getProfileImage(getContext())).fit().centerCrop().into(dashboardImage);
                            //Picasso.with(getContext()).load(coverJsonObject.getString("img")).fit().centerCrop().into(headerImageView);
                            Nokri_SharedPrefManager.saveProfileImage(dpJsonObject.getString("img"),getContext());
                            Nokri_SharedPrefManager.saveCoverImage(coverJsonObject.getString("img"),getContext());
                            for(int i = 0; i<jsonArray.length();i++){
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                if(jsonData.getString("field_type_name").equals("emp_name"))
                                {
                                    nameTextView.setText(jsonData.getString("value"));
                                    continue;
                                }


                                if(jsonData.getString("field_type_name").equals("emp_head")){
                                    addressTextView.setText(jsonData.getString("value"));
                                    Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                    model.setTitle(jsonData.getString("key"));
                                    model.setDescription(jsonData.getString("value"));
                                    modelList.add(model);
                                    continue;
                                }
                                if(jsonData.getString("field_type_name").equals("about_me"))
                                {       aboutMeTextView.setText(jsonData.getString("key"));
                                String aboutMeText = Nokri_Utils.stripHtml(jsonData.getString("value")).toString();
                                if(aboutMeText.isEmpty() || aboutMeText == null)
                                    aboutMeDataTextView.setText(defaultAboutText);
                                else
                                aboutMeDataTextView.setText(aboutMeText);
                                    Nokri_SharedPrefManager.saveAbout(jsonData.getString("value"),getContext());
                                    continue;
                                }

                                if(jsonData.getString("field_type_name").equals("your_dashbord")){
                                    yourDashboardTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if(jsonData.getString("key").equals("dp")||jsonData.getString("key").equals("cover"))
                                    continue;
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(jsonData.getString("key"));
                                model.setDescription(jsonData.getString("value"));
                                if(jsonData.getString("field_type_name").equals("emp_est"))
                                    Nokri_SharedPrefManager.saveEstablishedSince(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("emp_rgstr"))
                                    Nokri_SharedPrefManager.saveMemberSince(jsonData.getString("value"),getContext());
                                if(jsonData.getString("field_type_name").equals("emp_head"))
                                    Nokri_SharedPrefManager.saveHeadline(jsonData.getString("value"),getContext());



                                modelList.add(model);
                                dialogManager.hideAlertDialog();
                            }
                        }

                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(getContext(),e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(getContext(),e.getMessage());
                    }
                    adapter1 = new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, new Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Nokri_DescriptionModel item) {
                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            RelativeLayout placeholder = getActivity().findViewById(R.id.fragment_placeholder);
                            fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CompanyEditProfileFragment()).commit();
                        }
                    });

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView1.setLayoutManager(layoutManager);

                    recyclerView1.setItemAnimator(new DefaultItemAnimator());
                    recyclerView1.setAdapter(adapter1);
//                    Nokri_DialogManager.hideAlertDialog();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage()); }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_employeer_dashboard, container, false);
        return view;
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){

        case R.id.img_btn_facebook:
            Nokri_Utils.opeInBrowser(getContext(),facebook);
            break;
        case R.id.img_btn_twitter:
            Nokri_Utils.opeInBrowser(getContext(),twitter);
            break;
        case R.id.img_btn_linkedin:
            Nokri_Utils.opeInBrowser(getContext(),linkedin);
            break;
        case R.id.img_btn_goole_plus:
            Nokri_Utils.opeInBrowser(getContext(),googleplus);
            break;

    }
    }


}
