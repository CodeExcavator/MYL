package com.productiveengine.myl.UIL;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileChooserActivity extends AppCompatActivity {

    List<String> item = new ArrayList<String>();
    List<String> path = new ArrayList<String>();
    String root="/";
    ListView dirList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
    }

    /**
     * Project the files of a directory
     *
     * @param dirPath
     */
    private void getDir(String dirPath)
    {
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if(!dirPath.equals(root))
        {

            item.add(root);
            path.add(root);

            item.add("../");
            path.add(f.getParent());

        }

        for(int i=0; i < files.length; i++)
        {
            File file = files[i];
            path.add(file.getPath());
            if(file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.file_explorer_row, item);
        dirList.setAdapter(fileList);
    }
}
