  var app = require('express')()
  var http = require('http').Server(app)
  var io = require('socket.io')(http);
  var INFO = "INFO", ERROR = "ERROR", REC = "RECEIVED", BRD = "BROADCAST"
  var serverList = ['server1', 'server2']

  io.on('connection', function(socket){
    Log(INFO, "User connected!")
    socket.on('req', function(msg){
      Log(REC, "Request received, code: " + msg)
      parseRequest(msg);
    })
    socket.on('disconnect', function(){
      Log(INFO, "User disconnected!")
    })
  })

  http.listen(3000, function(){
    Log(INFO, "Listening on port 3000")
  })

  function parseRequest(request) {
    switch (request) {
      case "getServers":
        var message = parseServerList()
        io.emit('serverList', message)
        Log(BRD, "Sent broadcast to socket req, broadcast message: " + message)
        break
    }
  }

  function parseServerList() {
    var message = ""
    for ( x in serverList ) {
      message += serverList[x] + ":"
    }
    return message.substring(0, message.length - 1) + "-"
  }

  function Log(type, message) {
    console.log('[' + type + '] ' + message )
  }