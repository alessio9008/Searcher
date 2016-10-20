/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import config.Config;
import config.DirectoryRootSearch;
import config.FileRootSearch;
import config.IOConfig;
import config.OutputCommonFile;
import config.OutputMode;
import config.OutputSeparateFile;
import config.PrintMode;
import config.SearchConfig;
import config.SearchItem;
import config.SearchItems;
import config.ThreadPoolConfig;
import config.TimeIntervall;
import custom.Timestamp;

/**
 *
 * @author alessio
 */
public class Searcher {

    private static final Logger LOGGER;

    private static final String CONFIGFILE;

    static {
        CONFIGFILE = System.getProperty("config.file", "config.xml");
        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "log4j2.xml");
        }
        LOGGER = LogManager.getLogger(Searcher.class);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("configuration fileName = " + CONFIGFILE);
        XStream xstream = initXstream();
        if (args.length > 0) {
            initExample(xstream);
        } else {
            start(xstream);
        }

    }

    private static XStream initXstream() {
        XStream xstream = new XStream();
        xstream.processAnnotations(Config.class);
        xstream.processAnnotations(DirectoryRootSearch.class);
        xstream.processAnnotations(FileRootSearch.class);
        xstream.processAnnotations(SearchItem.class);
        xstream.processAnnotations(SearchItems.class);
        xstream.processAnnotations(ThreadPoolConfig.class);
        xstream.processAnnotations(IOConfig.class);
        xstream.processAnnotations(TimeIntervall.class);
        xstream.processAnnotations(PrintMode.class);
        xstream.processAnnotations(OutputMode.class);
        xstream.processAnnotations(OutputCommonFile.class);
        xstream.processAnnotations(OutputSeparateFile.class);
        return xstream;
    }

    private static void initExample(XStream xstream) {
        try {
            List<SearchItem> searchItems = new ArrayList<SearchItem>();
            ThreadPoolConfig thePoolConfig = new ThreadPoolConfig(5, 30, 5000, 10000);
            OutputMode mode = null;
            //mode = new OutputCommonFile(Paths.get("outputExample.txt"));
            //mode= new OutputSeparateFile(Paths.get("outputDir"), "result");
            IOConfig ioconfig = new IOConfig(524288, mode);
            Config config = new Config(true, thePoolConfig, ioconfig, new SearchItems(searchItems));
            Calendar start = new Timestamp(TimeZone.getTimeZone(Timestamp.TIMEZONE));
            start.add(Calendar.DAY_OF_YEAR, -1);
            Calendar end = new Timestamp(TimeZone.getTimeZone(Timestamp.TIMEZONE));
            TimeIntervall timeIntervall = new TimeIntervall(start, end);
            FileRootSearch fileRoot = new FileRootSearch(Paths.get("example.txt"), new SearchConfig(false, true, "textTest"), PrintMode.ABSOLUTE);
            DirectoryRootSearch directoryRootSearch = new DirectoryRootSearch(Paths.get(""), new SearchConfig(false, true, "textTest"), timeIntervall, new SearchConfig(false, true, "textFileNameTest"), PrintMode.FILENAME, TimeIntervall.class.cast(timeIntervall.clone()));
            searchItems.add(fileRoot);
            searchItems.add(directoryRootSearch);
            writeExample(xstream, config);
            LOGGER.info("file di esempio scritto");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void writeExample(XStream xstream, Config config) {
        try (BufferedWriter buff = new BufferedWriter(new FileWriter(Paths.get(CONFIGFILE).toFile(),false))) {
            xstream.toXML(config, buff);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void start(XStream xstream) {
        try {
            Path path = Paths.get(CONFIGFILE);
            if (Files.exists(path) && !Files.isDirectory(path) && Files.isReadable(path)) {
                Object result = readConfiguration(path, xstream);
                if (result instanceof Config) {
                    elaborateConfiguration(result);
                } else {
                    LOGGER.error("configurazione non valida");
                }
            } else {
                LOGGER.error("file di configurazione non valido");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void elaborateConfiguration(Object result) {
        final Config configuration = Config.class.cast(result);
        LOGGER.info("configurazione caricata correttamente");
        LOGGER.debug("configurazione attuale = " + configuration);
        start(configuration, outputMode(configuration));
    }

    private static OutputSeparateFile outputMode(final Config configuration) {
        OutputSeparateFile outputMode = null;
        if (configuration.getIoconfig().getOutputMode() instanceof OutputCommonFile) {
            outputCommonFile(configuration);
        } else if (configuration.getIoconfig().getOutputMode() instanceof OutputSeparateFile) {
            outputMode = outputSeparateFile(configuration);
        }
        return outputMode;
    }

    private static void start(final Config configuration, final OutputSeparateFile outputMode) {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(configuration.getPoolConfig().getMinPoolSize(), configuration.getPoolConfig().getMaxPoolSize(), configuration.getPoolConfig().getIdleTimeOut(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(configuration.getPoolConfig().getMaxQueueSize()));
        configuration.getSearchItems().getSearchItems().parallelStream().forEach(searchItem -> searchItempLoopCore(searchItem, executor, configuration, outputMode));
        executor.shutdown();
    }

    private static OutputSeparateFile outputSeparateFile(Config configuration) {
        OutputSeparateFile outputMode;
        outputMode = OutputSeparateFile.class.cast(configuration.getIoconfig().getOutputMode());
        LOGGER.info("output in file separati");
        return outputMode;
    }

    private static void outputCommonFile(Config configuration) {
        OutputCommonFile outputCommonFile = OutputCommonFile.class.cast(configuration.getIoconfig().getOutputMode());
        try {
            Files.createDirectories(outputCommonFile.getOutputFilePath().getParent());
            PrintStream outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputCommonFile.getOutputFilePath().toFile(),false)));
            System.setOut(outputStream);
            System.setErr(outputStream);
            LOGGER.info("output redirect to = " + outputCommonFile.getOutputFilePath().toAbsolutePath().toString());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static Object readConfiguration(Path path, XStream xstream) {
        Object result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            result = xstream.fromXML(reader);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return result;
    }

    private static void searchItempLoopCore(SearchItem searchItem, ThreadPoolExecutor executor, Config configuration, OutputSeparateFile outputMode) {
        try {
            if (searchItem instanceof DirectoryRootSearch) {
                ScanDirectory scanDirectory = new ScanDirectory(DirectoryRootSearch.class.cast(searchItem), executor, configuration.isArchiveScan(), configuration.getIoconfig().getBufferReaderSize(), outputMode);
                Files.walkFileTree(searchItem.getPath(), scanDirectory);
            } else if (searchItem instanceof FileRootSearch) {
                executor.execute(new FileScanRunnable(FileRootSearch.class.cast(searchItem), configuration.getIoconfig().getBufferReaderSize(), configuration.isArchiveScan(), outputMode));
                LOGGER.info("file da scansionare = " + searchItem.getPath().toAbsolutePath().toString());
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

}
