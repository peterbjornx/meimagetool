package nl.peterbjornx.intelme.entry;

import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.model.mfs.MFSFileBacking;
import nl.peterbjornx.intelme.model.mfs.MFSVolume;
import nl.peterbjornx.intelme.util.MEFormatException;
import nl.peterbjornx.intelme.util.MFSException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestMain {

    public static void main(String[] args) throws IOException, MEFormatException, MFSException {
        MFSFileBacking f = new MFSFileBacking(new File("newmfs.bin"));
        MFSVolume v = new MFSVolume(0, f.Size(), f);
        v.CreateFile(6, ByteBufTools.ReadToByteBuffer("expl_me_8/mfs6.bin").array());
        v.CreateFile(7, ByteBufTools.ReadToByteBuffer("expl_me_8/mfs7.bin").array());
        v.SyncMetadata();
    }
}
