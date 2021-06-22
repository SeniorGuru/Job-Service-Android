package com.wakeapp.gigsfinder.employeer.dashboard;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.activities.Nokri_MainActivity;
import com.wakeapp.gigsfinder.candidate.jobs.fragments.Nokri_AllJobsFragment;
import com.wakeapp.gigsfinder.employeer.dashboard.fragments.Nokri_EmployeerDashboardFragment;
import com.wakeapp.gigsfinder.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.wakeapp.gigsfinder.employeer.edit.fragments.Nokri_CompanyEditProfileFragment;
import com.wakeapp.gigsfinder.employeer.email.fragments.Nokri_EditEmailTemplate;
import com.wakeapp.gigsfinder.employeer.jobs.adapters.Nokri_ActiveJobsAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_ActiveJobsFragment;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_JobsFragment;
import com.wakeapp.gigsfinder.employeer.jobs.fragments.Nokri_PostJobFragment;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_ActiveJobsModel;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_ResumeReceivedModel;
import com.wakeapp.gigsfinder.employeer.payment.fragments.Nokri_PackageDetailFragment;
import com.wakeapp.gigsfinder.employeer.payment.fragments.Nokri_PricingTableFragment;
import com.wakeapp.gigsfinder.guest.blog.fragments.Nokri_BlogGridFragment;
import com.wakeapp.gigsfinder.guest.home.fragments.Nokri_FeaturedJobsFragment;
import com.wakeapp.gigsfinder.guest.home.fragments.Nokri_Home2ScreenFragment;
import com.wakeapp.gigsfinder.guest.home.fragments.Nokri_HomeScreenFragment;
import com.wakeapp.gigsfinder.guest.home.fragments.Nokri_RecentJobsFragment;
import com.wakeapp.gigsfinder.guest.search.fragments.Nokri_CandidateSearchFragment;
import com.wakeapp.gigsfinder.guest.search.fragments.Nokri_JobSearchFragment;
import com.wakeapp.gigsfinder.guest.settings.fragments.Nokri_SettingsFragment;
import com.wakeapp.gigsfinder.manager.Nokri_AdManager;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.manager.notification.FireBaseNotificationModel;
import com.wakeapp.gigsfinder.manager.notification.Nokri_NotificationPopup;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_LanguageSupport;
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

import android.app.NotificationManager;
import android.app.PendingIntent;
/**
 * Created by Glixen Technologies on 27/01/2018.
 */

public class Nokri_EmployeerDashboardActivity extends AppCompatActivity implements Nokri_PopupManager.ConfirmInterface, BillingProcessor.IBillingHandler {
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;
    private Nokri_FontManager fontManager;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private LinearLayout toolbarTitleContainer;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private CircularImageView profileImage;
    private Nokri_EmployeerDashboardModel employeerDashboardModel;
    private LinearLayout bottomAdContainer, topAdContainer;

    boolean doubleBackToExitPressedOnce = false;
    private Nokri_PopupManager popupManager;
    private Fragment fragment;
    private Class fragmentClass;
    private Nokri_DialogManager dialogManager;

    private List<Nokri_ActiveJobsModel> modelList;
    private  boolean isVisibleCalled = false;
    private int maxNumOfPages,currentPage,nextPage=1,increment,currentNoOfJobs;
    private RecyclerView recyclerView;
    private Nokri_ActiveJobsAdapter adapter;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private  int counter = 0;
    private ArrayList<String> spinnerNmaes;
    private ArrayList<String>spinnerIds;
    private Nokri_ActiveJobsFragment.nokri_pagerCallback listener;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private String id;
    private ProgressBar progressBar;
    private boolean hasNextPage,loading = true;
    private Button loadMoreButton;
    private boolean isCallFromFilters = false;
    private boolean result_value = false;
    private List<Nokri_ResumeReceivedModel> a_modelList;
    private BillingProcessor bp;
    public boolean purchased;
    private static String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkCXQyKE6qvZhg3oFxKvZrOL9efX/1v5qAw1IP7aT8xMH8bJrJjVJRHlpXxGJon0oaRbEb+TE49vqcT2JpP4imqJbAkYlU2S7pY/ffv6Satew8p+8fKE1ttjJOlZdU/IUP/1j5Gq75tZ7o/3ds/Hjyg2ZHGDXljME0Pg2KGpt+DXju82WVPTNhvhD331v9E8QugyAKpZH0GKJlbm8xdj7Lq3hQy+dDVEgq5/VKV9+hzNWVSbxFnXR22CCft5whthqvyMJYwQehJWSG50sI9XlBwLqDz2dOuYbyhwNY3Uv5j3CpBdsl/IbBa0NJT+Xzqu7//gbh+/+W02gpa3jkipkcwIDAQAB";

    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bp = new BillingProcessor(this, LICENSE_KEY, this);
        bp.initialize();
        purchased = true;
        if (bp.loadOwnedPurchasesFromGoogle()) {
            TransactionDetails subscriptionTransactionDetails = bp.getPurchaseTransactionDetails("com.basic.gigfinder");
            if(subscriptionTransactionDetails!=null)
                purchased = true;
             else
                 purchased = false;
        }else{
            purchased = false;
        }

