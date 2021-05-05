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
import com.kernelpanic.yorickmessenger.util.Prefs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckPINCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckPINCodeFragment extends Fragment {

    protected PinView pinView;
    protected AppCompatButton proceedBtn;
    protected FragmentActivity context;

    public CheckPINCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckPINCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckPINCodeFragment newInstance(String param1, String param2) {
        CheckPINCodeFragment fragment = new CheckPINCodeFragment();
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
        return inflater.inflate(R.layout.fragment_check_pin_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pinView = view.findViewById(R.id.pinView);
        proceedBtn = view.findViewById(R.id.proceedBtn);

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receivedPinCode = pinView.getText().toString();
                if (receivedPinCode.length() != 4)
                    Toast.makeText(getActivity(), "Not enough symbols", Toast.LENGTH_SHORT).show();
                else
                    if (checkPinCode(receivedPinCode)) {
                        FragmentManager fragmentManager = context.getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainer, ReadyToScanFragment.class, null)
                                .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                                .commit();
                    }
                    else
                        Toast.makeText(getActivity(), "Invalid PIN Code. Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPinCode(String receivedPinCode) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                Prefs.PREFERENCE_NAME_CREATEPINCODE,
                Context.MODE_PRIVATE);
        String userPinCode = preferences.getString(Prefs.PREFERENCE_KEY_PIN_VALUE, "");
        return userPinCode.equals(receivedPinCode);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        this.context = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}