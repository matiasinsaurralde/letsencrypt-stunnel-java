Let's Encrypt + stunnel
==

This is a step by step guide to setup a TCP socket server under stunnel (using Let's Encrypt).

# Requirements

- GNU/Linux
- stunnel
- NodeJS
- Java

## First step

Clone this repository first:

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

In this case we won't be using the Apache option in the Let's Encrypt Wizard, so just run something like after installing

```
% ./letsencrypt-auto certonly --standalone -d example.com -d www.example.com
```
In my case I did something like this (I'm using a single subdomain):
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
