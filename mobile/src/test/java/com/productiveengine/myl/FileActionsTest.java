package com.productiveengine.myl;

import com.productiveengine.myl.Common.FileActions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nikolaos on 22/10/2016.
 */

public class FileActionsTest {

    @Test
    public void moveFile() throws Exception {
        FileActions fa = new FileActions();
        fa.moveFile("","","");
        assertEquals(4, 2 + 2);
    }

    @Test
    public void deleteFile() throws Exception {
        FileActions fa = new FileActions();
        fa.deleteFile("","");
        assertEquals(4, 2 + 2);
    }

    @Test
    public void copyFile() throws Exception {
        FileActions fa = new FileActions();
        fa.copyFile("","","");
        assertEquals(4, 2 + 2);
    }

    @Test
    public void fixSameNameFiles() throws Exception {
        FileActions fa = new FileActions();
        fa.moveFile("","","");
        assertEquals(4, 2 + 2);
    }
}
