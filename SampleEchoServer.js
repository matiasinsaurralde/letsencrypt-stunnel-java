var net = require('net'),
    server = net.createServer( function( socket ) {
      socket.pipe( socket )
    }).listen( 9090 )
