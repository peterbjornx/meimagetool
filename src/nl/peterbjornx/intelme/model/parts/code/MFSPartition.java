package nl.peterbjornx.intelme.model.parts.code;

import nl.peterbjornx.intelme.model.fpt.FPTEntry;
import nl.peterbjornx.intelme.model.mfs.MFSVolume;
import nl.peterbjornx.intelme.model.parts.Partition;

import java.nio.ByteBuffer;

public class MFSPartition extends Partition {
    private MFSVolume Volume;

    public MFSPartition(FPTEntry entry, ByteBuffer buf) {
        super(entry, buf);
        //TODO: Volume = new MFSVolume(entry.getLength(), Data);
    }

    public MFSVolume getVolume() {
        return Volume;
    }
}
