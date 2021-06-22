package com.wakeapp.gigsfinder.employeer.jobs.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_InactiveJobsModel;
import com.wakeapp.gigsfinder.guest.models.Nokri_MenuActiveJobsModel;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Glixen Technologies on 05/01/2018.
 */

public class Nokri_InactiveJobsAdapter extends RecyclerView.Adapter<Nokri_InactiveJobsAdapter.MyViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(Nokri_InactiveJobsModel item);
        void menuItemSelected(Nokri_InactiveJobsModel model, MenuItem item);
        void onActive(Nokri_InactiveJobsModel model);
        void onDeleteClick(Nokri_InactiveJobsModel model);

    }
    private final OnItemClickListener listener;
    private List<Nokri_InactiveJobsModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;

    public Nokri_InactiveJobsAdapter(List<Nokri_InactiveJobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_inactive_jobs,parent,false);

        return new Nokri_InactiveJobsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_InactiveJobsModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.jobTypeTextView.setText(model.getJobType());
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobExpireTextView.setText(model.getJobExpire());
        holder.expireDateTextView.setText(model.getJobExpireDate());


        holder.addressTextView.setText(model.getAddress());
        Picasso.with(context).load(R.drawable.location_icon).into(holder.locationImageView);
        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobExpireTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.expireDateTextView,context.getAssets());

        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView,context.getAssets());

        fontManager.nokri_setOpenSenseFontButton(holder.activeButton,context.getAssets());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView jobTypeTextView,jobTitleTextView,jobExpireTextView,expireDateTextView,addressTextView,menuTextView;
        private ImageView locationImageView;
        private Button activeButton;
        public ImageButton deleteImageButton;


    public MyViewHolder(View itemView) {
        super(itemView);
        jobTypeTextView = itemView.findViewById(R.id.txt_job_type);
        Nokri_Utils.setRoundButtonColor(context,jobTypeTextView);
        jobTitleTextView = itemView.findViewById(R.id.txt_job_title);
        jobExpireTextView = itemView.findViewById(R.id.txt_job_expire);
        expireDateTextView = itemView.findViewById(R.id.txt_job_expire_date);
        expireDateTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        menuTextView = itemView.findViewById(R.id.txt_menu);
        menuTextView.setVisibility(View.GONE);
        addressTextView = itemView.findViewById(R.id.txt_address);
        locationImageView = itemView.findViewById(R.id.img_location);
        locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));
        activeButton = itemView.findViewById(R.id.btn_inactive);
        activeButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        deleteImageButton = itemView.findViewById(R.id.img_btn_delete);


    }
        public void nokri_bind(final Nokri_InactiveJobsModel model, final Nokri_InactiveJobsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });


            activeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onActive(model);
                }
            });

            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onDeleteClick(model);
                }
            });

            menuTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.PopupMenu);

                    PopupMenu popup = new PopupMenu(ctw, menuTextView);

                    try {
                        Field[] fields = popup.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if ("mPopup".equals(field.getName())) {
                                field.setAccessible(true);
                                Object menuPopupHelper = field.get(popup);
                                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                setForceIcons.invoke(menuPopupHelper, true);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popup.inflate(R.menu.active_jobs_menu_item);
                    Nokri_MenuActiveJobsModel activeJobsModel = Nokri_SharedPrefManager.getActiveJobMenuSettings(context);

                    Menu menu = popup.getMenu();
                    menu.findItem(R.id.menu_resume_received).setTitle(activeJobsModel.getResumeReceived());
                    menu.findItem(R.id.menu_delete).setTitle(activeJobsModel.getDelete());
                    menu.findItem(R.id.menu_edit).setTitle(activeJobsModel.getEdit());
                    menu.findItem(R.id.menu_view_job).setTitle(activeJobsModel.getViewJob());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            listener.menuItemSelected(model,item);
                            return false;
                        }
                    });
                    popup.show();
                }
            });

        }
}
}
