var ipAddress = process.env.OPENSHIFT_NODEJS_IP || "127.0.0.1";
var webServerPort = process.env.OPENSHIFT_NODEJS_PORT || process.env.PORT || 8080;

// Require HTTP module (to start server) and Socket.IO
var http = require('http'), io = require('socket.io');
var clients = [];

// Start the server
var server = http.createServer(function(req, res){ 
	// Send HTML headers and message
	
	clients = clients.filter(function(socket){return socket.connected;});
	
	res.writeHead(200,{ 'Content-Type': 'text/html' }); 
	
		res.write('<meta http-equiv="refresh" content="5" />');
		res.write('<h1>Hello Team ZeroC - Online Telemetry Socket Manager!</h1>');
		res.write('Clients connected: ' + clients.length + '<br>');
		for(var i = 0; i < clients.length; i++){
			res.write(i + " \t" + clients[i].request.connection.remoteAddress + "\t" + clients[i].connected + "<br>");
		}
	
	res.end('');
});

//server.listen(webServerPort, ipAddress, function() {});
server.listen(webServerPort);


// Create a Socket.IO instance, passing it our server
var socket = io.listen(server);

// Add a connect listener
socket.on('connection', function(client){ 
	
	//Filter connection
	clients.push(client);
	clients = clients.filter(function(socket){return socket.connected;});
	
	//console.log('Client connected');
	console.log('Connected clients: ' +  clients.length);
	client.emit('msg', 'Connection successfull');

	// Success!  Now listen to messages to be received
	client.on('escorpio',function(event){ 
		//console.log('Received message from escorpio!',event);
		//client.broadcast.emit('msg', 'Phone sent data');
		client.broadcast.emit('data', event);
	});
	
	client.on('disconnect',function(){
		console.log('Client has disconnected');
	});
	
	//setInterval(function(){client.emit('msg','Hey are you still there?'); }, 3000);

});
