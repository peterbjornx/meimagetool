package nl.peterbjornx.intelme.model.parts.code.file;

import nl.peterbjornx.intelme.model.man.Manifest;
import nl.peterbjornx.intelme.model.parts.code.CPDEntry;

import java.nio.ByteBuffer;

public class ManifestFile extends CodeFile {
    private Manifest manifest;

    public ManifestFile(CPDEntry entry, ByteBuffer buf, boolean hdr) {
        super(entry, buf);
        manifest = new Manifest(Data, hdr);
    }

    public Manifest getManifest() {
        return manifest;
    }
}
