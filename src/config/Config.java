/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import java.io.Serializable;

/**
 *
 * @author alessio
 */
@XStreamAlias("config")
public class Config implements Serializable {

    @XStreamAlias("archiveScan")
    @XStreamAsAttribute
    protected boolean archiveScan;
    @XStreamAlias("poolconfig")
    protected ThreadPoolConfig poolConfig;
    @XStreamAlias("ioconfig")
    protected IOConfig ioconfig;
    @XStreamAlias("searchItems")
    protected SearchItems searchItems;

    

    public Config() {
    }

    public Config(boolean archiveScan, ThreadPoolConfig poolConfig, IOConfig ioconfig, SearchItems searchItems) {
        this.archiveScan = archiveScan;
        this.poolConfig = poolConfig;
        this.ioconfig = ioconfig;
        this.searchItems = searchItems;
    }

	public boolean isArchiveScan() {
		return archiveScan;
	}

	public void setArchiveScan(boolean archiveScan) {
		this.archiveScan = archiveScan;
	}

	public ThreadPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(ThreadPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public IOConfig getIoconfig() {
        return ioconfig;
    }

    public void setIoconfig(IOConfig ioconfig) {
        this.ioconfig = ioconfig;
    }

    public SearchItems getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(SearchItems searchItems) {
        this.searchItems = searchItems;
    }

	@Override
	public String toString() {
		return "Config [archiveScan=" + archiveScan + ", poolConfig=" + poolConfig + ", ioconfig=" + ioconfig + ", searchItems=" + searchItems + "]";
	}

}
