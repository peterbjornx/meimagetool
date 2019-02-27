package nl.peterbjornx.intelme.model.mfs.chunk;

import nl.peterbjornx.intelme.model.mfs.MFSBackingStore;
import nl.peterbjornx.intelme.model.mfs.page.MFSDataPage;
import nl.peterbjornx.intelme.model.mfs.page.MFSPageInfo;
import nl.peterbjornx.intelme.model.mfs.page.MFSSystemPage;
import nl.peterbjornx.intelme.util.CrcUtil;
import nl.peterbjornx.intelme.util.MFSException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MFSChunkStore {
    private static final int MFS_PAGE_SIZE = 8192;
    private static final int MFS_CHUNK_SIZE = 64;
    private static final int MFS_DATA_CHUNKS = 122;
    private static final int MFS_SYSTEM_CHUNKS = 121;
    private int PartOffset;
    private MFSBackingStore Store;
    private MFSPageInfo PageInfo[];
    private MFSSystemChunkRef SystemChunks[] = new MFSSystemChunkRef[14*121];
    private MFSSystemPage SystemPages[];
    private MFSDataPage DataPages[];
    private int ErasedPage;
    private int SystemPageCount = 0;
    private int SystemChunkCount = 0;
    private int DataPageCount = 0;
    private int SystemCapacity = 0;
    private int PageCount;
    private int DataChunkCount;

    public MFSChunkStore( int offset, int size, MFSBackingStore store ) throws MFSException {
        Store = store;
        PartOffset = offset;

        PageInfo = new MFSPageInfo[size/MFS_PAGE_SIZE];

        PageCount = size / MFS_PAGE_SIZE;
        SystemPageCount = PageCount / 12;
        DataPageCount   = PageCount - SystemPageCount - 1;
        SystemChunkCount= SystemPageCount * MFS_SYSTEM_CHUNKS;
        DataChunkCount  = DataPageCount * MFS_DATA_CHUNKS;
        SystemCapacity  = SystemChunkCount * MFS_CHUNK_SIZE;

        for ( int i = 0; i < SystemChunkCount; i++ )
            SystemChunks[i] = new MFSSystemChunkRef();

        SystemPages = new MFSSystemPage[ SystemPageCount ];
        DataPages = new MFSDataPage[ DataPageCount ];

        int spCount = 0, dpCount = 0;

        for ( int i = 0; i < PageInfo.length; i++ ) {
            int pageStart = i * MFS_PAGE_SIZE + PartOffset;
            PageInfo[i] = new MFSPageInfo( Store.Read(pageStart, MFSPageInfo.METADATA_SIZE) );
            pageStart += MFSPageInfo.METADATA_SIZE;
            if ( PageInfo[i].getMagic() == 0 )
                ErasedPage = i;
            else if ( PageInfo[i].getFirstChunk() == 0 )
                SystemPages[spCount++] = new MFSSystemPage( i, Store.Read(pageStart, MFSSystemPage.METADATA_SIZE) );
            else
                DataPages[dpCount++] = new MFSDataPage( i, Store.Read(pageStart, MFSDataPage.METADATA_SIZE) );
        }

        HandleSystemPages();

    }

    private void HandleSystemPages() {
        for ( MFSSystemPage Page : SystemPages ) {
            int idxs[] = Page.getIndices();
            int usn = PageInfo[Page.getPageIndex()].getUpdateSerialNumber();

            for (int j = 0; j < idxs.length; j++) {
                int idx = idxs[j];
                if ( idx == -1 || SystemChunks[idx].USN > usn)
                    continue;
                SystemChunks[idx].USN = usn;
                SystemChunks[idx].Page = Page.getPageIndex();
                SystemChunks[idx].Chunk = j;
            }
        }

    }

    public int AllocateDataChunk() throws MFSException {
        for (MFSDataPage DataPage : DataPages) {
            int firstChunk = PageInfo[DataPage.getPageIndex()].getFirstChunk();
            if (DataPage.getFreeCount() == 0)
                continue;
            return firstChunk + DataPage.AllocateChunk();
        }
        throw new MFSException("No spare data chunks left, erase not yet supported");
    }

    public void ReadChunk ( int chunkPos, byte[] buffer ) throws MFSException {
        byte[] data = new byte[2];
        int chunkIndex = -1;
        int chunkPage = 0;
        int metadataSize = MFSPageInfo.METADATA_SIZE;
        if ( chunkPos * 64 < SystemCapacity ) {
            MFSSystemChunkRef chunkref = SystemChunks[chunkPos];
            if ( chunkref.USN == 0 )
                chunkPage = -1;
            else {
                chunkIndex = chunkref.Chunk;
                chunkPage = chunkref.Page;
                metadataSize += MFSSystemPage.METADATA_SIZE;
            }
        } else {
            int dataIndex = chunkPos - SystemCapacity * 64;
            for (MFSDataPage DataPage : DataPages) {
                MFSPageInfo p = PageInfo[DataPage.getPageIndex()];
                int pageStartChunk = p.getFirstChunk();
                int chunkInPage = dataIndex - pageStartChunk;

                /* Skip system pages */
                if (pageStartChunk == 0)
                    continue;

                if (chunkInPage < 0 || chunkInPage >= 122)
                    continue;

                chunkIndex = chunkInPage;
                chunkPage = DataPage.getPageIndex();
            }
            metadataSize += MFSDataPage.METADATA_SIZE;
        }

        if ( chunkPage == -1 ) {
            Arrays.fill(buffer,(byte)0);
            return;
        } else if ( chunkIndex == -1 )
            throw new MFSException("Unknown chunk!");

        int readOffset = PartOffset + MFS_PAGE_SIZE * chunkPage + metadataSize + (MFS_CHUNK_SIZE+2) * chunkIndex;

        Store.Read( readOffset, buffer,0,MFS_CHUNK_SIZE );

        Store.Read( readOffset + MFS_CHUNK_SIZE, data,0, 2);

        int crc_calc = CrcUtil.Crc16Chunk( buffer, MFS_CHUNK_SIZE, chunkPos );
        int crc_read = (data[0] & 0xFF) | ((data[1] << 8) & 0xFF00);
        if ( crc_calc != crc_read )
            throw new MFSException("CRC Error!");

    }

    public void WriteChunk( int chunkPos, byte[] buffer ) throws MFSException {
        int chunkIndex = -1;
        int chunkPage = 0;
        int metadataSize = MFSPageInfo.METADATA_SIZE;
        if ( chunkPos * 64 >= SystemCapacity ) {
            MFSSystemChunkRef chunkref = SystemChunks[chunkPos];
            if ( chunkref.USN == 0 )
                chunkPage = -1;
            else {
                chunkIndex = chunkref.Chunk;
                chunkPage = chunkref.Page;
                metadataSize += MFSSystemPage.METADATA_SIZE;
            }
            for (MFSSystemPage SystemPage : SystemPages) {

                if (PageInfo[SystemPage.getPageIndex()].getFirstChunk() != 0)
                    continue;

                if (SystemPage.getFreeCount() == 0)
                    continue;

                int pageUSN = PageInfo[SystemPage.getPageIndex()].getUpdateSerialNumber();

                if (pageUSN < chunkref.USN)
                    continue;

                chunkref.Page  = chunkPage  = SystemPage.getPageIndex();
                chunkref.Chunk = chunkIndex = SystemPage.AllocateChunk(chunkPos);
                chunkref.USN   = pageUSN;

            }
        } else {
            int dataIndex = chunkPos - SystemCapacity * 64;
            for (MFSDataPage DataPage : DataPages) {
                MFSPageInfo p = PageInfo[DataPage.getPageIndex()];
                int pageStartChunk = p.getFirstChunk();
                int chunkInPage = dataIndex - pageStartChunk;

                /* Skip system pages */
                if (pageStartChunk == 0)
                    continue;

                if (chunkInPage < 0 || chunkInPage >= 122)
                    continue;

                chunkIndex = chunkInPage;
                chunkPage = DataPage.getPageIndex();
            }
            metadataSize += MFSDataPage.METADATA_SIZE;
        }

        if ( chunkIndex == -1 )
            throw new MFSException("No spare system chunks left, erase not yet supported");

        int readOffset = PartOffset + MFS_PAGE_SIZE * chunkPage + metadataSize + (MFS_CHUNK_SIZE+2) * chunkIndex;

        int crc_calc = CrcUtil.Crc16Chunk( buffer, MFS_CHUNK_SIZE, chunkPos );

        byte[] data = {(byte) (crc_calc & 0xFF), (byte) ((crc_calc >> 8) & 0xFF)};

        Store.Write( readOffset, buffer,0,MFS_CHUNK_SIZE + 2 );
        Store.Write( readOffset + MFS_CHUNK_SIZE, data,0, 2);

    }


    public void ReadBytes(int pos, byte[] buffer, int offset, int count) throws MFSException {
        byte chunkData[] = new byte[MFS_CHUNK_SIZE];
        int endPos = pos + count;
        int startChunk = pos / MFS_CHUNK_SIZE;
        int endChunk = (endPos + MFS_CHUNK_SIZE - 1) / MFS_CHUNK_SIZE;

        for ( int chunk = startChunk; chunk < endChunk; chunk++ ) {
            int chunkStart   = chunk * MFS_CHUNK_SIZE;
            int chunkSize    = endPos - chunkStart;
            int chunkOffset  = pos - chunkStart;
            int bufferOffset = -chunkOffset;

            if ( chunkSize > MFS_CHUNK_SIZE)
                chunkSize = MFS_CHUNK_SIZE;

            if ( chunkOffset < 0 )
                chunkOffset = 0;

            if ( bufferOffset < 0 )
                bufferOffset = 0;

            chunkSize -= chunkOffset;

            ReadChunk( chunk, chunkData );

            System.arraycopy( chunkData, chunkOffset, buffer, bufferOffset + offset, chunkSize );

        }
    }

    public void WriteSystemBytes(int pos, byte[] buffer, int offset, int count) throws MFSException {
        byte chunkData[] = new byte[MFS_CHUNK_SIZE];
        int endPos = pos + count;
        int startChunk = pos / MFS_CHUNK_SIZE;
        int endChunk = (endPos + MFS_CHUNK_SIZE - 1) / MFS_CHUNK_SIZE;
        for ( int chunk = startChunk; chunk < endChunk; chunk++ ) {
            int chunkStart   = chunk * MFS_CHUNK_SIZE;
            int chunkSize    = endPos - chunkStart;
            int chunkOffset  = pos - chunkStart;
            int bufferOffset = -chunkOffset;

            if ( chunkSize > MFS_CHUNK_SIZE)
                chunkSize = MFS_CHUNK_SIZE;

            if ( chunkStart < 0 )
                chunkOffset = 0;

            if ( bufferOffset < 0 )
                bufferOffset = 0;

            chunkSize -= chunkOffset;

            ReadChunk( chunk, chunkData );
            boolean hadDiff = false;

            for ( int sp = bufferOffset + offset, dp = chunkOffset, c = 0; c < chunkSize; c++, sp++, dp++ ) {
                byte sd = buffer[sp];
                if ( sd != chunkData[dp] ) {
                    chunkData[dp] = sd;
                    hadDiff = true;
                }
            }

            if ( hadDiff )
                WriteChunk( chunk, chunkData );

        }

    }

    public ByteBuffer ReadBytes(int pos, int count ) throws MFSException {
        byte[] temp = new byte[count];
        ReadBytes(pos, temp, 0, count);
        return ByteBuffer.wrap(temp).order(ByteOrder.LITTLE_ENDIAN);
    }

    public int getDataPageCount() {
        return DataPageCount;
    }

    public int getDataChunkCount() {
        return DataChunkCount;
    }

    public class MFSSystemChunkRef {
        public int Page;
        public int Chunk;
        public int USN = 0;
    }
}
