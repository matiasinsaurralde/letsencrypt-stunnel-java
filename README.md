Let's Encrypt + stunnel
==

This is a step by step guide to setup a TCP socket server under stunnel (using Let's Encrypt).

# Requirements

- GNU/Linux
- stunnel
- NodeJS
- Java

## First step

Please follow the Let's Encrypt guide and run the right commands for your domain/FQDN:

https://letsencrypt.org/howitworks/

```
$ git clone https://github.com/letsencrypt/letsencrypt
$ cd letsencrypt
```

In this case we won't be using the Apache option in the Let's Encrypt Wizard, so just run something like after installing

```
./letsencrypt-auto certonly --standalone -d example.com -d www.example.com
```
In my case I did something like this (I'm using a single subdomain):
```
./letsencrypt-auto certonly --standalone -d ssltest.insaurral.de
```
