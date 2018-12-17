package com.example.chars.chars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.security.cert.CRL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import datebase.CrimeDbSchema;

public class FragmentCrime extends Fragment {
    private EditText edTitle;
    private Button btnDate, btnSuspect, btnReport;
    private CheckBox isSolved;
    private Crime crime;
    private UUID uuid;
    private ImageButton btnPhoto;
    private ImageView ivPhoto;
    private File photoFile;
    private Callbacks mCallbacks;

    public static final String FRAGMENT_EXTRA_ID = "com.example.chars.chars.fragment.extra.id";
    public static final String DIALOG_DATE = "com.example.chars.chars.dialog.date";
    public static final int DATE_REQUEST = 0;
    public static final int REQUEST_CONTENT = 1;
    public static final int REQUEST_IMAGE = 2;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.crime_details, container, false);
        final Intent intentSuspect = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        PackageManager packageManager = getActivity().getPackageManager();

        uuid = (UUID) getArguments().getSerializable(FragmentCrime.FRAGMENT_EXTRA_ID);
        crime = CrimeLab.getInstance(getContext()).getCrime(uuid);
        photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);

        final Intent intentImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (photoFile != null && intentImage.resolveActivity(packageManager) != null);
        if (canTakePhoto) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                Uri uriContent = FileProvider.getUriForFile(getActivity(),"com.example.chars.chars.fileProvide",
                        photoFile);
                intentImage.putExtra(MediaStore.EXTRA_OUTPUT,uriContent);
                intentImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }else {
                Uri uri = Uri.fromFile(photoFile);
                intentImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        } else
            btnPhoto.setEnabled(false);
        btnPhoto = view.findViewById(R.id.btn_photo_button);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("IMAGE:",CrimeLab.getInstance(getActivity()).getPhotoFile(crime).getAbsolutePath());
                startActivityForResult(intentImage, REQUEST_IMAGE);
            }
        });

        ivPhoto = view.findViewById(R.id.Iv_image_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentImage imageFragment = FragmentImage.newInstance(
//                        CrimeLab.getInstance(getActivity()).getPhotoFile(crime));
//                getActivity().getSupportFragmentManager().beginTransaction().
//                        replace(R.id.crime_detials_container,imageFragment,null).
//                addToBackStack(null).commit();
                ImageDialog imageDialog = ImageDialog.newInstance(
                        CrimeLab.getInstance(getActivity()).getPhotoFile(crime));
                imageDialog.show(getFragmentManager(),null);
            }
        });

        btnReport = view.findViewById(R.id.btn_send_message);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Crime Report");
                intent = intent.createChooser(intent, "Send message via:");
                startActivity(intent);
            }
        });

        btnSuspect = view.findViewById(R.id.btn_suspect);
        btnSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intentSuspect, REQUEST_CONTENT);
            }
        });
        if (crime.getSuspect() != null) {
            String ss = "Target:" + crime.getSuspect();
            btnSuspect.setText(ss);
        }
        if (packageManager.resolveActivity(intentSuspect, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            btnSuspect.setEnabled(false);
        }


        edTitle = view.findViewById(R.id.et_crime_title);
        edTitle.setText(crime.getTitle());

        btnDate = view.findViewById(R.id.btn_date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        DateFormat formats = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.CHINA) ;
        btnDate.setText(format.format(crime.getDate()));
//        btnDate.setText(formats.format(crime.getDate()));

        isSolved = view.findViewById(R.id.cb_crime_isSolved);
        isSolved.setChecked(crime.getChecked());

        edTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                crime.setTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        isSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                crime.setChecked(b);
                updateCrime();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DateAlterDialog dialog = DateAlterDialog.newInstance(crime.getDate());
                dialog.setTargetFragment(FragmentCrime.this, DATE_REQUEST);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        updatePhotoView();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).updateCrime(crime);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.getInstance(getActivity()).deleteCrime(crime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == DATE_REQUEST) {
            Date date = (Date) data.getSerializableExtra(DateAlterDialog.DATE_RETURNED);
            crime.setDate(date);
            CrimeLab.getInstance(getActivity()).updateCrime(crime);
            btnDate.setText(crime.getDate().toString());
            updateCrime();
        } else if (requestCode == REQUEST_CONTENT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);
            try {
                if (cursor.getCount() == 0)
                    return;
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                crime.setSuspect(suspect);
                String ss = "Target:" + suspect;
                btnSuspect.setText(ss);
                updateCrime();
            } finally {
                cursor.close();
            }
        } else if (requestCode == REQUEST_IMAGE) {
            updatePhotoView();
            updateCrime();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public String getCrimeReport() {
        String suspect = crime.getSuspect();
        return getString(R.string.crime_report, suspect);
    }

    public static Fragment newIntent(UUID id) {
        Fragment fragment = new FragmentCrime();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_EXTRA_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            ivPhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            ivPhoto.setImageBitmap(bitmap);
        }
    }

    public void updateCrime(){
        CrimeLab.getInstance(getActivity()).updateCrime(crime);
        mCallbacks.onCrimeUpdated(crime);
    }
}
