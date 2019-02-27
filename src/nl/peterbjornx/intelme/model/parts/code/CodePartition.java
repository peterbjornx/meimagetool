package nl.peterbjornx.intelme.model.parts.code;

import nl.peterbjornx.intelme.model.fpt.FPTEntry;
import nl.peterbjornx.intelme.model.parts.Partition;
import nl.peterbjornx.intelme.model.parts.code.file.CodeFile;

import java.nio.ByteBuffer;

public class CodePartition extends Partition {

    protected CodePartitionDirectory Cpd;
    private CodeFile[] Files;

    public CodePartition(FPTEntry entry, ByteBuffer buf ) {
        super(entry, buf);
        Cpd = new CodePartitionDirectory( Data );
        Files = new CodeFile[Cpd.getCpdHeader().getEntries()];
        CPDEntry[] es = Cpd.getCpdEntries();
        for ( int i = 0; i < es.length; i++ ) {
            Files[i] = CodeFile.decode(es[i], Data);
        }
    }

    public CodeFile[] getFiles() {
        return Files;
    }
}
