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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            finallyMethod();
        }
    }

    private void scanFile() throws IOException {
        if (archiveScan && Utils.checkZipFile(fileRootSearch.getPath())) {
            LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sara scanzionato come file zip");
            scanZipFile();
        }
        if (archiveScan && Utils.checkRarFile(fileRootSearch.getPath())) {
            LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sara scanzionato come file rar");
            scanRarFile();
        } else {
            LOGGER.info("il file " + fileRootSearch.getPath().toAbsolutePath().toString() + " sarà processato come file di testo");
            scanTextFile();
        }
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
        Archive archive = null;
        try {
            archive = new Archive(new FileVolumeManager(fileRootSearch.getPath().toFile()));
            FileHeader fileHeader = archive.nextFileHeader();
            while (fileHeader != null) {
                InputStream inRar = null;
                InputStreamReader inReader = null;
                BufferedReader reader = null;
                try {
                    if (!fileHeader.isDirectory()) {
                        inRar = archive.getInputStream(fileHeader);
                        inReader = new InputStreamReader(inRar);
                        reader = new BufferedReader(inReader, bufferReaderSize);
                        scanFile(reader);
                    }
                } catch (Throwable ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } finally {
                    fileHeader = archive.nextFileHeader();
                    closeRarHeaderFile(inRar, inReader, reader);
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            closeRarArchive(archive);
        }
    }

    private void closeRarArchive(Archive archive) {
        if (archive != null) {
            try {
                archive.close();
            } catch (Throwable ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private void closeRarHeaderFile(InputStream inRar, InputStreamReader inReader, BufferedReader reader) throws IOException {
        if (inRar != null) {
            inRar.close();
        }
        if (inReader != null) {
            inReader.close();
        }
        if (reader != null) {
            reader.close();
        }
    }

    private void scanTextFile() throws IOException {
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            fileReader = new FileReader(fileRootSearch.getPath().toFile());
            reader = new BufferedReader(fileReader, bufferReaderSize);
            scanFile(reader);
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            closeFile(fileReader, reader);
        }
    }

    private void closeFile(FileReader fileReader, BufferedReader reader) throws IOException {
        if (fileReader != null) {
            fileReader.close();
        }
        if (reader != null) {
            reader.close();
        }
    }

    private void scanZipFile() throws IOException {
        InputStream filein = null;
        BufferedInputStream bin = null;
        ZipInputStream zin = null;
        try {
            filein = Files.newInputStream(fileRootSearch.getPath());
            bin = new BufferedInputStream(filein, bufferReaderSize);
            zin = new ZipInputStream(bin);
            ZipEntry ze = zin.getNextEntry();
            while (ze != null) {
                InputStreamReader isr = null;
                BufferedReader reader = null;
                try {
                    if (!ze.isDirectory()) {
                        isr = new InputStreamReader(zin);
                        reader = new BufferedReader(isr, bufferReaderSize);
                        scanFile(reader);
                    }
                } catch (Throwable ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } finally {
                    ze = zin.getNextEntry();
                    closeEntryFile(reader, isr);
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            closeZip(zin, bin, filein);
        }
    }

    private void closeEntryFile(BufferedReader reader, InputStreamReader isr) throws IOException {
        if (reader != null) {
            reader.close();
        }
        if (isr != null) {
            isr.close();
        }
    }

    private void closeZip(ZipInputStream zin, BufferedInputStream bin, InputStream filein) throws IOException {
        if (zin != null) {
            zin.close();
        }
        if (bin != null) {
            bin.close();
        }
        if (filein != null) {
            filein.close();
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
            } catch (Throwable ex) {
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
            } catch (Throwable ex) {
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
