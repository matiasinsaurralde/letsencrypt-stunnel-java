Let's Encrypt + stunnel + Java
==
This is a step by step guide to setup a TCP socket server under stunnel (using Let's Encrypt).

I started this experiment because someone requested my help on a Java application SSL setup. This application used a standard TCP socket and instead of natively using ```SSLServerSocket``` I wanted to try out two cool projects: [stunnel](https://www.stunnel.org/index.html) & [Let's Encrypt](https://letsencrypt.org/).

I prepared a quick class for wrapping the standard SSLSocketFactory and providing an easier way of loading a custom ```KeyStore``` that holds the trusted Let's Encrypt certificates.

***Disclaimer:*** I think there's already [some kind of library for quickly integrating Let's Encrypt with Java](https://github.com/shred/acme4j) and you may find it useful, however I'm not a Java developer myself ~~and I'm not sure about how to use Maven at all lol~~.

# Requirements

- GNU/Linux
- stunnel
- NodeJS
- Java
- Laziness

## First step

Clone this repository first :)

```
% cd ~
% git clone URL
```

Please follow the Let's Encrypt guide and run the right commands for your domain/FQDN:

https://letsencrypt.org/howitworks/

```
% cd ~
% git clone https://github.com/letsencrypt/letsencrypt
% cd letsencrypt
```

In this case we won't be using the Apache option provided by the Let's Encrypt wizard, so just run something like this:

```
% ./letsencrypt-auto certonly --standalone -d example.com -d www.example.com
```
In my case I did (I'm using a single subdomain):
```
% ./letsencrypt-auto certonly --standalone -d ssltest.insaurral.de
```

After requesting our e-mail it will print the certificate path and quit.

```
% find /etc/letsencrypt/live/
/etc/letsencrypt/live/
/etc/letsencrypt/live/ssltest.insaurral.de
/etc/letsencrypt/live/ssltest.insaurral.de/chain.pem
/etc/letsencrypt/live/ssltest.insaurral.de/cert.pem
/etc/letsencrypt/live/ssltest.insaurral.de/fullchain.pem
/etc/letsencrypt/live/ssltest.insaurral.de/privkey.pem
```

## Preparing our echo/test server

I prepared a small NodeJS program for this:

```
var net = require('net'),
    server = net.createServer( function( socket ) {
      socket.pipe( socket )
    }).listen( 9090 )
```

This program will listen for incoming connections on TCP port 9090 and we should expect it to reply with an echo of whatever we send. We send "hello", it should reply "hello", etc.

It would be good to keep it running with the following command:

```
% cd ~/letsencrypt-experiment
% node SampleEchoServer.js
```

Note that in some distros you will find NodeJS as ```nodejs```, in that case:

```
% nodejs SampleEchoServer.js
```

## Setting up stunnel

```Stunnel is a proxy designed to add TLS encryption functionality to existing clients and servers without any changes in the programs' code``` or "SSL for lazy people" :P

Install it:

```
% apt-get install stunnel -y
```

stunnel has been installed. We need to create a configuration file inside the stunnel configuration directory (usually ```/etc/stunnel```), the path could be: ```/etc/stunnel/echo.conf``` or ```/etc/stunnel/something.conf```.

stunnel will load all the available *.conf files.

The important stuff goes inside that file (thank you [crow](https://community.letsencrypt.org/t/configure-stunnel/3611)):

```
accept=0.0.0.0:9091
connect=127.0.0.1:9090
ciphers=ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-SHA384:ECDHE-RSA-AES256-SHA:EC
DHE-RSA-AES128-SHA256:ECDHE-RSA-AES128-SHA:AES256-GCM-SHA384:AES128-GCM-SHA256:AES256-SHA256:AES128-SHA256:AES1
28-SHA:DES-CBC3-SHA
options=CIPHER_SERVER_PREFERENCE
```

We are telling stunnel to listen on all the available interfaces, port 9091.

And that we are expecting to forward those connections to 127.0.0.1, port 9090 (this is the actual echo server that speaks unencrypted stuff), using the [specified ciphers](https://github.com/letsencrypt/letsencrypt/blob/74b2e3bc515b5f7e805883a26f1b0e47ed686098/letsencrypt-nginx/letsencrypt_nginx/options-ssl-nginx.conf#L8).

In my installation I wasn't able to get stunnel running just after the previous steps.
I needed to change the ```ENABLED``` in ```/etc/default/stunnel4```:

```
# /etc/default/stunnel
# Julien LEMOINE <speedblue@debian.org>
# September 2003

# Change to one to enable stunnel automatic startup
ENABLED=1
FILES="/etc/stunnel/*.conf"
OPTIONS=""

# Change to one to enable ppp restart scripts
PPP_RESTART=0
```
And finally:

```
% service stunnel4 start
Starting SSL tunnels: [Started: /etc/stunnel/echo.conf] stunnel.
```

## Checking the server

OpenSSL provides a way of checking the certificates associated with a specific server, something like:

```
openssl s_client -connect example.com:9091 -showcerts
```

In my case:

```
openssl s_client -connect ssltest.insaurral.de:9091 -showcerts
```

The output should include ~~very cool alphanumeric stuff~~ the certificates and information about the issuer, you should read ***Let's Encrypt*** somewhere around the ```Server certificate``` section.

## Preparing our Java client

At this point we have a funny, SSL-speaking, echo server that is being exposed through all your interfaces (maybe the Internet?). I'll assume that your Java environment is ready.

The final idea is to prepare a Java client that will connect and send some "Hello world" to our echo server. There's a quick & dirty Java client inside this repo, and you should find it in the root directory:

```
% cd ~
% cd dir
% javac SampleEchoClient.java
```

We are ready to run the echo client, but Java doesn't know about the Let's Encrypt certificates. The ```keytool``` can generate a keystore for us, please follow the wizard and choose a password:

```
% cd ~    # just in case
% keytool -keystore samplekeystore -genkey
```

Let's fetch and store the Let's Encrypt certificates in this keystore:

```
% wget https://letsencrypt.org/certs/isrgrootx1.pem
% wget https://letsencrypt.org/certs/letsencryptauthorityx1.der
% keytool -keystore samplekeystore -trustcacerts -importcert -alias isrgrootx1 -file isrgrootx1.pem
% keytool -keystore samplekeystore -trustcacerts -importcert -alias letsencryptauthorityx1 -file letsencryptauthorityx1.der
```

The ```keytool``` will ask for the keystore password and a confirmation. I used ```123456```.

Let's try to run the Java program, and override (or specify the new keystore). ```javax.net.ssl.keyStore``` indicates the path of our new keystore, and ```javax.net.ssl.keyStorePassword``` indicates the previously set keystore password.

```
% java -Djavax.net.ssl.keyStore=samplekeystore -Djavax.net.ssl.keyStorePassword=123456 SampleEchoClient
```

If everything is ok, the echo should work:

```
% java -Djavax.net.ssl.keyStore=samplekeystore -Djavax.net.ssl.keyStorePassword=123456 SampleEchoClient
hello
Receiving: hello
bye
Receiving: bye
```
