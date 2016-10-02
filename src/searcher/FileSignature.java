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

        public static final String ZIPFILEHEADER = "504B0304";
    }

    public interface Rar {

        public static final String START_RAR_HEADER_1 = "526172211A0700";
        public static final String START_RAR_HEADER_2 = "526172211A070100";

    }

    public interface Gzip {

        public static final String GZIPFILEHEADER = "1F8B";
    }

    public interface SevenZ {
        public static final String SEVENZHEADER = "377ABCAF271C";
    }
}
