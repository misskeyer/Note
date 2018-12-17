package com.example.chars.chars;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class CrimeListActivity extends AbstractRootActivity implements FragmentListCrime.Callbacks,
        FragmentCrime.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new FragmentListCrime();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetails;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_container) == null) {
            Intent intent = CrimeActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment fragment = FragmentCrime.newIntent(crime.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, fragment).
                    commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentListCrime fragment = (FragmentListCrime) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.UpdateUI();
    }
}
