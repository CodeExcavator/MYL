package com.productiveengine.myl.UIL;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.productiveengine.myl.BLL.AudioPlayBL;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.Common.RequestCodes;
import com.productiveengine.myl.DomainClasses.Song;
import com.productiveengine.myl.UIL.databinding.FragmentPlayBinding;
import com.productiveengine.myl.UIL.databinding.FragmentSettingsBinding;
import com.productiveengine.myl.ViewModels.PlayVM;
import com.productiveengine.myl.ViewModels.SettingsVM;

import java.io.File;
import java.util.List;

import ar.com.daidalos.afiledialog.FileChooserActivity;

public class MainActivity extends AppCompatActivity {

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
    //----------------------------
    private static long selected_song_id = 0;
    private static long playing_now_song_id = 0;

    //My Locks
    public static boolean initialPlay = true;
    private boolean playOrPause = true;

    //Backgroud Service instance
    public AudioPlayBL audioPlayService;

    //Service Binding
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            audioPlayService = ((AudioPlayBL.MyBinder) binder).getService();

        }
        public void onServiceDisconnected(ComponentName className) {
            audioPlayService = null;
        }
    };
    void doBindService() {
        bindService(new Intent(this, AudioPlayBL.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }
    public void playNextSong(String nextSongPath){
        Intent intent = new Intent(this, AudioPlayBL.class);
        Bundle b = new Bundle();
        b.putString("songPath",nextSongPath);
        intent.putExtras(b);

        audioPlayService.applyLoveCriteria();
        audioPlayService.onDestroy();
        audioPlayService.startService(intent);
    }
    //----------------------------------------------------------
    public void onTargetFolderClicked(View v){
        Intent intent = new Intent(this, FileChooserActivity.class);
        intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
        this.startActivityForResult(intent, RequestCodes.CHOOSE_TARGET_FOLDER);
    }
    //----------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        TextView lblRootFolderPath = (TextView) findViewById(R.id.lblRootFolderPath);
        TextView lblTargetFolderPath = (TextView) findViewById(R.id.lblTargetFolderPath);

        //If the request went well (OK) and the request was PICK_CONTACT_REQUEST
        if (resultCode == Activity.RESULT_OK ) {
            Bundle bundle = data.getExtras();

            if(bundle != null)
            {
                File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                //--------------------------------------------------------------------
                if(requestCode == RequestCodes.CHOOSE_ROOT_FOLDER) {
                    lblRootFolderPath.setText(file.getPath());

                    if(!checkFolderPaths(lblRootFolderPath.getText()+"",lblTargetFolderPath.getText()+"")){
                        lblRootFolderPath.setText("");
                    }
                }
                else if(requestCode == RequestCodes.CHOOSE_TARGET_FOLDER) {
                    lblTargetFolderPath.setText(file.getPath());

                    if(!checkFolderPaths(lblRootFolderPath.getText()+"",lblTargetFolderPath.getText()+"")){
                        lblTargetFolderPath.setText("");
                    }
                }
            }
        }
    }
    private boolean checkFolderPaths(String folderPath1, String folderPath2){
        boolean ok = true;

        if(folderPath1.equals(folderPath2)) {
            Toast toast = Toast.makeText(this, "Root and Target folders must be different!!!", Toast.LENGTH_LONG);
            toast.show();
            ok = false;
        }

        return ok;
    }
    //----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the two
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

        //Bind with background service -----------------------------------------------
        doBindService();
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

        FragmentSettingsBinding fragmentSettingsBinding;
        SettingsVM settingsVM;

        FragmentPlayBinding fragmentPlayBinding;
        PlayVM playVM;

        TextView txtTest;

        EditText txtTimeLimit;
        EditText txtTimePercentage;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int index = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;

            if(index == 0){
                //-------------------------------------------------------------------------
                //Settings fragment
                //-------------------------------------------------------------------------
                //rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);

                settingsVM = new SettingsVM();
                fragmentSettingsBinding.setSettingsVM(settingsVM);
                //fragmentSettingsBinding.setListeners(new Settings.Listeners(fragmentSettingsBinding));
                rootView = fragmentSettingsBinding.getRoot();

                Button btnRootFolder = (Button) rootView.findViewById(R.id.btnRootFolder);
                btnRootFolder.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onRootFolderClicked(v);
                    }
                });

                Button btnTargetFolder = (Button) rootView.findViewById(R.id.btnTargetFolder);
                btnTargetFolder.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onTargetFolderClicked(v);
                    }
                });

                RadioButton btnTimeLimit = (RadioButton) rootView.findViewById(R.id.btnTimeLimit);
                btnTimeLimit.setChecked(true);
                btnTimeLimit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onLoveCriteriaChanged(v);
                    }
                });

                RadioButton btnPercentage = (RadioButton) rootView.findViewById(R.id.btnPercentage);
                btnPercentage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onLoveCriteriaChanged(v);
                    }
                });

                txtTimeLimit = (EditText) rootView.findViewById(R.id.txtTimeLimit);
                txtTimePercentage = (EditText) rootView.findViewById(R.id.txtTimePercentage);
                txtTimePercentage.setEnabled(false);
            }
            else if(index == 1){
                //-------------------------------------------------------------------------
                //Play fragment
                //-------------------------------------------------------------------------
                fragmentPlayBinding = FragmentPlayBinding.inflate(inflater, container, false);

                playVM = new PlayVM();
                fragmentPlayBinding.setPlayVM(playVM);
                rootView = fragmentPlayBinding.getRoot();


                txtTest = (TextView) rootView.findViewById(R.id.txtTest);

                View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
                //txtTest.setText(settingsView.getContext(). .getRootFolder());

                Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
                btnNext.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onNextClicked(v);
                    }
                });

                Button btnRefreshSongList = (Button) rootView.findViewById(R.id.btnRefreshSongList);
                btnRefreshSongList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefreshSongListClicked(v);
                    }
                });
            }

            return rootView;
        }
        //Play -------------------------------------------------------------------------------
        public void onNextClicked(View v){
            Song song = playVM.getNextSong();

            if(song != null){
                MainActivity ma = (MainActivity) this.getActivity();
                ma.playNextSong(song.path);
            }
        }
        public void onRefreshSongListClicked(View v){
            playVM.refreshSongList();
        }
        //Settings -------------------------------------------------------------------------------
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
        public void onLoveCriteriaChanged(View v) {

            boolean checked = ((RadioButton) v).isChecked();

            switch(v.getId()) {
                case R.id.btnTimeLimit:
                    if (checked){
                        settingsVM.setLoveCriteria(LoveCriteria.TIME_LIMIT);

                        txtTimeLimit.setEnabled(true);
                        txtTimePercentage.setEnabled(false);
                        settingsVM.setTimePercentage(0);
                        settingsVM.setTimeLimitChk(true);
                    }
                break;

                case R.id.btnPercentage:
                    if (checked){
                        settingsVM.setLoveCriteria(LoveCriteria.PERCENTAGE);

                        txtTimeLimit.setEnabled(false);
                        txtTimePercentage.setEnabled(true);
                        settingsVM.setTimeLimit(0);
                        settingsVM.setTimeLimitChk(false);
                    }
                break;
            }
        }
    }
}
