package com.example.chars.chars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class CrimeActivity extends AppCompatActivity implements FragmentCrime.Callbacks {
    private ViewPager mPager;
    private List<Crime> mCrimes;

    public static final String EXTRA_CRIME_ID = "com.example.chars.chars.extra.crime.id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_page_view);
        UUID uuid = (UUID) getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        mPager = findViewById(R.id.view_page_fragment);
        mCrimes = CrimeLab.getInstance(getBaseContext()).getCrimeList();
        FragmentManager fm = getSupportFragmentManager();
        mPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                Crime crime = mCrimes.get(i);
                return FragmentCrime.newIntent(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        for (int i = 0; i < mCrimes.size(); i++){
            if (mCrimes.get(i).getId().equals(uuid)) {
                mPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID uuid){
        Intent i = new Intent(packageContext,CrimeActivity.class);
        i.putExtra(EXTRA_CRIME_ID,uuid);
        return i;
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
