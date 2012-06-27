package org.dyndns.fzoli.http.trustmanager;

import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class StrictReloadableX509TrustManager extends ReloadableX509TrustManager {

  public StrictReloadableX509TrustManager(URL tspath, String password) throws Exception {
      super(tspath, password);
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
      getTrustManager().checkServerTrusted(chain, authType);
  }

}