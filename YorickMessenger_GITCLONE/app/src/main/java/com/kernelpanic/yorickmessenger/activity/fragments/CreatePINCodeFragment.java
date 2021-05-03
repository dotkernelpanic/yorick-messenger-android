package com.kernelpanic.yorickmessenger.activity.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.kernelpanic.yorickmessenger.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePINCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePINCodeFragment extends Fragment {

    protected PinView pinView;
    protected AppCompatButton saveBtn;
    public static final String PREFERENCE_NAME_USER_PINCODE = "yorickmessenger_userpincode";
    public static final String PREFERENCE_KEY_USER_PINCODE_VALUE = "userPinCode";
    public static final String PREFERENCE_KEY_USER_PINCODE_BOOLEAN_CREATED = "isPinCodeCreated";
    public static final String PREFERENCE_KEY_USER_PINCODE_BOOLEAN_WANT = "isPinCodeCreated";

    protected FragmentActivity context;

    public CreatePINCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreatePINCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePINCodeFragment newInstance(String param1, String param2) {
        CreatePINCodeFragment fragment = new CreatePINCodeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_pin_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pinView = view.findViewById(R.id.pinView);
        saveBtn = view.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinCode = pinView.getText().toString();

                if (pinCode.length() != 4)
                    Toast.makeText(getActivity(), "Not enough symbols",
                            Toast.LENGTH_SHORT).show();
                else {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                            PREFERENCE_NAME_USER_PINCODE,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(PREFERENCE_KEY_USER_PINCODE_BOOLEAN_CREATED, true);
                    editor.putString(PREFERENCE_KEY_USER_PINCODE_VALUE, pinCode);
                    editor.commit();
                    Toast.makeText(getActivity(), "PIN Code created", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = context.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                            .add(R.id.fragmentContainer, ReadyToScanFragment.class, null)
                            .commit();
                }
            }
        });

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        this.context = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}