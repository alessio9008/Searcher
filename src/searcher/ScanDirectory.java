/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import config.DirectoryRootSearch;
import config.FileRootSearch;
import config.OutputSeparateFile;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author alessio
 */
public class ScanDirectory extends SimpleFileVisitor<Path> {

    private static final Logger LOGGER = LogManager.getLogger(ScanDirectory.class);

    private DirectoryRootSearch rootSearch;
    private boolean archiveScan;
    private ThreadPoolExecutor executor;
    private int bufferSize;
    private OutputSeparateFile outputSeparateFile;

    public ScanDirectory(DirectoryRootSearch directoryRootSearch, ThreadPoolExecutor executor, boolean archiveScan, int bufferSize, OutputSeparateFile outputSeparateFile) {
        this.rootSearch = directoryRootSearch;
        this.executor = executor;
        this.archiveScan = archiveScan;
        this.bufferSize = bufferSize;
        this.outputSeparateFile = outputSeparateFile;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        try {
            if (checkFileAttribute(attrs, file)) {
                return FileVisitResult.CONTINUE;
            }
            scanFileName(file);
        } catch (Exception ex) {
            return FileVisitResult.CONTINUE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private void putFile(Path file) throws InterruptedException {
        FileRootSearch fileRoot = new FileRootSearch(file, rootSearch.getSearchText(), rootSearch.getPrintMode());
        executor.execute(new FileScanRunnable(fileRoot, bufferSize, archiveScan, outputSeparateFile));
        LOGGER.info("file da scansionare = " + file.toAbsolutePath().toString());
    }

    private void scanFileName(Path file) throws InterruptedException {
        if (rootSearch.getSearchFileName().isRegex()) {
            checkFileNameWithRegex(file);
        } else {
            checkFileNameWithoutRegex(file);
        }
    }

    private boolean checkFileAttribute(BasicFileAttributes attrs, Path file) {
        if (checkTime(attrs, file)) {
            return true;
        }
        if (checkArchive(file)) {
            return true;
        }
        return false;
    }

    private void checkFileNameWithoutRegex(Path file) throws InterruptedException {
        if (rootSearch.getSearchFileName().isCaseSensitive()) {
            checkFileNameWithoutRegexCaseSensitive(file);
        } else {
            checkFileNameWithoutRegexCaseInsensitive(file);
        }
    }

    private void checkFileNameWithoutRegexCaseInsensitive(Path file) throws InterruptedException {
        if (file.getFileName().toString().equalsIgnoreCase(rootSearch.getSearchFileName().getToSearch())) {
            putFile(file);
        }
    }

    private void checkFileNameWithoutRegexCaseSensitive(Path file) throws InterruptedException {
        if (file.getFileName().toString().equals(rootSearch.getSearchFileName().getToSearch())) {
            putFile(file);
        }
    }

    private void checkFileNameWithRegex(Path file) throws InterruptedException {
        Pattern regex = Utils.regex(rootSearch.getSearchFileName());
        Matcher marcher = regex.matcher(file.getFileName().toString());
        if (marcher.find()) {
            putFile(file);
        }
    }

    private boolean checkArchive(Path file) {
        if (!archiveScan) {
            if (Utils.checkZipFile(file)) {
                LOGGER.debug("file scartato = " + file.toAbsolutePath().toString());
                return true;
            }
            if (Utils.checkGzipFile(file)) {
                LOGGER.debug("file scartato = " + file.toAbsolutePath().toString());
                return true;
            }
            if (Utils.checkRarFile(file)) {
                LOGGER.debug("file scartato = " + file.toAbsolutePath().toString());
                return true;
            }
        }
        return false;
    }

    private boolean checkTime(BasicFileAttributes attrs, Path file) {
        StringBuilder builder = new StringBuilder();
        builder.append("lastModifiedTime = ");
        if (!Utils.checkTime(attrs.lastModifiedTime().toMillis(), rootSearch.getLastModifiedTimeIterval(), builder)) {
            builder.append(" file scartato = " + file.toAbsolutePath().toString());
            LOGGER.debug(builder.toString());
            return true;
        }
        builder.setLength(0);
        builder.append("creationTime = ");
        if (!Utils.checkTime(attrs.creationTime().toMillis(), rootSearch.getCreationTimeIterval(), builder)) {
            builder.append(" file scartato = " + file.toAbsolutePath().toString());
            LOGGER.debug(builder.toString());
            return true;
        }
        return false;
    }

}
