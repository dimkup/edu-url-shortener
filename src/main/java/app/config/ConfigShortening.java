package app.config;

import java.net.URL;

/**
 * Shortening configuration provider interface
 */
public interface ConfigShortening {
    /**
     *
     * @return base part of the short URL
     */
    URL baseUrl();

    /**
     *
     * @return length of the hash part of the short URL
     */
    Integer hashLen();
}
