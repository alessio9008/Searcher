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
@XStreamAlias("nodeSearchConfig")
public class SearchConfig implements Serializable{
    @XStreamAlias("regex")
    @XStreamAsAttribute
    protected boolean regex;
    @XStreamAlias("caseSensitive")
    @XStreamAsAttribute
    protected boolean caseSensitive;
    @XStreamAlias("toSearch")
    protected String toSearch;

    public SearchConfig(boolean regex, boolean caseSensitive, String toSearch) {
        this.regex = regex;
        this.caseSensitive = caseSensitive;
        this.toSearch = toSearch;
    }

    public SearchConfig() {
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public String getToSearch() {
        return toSearch;
    }

    public void setToSearch(String toSearch) {
        this.toSearch = toSearch;
    }

    @Override
    public String toString() {
        return "SearchConfig{" + "regex=" + regex + ", caseSensitive=" + caseSensitive + ", toSearch=" + toSearch + '}';
    }

    
    
    
}
