/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Alessio
 */
public class PathConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        if (type != null) {
            try {
                return Path.class.isAssignableFrom(type);
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Object fromString(String string) {
        if (string != null) {
            try {
                return Paths.get(string);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    public String toString(Object obj) {
        if (obj instanceof Path) {
            return Path.class.cast(obj).toAbsolutePath().toString();
        }
        return null;
    }

}
