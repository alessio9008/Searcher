/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

/**
 *
 * @author alessio
 */
@XStreamAlias("IOConfig")
public class IOConfig implements Serializable {

    @XStreamAlias("bufferReaderSize")
    protected int bufferReaderSize;
    @XStreamAlias("outputMode")
    protected OutputMode outputMode;
    
    
    public IOConfig() {
    }

    public IOConfig(int bufferReaderSize, OutputMode outputMode) {
        this.bufferReaderSize = bufferReaderSize;
        this.outputMode = outputMode;
    }

    public OutputMode getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(OutputMode outputMode) {
        this.outputMode = outputMode;
    }

    

    public int getBufferReaderSize() {
        return bufferReaderSize;
    }

    public void setBufferReaderSize(int bufferReaderSize) {
        this.bufferReaderSize = bufferReaderSize;
    }

    @Override
    public String toString() {
        return "IOConfig{" + "bufferReaderSize=" + bufferReaderSize + ", outputMode=" + outputMode + '}';
    }


}
