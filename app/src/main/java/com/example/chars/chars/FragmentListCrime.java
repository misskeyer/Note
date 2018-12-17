package com.example.chars.chars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class FragmentListCrime extends Fragment {
    private RecyclerView mRecyclerView;
    private CrimeAdapter mAdapter;
    private View message;
    private boolean subtitleVisible = false;
    private static final String SUBTITLE_STATE = "com.example.chars.subtitle.state";
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
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
        View view = inflater.inflate(R.layout.activity_crime_list, container, false);
        message = view.findViewById(R.id.message_view);
        Button button = view.findViewById(R.id.btn_crime_linked);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crime c = new Crime();
                CrimeLab.getInstance(getContext()).addCrime(c);
//                Intent intent = CrimeActivity.newIntent(getActivity(), c.getId());
//                startActivity(intent);
                mCallbacks.onCrimeSelected(c);
            }
        });
        mRecyclerView = view.findViewById(R.id.recycler_view_list_crime);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SUBTITLE_STATE);
        }
        UpdateUI();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_STATE, subtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_menu, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible)
            subtitleItem.setTitle("隐藏子标题");
        else {
            subtitleItem.setTitle("展开子标题");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_crime:
                Crime c = new Crime();
                CrimeLab.getInstance(getContext()).addCrime(c);
//                Intent intent = CrimeActivity.newIntent(getActivity(), c.getId());
//                startActivity(intent);
                mCallbacks.onCrimeSelected(c);
                UpdateUI();
                return true;
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateSubtitle() {
        int count = CrimeLab.getInstance(getContext()).getCrimeList().size();
        String subtilte = getResources().getQuantityString(R.plurals.subtitle_plural, count, count);
        if (!subtitleVisible)
            subtilte = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Objects.requireNonNull(activity.getSupportActionBar()).setSubtitle(subtilte);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void UpdateUI() {
        List<Crime> crimeList = CrimeLab.getInstance(getActivity()).getCrimeList();
        if (crimeList.size() != 0) {
            message.setVisibility(View.GONE);
        } else {
            message.setVisibility(View.VISIBLE);
        }
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimeList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrime(crimeList);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder {
        private TextView titleView, dateView;
        private CheckBox checkBox;
        private Crime crime;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.tv_title);
            dateView = itemView.findViewById(R.id.tv_date);
            checkBox = itemView.findViewById(R.id.check_box);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    crime.setChecked(b);
                    CrimeLab.getInstance(getActivity()).updateCrime(crime);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent i = CrimeActivity.newIntent(getActivity(), crime.getId());
//                    startActivity(i);
                    mCallbacks.onCrimeSelected(crime);
                }
            });
        }

        private void bindView(Crime crime) {
            this.crime = crime;
            titleView.setText(crime.getTitle());
            checkBox.setChecked(crime.getChecked());
            dateView.setText(crime.getDate().toString());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mList;

        public CrimeAdapter(List<Crime> list) {
            mList = list;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.crime_list_view, viewGroup, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime crime = mList.get(i);
            crimeHolder.bindView(crime);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setCrime(List<Crime> crimeList) {
            mList = crimeList;
        }
    }
}
