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
	private static final int ZIPFILEHEADER = 0x504b0304;
	private static final byte[] START_RAR_HEADER = { 0x52, 0x61, 0x72, 0x21, 0x1A, 0x07 };
	private static final byte END_RAR_HEADER1_5TOONWARDS = 0x00;
	private static final byte[] END_RAR_HEADER5_0TOONWARDS = { 0x01, 0x00 };

	public static boolean checkZipFile(Path file) {
		DataInputStream in = null;
		BufferedInputStream bin = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file.toFile());
			bin = new BufferedInputStream(fin);
			in = new DataInputStream(bin);
			int test = in.readInt();
			boolean result = test == ZIPFILEHEADER;
			if (!result) {
				LOGGER.error("il file = " + file.toAbsolutePath().toString() + " non è zip");
			}
			return result;
		} catch (Throwable ex) {
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
			if (bin != null) {
				try {
					bin.close();
				} catch (Throwable ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
			if (fin != null) {
				try {
					fin.close();
				} catch (Throwable ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
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
		} catch (Throwable ex) {
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
			if (bin != null) {
				try {
					bin.close();
				} catch (Throwable ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
			if (fin != null) {
				try {
					fin.close();
				} catch (Throwable ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		}
	}

	private static boolean equalsRar(byte[] data) {
		try {
			if (data.length >= START_RAR_HEADER.length + 2) {
				for (int i = 0; i < START_RAR_HEADER.length; i++) {
					if (data[i] != START_RAR_HEADER[i]) {
						return false;
					}
				}
				if (data[START_RAR_HEADER.length] == END_RAR_HEADER1_5TOONWARDS) {
					return true;
				}
				if (data[START_RAR_HEADER.length] == END_RAR_HEADER5_0TOONWARDS[0] && data[START_RAR_HEADER.length + 1] == END_RAR_HEADER5_0TOONWARDS[1]) {
					return true;
				}
			}
		} catch (Throwable ex) {
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

}
