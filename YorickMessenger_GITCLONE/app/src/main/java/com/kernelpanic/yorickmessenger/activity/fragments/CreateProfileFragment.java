package com.kernelpanic.yorickmessenger.activity.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.database.SQLiteDbHelper;
import com.kernelpanic.yorickmessenger.database.User;

public class CreateProfileFragment extends Fragment {


    public static final String PREFERENCE_NAME_USER_PROFILE = "yorickmessenger_userprofile";
    public static final String PREFERENCE_KEY_USER_PROFILE = "isProfileCreated";
    private final int IMAGE_PICK_CODE = 1;
    private EditText nameField;
    private AppCompatButton createProfileButton;
    private ReadyToScanFragment readyToScanFragment;
    private AppCompatButton browseButton;
    private byte[] imageBytes;
    private ImageView profilePic;
    private SQLiteDbHelper sqlHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameField = view.findViewById(R.id.nameField);
        browseButton = view.findViewById(R.id.browseButton);
        createProfileButton = view.findViewById(R.id.createProfileButton);
        profilePic = view.findViewById(R.id.profilePic);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                .WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        sqlHelper = new SQLiteDbHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = sqlHelper.getWritableDatabase();

        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = nameField.getText().toString();
                readyToScanFragment = new ReadyToScanFragment();

                User user = new User(fullname);
                sqlHelper.createUser(user);
                Toast.makeText(getActivity(), "Added new user", Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFERENCE_NAME_USER_PROFILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(PREFERENCE_KEY_USER_PROFILE, true);
                editor.commit();

                getActivity().finish();
/*                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .replace(R.id.fragmentContainer, readyToScanFragment, "readyToScanFragment")
                        .setReorderingAllowed(true)
                        .commit();*/
            }
        });

/*        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });*/
    }


/*    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            profilePic.setImageURI(data.getData());

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
        }
    }*/
}