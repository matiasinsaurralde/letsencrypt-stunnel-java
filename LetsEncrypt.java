import java.io.InputStream;
import java.io.FileInputStream;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;

import java.security.KeyStore;

class LetsEncrypt {
  public static SSLSocketFactory SocketFactory() throws Exception {

    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    InputStream readStream = new FileInputStream( "keystore" );
    keystore.load( readStream, "".toCharArray() );

    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keystore);

    SSLSocketFactory factory = null;
    SSLContext context = SSLContext.getInstance( "TLS" );

    context.init( null, tmf.getTrustManagers(), null);
    factory = context.getSocketFactory();


    return factory;
  }

  public static void main( String[] args ) {
    try {
      SSLSocketFactory factory = LetsEncrypt.SocketFactory();
    } catch( Exception e ) {
      System.out.println( e );
    }
  }
}
