package com.kernelpanic.yorickmessenger_gitclone;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.ChatFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanDevicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanDevicesFragment extends Fragment {

    protected   AppCompatButton     testOpenChatButton;
    protected   FragmentManager     fragmentManager;
    protected   ChatFragment        chatFragment;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanDevicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanDevicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanDevicesFragment newInstance(String param1, String param2) {
        ScanDevicesFragment fragment = new ScanDevicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_scan_devices, container, false);

        testOpenChatButton = view.findViewById(R.id.searchDevicesButton);
        chatFragment = new ChatFragment();


        testOpenChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .replace(R.id.scanFragmentContainer, chatFragment, "chatFragment")
                        .commit();
            }
        });

        return view;
    }
}