package devapp.pro.vk.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

import devapp.pro.vk.R;

/**
 * Message fragment
 */
public class MessageFragment extends Fragment {

    private Uri outputFileUri;

    public int user_id;

    public ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("...");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.message_fragment, container, false);
        ImageButton photo = (ImageButton) v.findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] addPhoto=new String[]{ getString(R.string.camera) , getString(R.string.gallery) };
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle(getResources().getString(R.string.method));

                dialog.setItems(addPhoto,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(id==0){
                            // Camera
                            final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "VkApp" + File.separator);
                            root.mkdirs();
                            final File sdImageMainDirectory = new File(root, "vk.jpg");
                            outputFileUri = Uri.fromFile(sdImageMainDirectory);

                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            startActivityForResult(takePicture, 0);
                        }
                        if(id==1){
                            // Gallery
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);
                        }

                    }
                });

                dialog.setNeutralButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }});
                dialog.show();
            }
        });

        Button button = (Button) getActivity().findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                final String message = ((EditText)v.findViewById(R.id.message_text)).getText().toString();
                // if attach file
                if (outputFileUri != null) {
                    try {
                        InputStream imageStream = getActivity().getContentResolver().openInputStream(outputFileUri);
                        Bitmap photo = BitmapFactory.decodeStream(imageStream);
                        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), 0, 60479154);
                        request.setRequestListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                try {
                                    JSONArray jsonArray = response.json.getJSONArray("response");
                                    if (jsonArray.length() > 0) {
                                        JSONObject jsObj = jsonArray.getJSONObject(0);
                                        String photo = "photo" + jsObj.getString("owner_id") + "_" + jsObj.getString("id");
                                        sendMessage(message, photo);
                                    }
                                } catch (Exception e) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.hide();
                                    }
                                    Toast t = Toast.makeText(getActivity(), getString(R.string.error_file_upload), Toast.LENGTH_LONG);
                                    t.show();
                                }
                            }

                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                if (progressDialog.isShowing()) {
                                    progressDialog.hide();
                                }
                                Toast t = Toast.makeText(getActivity(), getString(R.string.error_file_upload), Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                        request.start();
                    } catch (Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.hide();
                        }
                        Toast t = Toast.makeText(getActivity(), getString(R.string.error_file_attach), Toast.LENGTH_LONG);
                        t.show();
                    }

                } else {
                    sendMessage(message, null);
                }

            }
        });

        return v;
    }

    /**
     * Отправка сообщения
     * @param message
     * @param photo
     */
    protected void sendMessage(String message, @Nullable String photo){
        VKRequest r = new VKRequest("messages.send");
        r.addExtraParameter("message", message);
        r.addExtraParameter("user_id", user_id);

        if(photo != null && !photo.equals("")){
            r.addExtraParameter("attachment", photo);
        }

        r.setRequestListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Toast t = Toast.makeText(getActivity(), getString(R.string.success_send_message), Toast.LENGTH_LONG);
                t.show();
                outputFileUri = null;
                ImageButton imageButton = (ImageButton) getView().findViewById(R.id.photo);
                imageButton.setImageResource(R.mipmap.ic_launcher);
                ((EditText)getView().findViewById(R.id.message_text)).setText("");
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
            }

            public void onError(VKError error) {
                if (progressDialog.isShowing()) {
                    progressDialog.hide();
                }
                Toast t = Toast.makeText(getActivity(), getString(R.string.error_send_message), Toast.LENGTH_LONG);
                t.show();
            }
        });

        r.start();
    }



    public void onPause(){
        super.onPause();
        Button button = (Button) getActivity().findViewById(R.id.send);
        button.setVisibility(View.INVISIBLE);
    }

    public void onResume(){
        super.onResume();
        Button button = (Button) getActivity().findViewById(R.id.send);
        button.setVisibility(View.VISIBLE);

        ImageButton imageButton = (ImageButton) getView().findViewById(R.id.photo);
        if(outputFileUri != null){
            imageButton.setImageURI(outputFileUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        ImageButton imageview = (ImageButton) getView().findViewById(R.id.photo);
        imageview.setImageURI(outputFileUri);

        switch(requestCode) {
            case 0:
                if(resultCode == getActivity().RESULT_OK){
                    try{
                        imageview.setImageURI(outputFileUri);
                    } catch (Exception e){

                    }

                }
                break;
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    try{
                        Uri selectedImage = imageReturnedIntent.getData();
                        outputFileUri = selectedImage;
                        imageview.setImageURI(selectedImage);
                    } catch (Exception e){

                    }

                }
                break;
        }
    }
}
