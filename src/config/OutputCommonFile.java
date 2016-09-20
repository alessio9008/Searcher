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
@XStreamAlias("OutputCommonFile")
public class OutputCommonFile implements OutputMode{
    @XStreamAlias("outputFilePath")
    @XStreamAsAttribute
    @XStreamConverter(PathConverter.class)
    protected Path outputFilePath;

    public OutputCommonFile() {
    }

    public OutputCommonFile(Path outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public Path getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(Path outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    @Override
    public String toString() {
        return "OutputCommonFile{" + "outputFilePath=" + outputFilePath + '}';
    }
    
    
}
