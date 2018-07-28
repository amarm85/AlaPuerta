package com.attebion.api.alapuerta.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.attebion.api.alapuerta.R;
import com.attebion.api.alapuerta.utilities.PhotoUtils;
import com.dd.processbutton.iml.ActionProcessButton;
import com.isseiaoki.simplecropview.CropImageView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateProfilePicFragment.Listener} interface
 * to handle interaction events.
 * Use the {@link UpdateProfilePicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfilePicFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "cancelButtonText";
    private String mbtnTextCancel;
    private Listener mListener;
    private static final String NEW_PROFILE_URL_KEY = "NEW_PROFILE_URL_KEY";
    private Dialog dialog = null;

    // declare the UI variables
    ImageView ivlogo;
    ImageView ivPofilePic;
    ActionProcessButton btnCancel;
    ActionProcessButton btnUpdate;
    private CropImageView mCropView;
    private LinearLayout llLayoutCropImageRoot, llLayoutProfilePicRoot;

    Uri newPicUri = null;

    private final String TAG = getClass().getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UpdateProfilePicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfilePicFragment newInstance(String param1) {
        UpdateProfilePicFragment fragment = new UpdateProfilePicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateProfilePicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "oncreate getArguments not null");
            mbtnTextCancel = getArguments().getString(ARG_PARAM1);

        }
        if (savedInstanceState != null) {
            Log.d(TAG, "oncreate saveinstancestate not null");
            if (savedInstanceState.getString(NEW_PROFILE_URL_KEY) != null) {
                newPicUri = Uri.parse(savedInstanceState.getString(NEW_PROFILE_URL_KEY));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile_pic, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        ivlogo = (ImageView) getActivity().findViewById(R.id.app_logo);
        ivPofilePic = (CircularImageView) getActivity().findViewById(R.id.iv_profile_pic);

        btnCancel = (ActionProcessButton) getActivity().findViewById(R.id.btn_profile_pic_cancel);
        btnUpdate = (ActionProcessButton) getActivity().findViewById(R.id.btn_profile_pic_update);

        llLayoutCropImageRoot = (LinearLayout) getActivity().findViewById(R.id.layout_crop_image_root);
        llLayoutProfilePicRoot = (LinearLayout) getActivity().findViewById(R.id.layout_profile_pic_root);

        mCropView = (CropImageView) getActivity().findViewById(R.id.cropImageView);
        getActivity().findViewById(R.id.buttonback).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonRotateLeft).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonRotateRight).setOnClickListener(this);
        getActivity().findViewById(R.id.buttonDone).setOnClickListener(this);

        mCropView.setCropMode(CropImageView.CropMode.CIRCLE);

        // set endless mode to action progress Button
        btnCancel.setMode(ActionProcessButton.Mode.ENDLESS);
        btnUpdate.setMode(ActionProcessButton.Mode.ENDLESS);

        //Set the app logo programmatically from drawable resource. This is necessary
        //to avoid main thread to do resizing on image based on view container.

        PhotoUtils photoUtil = PhotoUtils.getInstance(getContext(), getString(R.string.app_name)
                , getString(R.string.camera_support_error_msg)
                , getString(R.string.io_error_msg_image_folder));
        // display profile pic if already exists.
        try {
            File profilePic = new File(getContext().getFilesDir(), getString(R.string.profile_pic_name));
            if (profilePic.exists()) {
                photoUtil.decodeAndSetView(ivPofilePic, Uri.fromFile(profilePic), 250);
                newPicUri = Uri.fromFile(profilePic);

            } else {
                photoUtil.decodeAndSetView(ivPofilePic, getActivity().getResources(), R.drawable.default_profile_pic, 250);
            }

            //Set the default profile pic programmatically from drawable resource. This is necessary
            //to avoid main thread to do resizing on image based on view container.
            photoUtil.decodeAndSetView(ivlogo, getActivity().getResources(), R.drawable.mexicobandera, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // set the button text on cancel skip button. This text is set programmatically in case
        // if user is singing on then will be skip other wise in future if user in updating the pic the cancel
        // default text is skip

        if (mbtnTextCancel != null) {
            btnCancel.setText(mbtnTextCancel);
        }

        // set on click listener on profile Image view
        ivPofilePic.setOnClickListener(this);

        // set on click listener on skip or cancel button
        btnCancel.setOnClickListener(this);

        // set on click listener on update button
        btnUpdate.setOnClickListener(this);

    }


    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        try {
            mListener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile_pic:
                updateProfilePic();
                break;
            case R.id.btn_profile_pic_cancel:
                // call interface method to cancel or skip profile picture update
                mListener.cancelPorfilePicUpdate();
                break;
            case R.id.btn_profile_pic_update:
                break;
            case R.id.btn_camera:
                dialog.hide();
                takePictureFromCamera();
                break;
            case R.id.btn_gallery:
                dialog.hide();
                getPictureFromGallery();
                break;
            case R.id.buttonDone:
                break;
            case R.id.buttonRotateLeft:
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                break;
            case R.id.buttonRotateRight:
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
            case R.id.buttonback:

                break;

            default:
                break;
        }
    }


    private void updateProfilePic() {
        // display a dialog to user to choose the picture from camera or gallery
        dialog = new Dialog(getContext());
        dialog.setTitle(R.string.dialog_pic_title);
        dialog.setContentView(R.layout.image_chooser_dialog);
        dialog.show();
        Button btnCamera = (Button) dialog.findViewById(R.id.btn_camera);
        Button btnGallery = (Button) dialog.findViewById(R.id.btn_gallery);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
    }

    private void takePictureFromCamera() {
        Log.d(TAG, "In takePictureFromCamera");
        PhotoUtils photoUtil = PhotoUtils.getInstance(getContext(), getString(R.string.app_name),
                getString(R.string.camera_support_error_msg),
                getString(R.string.io_error_msg_image_folder));
        newPicUri = photoUtil.getOutputPicUri();
        //photoUtil.getPicturefromCamera(newPicUri);

        if (!photoUtil.isDeviceSupportCamera()) {
            Toast.makeText(getContext(), getString(R.string.camera_support_error_msg), Toast.LENGTH_LONG).show();
        } else {
            //picUri = getOutputPicUri();
            if (newPicUri != null) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPicUri);

                startActivityForResult(cameraIntent, PhotoUtils.CAMERA_REQUEST_CODE);
            }


        }

    }

    private void getPictureFromGallery() {
        Log.d(TAG,"In getPicturefromGallery");
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_chooser_title)), PhotoUtils.GALLERY_REQUEST_CODE);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "In on activity Result");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {

            PhotoUtils photoUtil = PhotoUtils.getInstance(getContext(), getString(R.string.app_name),
                    getString(R.string.camera_support_error_msg),
                    getString(R.string.io_error_msg_image_folder));
            try {
                //newPicUri = photoUtil.processPicture(requestCode, resultCode, ivPofilePic, data, newPicUri);
                newPicUri = photoUtil.processPicture(requestCode, resultCode, mCropView, data, newPicUri);
                photoUtil.saveImageToAppPrivateFolder(getString(R.string.profile_pic_name), newPicUri);
                llLayoutProfilePicRoot.setVisibility(View.GONE);
                llLayoutCropImageRoot.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if (newPicUri != null) {
            outState.putString("NEW_PROFILE_URL_KEY", newPicUri.getPath());
            Log.d(TAG, "onSaveInstanceState " + newPicUri.getPath().toString());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Listener {

        void cancelPorfilePicUpdate();
    }

}
