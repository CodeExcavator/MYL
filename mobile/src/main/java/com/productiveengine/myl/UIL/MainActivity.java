package com.productiveengine.myl.UIL;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.productiveengine.myl.BLL.CriteriaBL;
import com.productiveengine.myl.Async.RefreshSongListTask;
import com.productiveengine.myl.Common.HateCriteria;
import com.productiveengine.myl.Common.InputFilterMinMax;
import com.productiveengine.myl.Common.LoveCriteria;
import com.productiveengine.myl.Common.RequestCodes;
import com.productiveengine.myl.Common.Util;
import com.productiveengine.myl.Services.MediaPlayerService;
import com.productiveengine.myl.UIL.databinding.FragmentPlayBinding;
import com.productiveengine.myl.UIL.databinding.FragmentSettingsBinding;
import com.productiveengine.myl.ViewModels.PlayVM;
import com.productiveengine.myl.ViewModels.SettingsVM;

import java.io.File;

import ar.com.daidalos.afiledialog.FileChooserActivity;

import static com.productiveengine.myl.Common.RequestCodes.ACTION_NEXT;
import static com.productiveengine.myl.Common.RequestCodes.ACTION_PAUSE;
import static com.productiveengine.myl.Common.RequestCodes.ACTION_PLAY;
import static com.productiveengine.myl.Common.RequestCodes.ACTION_PREVIOUS;
import static com.productiveengine.myl.Common.RequestCodes.ACTION_STOP;
import static com.productiveengine.myl.Common.RequestCodes.MEDIA_PLAYER_INFO;
import static com.productiveengine.myl.Common.RequestCodes.MEDIA_PLAYER_MSG;
import static com.productiveengine.myl.Common.RequestCodes.MEDIA_PLAYER_RESULT;
import static com.productiveengine.myl.Common.RequestCodes.MP_CURRENT_POSITION;
import static com.productiveengine.myl.Common.RequestCodes.MP_DURATION;
import static com.productiveengine.myl.Common.RequestCodes.MP_NAME;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener{

    private AudioManager mAudioManager;
    BroadcastReceiver msgReceiver;
    BroadcastReceiver infoReceiver;
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

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);
        */
        //Bind with background service -----------------------------------------------
        //doBindService();
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
                txtCurrentSong.setText(songName);

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
                txtSongSate.setText(Util.milliSecondsToTimer(currentPositionMS) + "/" +
                        Util.milliSecondsToTimer(durationMS) +" " +
                        String.format("%.2f", percentage) + "%");

                SeekBar musicSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);

                if(musicSeekBar != null){
                    musicSeekBar.setProgress((int) percentage);

                    if(android.os.Build.VERSION.SDK_INT >= 11){
                        // will update the "progress" propriety of seekbar until it reaches progress
                        ObjectAnimator animation = ObjectAnimator.ofInt(musicSeekBar, "progress", (int) percentage);
                        animation.setDuration(500); // 0.5 second
                        animation.setInterpolator(new DecelerateInterpolator());
                        animation.start();
                    }
                    else
                        musicSeekBar.setProgress((int) percentage); // no animation on Gingerbread or lower
                }

                //musicSeekBar.setBackgroundColor(CriteriaBL.applyCriteriaInMemory_SeekbarColor(currentPosition, percentage));
                int seekbarColor = CriteriaBL.applyCriteriaInMemory_SeekbarColor(currentPosition, percentage);

                musicSeekBar.getProgressDrawable().setColorFilter(seekbarColor, PorterDuff.Mode.SRC_IN);
                musicSeekBar.getThumb().setColorFilter(seekbarColor, PorterDuff.Mode.SRC_IN);
            }
        };
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
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
        mAudioManager.abandonAudioFocus(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((msgReceiver),
                new IntentFilter(MEDIA_PLAYER_RESULT)
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((infoReceiver),
                new IntentFilter(MEDIA_PLAYER_INFO)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(infoReceiver);
        super.onStop();
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

        EditText txtLoveTimeLimit;
        EditText txtLoveTimePercentage;
        EditText txtHateTimeLimit;
        EditText txtHateTimePercentage;

        SeekBar musicSeekBar;
        private final Handler handler = new Handler();

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

                txtLoveTimeLimit = (EditText) rootView.findViewById(R.id.txtLoveTimeLimit);
                txtLoveTimePercentage = (EditText) rootView.findViewById(R.id.txtLoveTimePercentage);
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

                txtHateTimeLimit = (EditText) rootView.findViewById(R.id.txtHateTimeLimit);
                txtHateTimePercentage = (EditText) rootView.findViewById(R.id.txtHateTimePercentage);
                txtHateTimePercentage.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
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

                View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

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
            return rootView;
        }

        //Play -------------------------------------------------------------------------------
        public void onRefreshSongListClicked(View v){
            MainActivity ma = (MainActivity) this.getActivity();
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

                        txtLoveTimeLimit.setEnabled(true);
                        txtLoveTimePercentage.setEnabled(false);
                        settingsVM.setLoveTimePercentage(0);
                        settingsVM.setLoveTimeLimitChk(true);
                    }
                break;

                case R.id.btnLovePercentage:
                    if (checked){
                        settingsVM.setLoveCriteria(LoveCriteria.PERCENTAGE);

                        txtLoveTimeLimit.setEnabled(false);
                        txtLoveTimePercentage.setEnabled(true);
                        settingsVM.setLoveTimeLimit(0);
                        settingsVM.setLoveTimeLimitChk(false);
                    }
                break;
            }

        }
        public void onHateCriteriaChanged(View v) {

            boolean checked = ((RadioButton) v).isChecked();

            switch(v.getId()) {
                case R.id.btnHateTimeLimit:
                    if (checked){
                        settingsVM.setHateCriteria(HateCriteria.TIME_LIMIT);
                        settingsVM.setHateTimePercentage(0);
                        settingsVM.setHateTimeLimitChk(true);
                    }
                    break;

                case R.id.btnHatePercentage:
                    if (checked){
                        settingsVM.setHateCriteria(HateCriteria.PERCENTAGE);
                        settingsVM.setHateTimeLimit(0);
                        settingsVM.setHateTimeLimitChk(false);
                    }
                    break;
            }
        }
    }
}
