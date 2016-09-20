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
@XStreamAlias("PrintMode")
public enum PrintMode implements Serializable{
    NONE,ABSOLUTE,FILENAME;
}
