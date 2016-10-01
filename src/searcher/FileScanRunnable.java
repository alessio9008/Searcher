/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.junrar.Archive;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

import config.FileRootSearch;
import config.OutputSeparateFile;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author alessio
 */
public class FileScanRunnable implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(FileScanRunnable.class);

    private final FileRootSearch fileRootSearch;
    private final int bufferReaderSize;
    private final boolean archiveScan;
    private OutputSeparateFile outputSeparateFile;
    private PrintWriter out;
    private boolean separateFile;
    private static final long BASE_PRINT = 3;

    public FileScanRunnable(FileRootSearch fileRootSearch, int bufferReaderSize, boolean archiveScan, OutputSeparateFile outputSeparateFile) {
        this.fileRootSearch = fileRootSearch;
        this.bufferReaderSize = bufferReaderSize;
        this.archiveScan = archiveScan;
        this.outputSeparateFile = outputSeparateFile;
    }

    @Override
    public void run() {
        try {
            scanFile();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            finallyMethod();
        }
    }

    private void scanFile() throws IOException {
        if (archiveScan) {
            if (Utils.checkZipFile(fileRootSearch.getPath())) {
                LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sara scanzionato come file zip");
                scanZipFile();
                return;
            } else if (Utils.checkGzipFile(fileRootSearch.getPath())) {
                LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sara scanzionato come file Gzip");
                scanGzipFile();
                return;
            } else if (Utils.checkRarFile(fileRootSearch.getPath())) {
                LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sara scanzionato come file rar");
                scanRarFile();
                return;
            }
        }
        LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sarà processato come file di testo");
        scanTextFile();

    }

    private void finallyMethod() {
        if (out != null) {
            out.flush();
            if (separateFile) {
                out.close();
                out = null;
            }
        }
    }

    private void scanRarFile() {
        try (Archive archive = new Archive(new FileVolumeManager(fileRootSearch.getPath().toFile()))) {
            FileHeader fileHeader = archive.nextFileHeader();
            while (fileHeader != null) {
                try {
                    if (!fileHeader.isDirectory()) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(archive.getInputStream(fileHeader)), bufferReaderSize)) {
                            scanFile(reader);
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } finally {
                    fileHeader = archive.nextFileHeader();
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void scanTextFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(fileRootSearch.getPath(), StandardCharsets.UTF_8), bufferReaderSize)) {
            scanFile(reader);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void scanGzipFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new BufferedInputStream(Files.newInputStream(fileRootSearch.getPath()), bufferReaderSize), bufferReaderSize)), bufferReaderSize)) {
            scanFile(reader);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void scanZipFile() throws IOException {
        try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(Files.newInputStream(fileRootSearch.getPath()), bufferReaderSize))) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zin), bufferReaderSize)) {
                ZipEntry ze = zin.getNextEntry();
                while (ze != null) {
                    try {
                        if (!ze.isDirectory()) {
                            scanFile(reader);
                        }
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    } finally {
                        ze = zin.getNextEntry();
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void scanFile(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        long counter = 1;
        long limit = 1;
        while (line != null) {
            try {
                if (counter == limit) {
                    LOGGER.debug("linea di esempio n° " + counter + " letta dal file = " + fileRootSearch.getPath().toAbsolutePath().toString() + " <=> " + line);
                    limit = counter * BASE_PRINT;
                }
                counter++;
                if (fileRootSearch.getSearchText().isRegex()) {
                    checkRegexInLine(line);
                } else {
                    checkTextInLine(line);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            line = reader.readLine();
        }
    }

    private void checkTextInLine(String line) {
        if (fileRootSearch.getSearchText().isCaseSensitive()) {
            checkTextInLineCaseSensitive(line);
        } else {
            checkTextInLineCaseInsensitive(line);
        }
    }

    private void checkTextInLineCaseInsensitive(String line) {
        if (line.toLowerCase().contains(fileRootSearch.getSearchText().getToSearch().toLowerCase())) {
            print(line);
        }
    }

    private void checkTextInLineCaseSensitive(String line) {
        if (line.contains(fileRootSearch.getSearchText().getToSearch())) {
            print(line);
        }
    }

    private void checkRegexInLine(String line) {
        Pattern regex = Utils.regex(fileRootSearch.getSearchText());
        Matcher matcher = regex.matcher(line);
        if (matcher.find()) {
            print(line);
        }
    }

    private void print(String mex) {
        checkInitStream();
        switch (fileRootSearch.getPrintMode()) {
            case ABSOLUTE: {
                out.println(fileRootSearch.getPath().toAbsolutePath().toString() + ":" + mex);
                break;
            }
            case FILENAME: {
                out.println(fileRootSearch.getPath().getFileName().toString() + ":" + mex);
                break;
            }
            default: {
                out.println(mex);
                break;
            }

        }

    }

    private void checkInitStream() {
        if (out == null) {
            initStream();
        }
    }

    private void initStream() {
        if (outputSeparateFile != null) {
            try {
                Files.createDirectories(outputSeparateFile.getDirectoryPathResult());
                Path file = Files.createTempFile(outputSeparateFile.getDirectoryPathResult(), outputSeparateFile.getPrefix() + "_", "_" + fileRootSearch.getPath().getFileName().toString() + ".txt");
                BufferedWriter bufferedWriter = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                out = new PrintWriter(bufferedWriter);
                separateFile = true;
                LOGGER.info("il risultato della ricerca nel file = " + fileRootSearch.getPath().toAbsolutePath().toString() + " verra' scritto nel file = " + file.toAbsolutePath().toString());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        if (out == null) {
            out = new PrintWriter(new BufferedOutputStream(System.out));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        finallyMethod();
    }

}
