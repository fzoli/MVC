package org.dyndns.fzoli.http;

import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.X509HostnameVerifier;
/**
 * HTTPS kapcsolathoz tanusítvány kezelő osztály.
 * @author Farkas Zoltán
 */
public abstract class HttpClientWrapper {

    /**
     * Minden hostnevet megengedő ellenörző objektum.
     * Hasznos ha a tanusítvány nem a szerver hostnevét tartalmazza vagy ha
     * több hostról is elérhető a szerver (pl. teszteléskor)
     * A leörökölt osztályokban használható fel a wrapHttpClient metódus implementálásakor.
     */
    protected static X509HostnameVerifier hv = new X509HostnameVerifier() {

        @Override
        public void verify(String string, SSLSocket ssls) throws IOException {
            ;
        }

        @Override
        public void verify(String string, X509Certificate xc) throws SSLException {
            ;
        }

        @Override
        public void verify(String string, String[] strings, String[] strings1) throws SSLException {
            ;
        }

        @Override
        public boolean verify(String string, SSLSession ssls) {
            return true;
        }
    };
    /**
     * Minden tanusítványt elfogadó ellenörző objektum.
     * Ha nincs saját cacert fájl generálva,
     * viszont nincs érvényes tanusítványunk (pl. önaláírt tanusítvány),
     * akkor megtehetjük, hogy minden tanusítványt elfogadunk kivétel nélkül.
     * A leörökölt osztályokban használható fel a wrapHttpClient metódus implementálásakor.
     */
    protected static X509TrustManager tm = new X509TrustManager() {
        
        @Override
	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            ;
        }
        
        @Override
	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
            ;
	}

        @Override
	public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
	}
        
    };

    /**
     * Az apache által írt HttpClient, amit beállít az osztály, ha kérik.
     */
    protected final HttpClient HTTP_CLIENT;
    
    public HttpClientWrapper(HttpClient httpClient) {
        HTTP_CLIENT = httpClient;
    }
    
    /**
     * Minden hostnév és tanusítvány elfogadása.
     */
    public void wrapHttpClient() {
        try {
            wrapHttpClient(null);
        }
        catch(Exception ex) {
            ;
        }
    }
    
    /**
     * Minden hostnév elfogadása, de csak a megadott tanusítvány tárolóban lévő
     * tanusítványok elfogadása.
     * @param cacertPath a tanusítványt tároló fájl elérési útvonala
     * @throws Exception ha bármi hiba történik (pl. az URL null)
     */
    public void wrapHttpClient(URL cacertPath) throws Exception {
        wrapHttpClient(cacertPath, null);
    }
    
    /**
     * Minden hostnév elfogadása, de csak a megadott tanusítvány tárolóban lévő
     * tanusítványok elfogadása.
     * Ha a jelszó nem null, akkor ellenőrzi a tanusítvány jelszavát és kivételt dob,
     * ha nem egyezik a megadott jelszó a tároló jelszavával.
     * @param cacertPath a tanusítványt tároló fájl elérési útvonala
     * @param cacertPassword a tanusítvány tároló jelszava
     * @throws Exception ha bármi hiba történik<br>(pl. az URL null vagy hibás a jelszó)
     */
    public void wrapHttpClient(URL cacertPath, String cacertPassword) throws Exception {
        wrapHttpClient(false, cacertPath, cacertPassword);
    }
    
    /**
     * A megadott tanusítvány tárolóban lévő tanusítványok elfogadása és opcionális hostnév ellenőrzés.
     * Ha a jelszó nem null, akkor ellenőrzi a tanusítvány jelszavát és kivételt dob,
     * ha nem egyezik a megadott jelszó a tároló jelszavával.
     * @param verifyHostname ha igaz, akkor ellenőrzi a hostnevet is
     * @param cacertPath a tanusítványt tároló fájl elérési útvonala
     * @param cacertPassword a tanusítvány tároló jelszava.
     * @throws Exception ha bármi hiba történik<br>(pl. az URL null vagy hibás a jelszó)
     */
    public abstract void wrapHttpClient(boolean verifyHostname, URL cacertPath, String cacertPassword) throws Exception;
    
    /**
     * HttpsURLConnection SSLSocketFactory beállítása.
     * Hogy ne csak a HttpClient objektumra alkalmazódjon a beállítás.
     * A leörökölt osztályokban használható fel a wrapHttpClient metódus implementálásakor.
     */
    protected static void wrapHttpsUrlConnection(SSLContext context, boolean verifyHostname) {
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        if (!verifyHostname) HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    
}