        Log.d("purchased", String.valueOf(purchased));
        result_value = false;
        Nokri_Config.APP_COLOR = Nokri_SharedPrefManager.getAppColor(this);
        setContentView(R.layout.activity_employeer_dashboard);

        Nokri_FeaturedJobsFragment.CALLED_FROM_DASHBOARD = true;
        Nokri_RecentJobsFragment.CALLED_FROM_DASHBOARD = true;
        Nokri_GoogleAnalyticsManager.initialize(this);

        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);

        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
        FireBaseNotificationModel fireBaseNotificationModel = Nokri_SharedPrefManager.getFirebaseNotification(this);

        Nokri_GoogleAnalyticsManager.initialize(this);

        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);


        if (fireBaseNotificationModel != null) {
            if (!fireBaseNotificationModel.getTitle().trim().isEmpty() && Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION) {

                Nokri_NotificationPopup.showNotificationDialog(this, fireBaseNotificationModel.getTitle(), fireBaseNotificationModel.getMessage(), fireBaseNotificationModel.getImage());
                Nokri_Globals.SHOULD_HOW_FIREBASE_NOTIFICATION = false;
            }
        }

        popupManager = new Nokri_PopupManager(this, this);
        employeerDashboardModel = Nokri_SharedPrefManager.getEmployeerSettings(this);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTextView = findViewById(R.id.toolbar_title);
        toolbarTitleContainer = findViewById(R.id.toolbar_title_container);
        toolbar.findViewById(R.id.collapse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) findViewById(R.id.collapse);
//                ImageView imageViewRefresh = (ImageView) findViewById(R.id.refresh);
                imageView.setVisibility(View.VISIBLE);
                ImageView imageViewRefresh = (ImageView) findViewById(R.id.refresh);
                imageViewRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();

                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
                    }
                });


//                imageViewRefresh.setOnClickListener(v -> Nokri_GuestDashboardActivity.this.recreate());
//                if (getSupportActionBar() != null) {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setDisplayShowHomeEnabled(true);
//                }
                Nokri_JobSearchFragment fragment = new Nokri_JobSearchFragment();
