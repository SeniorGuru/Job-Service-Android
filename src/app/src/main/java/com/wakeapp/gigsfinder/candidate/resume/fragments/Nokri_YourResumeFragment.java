package com.wakeapp.gigsfinder.candidate.resume.fragments;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;


import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import com.wakeapp.gigsfinder.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.wakeapp.gigsfinder.candidate.resume.adapter.Nokri_YourResumeAdapter;
import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.manager.download.manager.service.Nokri_DownloadService;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;


import com.wakeapp.gigsfinder.candidate.resume.model.Nokri_ResumeModel;
import com.wakeapp.gigsfinder.custom.ProgressRequestBody;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
import com.wakeapp.gigsfinder.manager.Nokri_UploadProgressDialolque;

import com.wakeapp.gigsfinder.utils.Nokri_Globals;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.wakeapp.gigsfinder.utils.RuntimePermissionHelper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_YourResumeFragment extends Fragment implements View.OnClickListener,Nokri_PopupManager.ConfirmInterface,ProgressRequestBody.UploadCallbacks, RuntimePermissionHelper.permissionInterface {
    private TextView yourResumeTextView,srTextView,nameTextView,viewReusmeTextView,editResumeTextView;
    private Button addMoreButton;
    private Nokri_FontManager fontManager;
    private RecyclerView recyclerView;
    private List<Nokri_ResumeModel> modelList;

    private ArrayList<String> paths =new ArrayList<>() ;
    private ArrayList<NormalFile>docPaths;
    private Nokri_PopupManager popupManager;
    private String id;
    private Nokri_YourResumeAdapter adapter;
RuntimePermissionHelper runtimePermissionHelper;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private DownloadResult serviceResult;
    private Nokri_UploadProgressDialolque progressDialolque;
    private Snackbar snackbar;
    private Nokri_DialogManager dialogManager;
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_YourResumeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();

        addMoreButton.setOnClickListener(this);

        //  setupRecyclerview();
        nokri_getCandidateResumeList();
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getResume());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_your_resume, container, false);
    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(yourResumeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(srTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(viewReusmeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(editResumeTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
      //  fontManager.nokri_setOpenSenseFontButton(addMoreButton,getActivity().getAssets());
    }

    private void setupRecyclerview() {

        //populuateListWithDummyData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_YourResumeAdapter(modelList,getContext(),new Nokri_YourResumeAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Nokri_ResumeModel item, int flag) {
                runtimePermissionHelper.requestStorageCameraPermission(1);

                if(flag == 0){
                    Intent intent = new Intent(getActivity(),Nokri_DownloadService.class);
                    intent.putExtra("url",item.getLink());
                    intent.putExtra("filename",item.getName());
                    intent.putExtra("result",serviceResult);
                    getActivity().startService(intent);

                }
                if(flag==1){

                    popupManager.nokri_showDeletePopup();
                    id = item.getId();
                }
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialogManager.hideAlertDialog();}

    @Override
    public void onProgressUpdate(int percentage) {
        progressDialolque.updateProgress(percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccessPermission(int code) {

    }

    @Override
    public void onBackPressed() {

    }

    public class DownloadResult extends ResultReceiver{
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public DownloadResult(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        switch (resultCode)
        {
            case 1000:
                if(resultData.getBoolean("state"))
                    setSnackBar(getContext(),getView().findViewById(R.id.container),"Download Complete",resultData.getString("path"));
                break;
        }
    }
}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(snackbar!=null && snackbar.isShown())
            snackbar.dismiss();
    }

    private  void setSnackBar(final Context cotext , View coordinatorLayout, String snackTitle, final String path) {
          snackbar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("Open", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  cotext.startActivity(new Intent(Environment.DIRECTORY_DOWNLOADS));
                    snackbar.dismiss();
                    Intent install = new Intent(Intent.ACTION_VIEW);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Uri apkURI = FileProvider.getUriForFile(
                                getContext(),
                                getContext().getApplicationContext()
                                        .getPackageName() + ".provider", new File(path));
                        install.setDataAndType(apkURI, Nokri_Utils.getMimeType(path));
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    else {
                        install.setDataAndType(Uri.fromFile(new File(path)),
                                Nokri_Utils.getMimeType(path));
                    }


                        try {
                            cotext.startActivity(install);
                        }
                       catch (ActivityNotFoundException e){
                        Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.APP_NOT_FOUNT);
                       }

                }
            });


        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);


    }
    private void nokri_deleteResume() {


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("resume_id",id);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addHeaders());
        }
      //  Call<ResponseBody> myCall = restService.postDeleteResume(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            nokri_getCandidateResumeList();
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


    private void nokri_initialize() {
        modelList = new ArrayList<>();
        runtimePermissionHelper=new RuntimePermissionHelper(getActivity(),this);
        fontManager = new Nokri_FontManager();
        recyclerView = getView().findViewById(R.id.recyclerview);
        yourResumeTextView = getView().findViewById(R.id.txt_your_resume);
        srTextView = getView().findViewById(R.id.txt_sr);;
        nameTextView = getView().findViewById(R.id.txt_name);
        viewReusmeTextView = getView().findViewById(R.id.txt_view);
        editResumeTextView = getView().findViewById(R.id.txt_edit);
        emptyTextView = getView().findViewById(R.id.txt_empty);

        addMoreButton = getView().findViewById(R.id.btn_add_more);

        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.with(getContext()).load(R.drawable.logo).into(messageImage);

        serviceResult = new DownloadResult(new Handler(Looper.getMainLooper()));





        popupManager = new Nokri_PopupManager(getContext(),this);

        fontManager = new Nokri_FontManager();
    }


    private void nokri_getCandidateResumeList(){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateResumeList( Nokri_RequestHeaderManager.addHeaders());
        }
       // Call<ResponseBody> myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {

                    try {
                        modelList = new ArrayList<>();
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.d("responceuplaodresumec",response.toString());
                            String buttonText = null;
                            String deleteButtonText = null;
                        if(response.getBoolean("success")){
                            JSONObject data = response.getJSONObject("data");
                            JSONArray extras = data.getJSONArray("extras");
                            for(int i =0;i<extras.length();i++){
                                JSONObject object = extras.getJSONObject(i);
                                if(object.getString("field_type_name").equals("section_label"))
                                {
                                    yourResumeTextView.setText(object.getString("value"));
                                }
                                else
                                if(object.getString("field_type_name").equals("ad_more_btn"))
                                {
                                    addMoreButton.setText(object.getString("value"));

                                }
                                else
                                if(object.getString("field_type_name").equals("sr_txt"))
                                {srTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("resume_name"))
                                {nameTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("dwnld_resume"))
                                {viewReusmeTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("del_resume"))
                                {editResumeTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("dwnld"))
                                {buttonText = object.getString("value");}
                                else
                                if(object.getString("field_type_name").equals("section_name"))
                                {TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                toolbarTitleTextView.setText(object.getString("value"));}
                                if(object.getString("field_type_name").equals("del_resume"))
                                {
                                   deleteButtonText = object.getString("value");
                                }
                                if(object.getString("field_type_name").equals("not_added"))
                                {
                                    emptyTextView.setText(object.getString("value"));
                                }
                            }


                            JSONArray dataArray = data.getJSONArray("resumes");
                            if(dataArray.length()<=0)
                            {   messageContainer.setVisibility(View.VISIBLE);
                                dialogManager.hideAlertDialog();

                            }
                            else
                                messageContainer.setVisibility(View.GONE);



                            for(int i=0;i<dataArray.length();i++) {
                                JSONArray objectArray = dataArray.getJSONArray(i);
                                Nokri_ResumeModel model = new Nokri_ResumeModel();
                                model.setSrNum(i+1+"");
                                model.setButtonText(buttonText);
                                model.setDeleteButtonText(deleteButtonText);
                                for (int j = 0; j < objectArray.length(); j++) {
                                    JSONObject dataObject = objectArray.getJSONObject(j);

                                    if (dataObject.getString("field_type_name").equals("resume_name")) {
                                        model.setName(dataObject.getString("value"));
                                    } else if (dataObject.getString("field_type_name").equals("resume_url")) {
                                        model.setLink(dataObject.getString("value"));
                                    }
                                    else if (dataObject.getString("field_type_name").equals("resume_id"))
                                        model.setId(dataObject.getString("value"));

                                    if(j+1 == objectArray.length())
                                        modelList.add(model);
                                }
                            }
                            setupRecyclerview();}
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


    @Override
    public void onClick(View view) {

//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(paths)
//                .setActivityTheme(R.style.AppTheme)
//                .enableDocSupport(true) //if you want to hide default tabs for document
//                .pickFile(this);
        if (R.id.txt_add_resume == R.id.txt_add_resume) {
            Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
            intent4.putExtra(Constant.MAX_NUMBER, 9);
            intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf","txt"});
            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(FilePickerConst.REQUEST_CODE_DOC == requestCode){
//            if(resultCode== Activity.RESULT_OK && data!=null)
//
//            {
//
//                docPaths = new ArrayList<>();
//                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//
//                nokri_uploadResumeRequest(docPaths.get(0));
//            }
//        }

        /*if(requestCode == REQUEST_CODE && data!=null){
            nokri_uploadResumeRequest(Nokri_PathUtils.getPath(this,data.getData()));*/
        if (Constant.REQUEST_CODE_PICK_FILE==requestCode) {
            if (resultCode == RESULT_OK && data!=null) {
//        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                docPaths = new ArrayList<>();
                docPaths.addAll(data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE));

                Log.d("SHittttttttttttttttt",docPaths.get(0).getPath());
                nokri_uploadResumeRequest(docPaths.get(0).getPath());
            }

        }
    }




    private void nokri_uploadResumeRequest(String absolutePath){

        progressDialolque = new Nokri_UploadProgressDialolque(getContext());
        progressDialolque.showUploadDialogue();

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file,this);
       // RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("my_cv_upload",file.getName(),requestBody);
        RestService restService =  Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        final Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else

        {
            myCall = restService.postUploadResume( fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

       // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload",responseObject.message());
                if(responseObject.isSuccessful()){

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.d("responceuplaodresumeu",jsonObject.toString());
                        if(jsonObject.getBoolean("success")){
                           // JSONArray dataArray = jsonObject.getJSONArray("data");

                            Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));
                           // Log.v("Resume Upload",dataArray.toString());



                               nokri_getCandidateResumeList();
                            progressDialolque.handleSuccessScenerion();

                        }
                        else {
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                            progressDialolque.handleFailedScenerio();
                           // nokri_getCandidateResumeList();
                        }
                    } catch (JSONException e) {


                        progressDialolque.handleFailedScenerio();
                        e.printStackTrace();
                    } catch (IOException e) {

                        progressDialolque.handleFailedScenerio();
                        e.printStackTrace();
                    }
                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               progressDialolque.handleFailedScenerio();
               t.printStackTrace();
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage()); }
        });
    }


    @Override
    public void onConfirmClick(Dialog dialog) {

        nokri_deleteResume();
        dialog.dismiss();
    }
}
