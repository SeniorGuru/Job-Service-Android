package com.wakeapp.gigsfinder.candidate.edit.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import com.wakeapp.gigsfinder.manager.Nokri_DialogManager;
import com.wakeapp.gigsfinder.manager.Nokri_RequestHeaderManager;
import com.wakeapp.gigsfinder.manager.Nokri_SharedPrefManager;
import com.wakeapp.gigsfinder.manager.Nokri_ToastManager;
import com.wakeapp.gigsfinder.network.Nokri_ServiceGenerator;
import com.wakeapp.gigsfinder.rest.RestService;
import com.wakeapp.gigsfinder.R;
import com.wakeapp.gigsfinder.candidate.edit.adapters.Nokri_FilePreviewAdapter;
import com.wakeapp.gigsfinder.candidate.edit.models.Nokri_FileModel;
import com.wakeapp.gigsfinder.custom.ProgressRequestBody;
import com.wakeapp.gigsfinder.manager.Nokri_FontManager;
import com.wakeapp.gigsfinder.manager.Nokri_GoogleAnalyticsManager;
import com.wakeapp.gigsfinder.manager.Nokri_PopupManager;
import com.wakeapp.gigsfinder.manager.Nokri_UploadProgressDialolque;
import com.wakeapp.gigsfinder.utils.Nokri_Utils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_AddResumeFragment extends Fragment implements View.OnClickListener,Nokri_FilePreviewAdapter.OnItemClickListener,Nokri_PopupManager.ConfirmInterface,ProgressRequestBody.UploadCallbacks{
    private Nokri_FontManager fontManager;
    private TextView addResumeTextView,resumeFormatAllowedTextView,dropFilesTextView;
    private Button saveSkillsButton;
    private RelativeLayout fileUpload;
    private ArrayList<String>paths =new ArrayList<>() ;
    private ArrayList<NormalFile> docPaths;
    private ArrayList<Nokri_FileModel>modelList;
    private Nokri_PopupManager popupManager;
    private Nokri_FilePreviewAdapter adapter;
    private RecyclerView recyclerView;
    private String id;
    private Nokri_UploadProgressDialolque progressDialolque;
    private Nokri_DialogManager dialogManager;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_AddResumeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_add_resume, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();
        nokri_getCandidateResumeList();
    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(addResumeTextView,getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(resumeFormatAllowedTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(dropFilesTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(saveSkillsButton,getActivity().getAssets());
    }

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();
        fileUpload = getView().findViewById(R.id.file_upload);
        addResumeTextView = getView().findViewById(R.id.txt_add_resume);
        resumeFormatAllowedTextView = getView().findViewById(R.id.txt_resume_format_allowed);

        dropFilesTextView = getView().findViewById(R.id.txt_drop_files);


        saveSkillsButton = getView().findViewById(R.id.btn_saveskills);
        Nokri_Utils.setEditBorderButton(getContext(),saveSkillsButton);
       recyclerView = getView().findViewById(R.id.recyclerview);
       popupManager = new Nokri_PopupManager(getContext(),this);
        fileUpload.setOnClickListener(this);
    }

    private void nokri_setupRecyclerview(){


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_FilePreviewAdapter(modelList,getContext(),this,0);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialogManager.hideAfterDelay(6000);
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
       // Call<ResponseBody> myCall = restService.postDeleteResume(params,Nokri_RequestHeaderManager.addHeaders());

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
     //   Call<ResponseBody> myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful())
                {

                    try {String buttonText = null;
                        modelList = new ArrayList<>();
                        JSONObject response = new JSONObject(responseObject.body().string());

                        if(response.getBoolean("success")){
                            JSONObject data = response.getJSONObject("data");
                            JSONArray extrasArray = data.getJSONArray("extras");

                            for(int i = 0;i<extrasArray.length();i++){

                                JSONObject extra = extrasArray.getJSONObject(i);
                                if(extra.getString("field_type_name").equals("section_name")){
                                    addResumeTextView.setText(extra.getString("value")+":");
                                }
                                else
                                if(extra.getString("field_type_name").equals("section_text")){
                                    resumeFormatAllowedTextView.setText(extra.getString("value"));
                                }
                                else
                                if(extra.getString("field_type_name").equals("click_text")){
                                    if(data.getJSONArray("resumes").length()==0) {
                                        dropFilesTextView.setText(extra.getString("value"));
                                        dropFilesTextView.setVisibility(View.VISIBLE);
                                    }
                                    else dropFilesTextView.setVisibility(View.GONE);
                                }
                                else
                                if(extra.getString("field_type_name").equals("btn_name")){
                                    saveSkillsButton.setText(extra.getString("value"));
                                }
                                else
                                if(extra.getString("field_type_name").equals("del_resume")){
                                    buttonText = extra.getString("value");
                                }

                            }


                            JSONArray dataArray = data.getJSONArray("resumes");
                            for(int i=0;i<dataArray.length();i++) {
                                JSONArray objectArray = dataArray.getJSONArray(i);
                                Nokri_FileModel model = new Nokri_FileModel();

                                for (int j = 0; j < objectArray.length(); j++) {
                                    JSONObject dataObject = objectArray.getJSONObject(j);

                                    if (dataObject.getString("field_type_name").equals("resume_name")) {
                                        model.setName(dataObject.getString("value"));

                                    } else if (dataObject.getString("field_type_name").equals("resume_url")) {
                                        model.setUrl(dataObject.getString("value"));
                                    }
                                    else if (dataObject.getString("field_type_name").equals("resume_id"))
                                        model.setId(dataObject.getString("value"));
                                    model.setButtonText(buttonText);
                                    if(j+1 == objectArray.length())
                                        modelList.add(model);
                                }
                            }
                            nokri_setupRecyclerview();}
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

                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage()); }
        });
    }

    @Override
    public void onClick(View view) {
//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(paths)
//                .setActivityTheme(R.style.AppTheme)
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
//
//            if(resultCode== Activity.RESULT_OK && data!=null)
//
//            {
//
//                docPaths = new ArrayList<>();
//                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//
//                nokri_uploadResumeRequest(docPaths.get(0));
//            }
//
//        }
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
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("my_cv_upload",file.getName(),requestBody);
        RestService restService =  Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        final Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else

        {
            myCall = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload",responseObject.message());
                if(responseObject.isSuccessful()){

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.v("Resume Upload",jsonObject.toString());
                        if(jsonObject.getBoolean("success")){



                            Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));


                            nokri_getCandidateResumeList();
                            progressDialolque.handleSuccessScenerion();
                        }
                        else
                        { Nokri_ToastManager.showLongToast(getContext(),jsonObject.getString("message"));
                            nokri_getCandidateResumeList();
                            progressDialolque.handleFailedScenerio();}

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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                progressDialolque.handleFailedScenerio();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(Nokri_FileModel item, int position) {
        id = item.getId();
        popupManager.nokri_showDeletePopup();

    }

    @Override
    public void onConfirmClick(Dialog dialog) {
        dialog.dismiss();
        nokri_deleteResume();

    }

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
}
