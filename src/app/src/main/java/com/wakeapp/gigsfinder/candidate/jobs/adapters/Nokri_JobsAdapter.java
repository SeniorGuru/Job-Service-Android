package com.wakeapp.gigsfinder.candidate.jobs.adapters;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.jobs.models.Nokri_JobsModel;
import com.wakeapp.gigsfinder.guest.models.Nokri_MenuJobModel;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_JobsAdapter extends RecyclerView.Adapter<Nokri_JobsAdapter.MyViewHolder>{
    private List<Nokri_JobsModel> jobList;

    public interface OnItemClickListener {

        void onItemClick(Nokri_JobsModel item);
        void onCompanyClick(Nokri_JobsModel item);
        void menuItemSelected(Nokri_JobsModel model, MenuItem item);

    }
    public interface OnJobClickListener{
      void onJobClick(Nokri_JobsModel model);
    }
    public interface OnImageClickListener{
        void onImageClick(Nokri_JobsModel model);
    }
    private final OnItemClickListener listener;
    private OnJobClickListener onJobClickListener;
    private OnImageClickListener onImageClickListener;
    private Nokri_FontManager fontManager;
    private Context context;

    public void setOnJobClickListener(OnJobClickListener onJobClickListener){
        this.onJobClickListener = onJobClickListener;
    }
    public void setOnImageClickListener(OnImageClickListener onImageClickListener){
        this.onImageClickListener = onImageClickListener;
    }


    public Nokri_JobsAdapter(List<Nokri_JobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jobs,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_JobsModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
//        Nokri_Utils.setRoundButtonColor(context,holder.jobTypeTextView);
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobDescriptionTextView.setText(model.getJobDescription());
        holder.timeRemainingTextView.setText(model.getTimeRemaining());
        nokri_setParagraphFont(holder);
        holder.addressTextView.setText(model.getAddress());
        holder.paymentTextView.setText(model.getPaymentPeriod());

        if(!model.isShowMenu()) {
            holder.menuTextView.setVisibility(View.GONE);
            holder.menuOverlay.setVisibility(View.GONE);
        }
        //holder.clockImageView.setImageBitmap(model.getTimeIcon());
        //holder.locationImageView.setImageBitmap(model.getLocationIcon());
        //holder.companyImageView.setImageBitmap(model.getCompanyIcon());
       // Picasso.with(context).load(model.getCompanyLogo()).resize((int) context.getResources().getDimension(R.dimen.liist_item_icon_size),(int) context.getResources().getDimension(R.dimen.liist_item_icon_size)).centerInside().into(holder.companyImageView);
        if(!TextUtils.isEmpty(model.getCompanyLogo()))
        Picasso.with(context).load(model.getCompanyLogo()).into(holder.companyImageView);

    }
    public void updateList(List<Nokri_JobsModel> list){
        this.jobList  = list;
        notifyDataSetChanged();
    }
    private void nokri_setParagraphFont(MyViewHolder holder){
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobDescriptionTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.timeRemainingTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.paymentTextView,context.getAssets());
    }
    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView jobTitleTextView,jobDescriptionTextView,timeRemainingTextView,addressTextView,paymentTextView,menuTextView;
        public ImageView clockImageView,locationImageView;
        public LinearLayout linearLayout;
        public CircularImageView companyImageView;

        public View menuOverlay;
      //  public CircularImageView companyImageView;
        public MyViewHolder(View itemView) {
            super(itemView);

            jobTitleTextView = itemView.findViewById(R.id.txt_job_title);
            linearLayout=itemView.findViewById(R.id.container);
            jobDescriptionTextView = itemView.findViewById(R.id.txt_job_description);
            timeRemainingTextView = itemView.findViewById(R.id.txt_time_remaining);
            addressTextView = itemView.findViewById(R.id.txt_address);
            menuTextView = itemView.findViewById(R.id.txt_menu);
            paymentTextView = itemView.findViewById(R.id.txt_payment_period);
            clockImageView = itemView.findViewById(R.id.img_clock);
            locationImageView = itemView.findViewById(R.id.img_location);
            companyImageView = itemView.findViewById(R.id.img_company_logo);
            menuOverlay = itemView.findViewById(R.id.menu_overlay);

//            clockImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.clock));
//            locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));

        }
        public void nokri_bind(final Nokri_JobsModel model, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });

        jobDescriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onJobClickListener!=null){
                    onJobClickListener.onJobClick(model);
                }else{
               listener.onCompanyClick(model);
            }
            }
        });
            linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onJobClickListener!=null)
                onJobClickListener.onJobClick(model);
            }
        });
        companyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onImageClickListener!=null)
                    onImageClickListener.onImageClick(model);
            }
        });
            menuOverlay.setOnClickListener(new View.OnClickListener() {
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

                    popup.inflate(R.menu.menu_job);

                    Menu menu = popup.getMenu();
                    Nokri_MenuJobModel menuJobModel = Nokri_SharedPrefManager.getJobMenuSettings(context);
                    menu.findItem(R.id.menu_view_job).setTitle(menuJobModel.getViewJob());
                    menu.findItem(R.id.menu_view_company_profile).setTitle(menuJobModel.getViewCompanyProfile());

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
