package letsencrypt;

import java.io.InputStream;
import java.io.FileInputStream;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;

import java.security.KeyStore;

public class LetsEncrypt {
  public static SSLSocketFactory SocketFactory( String keyStorePath, String keyStorePassword ) throws Exception {

    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    InputStream readStream = new FileInputStream( keyStorePath );
    keystore.load( readStream, keyStorePassword.toCharArray() );

    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keystore);

    SSLSocketFactory factory = null;
    SSLContext context = SSLContext.getInstance( "TLS" );

    context.init( null, tmf.getTrustManagers(), null);
    factory = context.getSocketFactory();


    return factory;
  }

  public static SSLSocketFactory SocketFactory() throws Exception {
    String keyStorePath = System.getProperty( "javax.net.ssl.keyStore" );
    String keyStorePassword = System.getProperty( "javax.net.ssl.keyStorePassword" );

    return SocketFactory( keyStorePath, keyStorePassword );
  }

}
