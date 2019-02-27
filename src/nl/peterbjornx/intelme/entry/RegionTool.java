package nl.peterbjornx.intelme.entry;

import nl.peterbjornx.intelme.io.ByteBufTools;
import nl.peterbjornx.intelme.model.MERegion;
import nl.peterbjornx.intelme.util.MEFormatException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class RegionTool {

    public static void main(String[] args) throws IOException, MEFormatException {
        if ( args.length == 1 && (args[0].equals("-h") ||
                    args[0].equals("--help") ||
                    args[0].equals("/?")) ) {
                usage("");
        } else if ( args.length != 3 ) {
            usage("Not enough arguments");
        }

        File bin = new File(args[1]);
        File dir = new File(args[2]);

        MERegion region;

        switch (args[0]) {
            case "x":
            case "v":
                region = new MERegion(ByteBufTools.ReadToByteBuffer(bin.getPath()), false);
                break;
            case "c":
                region = new MERegion(dir);
                break;
            default:
                usage("Invalid mode");
                return;
        }

        switch (args[0]) {
            case "x":
                region.extract(dir);
                break;
            case "c":
                region.compile(bin);
                break;
            case "v":
                region.verify();
                break;
        }

    }

    private static void usage(String reason) {
        System.err.println(reason);
        System.err.println("Usage: meregiontool [x|c] <binary> <directory>");
        System.err.println("       meregiontool   v   <binary>");
        System.err.println("       meregiontool   --help");
        System.err.println("       meregiontool   -h");
        System.err.println("       meregiontool   /?");
        System.err.println();
        System.err.println("The ME Region tool allows compiling and extracting ME region images");
        System.err.println("  The format used by the tool consists of a directory containing a metadata XML");
        System.err.println("  and a separate binary for each partition. The names of these binaries must");
        System.err.println("  match the short partition name as configured in the metadata.");
        System.err.println();
        System.err.println("  The modes of operation supported by the tool are:");
        System.err.println("      x     - Extract a binary to its constituent partitions and metadata.");
        System.err.println("      c     - Create a binary from its constituent partitions and metadata.");
        System.err.println("      v     - Validate the region level metadata in a binary.");
        System.err.println("     -h     - Displays this message.");
        System.err.println("     /?     - Displays this message.");
        System.err.println("     --help - Displays this message.");
        System.exit(255);
    }
}
