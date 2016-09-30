/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

/**
 *
 * @author Alessio
 */
public interface FileSignature {

    public interface Zip {

        public static final int ZIPFILEHEADER = 0x504b0304;
    }

    public interface Rar {

        public static final byte[] START_RAR_HEADER = {0x52, 0x61, 0x72, 0x21, 0x1A, 0x07};
        public static final byte END_RAR_HEADER1_5TOONWARDS = 0x00;
        public static final byte[] END_RAR_HEADER5_0TOONWARDS = {0x01, 0x00};

    }

    public interface Gzip{
        public static final short GZIPFILEHEADER = 0x1F8B;
    }
}
