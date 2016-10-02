/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Alessio
 */
public class SevenZFileExt extends SevenZFile {

    private static final Logger LOGGER = LogManager.getLogger(SevenZFileExt.class);

    public SevenZFileExt(File filename, byte[] password) throws IOException {
        super(filename, password);
    }

    public SevenZFileExt(File filename) throws IOException {
        super(filename);
    }

    public InputStream getInputStream(final int bufferSize) {
        final PipedInputStream in = new PipedInputStream(bufferSize);
        try {
            final PipedOutputStream out = new PipedOutputStream(in);
            Thread thread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[bufferSize];
                    int len = read(buffer);
                    while (len > 0) {
                        try {
                            out.write(buffer, 0, len);
                            len = read(buffer);
                        } catch (Exception ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                }
            });
            thread.setName("GenerateInputStreamSeven7File");
            thread.start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return in;
    }

    public InputStream getInputStream() {
        return this.getInputStream(8192);
    }
}
