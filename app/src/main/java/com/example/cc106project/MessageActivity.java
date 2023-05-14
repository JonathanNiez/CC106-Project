package com.example.cc106project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.example.cc106project.Fragments.ChatUsers;
import com.example.cc106project.Fragments.RecentChat;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class MessageActivity extends AppCompatActivity {

    private String TAG = "MessageActivity";

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Log.i(TAG, "onCreate");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("Chat");
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatUsers(), "Chat");
        viewPagerAdapter.addFragment(new RecentChat(), "Recent");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragmentArrayList;
        private ArrayList<String> stringArrayList;

        ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragmentArrayList = new ArrayList<>();
            this.stringArrayList = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentArrayList.add(fragment);
            stringArrayList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringArrayList.get(position);
        }
    }
}