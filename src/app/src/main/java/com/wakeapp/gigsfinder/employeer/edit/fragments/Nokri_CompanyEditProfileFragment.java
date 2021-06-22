package com.wakeapp.gigsfinder.employeer.edit.fragments;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wakeapp.gigsfinder.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.custom.Nokri_ViewPagerAdapter;

import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Globals;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CompanyEditProfileFragment extends Fragment  implements TabLayout.OnTabSelectedListener ,View.OnClickListener{


    private TextView nextStepTextView,nextStepTextViewData;


    private Nokri_FontManager fontManager;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private View overlay;
    private TextView totalStepsTextView;
    private ImageButton nextArrow;
    Nokri_EmployeerDashboardModel model;
    private  String tabTitles[];
    public Nokri_CompanyEditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupViewPager();
        nextStepTextViewData.setText("01");

        getView().findViewById(R.id.bottom_container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Nokri_Config.APP_COLOR));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);

        nokri_setFonts();
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getProfile()); }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();
        model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
      tabTitles = new String[3];

        nextArrow = getView().findViewById(R.id.txt_next_arrow);
        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.tabs);



        if(Nokri_Globals.IS_RTL_ENABLED){
            nextArrow.setRotationX(180);
            tabTitles[2] = model.getTabInfo();
            tabTitles[1] = model.getTabSocail();
            tabTitles[0] = model.getTabLocation();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }


        }
        else{
            tabTitles[0] = model.getTabInfo();
            tabTitles[1] = model.getTabSocail();
            tabTitles[2] = model.getTabLocation();
        }

        nextStepTextView = getView().findViewById(R.id.txt_next_step);
        nextStepTextView.setText(Nokri_Globals.NEXT_STEP);
        nextStepTextViewData = getView().findViewById(R.id.txt_next_step_data);
        totalStepsTextView = getView().findViewById(R.id.txt_total_steps);


        overlay = getView().findViewById(R.id.ovelay);

        overlay.setOnClickListener(this);


    }



    private void nokri_setupViewPager(){
        Nokri_ViewPagerAdapter pagerAdapter = new Nokri_ViewPagerAdapter(getChildFragmentManager());
        if(Nokri_Globals.IS_RTL_ENABLED){



            pagerAdapter.addFragment(new Nokri_LocationAndMapFragment(), model.getTabLocation());
            pagerAdapter.addFragment(new Nokri_CompanySocialLinksFragment(), model.getTabSocail());
            pagerAdapter.addFragment(new Nokri_CompanyInfoFragment(), model.getTabInfo());

        }

        else {
            pagerAdapter.addFragment(new Nokri_CompanyInfoFragment(), model.getTabInfo());
            pagerAdapter.addFragment(new Nokri_CompanySocialLinksFragment(), model.getTabSocail());
            pagerAdapter.addFragment(new Nokri_LocationAndMapFragment(), model.getTabLocation());
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        totalStepsTextView.setText("/0"+viewPager.getAdapter().getCount());

    }
    private void nokri_setFonts() {


        fontManager.nokri_setMonesrratSemiBioldFont(nextStepTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nextStepTextViewData, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(totalStepsTextView, getActivity().getAssets());
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            Log.d("test",i + " "+tabLayout.getTabCount());
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, tabLayout, false);
            TextView tv= relativeLayout.findViewById(R.id.tab_title);
            relativeLayout.findViewById(R.id.divider).setVisibility(View.GONE);
            if(i==0) {
                tv.setText(tabTitles[0]);
                tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            }
            else
            if(i>0) {
                tv.setText(tabTitles[i]);

            }
            fontManager.nokri_setOpenSenseFontTextView(tv,getActivity().getAssets());
            tabLayout.getTabAt(i).setCustomView(relativeLayout);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_company_edit_profile, container, false);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        totalStepsTextView.setText("/0"+viewPager.getAdapter().getCount());
        View view =  tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        int currentItem = viewPager.getCurrentItem()+1;
        String text = "0"+Integer.toString(currentItem);
        nextStepTextViewData.setText(text);
        if(viewPager.getCurrentItem()+1 == viewPager.getAdapter().getCount())
            nextArrow.setVisibility(View.INVISIBLE);
        else
            nextArrow.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view =  tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View view) {
        totalStepsTextView.setText("/0"+viewPager.getAdapter().getCount());
        int currentItem = viewPager.getCurrentItem()+1;
        String text = "0"+Integer.toString(currentItem);
        nextStepTextViewData.setText(text);
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        if(viewPager.getCurrentItem()+1 == viewPager.getAdapter().getCount())
            nextArrow.setVisibility(View.INVISIBLE);
        else
            nextArrow.setVisibility(View.VISIBLE);
    }
}
