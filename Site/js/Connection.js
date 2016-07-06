
function connectToWs(msgCallback){

    showMsg("Connecting to the server...");

    window.WebSocket = window.WebSocket || window.MozWebSocket;

    if (!window.WebSocket) {
        showAlert("This web browser doesn't support web socket connection.\nThe dashboard isn't available!");
        return;
    }
		
    var wsServer = "ws://telemetrywsserver-escorpioevo.rhcloud.com:8000";
    var connection = new WebSocket(wsServer);
    connection.binaryType = "arraybuffer";

    connection.onopen = function () {
        showMsg("Connection to the server successful!");
    };
	
    connection.onerror = function (error) {
        if (error.code == 1000)
            reason = "Normal closure, meaning that the purpose for which the connection was established has been fulfilled.";
        else if(error.code == 1001)
            reason = "An endpoint is \"going away\", such as a server going down or a browser having navigated away from a page.";
        else if(error.code == 1002)
            reason = "An endpoint is terminating the connection due to a protocol error";
        else if(error.code == 1003)
            reason = "An endpoint is terminating the connection because it has received a type of data it cannot accept (e.g., an endpoint that understands only text data MAY send this if it receives a binary message).";
        else if(error.code == 1004)
            reason = "Reserved. The specific meaning might be defined in the future.";
        else if(error.code == 1005)
            reason = "No status code was actually present.";
        else if(error.code == 1006)
           reason = "The connection was closed abnormally.";
        else if(error.code == 1007)
            reason = "An endpoint is terminating the connection because it has received data within a message that was not consistent with the type of the message (e.g., non-UTF-8 [http://tools.ietf.org/html/rfc3629] data within a text message).";
        else if(error.code == 1008)
            reason = "An endpoint is terminating the connection because it has received a message that \"violates its policy\". This reason is given either if there is no other sutible reason, or if there is a need to hide specific details about the policy.";
        else if(error.code == 1009)
           reason = "An endpoint is terminating the connection because it has received a message that is too big for it to process.";
        else if(error.code == 1010) // Note that this status code is not used by the server, because it can fail the WebSocket handshake instead.
            reason = "An endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn't return them in the response message of the WebSocket handshake. <br /> Specifically, the extensions that are needed are: " + event.reason;
        else if(error.code == 1011)
            reason = "A server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.";
        else if(error.code == 1015)
            reason = "The connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can't be verified).";
        else
            reason = "Unknown reason";

        showAlert("Error [" + error.code + "]: " + reason);
        connection.close();
    };
	
    connection.onmessage = msgCallback;

    
    connection.onclose = function(){
        showAlert("Connection to the server failed! Trying to reconnect...");
        setTimeout(connectToWs, 5000, msgCallback);
    };
    /*
	
    setTimeout(function() {
        if (connection.readyState !== 1) {
            //setTimeout(connectToWs, 5000, msgCallback);
        }
    }, 10000);
    */
}