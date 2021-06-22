package com.wakeapp.gigsfinder.employeer.jobs.adapters;

import android.content.Context;

import android.graphics.Color;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.wakeapp.gigsfinder.custom.Nokri_SpinnerAdapter;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_ActiveJobsModel;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.employeer.jobs.models.Nokri_ResumeReceivedModel;
import com.wakeapp.gigsfinder.guest.models.Nokri_MenuActiveJobsModel;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.utils.Nokri_Config;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_ActiveJobsAdapter extends RecyclerView.Adapter<Nokri_ActiveJobsAdapter.MyViewHolder>{
    public interface OnItemClickListener {

        void onItemClick(Nokri_ActiveJobsModel item);
        void menuItemSelected(Nokri_ActiveJobsModel model, MenuItem item);
        void onInActive(Nokri_ActiveJobsModel model);
        void onDeleteClick(Nokri_ActiveJobsModel model);

    }

    private final OnItemClickListener listener;
    private List<Nokri_ActiveJobsModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    public Nokri_ActiveJobsAdapter(List<Nokri_ActiveJobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    private List<Nokri_ResumeReceivedModel> modelList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_active_jobs,parent,false);

        return new Nokri_ActiveJobsAdapter.MyViewHolder(itemView);
    }
    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobExpireTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.expireDateTextView,context.getAssets());

        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView,context.getAssets());
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Log.d("active", jobList.get(position).getJobId());
        Nokri_ActiveJobsModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.jobTypeTextView.setText(model.getJobType());
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobExpireTextView.setText(model.getJobExpire());
        holder.expireDateTextView.setText(model.getJobExpireDate());

        holder.inactiveButton.setText(model.getInavtiveText());
        holder.locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));
        holder.addressTextView.setText(model.getAddress());
        nokri_filterReceivedResumes(holder, jobList.get(position).getJobId());

        nokri_setParagraphFont(holder);}
    private void nokri_filterReceivedResumes(MyViewHolder holder, String jobid) {
        JsonObject params = new JsonObject();
        params.addProperty("c_status","0");
        params.addProperty("job_id",jobid);
        params.addProperty("page_number",1);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this.context), Nokri_SharedPrefManager.getPassword(this.context),this.context);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(this.context)) {
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
                            modelList = new ArrayList<>();

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");
                        JSONObject data = response.getJSONObject("data");

                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if(companiesArray.length() == 0){
                            return;
                        }
                        else
                            holder.alert_message.setVisibility(View.VISIBLE);

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

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView jobTypeTextView,jobTitleTextView,jobExpireTextView,expireDateTextView,addressTextView,menuTextView;
        public ImageView locationImageView,alert_message;
        public Button inactiveButton;
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

            addressTextView = itemView.findViewById(R.id.txt_address);
            locationImageView = itemView.findViewById(R.id.img_location);


           inactiveButton = itemView.findViewById(R.id.btn_inactive);
            inactiveButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
           deleteImageButton = itemView.findViewById(R.id.img_btn_delete);

            alert_message = itemView.findViewById(R.id.alert_message);


        }
        public void nokri_bind(final Nokri_ActiveJobsModel model, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });


            inactiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onInActive(model);
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
        public void callImage()
        {
           // imgView = getView().findViewById(R.id.alert_message);
           // imgView .setVisibility(View.GONE);//calling B class's method
        }

    }


}
