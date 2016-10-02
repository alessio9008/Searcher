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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    public static boolean checkSevenZFile(Path file) {
        boolean result = convertByteToHex(readByteFile(file, 6)).equals(FileSignature.SevenZ.SEVENZHEADER);
        if (!result) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è 7z");
        }
        return result;
    }

    public static boolean checkGzipFile(Path file) {
        boolean result = convertByteToHex(readByteFile(file, 2)).equals(FileSignature.Gzip.GZIPFILEHEADER);
        if (!result) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è gzip");
        }
        return result;
    }

    public static boolean checkZipFile(Path file) {
        boolean result = convertByteToHex(readByteFile(file, 4)).equals(FileSignature.Zip.ZIPFILEHEADER);
        if (!result) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è zip");
        }
        return result;
    }

    public static boolean checkRarFile(Path file) {
        String hex = convertByteToHex(readByteFile(file, 8));
        boolean result = hex.startsWith(FileSignature.Rar.START_RAR_HEADER_1) || hex.startsWith(FileSignature.Rar.START_RAR_HEADER_2);
        if (!result) {
            LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è rar");
        }
        return result;
    }

    private static byte[] readByteFile(Path file, int byteNumber) {
        byte[] returnValue = null;
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file), byteNumber)) {
            byte[] tempReturnValue = new byte[byteNumber];
            int len = in.read(tempReturnValue);
            if (len > 0) {
                returnValue = Arrays.copyOf(tempReturnValue, len);
            }
        } catch (Exception e) {
        }
        return returnValue;
    }

    private static String convertByteToHex(byte[] byteArr) {
        StringBuilder builder = new StringBuilder();
        try {
            if (byteArr != null) {
                for (byte b : byteArr) {
                    builder.append(String.format("%02x", b).toUpperCase());
                }
            }
        } catch (Exception e) {
        }
        return builder.toString();
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

}