//        fragment.setFilterText(edtSeach.getText().toString().trim());
//        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "";
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();
//                Nokri_ToastManager.showLongToast(Nokri_CandidateDashboardActivity.this,"sddfas");
            }
        });
        bottomAdContainer = findViewById(R.id.bottom_ad_container);
        topAdContainer = findViewById(R.id.top_ad_container);

        if (Nokri_Globals.SHOW_AD) {


            if (Nokri_Globals.IS_BANNER_EBABLED) {
                if (Nokri_Globals.SHOW_AD_TOP) {

                    Nokri_AdManager.nokri_displaybanners(this, topAdContainer);
                }

                if (!Nokri_Globals.SHOW_AD_TOP) {

                    Nokri_AdManager.nokri_displaybanners(this, bottomAdContainer);
                }

            }


            if (Nokri_Globals.IS_INTERTIAL_ENABLED)
                Nokri_AdManager.loadInterstitial(this);

        }


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // 0-index header
        TextView navHeaderTextView = headerView.findViewById(R.id.txt_nav_header);
        TextView navEmailTextView = headerView.findViewById(R.id.txt_nav_email);
        if (Nokri_SharedPrefManager.getName(this) != null)
            navHeaderTextView.setText(Nokri_SharedPrefManager.getName(this));
        profileImage = headerView.findViewById(R.id.img_profile);

        if (!TextUtils.isEmpty(Nokri_SharedPrefManager.getProfileImage(this)))
            Picasso.with(this).load(Nokri_SharedPrefManager.getProfileImage(this)).fit().centerCrop().into(profileImage);


        if (Nokri_SharedPrefManager.getEmail(this) != null)
            navEmailTextView.setText(Nokri_SharedPrefManager.getEmail(this));


        fontManager = new Nokri_FontManager();
        fontManager.nokri_setMonesrratSemiBioldFont(toolbarTitleTextView, getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(navHeaderTextView, getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(navEmailTextView, getAssets());
        nokri_setNavigationFont(navigationView.getMenu());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
//        //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
//        fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
            Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
            //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
            fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();
            toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());
        } else {
            Fragment homeScreen2Fragment = new Nokri_Home2ScreenFragment();
            fragmentTransaction.add(R.id.fragment_placeholder, homeScreen2Fragment).commit();
            toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());

        }
        setUpNavigationView();


        nokri_setDrawerMenuText(navigationView.getMenu());

     /*   Nokri_NotificationPopup.showNotificationDialog(this,Nokri_SharedPrefManager.getFirebaseNotificationTitme(this),
                Nokri_SharedPrefManager.getFirebaseNotificationMessage(this),
                Nokri_SharedPrefManager.getFirebaseNotificationImage(this));*/


        Drawable mDrawable = getResources().getDrawable(R.drawable.drawer_highlight);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN));
        navigationView.setItemBackground(mDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        headerView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        profileImage.setShadowColor(Color.parseColor(Nokri_Config.APP_COLOR));
        profileImage.setBorderColor(Color.parseColor(Nokri_Config.APP_COLOR));

        nokri_initialize();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                modelList.clear();
                counter = 0;
                nextPage = 1;
                currentPage = 1;
                nokri_loadMore();
            }
        }, 100);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MainActivity", "Activity Result");
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            Log.i("MainActivity", "Activity Results");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        Log.i("MainActivity", "Destroy");
        if (bp != null) {
            Log.i("MainActivity", "bp release");
            bp.release();
        }
        super.onDestroy();
    }

    private void nokri_initialize(){

        modelList = new ArrayList<>();
        a_modelList =  new ArrayList<>();

    }
    private void nokri_loadMore() {
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getApplicationContext()), Nokri_SharedPrefManager.getPassword(getApplicationContext()),getApplicationContext());
        JsonObject params = new JsonObject();
        params.addProperty("page_number",nextPage);
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getApplicationContext())) {
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
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");
                        maxNumOfPages = pagination.getInt("max_num_pages");
                        currentPage = pagination.getInt("current_page");
                        nextPage = pagination.getInt("next_page");

                        increment = pagination.getInt("increment");
                        currentNoOfJobs = pagination.getInt("current_no_of_ads");
                        hasNextPage = pagination.getBoolean("has_next_page");
                        JSONObject data = response.getJSONObject("data");



                        JSONObject jobFilterObject = data.getJSONObject("job_filter");
                        JSONArray valueArray = jobFilterObject.getJSONArray("value");

                        for (int i = 0;i<valueArray.length();i++){
                            JSONObject valueObject =  valueArray.getJSONObject(i);
                            spinnerNmaes.add(valueObject.getString("value"));
                            spinnerIds.add(valueObject.getString("key"));
                        }

                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if(companiesArray.length() == 0){
                            return;
                        }
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
                        for(int i = 0; i < modelList.size(); i ++){
                            Nokri_ActiveJobsModel model = modelList.get(i);
                            nokri_getReceivedResumes(model.getJobId());
                            nokri_filterReceivedResumes(model.getJobId());
                        }

                        //   Log.d("Pointz",modelList.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                }
                else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getApplicationContext(),t.getMessage());
            }
        });
    }
    public void nokri_filterReceivedResumes(String id) {
        JsonObject params = new JsonObject();
        params.addProperty("c_status","0");
        params.addProperty("job_id",id);
        params.addProperty("page_number",1);


        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getApplicationContext()), Nokri_SharedPrefManager.getPassword(getApplicationContext()),getApplicationContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getApplicationContext())) {
            myCall = restService.filterReceivedResume(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.filterReceivedResume(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {



                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");
                        JSONObject data = response.getJSONObject("data");

                        JSONArray companiesArray = data.getJSONArray("jobs");
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_ResumeReceivedModel model = new Nokri_ResumeReceivedModel();
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("cand_id"))
                                    model.setId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dp"))
                                    model.setImageUrl(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_name"))
                                {  model.setName(object.getString("value"));

                                }
                                else if (object.getString("field_type_name").equals("cand_dwnlod")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(false);

                                }
                                else if (object.getString("field_type_name").equals("cand_linked")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(true);

                                }
                                else if (object.getString("field_type_name").equals("cand_stat"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dwnlod"))
                                    model.setDownlaodUrl(object.getString("value"));

                                else if (object.getString("field_type_name").equals("cand_adress"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_date"))
                                    model.setDate(object.getString("value"));
                                else if (object.getString("field_type_name").equals("resume_name"))
                                    model.setFileName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_action"))
                                    model.setActionButtonText(object.getString("key"));

                                if(j+1==dataArray.length())
                                    a_modelList.add(model);

                            }
                            Log.d("Pointz",a_modelList.toString());
                            if(a_modelList.size() != 0 && !result_value){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    CharSequence name = "Jobs";
                                    String description = "Jobs received";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel("Jobs_notify", name, importance);
                                    channel.setDescription(description);
                                    // Register the channel with the system; you can't change the importance
                                    // or other notification behaviors after this
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);
                                }
                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(getApplicationContext(), "Jobs_notify")
                                                .setSmallIcon(R.drawable.logo)
                                                .setContentTitle("Job Notification")
                                                .setContentText("You have job proposal.")
                                                .setAutoCancel(true);

                                Intent notificationIntent = new Intent(getApplicationContext(), Nokri_EmployeerDashboardActivity.class);
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(contentIntent);

                                // Add as notification
                                NotificationManager manager = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                                }
                                manager.notify(0, builder.build());
                                result_value = true;
                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
    public void nokri_getReceivedResumes(String ID){


        JsonObject params = new JsonObject();
        params.addProperty("page_number",1);
        params.addProperty("job_id",ID);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getApplicationContext()), Nokri_SharedPrefManager.getPassword(getApplicationContext()),getApplicationContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getApplicationContext())) {
            myCall = restService.getReceivedResumes(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getReceivedResumes(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {


            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        Log.d("isssueeseasdf","data from get resume received "+data.toString());

                        JSONArray companiesArray = data.getJSONArray("jobs");
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_ResumeReceivedModel model = new Nokri_ResumeReceivedModel();
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("cand_id"))
                                    model.setId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dp"))
                                    model.setImageUrl(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_name"))
                                {  model.setName(object.getString("value"));

                                }
                                else if (object.getString("field_type_name").equals("cand_stat"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dwnlod")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(false);

                                }
                                else if (object.getString("field_type_name").equals("cand_linked")) {

                                    model.setDownlaodUrl(object.getString("value"));
                                    model.setJobLinkedin(true);

                                }
                                else if (object.getString("field_type_name").equals("cand_adress"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_date"))
                                    model.setDate(object.getString("value"));
                                else if (object.getString("field_type_name").equals("resume_name"))
                                    model.setFileName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_action"))
                                    model.setActionButtonText(object.getString("key"));
                                else if (object.getString("field_type_name").equals("cand_cover"))
                                {   model.setCoverLetterAvailable(object.getBoolean("is_required"));
                                    if(object.getBoolean("is_required"))
                                    {
                                        model.setCoverLetterTitle(object.getString("key"));
                                        model.setCoverLetterText(object.getString("value"));
                                    }
                                }
                                if(j+1==dataArray.length())
                                    a_modelList.add(model);
                                Log.d("I love you",String.valueOf(a_modelList.size()));
                            }
                        }
                    } catch (JSONException e) {
                    } catch (IOException e) {
                    }

                }
                else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
    private void nokri_setDrawerMenuText(Menu menu) {
        MenuItem dashboard = menu.findItem(R.id.nav_dashboard);
        MenuItem edit = menu.findItem(R.id.nav_editprofile);
        MenuItem email = menu.findItem(R.id.nav_email);
        MenuItem jobs = menu.findItem(R.id.nav_jobs);
        MenuItem allJobs = menu.findItem(R.id.nav_all_jobs);
        MenuItem packageDetails = menu.findItem(R.id.nav_package_details);
        MenuItem buyPackage = menu.findItem(R.id.nav_buy_package);
        MenuItem postJob = menu.findItem(R.id.nav_post_job);
        MenuItem blog = menu.findItem(R.id.nav_blog);
        MenuItem logout = menu.findItem(R.id.nav_logout);
        MenuItem exit = menu.findItem(R.id.nav_exit);
        MenuItem home = menu.findItem(R.id.nav_home);
        MenuItem settings = menu.findItem(R.id.nav_settings);
        MenuItem candidateSearch = menu.findItem(R.id.nav_candidate_search);

        dashboard.setTitle(employeerDashboardModel.getDashboard());
        edit.setTitle(employeerDashboardModel.getProfile());
        email.setTitle(employeerDashboardModel.getTemplates());
        allJobs.setTitle(employeerDashboardModel.getAllJobs());
        jobs.setTitle(employeerDashboardModel.getJobs());
        //post.setTitle(employeerDashboardModel.getPostJob());
        logout.setTitle(employeerDashboardModel.getLogout());
        blog.setTitle(employeerDashboardModel.getBlog());
//        packageDetails.setTitle(employeerDashboardModel.getPackageDetail());
//        buyPackage.setTitle(employeerDashboardModel.getBuyPackage());
        postJob.setTitle(employeerDashboardModel.getPostJob());
        exit.setTitle(employeerDashboardModel.getExit());
        home.setTitle(employeerDashboardModel.getHome());
        settings.setTitle(employeerDashboardModel.getSettings());

        candidateSearch.setTitle(employeerDashboardModel.getCandidateSearch());
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
//            Fragment homeScreenFragment = new Nokri_EmployeerDashboardFragment();
//            //  Fragment homeFragmeent = new Nokri_HomeScreenFragment();
//            fragmentTransaction.add(R.id.fragment_placeholder, homeScreenFragment).commit();
//            toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());
//        } else {
//            Fragment homeScreen2Fragment = new Nokri_Home2ScreenFragment();
//            fragmentTransaction.add(R.id.fragment_placeholder, homeScreen2Fragment).commit();
//            toolbarTitleTextView.setText(employeerDashboardModel.getDashboard());
//
//        }
    }

    private void nokri_setNavigationFont(Menu m) {
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            //for aapplying a font to subMenu ...
      /*      SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
*/
            //the method we have create in activity
            fontManager.nokri_applyFontToMenuItem(mi, getAssets());
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_job_listing, menu);
//        return super.onCreateOptionsMenu(menu);
//
//
//
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        mSearchAction = menu.findItem(R.id.action_search);
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.action_search:
                handleMenuSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar
//
//        if(isSearchOpened){ //test if the search is open
//
//            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
//            //action.setDisplayShowTitleEnabled(true); //show the title in the action bar
//            toolbarTitleContainer.setVisibility(View.VISIBLE);
//            //hides the keyboard
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
//
//            //add the search icon in the action bar
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_medium));
//            isSearchOpened = false;
//        } else { //open the search entry
//
//            action.setDisplayShowCustomEnabled(true); //enable it to display a
//            // custom view in the action bar.
//            action.setCustomView(R.layout.search_bar);//add the custom view
//            //  action.setDisplayShowTitleEnabled(false); //hide the title
//            toolbarTitleContainer.setVisibility(View.GONE);
//            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edittext_search); //the text editor
//            edtSeach.setHint(Nokri_Globals.JOB_SEARCH_PLACEHOLER);
//            fontManager.nokri_setOpenSenseFontEditText(edtSeach,getAssets());
//            //this is a listener to do a search when the user clicks on search button
//            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                        doSearch();
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//
//            edtSeach.requestFocus();
//
//            //open the keyboard focused in the edtSearch
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
//
//
//            //add the close icon
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close));
//
//            isSearchOpened = true;
//        }
    }

    private void doSearch() {

        Nokri_AllJobsFragment fragment = new Nokri_AllJobsFragment();
        fragment.setFilterText(edtSeach.getText().toString().trim());
        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "";
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).addToBackStack(null).commit();

    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
//                        fragmentClass = Nokri_HomeScreenFragment.class;
                        if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("1")) {
                            fragmentClass = Nokri_HomeScreenFragment.class;

                        } else if (Nokri_SharedPrefManager.getHomeType(getApplicationContext()).equals("2")) {
                            fragmentClass = Nokri_Home2ScreenFragment.class;

                        }
                        break;
                    case R.id.nav_dashboard:
                        fragmentClass = Nokri_EmployeerDashboardFragment.class;

                        break;
                    case R.id.nav_editprofile:
                        fragmentClass = Nokri_CompanyEditProfileFragment.class;

                        break;
                    case R.id.nav_email:
                        fragmentClass = Nokri_EditEmailTemplate.class;

                        break;
                    case R.id.nav_all_jobs:
