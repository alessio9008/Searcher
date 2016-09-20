/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import converter.PathConverter;
import java.nio.file.Path;

/**
 *
 * @author alessio
 */
@XStreamAlias("OutputSeparateFile")
public class OutputSeparateFile implements OutputMode{
    
    @XStreamAlias("directoryPathResult")
    @XStreamAsAttribute
    @XStreamConverter(PathConverter.class)
    protected Path directoryPathResult;
    @XStreamAlias("prefix")
    protected String prefix;

    public OutputSeparateFile() {
    }

    public OutputSeparateFile(Path directoryPathResult, String prefix) {
        this.directoryPathResult = directoryPathResult;
        this.prefix = prefix;
    }

    public Path getDirectoryPathResult() {
        return directoryPathResult;
    }

    public void setDirectoryPathResult(Path directoryPathResult) {
        this.directoryPathResult = directoryPathResult;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "OutputSeparateFile{" + "directoryPathResult=" + directoryPathResult + ", prefix=" + prefix + '}';
    }
    
    
}
