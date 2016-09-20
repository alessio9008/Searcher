/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.nio.file.Path;

/**
 *
 * @author alessio
 */
@XStreamAlias("directoryRoot")
public class DirectoryRootSearch extends SearchItem {

    @XStreamAlias("searchFileName")
    protected SearchConfig searchFileName;
    @XStreamAlias("lastModifiedTimeIterval")
    protected TimeIntervall lastModifiedTimeIterval;
    @XStreamAlias("creationTimeIterval")
    protected TimeIntervall creationTimeIterval;

    public DirectoryRootSearch() {
    }

    public DirectoryRootSearch(Path path, SearchConfig searchText, TimeIntervall lastModifiedTimeIterval,SearchConfig searchFileName,PrintMode printMode,TimeIntervall creationTimeIterval) {
        super(path, searchText,printMode);
        this.lastModifiedTimeIterval=lastModifiedTimeIterval;
        this.creationTimeIterval=creationTimeIterval;
        this.searchFileName=searchFileName;
    }

    public TimeIntervall getCreationTimeIterval() {
        return creationTimeIterval;
    }

    public void setCreationTimeIterval(TimeIntervall creationTimeIterval) {
        this.creationTimeIterval = creationTimeIterval;
    }
    
    

    public TimeIntervall getLastModifiedTimeIterval() {
        return lastModifiedTimeIterval;
    }

    public void setLastModifiedTimeIterval(TimeIntervall lastModifiedTimeIterval) {
        this.lastModifiedTimeIterval = lastModifiedTimeIterval;
    }

    public SearchConfig getSearchFileName() {
        return searchFileName;
    }

    public void setSearchFileName(SearchConfig searchFileName) {
        this.searchFileName = searchFileName;
    }

    @Override
    public String toString() {
        return "DirectoryRootSearch{" + "searchFileName=" + searchFileName + ", lastModifiedTimeIterval=" + lastModifiedTimeIterval + ", creationTimeIterval=" + creationTimeIterval + '}';
    }

   
    

}
