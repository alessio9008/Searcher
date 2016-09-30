/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import config.SearchConfig;
import config.TimeIntervall;
import custom.Timestamp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author alessio
 */
public class Utils {

    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    public static boolean checkGzipFile(Path file) {
        DataInputStream in = null;
        BufferedInputStream bin = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file.toFile());
            bin = new BufferedInputStream(fin);
            in = new DataInputStream(bin);
            int test = in.readShort();
            boolean result = test == FileSignature.Gzip.GZIPFILEHEADER;
            if (!result) {
                LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è gzip");
            }
            return result;
        } catch (Exception ex) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è gzip");
            return false;
        } finally {
            close(in, bin, fin);
        }
    }

    public static boolean checkZipFile(Path file) {
        DataInputStream in = null;
        BufferedInputStream bin = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file.toFile());
            bin = new BufferedInputStream(fin);
            in = new DataInputStream(bin);
            int test = in.readInt();
            boolean result = test == FileSignature.Zip.ZIPFILEHEADER;
            if (!result) {
                LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è zip");
            }
            return result;
        } catch (Exception ex) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è zip");
            return false;
        } finally {
            close(in, bin, fin);
        }
    }

    public static boolean checkRarFile(Path file) {
        DataInputStream in = null;
        BufferedInputStream bin = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file.toFile());
            bin = new BufferedInputStream(fin);
            in = new DataInputStream(bin);
            byte data[] = new byte[8];
            int len = in.read(data);
            boolean result = (len == 8 && equalsRar(data));
            if (!result) {
                LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è rar");
            }
            return result;
        } catch (Exception ex) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è rar");
            return false;
        } finally {
            close(in, bin, fin);
        }
    }

    private static boolean equalsRar(byte[] data) {
        try {
            if (data.length >= FileSignature.Rar.START_RAR_HEADER.length + 2) {
                for (int i = 0; i < FileSignature.Rar.START_RAR_HEADER.length; i++) {
                    if (data[i] != FileSignature.Rar.START_RAR_HEADER[i]) {
                        return false;
                    }
                }
                if (data[FileSignature.Rar.START_RAR_HEADER.length] == FileSignature.Rar.END_RAR_HEADER1_5TOONWARDS) {
                    return true;
                }
                if (data[FileSignature.Rar.START_RAR_HEADER.length] == FileSignature.Rar.END_RAR_HEADER5_0TOONWARDS[0] && data[FileSignature.Rar.START_RAR_HEADER.length + 1] == FileSignature.Rar.END_RAR_HEADER5_0TOONWARDS[1]) {
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public static Pattern regex(SearchConfig config) {
        Pattern regex = null;
        if (config.isCaseSensitive()) {
            regex = Pattern.compile(config.getToSearch());
        } else {
            regex = Pattern.compile(config.getToSearch(), Pattern.CASE_INSENSITIVE);
        }
        return regex;
    }

    public static boolean checkTime(long fileAttr, TimeIntervall timeIntervall, StringBuilder builder) {
        if (timeIntervall != null) {
            if (timeIntervall.getMinTimestamp() != null && timeIntervall.getMinTimestamp().getTimeInMillis() > fileAttr) {
                builder.append(convertStringTime(fileAttr) + " e' minore di " + timeIntervall.getMinTimestamp());
                return false;
            }
            if (timeIntervall.getMaxTimestamp() != null && timeIntervall.getMaxTimestamp().getTimeInMillis() < fileAttr) {
                builder.append(convertStringTime(fileAttr) + " e' maggiore di " + timeIntervall.getMaxTimestamp());
                return false;
            }
        }
        return true;
    }

    public static String convertStringTime(long fileAttr) {
        Timestamp timestamp = new Timestamp(TimeZone.getTimeZone(Timestamp.TIMEZONE));
        timestamp.setTimeInMillis(fileAttr);
        return new SimpleDateFormat(Timestamp.SDF_FORMAT).format(timestamp.getTime());
    }

    private static void close(DataInputStream in, BufferedInputStream bin, FileInputStream fin) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        if (bin != null) {
            try {
                bin.close();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        if (fin != null) {
            try {
                fin.close();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

}
