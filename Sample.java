import letsencrypt.LetsEncrypt;

import javax.net.ssl.SSLSocketFactory;

class Sample {

    public static void main( String[] args ) {

      try {
        SSLSocketFactory factory = LetsEncrypt.SocketFactory();
        // or...
        // SSLSocketFactory factory = LetsEncrypt.SocketFactory( "/tmp/mykeystore", "123456");
      } catch( Exception e ) {
        System.out.println( e );
      }
    }
}
