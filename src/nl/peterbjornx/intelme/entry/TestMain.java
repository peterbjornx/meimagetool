package nl.peterbjornx.intelme.entry;

import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.model.cfg.ConfigArchive;
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
        ConfigArchive ar = new ConfigArchive(ByteBufTools.ReadToByteBuffer("expl_mfs/intel.cfg"));
        System.out.println(ar);
    }
}
