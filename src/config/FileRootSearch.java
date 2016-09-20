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
@XStreamAlias("fileRoot")
public class FileRootSearch extends SearchItem {

    public FileRootSearch() {
    }

    public FileRootSearch(Path path, SearchConfig searchText, PrintMode printMode) {
        super(path, searchText, printMode);
    }

}
