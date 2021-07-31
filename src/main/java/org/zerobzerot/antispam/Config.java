package org.zerobzerot.antispam;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Config POJO
 */
public class Config {

    public final URL url;

    public Config() {
        try {
            this.url = new URL("https://raw.githubusercontent.com/nothub/clientside-antispam-data/master/bots.json");
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Default URL is invalid!");
        }
    }

}
