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
@XStreamAlias("poolConfig")
public class ThreadPoolConfig implements Serializable {

    @XStreamAlias("minPoolSize")
    protected int minPoolSize;
    @XStreamAlias("maxPoolSize")
    protected int maxPoolSize;
    @XStreamAlias("idleTimeOut")
    protected long idleTimeOut;
    @XStreamAlias("maxQueueSize")
    protected int maxQueueSize;

    public ThreadPoolConfig() {
    }

    public ThreadPoolConfig(int minPoolSize, int maxPoolSize, long idleTimeOut, int maxQueueSize) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.idleTimeOut = idleTimeOut;
        this.maxQueueSize = maxQueueSize;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }    

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public long getIdleTimeOut() {
        return idleTimeOut;
    }

    public void setIdleTimeOut(long idleTimeOut) {
        this.idleTimeOut = idleTimeOut;
    }

    @Override
    public String toString() {
        return "ThreadPoolConfig{" + "minPoolSize=" + minPoolSize + ", maxPoolSize=" + maxPoolSize + ", idleTimeOut=" + idleTimeOut + ", maxQueueSize=" + maxQueueSize + '}';
    }

    
}
