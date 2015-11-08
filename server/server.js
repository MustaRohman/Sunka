var app = require('express')()
var http = require('http').Server(app)
var io = require('socket.io')(http);
var INFO = "INFO"

io.on('connection', function(socket){
  Log(INFO, "User connected!")
  socket.on('disconnect', function(){
    Log(INFO, "User disconnected!")
  });
})

http.listen(3000, function(){
  Log(INFO, "Listening on port 3000")
})

function Log(type, message) {
  console.log('[' + type + '] ' + message )
}