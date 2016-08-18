package com.productiveengine.myl.BLL;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;

import com.productiveengine.myl.DomainClasses.Settings;
import com.productiveengine.myl.DomainClasses.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RefreshSongListTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog dialog;
    private FragmentActivity activity;

    private Settings settings;
    private SongBL songBL;
    private List<Song> songList;
    private Song currentSong;

    public RefreshSongListTask(FragmentActivity activity){
        this.activity = activity;
        dialog = new ProgressDialog(this.activity);
        songBL = new SongBL();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Progress start");
        this.dialog.show();
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog != null) {
            dialog.dismiss();
        }
        Snackbar.make(activity.getWindow().getDecorView(), "Process complete", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        refreshSongList();
        return null;
    }
    private void refreshSongList(){
        settings = (new SettingsBL()).initializeSettingsFromDB();

        if(settings.rootFolderPath != null && settings.rootFolderPath.trim().length() > 0){
            songList = new ArrayList<Song>();
            traverse(new File(settings.rootFolderPath));
            songBL.saveAll(songList);
        }
    }
    private void traverse (File dir) {
        String filePath = "";

        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                filePath = file.getAbsolutePath();

                if (file.isDirectory()) {
                    traverse(file);
                } else if(filePath.endsWith(".mp3") || filePath.endsWith(".3gp") || filePath.endsWith(".flac") || filePath.endsWith(".ogg") || filePath.endsWith(".wav") ) {
                    songList.add(new Song(file.getName(),filePath));
                }
            }
        }
    }
}
