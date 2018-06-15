package app.services.shortening;

import app.services.shortening.exceptions.UncheckedNoSuchAlgorithmException;
import io.seruco.encoding.base62.Base62;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Generates a hash from the URL
 */
public class Hasher {
    private final int hashLength;
    private final Random random;
    private final Base62 base62;
    private final ThreadLocal<MessageDigest> md;

    /**
     * Hasher constructor
     * @param hashLength desired length of the hash string
     */
    public Hasher(int hashLength) {
        this.hashLength = hashLength;
        if (hashLength<1||hashLength>20) throw new IllegalArgumentException("Hash length must be less than 31 characters");

        this.md = ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new UncheckedNoSuchAlgorithmException("MD5 is not available",e);
            }
        });

        this.md.get(); //Throw exception if MD5 is not available

        this.random = new Random();
        this.base62 = Base62.createInstance();
    }

    /**
     * Generates a hash from the URL
     * @param url - URL to hash
     * @return hash string
     */
    public String hashUrl(URL url) {
        byte[] b = (url+generateSalt()).getBytes();
        b = md.get().digest(b);

        return new String(base62.encode(b)).substring(0,hashLength);
    }

    /**
     * Random salt generator
     * @return random string of characters
     */
    private String generateSalt() {
        return Integer.toHexString(random.nextInt());
    }
}
