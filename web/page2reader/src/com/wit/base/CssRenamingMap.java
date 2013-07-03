package com.wit.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.template.soy.shared.SoyCssRenamingMap;

public class CssRenamingMap implements SoyCssRenamingMap {

    private Properties properties = new Properties();
    
    public CssRenamingMap(String filename) throws IOException {
        File file = new File(filename);
        InputStream in = new FileInputStream(file);
        if (in != null) {
            properties.load(in);
        }
        in.close();
    }
    
    @Override
    public String get(String key) {
        assert key != null && !key.isEmpty();
        String[] keys = key.split("-");
        for(int i = 0; i < keys.length; i++){
            keys[i] = properties.getProperty(keys[i]) == null ? keys[i] :
                    properties.getProperty(keys[i]);
        }
        String out = keys[0];
        for(int i = 1; i < keys.length; i++){
            out += "-" + keys[i];
        }
        
        return out;
    }
}
