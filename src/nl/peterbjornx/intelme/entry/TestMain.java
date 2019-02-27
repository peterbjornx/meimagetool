package nl.peterbjornx.intelme.entry;

import nl.peterbjornx.intelme.model.MERegion;
import nl.peterbjornx.intelme.model.mfs.MFSFileBacking;
import nl.peterbjornx.intelme.model.mfs.MFSVolume;
import nl.peterbjornx.intelme.util.MEFormatException;
import nl.peterbjornx.intelme.util.MFSException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class TestMain {

    public static void main(String[] args) throws IOException, MEFormatException, MFSException {
        MFSFileBacking f = new MFSFileBacking(new File("expl_me_8/MFS.mep"));
        MFSVolume v = new MFSVolume(0, f.Size(), f);
        System.out.println("MERegion"+v);
    }
}
