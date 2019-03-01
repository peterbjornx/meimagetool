package nl.peterbjornx.intelme.entry;

import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.model.mfs.MFSFileBacking;
import nl.peterbjornx.intelme.model.mfs.MFSVolume;
import nl.peterbjornx.intelme.util.MFSException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MFSTool {

    private static String[] names = {null, null, null, null, null, null, "intel.cfg", "fitc.cfg" };

    private static String getFileName( int i ) {
        if ( i >= 0 && i < names.length && names[i] != null )
            return names[i];
        return Integer.toString(i);
    }

    private static int getFileNumber( String str ) {
        for ( int i = 0; i < names.length; i++ )
            if ( names[i] != null && names[i].equals(str) )
                return i;
        return Integer.parseInt(str);
    }

    private static void createMFS(File template, File part) throws IOException {
        Files.copy(Paths.get(template.getAbsolutePath()),Paths.get(part.getAbsolutePath()));
    }

    private static MFSVolume mountImage( File part ) throws FileNotFoundException, MFSException {
        MFSFileBacking f = new MFSFileBacking(part);
        return new MFSVolume(0, f.Size(), f);
    }

    private static void appendFiles(MFSVolume vol, File directory) throws IOException, MFSException {
        File[] files = directory.listFiles();
        assert files != null;
        for ( File f : files ) {
            if ( f.isDirectory() )
                continue;
            int num = getFileNumber(f.getName());
            vol.CreateFile( num, ByteBufTools.ReadToByteBuffer(f.getPath()).array() );
        }
        vol.SyncMetadata();
    }

    private static void extractFiles(MFSVolume vol, File directory) throws MFSException, IOException {
        directory.mkdir();
        for ( int i = 0; i < vol.getFileCount(); i++ ) {
            if (!vol.FileExists(i))
                continue;
            File f = new File(directory,getFileName(i));
            byte[] buf = new byte[vol.getFileSize(i)];
            vol.ReadFile(i,0,buf,0,buf.length);
            Files.write(Paths.get(f.getAbsolutePath()),buf);
        }
    }

    private static void mainCreate(String[] args) throws IOException, MFSException {
        if ( args.length != 4 )
            usage("Not enough arguments");
        File bin = new File(args[1]);
        File tmp = new File(args[2]);
        File dir = new File(args[3]);

        createMFS(tmp, bin);
        appendFiles(mountImage(bin), dir);
    }

    private static void mainExtract(String[] args) throws IOException, MFSException {
        if ( args.length != 3 )
            usage("Not enough arguments");
        File bin = new File(args[1]);
        File dir = new File(args[2]);

        extractFiles(mountImage(bin), dir);
    }

    public static void main(String[] args) throws IOException, MFSException {
        if ( args.length == 1 && (args[0].equals("-h") ||
                args[0].equals("--help") ||
                args[0].equals("/?")) ) {
            usage("");
        }

        if ( args.length == 0)
            usage("No mode specified");

        switch (args[0]) {
            case "x":
                mainExtract(args);
                break;
            case "c":
                mainCreate(args);
                break;
            default:
                usage("Invalid mode");
        }
    }

    private static void usage(String reason) {
        System.err.println(reason);
        System.err.println("Usage: mfstool x <binary> <directory>");
        System.err.println("       mfstool c <binary> <template> <directory>");
        //System.err.println("       mfstool v <binary>");
        System.err.println("       mfstool --help");
        System.err.println("       mfstool   -h");
        System.err.println("       mfstool   /?");
        System.err.println();
        System.err.println("The MFS tool allows compiling and extracting MFS volumes.");
        System.err.println("  The format used by the tool consists of a directory containing a metadata XML");
        System.err.println("  and a separate binary for each partition. The names of these binaries must");
        System.err.println("  match the short partition name as configured in the metadata.");
        System.err.println();
        System.err.println("  The modes of operation supported by the tool are:");
        System.err.println("      x     - Extract a binary to its constituent files.");
        System.err.println("      c     - Create a binary from its constituent files and an empty template.");
        //System.err.println("      v     - Validate the FAT level metadata in a binary.");
        System.err.println("     -h     - Displays this message.");
        System.err.println("     /?     - Displays this message.");
        System.err.println("     --help - Displays this message.");
        System.exit(255);
    }
}
