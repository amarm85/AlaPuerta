package com.attebion.api.alapuerta.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.attebion.api.alapuerta.R;
import com.attebion.api.alapuerta.utilities.CommonUtils;
import com.attebion.api.alapuerta.utilities.PhotoUtils;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectionErrorFragment.Listener} interface
 * to handle interaction events.
 * Use the {@link ConnectionErrorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectionErrorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Listener mListener;
    private Button btnRetry;
    ImageView ivNetworkErrorImage;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectionErrorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectionErrorFragment newInstance(String param1, String param2) {
        ConnectionErrorFragment fragment = new ConnectionErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ConnectionErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_error, container, false);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ConnectionErrorFragment.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConnectionErrorFragment.Listener Interface");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnRetry = (Button) getActivity().findViewById(R.id.btn_retry);
        ivNetworkErrorImage = (ImageView) getActivity().findViewById(R.id.iv_network_error);

        //Set the app logo programmatically from drawable resource. This is necessary
        //to avoid main thread to do resizing on image based on view container.

        try {
            PhotoUtils.getInstance(getContext(), getString(R.string.app_name), null, null)
                    .decodeAndSetView(ivNetworkErrorImage,getActivity().getResources(),R.drawable.networkerror,200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* set a on click listener for retry button.
            call interface methods based on current connection status.
            if now internet is available then activity which is using this
            fragment will finish this fragment and continue with its normal
            work. if still internet is not there then activity will redisplay
            this fragment again.
         */
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils commonUtils = new CommonUtils(getContext());
                if(commonUtils.isConnected()){
                    mListener.yesNetworkConnected();
                }else{
                    mListener.noNetworkConnected();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface Listener {

         void yesNetworkConnected();
         void noNetworkConnected();
    }

}
