package com.productiveengine.myl.UIL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.productiveengine.myl.Common.RequestCodes;
import com.productiveengine.myl.UIL.databinding.FragmentSettingsBinding;
import com.productiveengine.myl.ViewModels.SettingsVM;

import java.io.File;

import ar.com.daidalos.afiledialog.FileChooserActivity;

public class MainActivity extends AppCompatActivity {

    //final TextView lblRootFolderPath = (TextView) findViewById(R.id.lblRootFolderPath);
    //final TextView lblTargetFolderPath = (TextView) findViewById(R.id.lblTargetFolderPath);

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public void onTargetFolderClicked(View v){
        Intent intent = new Intent(this, FileChooserActivity.class);
        intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
        this.startActivityForResult(intent, RequestCodes.CHOOSE_TARGET_FOLDER);
    }
    //----------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the request went well (OK) and the request was PICK_CONTACT_REQUEST
        if (resultCode == Activity.RESULT_OK ) {
            Bundle bundle = data.getExtras();

            if(bundle != null)
            {
                File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                //--------------------------------------------------------------------
                //Folders must be diffrent!!!
                if(requestCode == RequestCodes.CHOOSE_ROOT_FOLDER) {
                    TextView lblRootFolderPath = (TextView) findViewById(R.id.lblRootFolderPath);
                    lblRootFolderPath.setText(file.getPath());
                }
                else if(requestCode == RequestCodes.CHOOSE_TARGET_FOLDER) {
                    TextView lblTargetFolderPath = (TextView) findViewById(R.id.lblTargetFolderPath);
                    lblTargetFolderPath.setText(file.getPath());
                }
            }
        }
    }
    //----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        FragmentSettingsBinding binding;
        SettingsVM settings;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int index = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;

            if(index == 0){
                //rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                binding = FragmentSettingsBinding.inflate(inflater, container, false);

                settings = new SettingsVM();
                binding.setSettingsVM(settings);
                //binding.setListeners(new Settings.Listeners(binding));
                rootView = binding.getRoot();

                Button cmdRootFolder = (Button) rootView.findViewById(R.id.cmdRootFolder);
                cmdRootFolder.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onRootFolderClicked(v);
                    }
                });

                Button cmdTargetFolder = (Button) rootView.findViewById(R.id.cmdTargetFolder);
                cmdTargetFolder.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onTargetFolderClicked(v);
                    }
                });
            }
            else if(index == 1){
                rootView = inflater.inflate(R.layout.fragment_play, container, false);
            }
            //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));


            return rootView;
        }
        public void onRootFolderClicked(View v){
            Intent intent = new Intent(this.getActivity(), FileChooserActivity.class);
            intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
            this.getActivity().startActivityForResult(intent, RequestCodes.CHOOSE_ROOT_FOLDER);
        }
        public void onTargetFolderClicked(View v){
            Intent intent = new Intent(this.getActivity(), FileChooserActivity.class);
            intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
            this.getActivity().startActivityForResult(intent, RequestCodes.CHOOSE_TARGET_FOLDER);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Settings";
                case 1:
                    return "Play";
            }
            return null;
        }
    }
}
