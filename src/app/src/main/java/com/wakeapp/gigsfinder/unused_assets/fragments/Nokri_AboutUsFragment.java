package com.wakeapp.gigsfinder.unused_assets.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wakeapp.gigsfinder.guest.faq.adapters.Nokri_ExpandableAdapter;
import com.wakeapp.gigsfinder.guest.faq.models.Child;
import com.wakeapp.gigsfinder.guest.faq.models.Parent;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_AboutUsFragment extends Fragment {
    private TextView aboutUsTextView,aboutUsDataTextView;
    private Nokri_FontManager fontManager;
    private RecyclerView recyclerView;
    private Nokri_ExpandableAdapter adapter;
    private List<Parent> parentList;
    private static Nokri_AboutUsFragment aboutUsFragment;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public static Nokri_AboutUsFragment getInstance(){
        if(aboutUsFragment == null)
            return aboutUsFragment = new Nokri_AboutUsFragment();
        else
            return aboutUsFragment;
    }
    public Nokri_AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_about_us, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();

        parentList = getParentDummyData();
        adapter = new Nokri_ExpandableAdapter(parentList,getActivity().getBaseContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnGroupExpandCollapseListener(new GroupExpandCollapseListener() {
            @Override
            public void onGroupExpanded(ExpandableGroup group) {

            }

            @Override
            public void onGroupCollapsed(ExpandableGroup group) {

            }
        });
    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(aboutUsTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(aboutUsDataTextView,getActivity().getAssets());
    }

    private void nokri_initialize() {

        recyclerView = getView().findViewById(R.id.recyclerview);
    fontManager = new Nokri_FontManager();
    aboutUsTextView = getView().findViewById(R.id.txt_about_us);
    aboutUsDataTextView = getView().findViewById(R.id.txt_about_us_data);
    }
    public List<Parent> getParentDummyData() {
        parentList = new ArrayList<>();
        for(int i = 0;i<3;i++){
            List<Child>childList = new ArrayList<>();
            childList.add(new Child("This is some sample text."));
            parentList.add(new Parent("What We Do",childList));
        }
        return parentList;
    }
}
