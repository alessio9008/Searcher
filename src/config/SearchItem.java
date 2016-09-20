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
import java.io.Serializable;
import java.nio.file.Path;

/**
 *
 * @author alessio
 */
@XStreamAlias("searchItem")
public abstract class SearchItem implements Serializable {
    @XStreamAlias("rootPath")
    @XStreamAsAttribute
    @XStreamConverter(PathConverter.class)
    protected Path path;
    @XStreamAlias("searchText")
    protected SearchConfig searchText;
    @XStreamAlias("printMode")
    @XStreamAsAttribute
    protected PrintMode printMode;
    

    public SearchItem() {
    }

    public SearchItem(Path path, SearchConfig searchText,PrintMode printMode) {
        this.path = path;
        this.searchText = searchText;
        this.printMode=printMode;
    }

    public PrintMode getPrintMode() {
        if(printMode==null){
            return PrintMode.NONE;
        }
        return printMode;
    }

    public void setPrintMode(PrintMode printMode) {
        this.printMode = printMode;
    }
    

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public SearchConfig getSearchText() {
        return searchText;
    }

    public void setSearchText(SearchConfig searchText) {
        this.searchText = searchText;
    }

    @Override
    public String toString() {
        return "SearchItem{" + "path=" + path + ", searchText=" + searchText + ", printMode=" + printMode + '}';
    }
   
}
