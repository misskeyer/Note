package com.example.chars.chars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

public class FragmentImage extends Fragment {
    private ImageView mImageView;
    private File photoFile;
    private static final String IMAGE_PATH = "com.example.chars.chars.imagePath";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.crime_image,container,false);
        mImageView = view.findViewById(R.id.image_fragment);
        photoFile = (File) getArguments().getSerializable(IMAGE_PATH);
        Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(),getActivity());
        mImageView.setImageBitmap(bitmap);
        return view;
    }

    public static FragmentImage newInstance(File filePath){
        Bundle args = new Bundle();
        args.putSerializable(IMAGE_PATH,filePath);

        FragmentImage image = new FragmentImage();
        image.setArguments(args);
        return image;
    }
}
