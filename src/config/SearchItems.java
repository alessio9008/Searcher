package config;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.io.Serializable;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alessio
 */
@XStreamAlias("searchItems")
public class SearchItems implements Serializable{
    @XStreamAlias("searchItems")
    @XStreamImplicit
    protected List<SearchItem> searchItems;

    public SearchItems() {
    }

    public SearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
    
    

    @Override
    public String toString() {
        return "SearchItems{" + "searchItems=" + searchItems + '}';
    }
    
    
}
