
function connectToWs(msgCallback){

    showMsg('Connecting to the server...');

   // Create SocketIO instance, connect
	var socket = io.connect('http://escorpiotelemetryws-67106.onmodulus.net/');//io.connect('http://telemetrywsserver-escorpioevo.rhcloud.com');

	// Add a connect listener
	socket.on('connect',function() {
		console.log('Client has connected to the server!');
		showMsg('Connection to the server successful!');
	});
	
	// Add a disconnect listener
	socket.on('disconnect',function() {
		console.log('The client has disconnected!');
		showAlert('Disconnected from the server!');
	});
	
	// Add a reconnect listener
	socket.on('reconnecting',function() {
		console.log('The client is reconnection!');
		showMsg('Trying to reconnect to the server...');
	});
	
	// Add a reconnect listener
	socket.on('error',function() {
		console.log('Connection error!');
		showMsg('Connection error!');
	});
	
	
	// Add a data listener
	socket.on('data',function(data) {
		console.log('Received data:',data);
		msgCallback(data);
	});
	
	// Add msg listener
	socket.on('msg',function(data) {
		console.log('Received a message:',data);
	});
	
	// Sends a message to the server via sockets
	function sendMessageToServer(message) {
		socket.send(message);
	}
}