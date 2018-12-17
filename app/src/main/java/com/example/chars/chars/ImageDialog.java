package com.example.chars.chars;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;

public class ImageDialog extends DialogFragment {
    private ImageView mImageView;
    private File photoFile;
    public static final String IMAGE_DIALOG = "com.example.chars.image.dialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.crime_image,null);
        mImageView = view.findViewById(R.id.image_fragment);
        photoFile = (File) getArguments().getSerializable(IMAGE_DIALOG);
        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(),getActivity());
        mImageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity()).setTitle("原图：").setView(view).
                setPositiveButton("Close",null).create();
    }

    public static ImageDialog newInstance(File filePath){
        ImageDialog dialog = new ImageDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGE_DIALOG,filePath);
        dialog.setArguments(bundle);
        return dialog;
    }
}
