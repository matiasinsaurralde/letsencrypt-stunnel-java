import letsencrypt.LetsEncrypt;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class SampleClient {

  static SSLSocketFactory factory;
  static SSLSocket socket;

  public static void main( String[] args ) {

    try {
      factory = LetsEncrypt.SocketFactory();
      socket = (SSLSocket) factory.createSocket( "ssltest.gdgasuncion.org", 9091);

      PrintWriter out = new PrintWriter( socket.getOutputStream(), true);

      BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ));
      BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));

      String userInput;

      while ((userInput = stdIn.readLine()) != null) {
        out.println(userInput);
        System.out.println("Receiving: " + in.readLine());
      };
    } catch( Exception e ) {
      System.out.println( e );
    }
  }
}
