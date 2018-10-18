package com.productiveengine.myl.uil;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.productiveengine.myl.bll.CriteriaBL;
import com.productiveengine.myl.async.RefreshSongListTask;
import com.productiveengine.myl.bll.SongBL;
import com.productiveengine.myl.common.*;
import com.productiveengine.myl.domainclasses.AppDatabase;
import com.productiveengine.myl.domainclasses.ErrorLog;
import com.productiveengine.myl.domainclasses.Song;
import com.productiveengine.myl.services.MediaPlayerService;
import com.productiveengine.myl.uil.databinding.FragmentPlayBinding;
import com.productiveengine.myl.uil.databinding.FragmentSettingsBinding;
import com.productiveengine.myl.uil.databinding.FragmentStatsBinding;
import com.productiveengine.myl.viewmodels.*;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;

import java.io.File;
import ar.com.daidalos.afiledialog.FileChooserActivity;

import static com.productiveengine.myl.common.RequestCodes.*;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener{

    private AudioManager mAudioManager;
    BroadcastReceiver msgReceiver;
    BroadcastReceiver infoReceiver;
    private static final int REQUEST_EXTERNAL_STORAGE  = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean shouldAskPermission(){
        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);
    }

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
    public void informAudioService(String action){
        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction( action );
        startService( intent );
    }
    //----------------------------------------------------------
    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
            .setTitle("Quit Music You Love")
            .setMessage("Are you sure you want to exit M.Y.L. ?")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    informAudioService(ACTION_STOP);
                    abandonAudioFocus();
                    finish();
                } })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                } })
            .setIcon(android.R.drawable.ic_dialog_info)
            .show();
    }
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

        DatabaseConfig appDatabase = new DatabaseConfig.Builder(AppDatabase.class)
                .addModelClasses(ErrorLog.class)
                .addModelClasses(com.productiveengine.myl.domainclasses.Settings.class)
                .addModelClasses(Song.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabase)
                .build());

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

        getSupportActionBar().hide();

        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(MEDIA_PLAYER_MSG);

                TextView txtCurrentSong = (TextView) findViewById(R.id.txtCurrentSong);
                txtCurrentSong.setText("");
                TextView txtSongSate = (TextView) findViewById(R.id.txtSongState);
                txtSongSate.setText("");

                openDialog(s);
            }
        };

        infoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String songName = intent.getStringExtra(MP_NAME);
                String durationS = intent.getStringExtra(MP_DURATION);
                String currentPositionS = intent.getStringExtra(MP_CURRENT_POSITION);

                TextView txtCurrentSong = (TextView) findViewById(R.id.txtCurrentSong);

                if(txtCurrentSong != null) {
                    txtCurrentSong.setText(songName);
                }

                int durationMS = 0;
                int currentPositionMS = 0;
                int duration = 0;
                int currentPosition = 0;
                double percentage = 0;

                try{
                    durationMS = Integer.parseInt(durationS);
                    currentPositionMS = Integer.parseInt(currentPositionS);

                    duration = Util.convertTrackTimeToSeconds(durationMS);
                    currentPosition = Util.convertTrackTimeToSeconds(currentPositionMS);

                    percentage = (((double) currentPosition) / duration) * 100;
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                TextView txtSongSate = (TextView) findViewById(R.id.txtSongState);

                if(txtSongSate != null) {
                    txtSongSate.setText(Util.milliSecondsToTimer(currentPositionMS) + "/" +
                            Util.milliSecondsToTimer(durationMS) + " " +
                            String.format("%.2f", percentage) + "%");
                }

                SeekBar musicSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);

                if(musicSeekBar != null) {
                    if (musicSeekBar != null) {
                        musicSeekBar.setProgress((int) percentage);

                        if (android.os.Build.VERSION.SDK_INT >= 11) {
                            // will update the "progress" propriety of seekbar until it reaches progress
                            ObjectAnimator animation = ObjectAnimator.ofInt(musicSeekBar, "progress", (int) percentage);
                            animation.setDuration(500); // 0.5 second
                            animation.setInterpolator(new DecelerateInterpolator());
                            animation.start();
                        } else
                            musicSeekBar.setProgress((int) percentage); // no animation on Gingerbread or lower
                    }

                    //musicSeekBar.setBackgroundColor(CriteriaBL.applyCriteriaInMemory_SeekbarColor(currentPosition, percentage));
                    int seekbarColor = CriteriaBL.applyCriteriaInMemory_SeekbarColor(currentPosition, percentage);

                    musicSeekBar.getProgressDrawable().setColorFilter(seekbarColor, PorterDuff.Mode.SRC_IN);
                    musicSeekBar.getThumb().setColorFilter(seekbarColor, PorterDuff.Mode.SRC_IN);
                }
            }
        };
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        gainAudioFocus();

        if(shouldAskPermission()) {
            verifyStoragePermissions(this);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void openDialog(String msg){

        new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(infoReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();

        gainAudioFocus();

        LocalBroadcastManager.getInstance(this).registerReceiver((msgReceiver),
                new IntentFilter(MEDIA_PLAYER_RESULT)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((infoReceiver),
                new IntentFilter(MEDIA_PLAYER_INFO)
        );

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
    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange<=0) {
            informAudioService(ACTION_PAUSE);
        } else {
            informAudioService(ACTION_PLAY);
        }
    }
    //----------
    public void abandonAudioFocus(){
        mAudioManager.abandonAudioFocus(this);
    }
    public void gainAudioFocus(){
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
    //----------
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

        FragmentStatsBinding fragmentStatsBinding;
        StatsVM statsVM;

        TextView txtLoveTimeLimit;
        TextView txtLoveTimePercentage;
        TextView txtHateTimeLimit;
        TextView txtHateTimePercentage;

        Button btnMinusHateTime;
        Button btnMinusHatePercentage;
        Button btnPlusHateTime;
        Button btnPlusHatePercentage;

        Button btnMinusLoveTime;
        Button btnMinusLovePercentage;
        Button btnPlusLoveTime;
        Button btnPlusLovePercentage;
        ToggleButton toggBtnScreeonOn;

        SeekBar musicSeekBar;

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
                //---------------------------------------------------------------------------------------
                RadioButton btnLoveTimeLimit = (RadioButton) rootView.findViewById(R.id.btnLoveTimeLimit);
                btnLoveTimeLimit.setChecked(true);
                btnLoveTimeLimit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onLoveCriteriaChanged(v);
                    }
                });

                RadioButton btnLovePercentage = (RadioButton) rootView.findViewById(R.id.btnLovePercentage);
                btnLovePercentage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onLoveCriteriaChanged(v);
                    }
                });

                txtLoveTimeLimit = (TextView) rootView.findViewById(R.id.txtLoveTimeLimit);
                txtLoveTimePercentage = (TextView) rootView.findViewById(R.id.txtLoveTimePercentage);
                txtLoveTimePercentage.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
                //---------------------------------------------------------------------------------------

                RadioButton btnHateTimeLimit = (RadioButton) rootView.findViewById(R.id.btnHateTimeLimit);
                btnHateTimeLimit.setChecked(true);
                btnHateTimeLimit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onHateCriteriaChanged(v);
                    }
                });

                RadioButton btnHatePercentage = (RadioButton) rootView.findViewById(R.id.btnHatePercentage);
                btnHatePercentage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onHateCriteriaChanged(v);
                    }
                });

                txtHateTimeLimit = (TextView) rootView.findViewById(R.id.txtHateTimeLimit);
                txtHateTimePercentage = (TextView) rootView.findViewById(R.id.txtHateTimePercentage);
                txtHateTimePercentage.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});

                initializeMinusPlusButtons(rootView);
                //---------------------------------------------------------------------------------------
            }
            else if(index == 1){
                //-------------------------------------------------------------------------
                //Play fragment
                //-------------------------------------------------------------------------
                fragmentPlayBinding = FragmentPlayBinding.inflate(inflater, container, false);

                playVM = new PlayVM();
                fragmentPlayBinding.setPlayVM(playVM);
                rootView = fragmentPlayBinding.getRoot();

                Button btnRefreshSongList = (Button) rootView.findViewById(R.id.btnRefreshSongList);
                btnRefreshSongList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefreshSongListClicked(v);
                    }
                });

                Button btnPlay = (Button) rootView.findViewById(R.id.btnPlay);
                btnPlay.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onPlayClicked(v);
                    }
                });

                Button btnPause = (Button) rootView.findViewById(R.id.btnPause);
                btnPause.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onPauseClicked(v);
                    }
                });

                Button btnReplay = (Button) rootView.findViewById(R.id.btnReplay);
                btnReplay.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onReplayClicked(v);
                    }
                });
                Button btnInstantHate = (Button) rootView.findViewById(R.id.btnInstantHate);
                btnInstantHate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onInstantHateClicked(v);
                    }
                });
                Button btnInstantLove = (Button) rootView.findViewById(R.id.btnInstantLove);
                btnInstantLove.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onInstantLoveClicked(v);
                    }
                });
                Button btnGo40 = (Button) rootView.findViewById(R.id.btnGo40);
                btnGo40.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onGo40Clicked(v);
                    }
                });
                Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
                btnNext.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onNextClicked(v);
                    }
                });

                musicSeekBar = (SeekBar) rootView.findViewById(R.id.musicSeekBar);

                musicSeekBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }
            else if(index == 2){
                //-------------------------------------------------------------------------
                //Stats fragment
                //-------------------------------------------------------------------------
                fragmentStatsBinding = FragmentStatsBinding.inflate(inflater, container, false);

                statsVM = new StatsVM();
                fragmentStatsBinding.setStatsVM(statsVM);
                rootView = fragmentStatsBinding.getRoot();

                Button btnRefreshStats = (Button) rootView.findViewById(R.id.btnRefreshStats);
                btnRefreshStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefreshStats(v);
                    }
                });

                Button btnDeleteNeutralSongs = (Button) rootView.findViewById(R.id.btnDeleteNeutralSongs);
                btnDeleteNeutralSongs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteNeutralSongs(v);
                    }
                });
            }
            return rootView;
        }

        private void initializeMinusPlusButtons(View rootView){

            btnMinusHateTime = (Button) rootView.findViewById(R.id.btnMinusHateTime);
            btnMinusHateTime.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(settingsVM.getHateTimeLimit() > 0) {
                        settingsVM.setHateTimeLimit(settingsVM.getHateTimeLimit() - 1);
                    }
                }
            });
            btnMinusHatePercentage = (Button) rootView.findViewById(R.id.btnMinusHatePercentage);
            btnMinusHatePercentage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(settingsVM.getHateTimePercentage() > 0) {
                        settingsVM.setHateTimePercentage(settingsVM.getHateTimePercentage() - 1);
                    }
                }
            });
            btnPlusHateTime = (Button) rootView.findViewById(R.id.btnPlusHateTime);
            btnPlusHateTime.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    settingsVM.setHateTimeLimit(settingsVM.getHateTimeLimit()+1);
                }
            });
            btnPlusHatePercentage = (Button) rootView.findViewById(R.id.btnPlusHatePercentage);
            btnPlusHatePercentage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(settingsVM.getHateTimePercentage() < 100) {
                        settingsVM.setHateTimePercentage(settingsVM.getHateTimePercentage() + 1);
                    }
                }
            });

            btnMinusLoveTime = (Button) rootView.findViewById(R.id.btnMinusLoveTime);
            btnMinusLoveTime.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(settingsVM.getLoveTimeLimit() > 0) {
                        settingsVM.setLoveTimeLimit(settingsVM.getLoveTimeLimit() - 1);
                    }
                }
            });
            btnMinusLovePercentage = (Button) rootView.findViewById(R.id.btnMinusLovePercentage);
            btnMinusLovePercentage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(settingsVM.getLoveTimePercentage() > 0) {
                        settingsVM.setLoveTimePercentage(settingsVM.getLoveTimePercentage() - 1);
                    }
                }
            });
            btnPlusLoveTime = (Button) rootView.findViewById(R.id.btnPlusLoveTime);
            btnPlusLoveTime.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    settingsVM.setLoveTimeLimit(settingsVM.getLoveTimeLimit()+1);
                }
            });
            btnPlusLovePercentage = (Button) rootView.findViewById(R.id.btnPlusLovePercentage);
            btnPlusLovePercentage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(settingsVM.getLoveTimePercentage() < 100) {
                        settingsVM.setLoveTimePercentage(settingsVM.getLoveTimePercentage() + 1);
                    }
                }
            });
            toggBtnScreeonOn = (ToggleButton) rootView.findViewById(R.id.toggBtnScreeonOn);
            toggBtnScreeonOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settingsVM.setScreenOn(isChecked);
                    buttonView.setKeepScreenOn(isChecked);
                }
            });
            toggBtnScreeonOn.setChecked(settingsVM.isScreenOn());
            rootView.setKeepScreenOn(settingsVM.isScreenOn());
            //-------------------------------------
            toggleHateCriteria(settingsVM.isHateTimeLimitChk());
            toggleLoveCriteria(settingsVM.isLoveTimeLimitChk());
        }
        //Play -------------------------------------------------------------------------------
        public void onRefreshSongListClicked(View v){

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to refresh the song list ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            MainActivity ma = (MainActivity) getActivity();
                            ma.informAudioService(ACTION_STOP);

                            CriteriaBL.loadInMemoryCriteria();

                            if(!CriteriaBL.chkSettingsFolders()){
                                ma.openDialog(getString(R.string.setRootAndTarget));
                                return;
                            }
                            if(!CriteriaBL.chkSettingsHateLove()){
                                ma.openDialog(getString(R.string.setCriteria));
                                return;
                            }
                            AsyncTask task = new RefreshSongListTask(ma).execute();

                        } })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
        public void onPlayClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            CriteriaBL.loadInMemoryCriteria();
            ma.informAudioService(ACTION_PLAY);
        }
        public void onPauseClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            CriteriaBL.loadInMemoryCriteria();
            ma.informAudioService(ACTION_PAUSE);
        }
        public void onReplayClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            CriteriaBL.loadInMemoryCriteria();
            ma.informAudioService(ACTION_PREVIOUS);
        }
        public void onNextClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            CriteriaBL.loadInMemoryCriteria();
            ma.informAudioService(ACTION_NEXT);
        }

        //Instant --------------------------------------------------------------------------------
        public void onInstantHateClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            ma.informAudioService(ACTION_INSTANT_HATE);
        }
        public void onInstantLoveClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            ma.informAudioService(ACTION_INSTANT_LOVE);
        }
        public void onGo40Clicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
            ma.informAudioService(ACTION_GO40);
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
                case R.id.btnLoveTimeLimit:
                    if (checked){
                        settingsVM.setLoveCriteria(LoveCriteria.TIME_LIMIT);
                        toggleLoveCriteria(true);
                        settingsVM.setLoveTimePercentage(0);
                    }
                break;

                case R.id.btnLovePercentage:
                    if (checked){
                        settingsVM.setLoveCriteria(LoveCriteria.PERCENTAGE);
                        toggleLoveCriteria(false);
                        settingsVM.setLoveTimeLimit(0);
                    }
                break;
            }
        }
        private void toggleLoveCriteria(boolean value){
            txtLoveTimeLimit.setEnabled(value);
            btnMinusLoveTime.setEnabled(value);
            btnPlusLoveTime.setEnabled(value);

            txtLoveTimePercentage.setEnabled(!value);
            btnMinusLovePercentage.setEnabled(!value);
            btnPlusLovePercentage.setEnabled(!value);

            settingsVM.setLoveTimeLimitChk(value);
        }
        public void onHateCriteriaChanged(View v) {

            boolean checked = ((RadioButton) v).isChecked();

            switch(v.getId()) {
                case R.id.btnHateTimeLimit:
                    if (checked){
                        settingsVM.setHateCriteria(HateCriteria.TIME_LIMIT);
                        toggleHateCriteria(true);
                        settingsVM.setHateTimePercentage(0);
                    }
                    break;

                case R.id.btnHatePercentage:
                    if (checked){
                        settingsVM.setHateCriteria(HateCriteria.PERCENTAGE);
                        toggleHateCriteria(false);
                        settingsVM.setHateTimeLimit(0);
                    }
                    break;
            }
        }
        private void toggleHateCriteria(boolean value){
            txtHateTimeLimit.setEnabled(value);
            btnMinusHateTime.setEnabled(value);
            btnPlusHateTime.setEnabled(value);

            txtHateTimePercentage.setEnabled(!value);
            btnMinusHatePercentage.setEnabled(!value);
            btnPlusHatePercentage.setEnabled(!value);

            settingsVM.setHateTimeLimitChk(value);
        }

        //Stats -------------------------------------------------------------------------------
        public void onRefreshStats(View v){
            statsVM.refreshStats();
        }
        public void onDeleteNeutralSongs(View v){

            new SongBL().printSongs(Util.targetPath);
            /*
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to delete all the neutral songs ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            new SongBL().deleteNeutralSongs();
                            statsVM.refreshStats();
                        } })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
                    */
        }


    }
}