//                        fragmentClass = Nokri_JobSearchFragment.class;
                        fragmentClass = Nokri_AllJobsFragment.class;
                        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "";
                        Nokri_AllJobsFragment.purchased = purchased;
                        break;
                    case R.id.nav_jobs:
                        fragmentClass = Nokri_JobsFragment.class;

                        break;
                    case R.id.nav_package_details:
                  fragmentClass = Nokri_PackageDetailFragment.class;

                        break;
                    case R.id.nav_buy_package:
                        fragmentClass = Nokri_PricingTableFragment.class;

                        break;
                    case R.id.nav_post_job:

//                        fragmentClass = Nokri_PostJobFragment.class;
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment postJobFragment = new Nokri_PostJobFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("calledFrom","new");
                        Nokri_PostJobFragment.POST_JOB_CALLING_SOURCE = "";
                        Nokri_PostJobFragment.purchased = purchased;
                        postJobFragment.setArguments(bundle);
                        fragmentTransaction.replace(findViewById(R.id.fragment_placeholder).getId(), postJobFragment).addToBackStack(null).commit();
//                        Intent intent=new Intent(Nokri_EmployeerDashboardActivity.this, Nokri_PostJob_Activity.class);
//                        Nokri_PostJob_Activity.POST_JOB_CALLING_SOURCE = "";
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        Nokri_EmployeerDashboardActivity.this.finish();
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                             startActivity(intent);


                        break;

                    case R.id.nav_blog:
                        fragmentClass = Nokri_BlogGridFragment.class;

                        break;
                    case R.id.nav_candidate_search:
                        fragmentClass = Nokri_CandidateSearchFragment.class;
                        break;
                    case R.id.nav_logout:
                        fragmentClass = null;
                        dialogManager = new Nokri_DialogManager();
                        dialogManager.showAlertDialog(Nokri_EmployeerDashboardActivity.this);
                        Nokri_SharedPrefManager.invalidate(Nokri_EmployeerDashboardActivity.this);
                        startActivity(new Intent(Nokri_EmployeerDashboardActivity.this, Nokri_MainActivity.class));
                        dialogManager.hideAlertDialog();
                        finish();
                        break;

                    case R.id.nav_settings:
                        fragmentClass = Nokri_SettingsFragment.class;
                        break;
                    case R.id.nav_exit:
                        popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);
                        break;
                    default:
                        break;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


                drawer.closeDrawers();
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);

                if (fragmentClass != null) {
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fragment != null) {
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();
                    }

                    fragmentClass = null;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        navigationView.getMenu().getItem(0).setChecked(true);
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (doubleBackToExitPressedOnce) {
                    if (popupManager != null)
                        popupManager.nokri_showPopupWithCustomMessage(Nokri_Globals.EXIT_TEXT);
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Nokri_ToastManager.showShortToast(this, Nokri_Globals.ON_BACK_EXIT_TEXT);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else
                getSupportFragmentManager().popBackStack();
        }
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
        //  super.onBackPressed();
    }


    @Override
    public void onConfirmClick(Dialog dialog) {

        dialog.dismiss();
        finish();
    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Log.i("MainActivity", "Product purchased");
        purchased = true;
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.i("MainActivity", "Purchase History Restored");
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
        Log.i("MainActivity", "Billing initialized");
//        purchased = bp.isPurchased("com.basic.gigfinder");
    }

}
