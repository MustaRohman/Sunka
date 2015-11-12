  var app = require('express')()
  var http = require('http').Server(app)
  var io = require('socket.io')(http);
  var INFO = "INFO", ERROR = "ERROR", REC = "RECEIVED", BRD = "BROADCAST"
  var serverList = ['opponent1', 'opponent2']
  var matches = [];

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
    switch (true) {
      case request === "getServers":
        var message = parseServerList()
        io.emit('serverList', message)
        Log(BRD, "Sent broadcast to socket req, broadcast message: " + message)
        break
      case request.charAt(0) === "c":
        var server_name = request.substring(1,request.length)
        addServer(server_name)
        Log(BRD, "Received request to create server with name: " + serverList[serverList.length - 1])
        break
      case request.charAt(0) === "g":
        parsePlayers(request.substring(1, request.length))
        Log(BRD, "Game found!")
    }
  }

  function addServer(server){
    var x = 0;
    for ( index in serverList ){
      if (serverList[index] === server)
        x = 1;
    }
    if (x == 0) serverList[serverList.length] = server;
  }

  function removeServer(server){
    for ( index in serverList ){
      if ( serverList[index] === server )
        serverList.splice(index, index)
    }
  }

  function parsePlayers( playerList ) {
    for ( x in playerList ){
      if (playerList[x] === ":"){
        var index = matches.length
        Log(INFO, index + " games running currently")
        matches[index][0] = playerList.substring(0, x-1)
        matches[index][1] = playerList.substring(x+1, playerList.length - 1)
        Log(BRD, "Matched players " + matches[index][0] + " and " + matches[index][1])
        break;
      }
    }
  }

  function parseServerList() {
    var message = "" + serverList.length
    for ( x in serverList ) {
      message += serverList[x] + ":"
    }
    return message.substring(0, message.length - 1) + "-"
  }

  function Log(type, message) {
    console.log('[' + type + '] ' + message )
  }