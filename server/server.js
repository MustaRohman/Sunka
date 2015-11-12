  var app = require('express')()
  var randomstring = require("randomstring")
  var http = require('http').Server(app)
  var io = require('socket.io')(http)
  var INFO = "INFO", ERROR = "ERROR", REC = "RECEIVED", BRD = "BROADCAST"
  var serverList = ['opposite']
  var games = {}
  var matchID = []
  var player1, player2

  io.on('connection', function(socket){
    Log(INFO, "User connected!")
    socket.on('req', function(msg){
      Log(REC, "Request received, code: " + msg)
      parseRequest(msg)
    })
    socket.on('game', function(msg){
      Log("GAME", "Request received, code: " + msg)
      var parserWrap = parseGameMove(msg)
      io.emit(otherPlayer(parserWrap[1]), parserWrap)
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
        Log(INFO, "Received request to create server with name: " + serverList[serverList.length - 1])
        break
      case request.charAt(0) === "g":
        var id = createMatchId()
        parsePlayers(request.substring(1, request.length), id)
        addMatchID(id)
        io.emit('gameStart', player1 + ":" + player2 + ":" + id)
        Log(BRD, "Game started. Players: " + player1+":"+player2 + " id:" + id)
    }
  }

  function addServer(server){
    var x = 0
    for ( index in serverList )
      if (serverList[index] === server)
        x = 1
    if (x === 0) serverList.push(server)
  }

  function removeServer(server){
    for ( index in serverList )
      if ( serverList[index] === server )
        serverList.splice(index, index)
  }

  function createMatchId(){
    return randomstring.generate({
      length: 4,
      charset: 'alphabetic'
    })
  }

  function addMatchID(id){
    var x = 0
    for ( index in matchID)
      if (matchID[index] === id)
        x = 1
    if ( x === 0 ) matchID.push(id)
  }

  function parseGameMove(move){
    var x=0, player = ""
    var gameID =""
    while (move[x] != ":"){
      gameID += move[x++]
    }
    x++
    while ( move[x] != ":")
      player += move[x++]
    var pos = move[++x]
    return [gameID, player, pos]
    Log("GAME", "Received move: " + gameID + " " + player + " " + pos)
  }

  function otherPlayer (player){

  }

  function parsePlayers( playerList, id ) {
    player1 = "", player2 = ""
    var index
    for ( x in playerList ){
      if (playerList[x] === ":"){
        player1 = playerList.substring(0, x)
        index = x
        break
      }
    }
    while (playerList[index] != "-")
      player2 += playerList[index++]
    player2 = player2.substring(1, player2.length )
    games[id] = [player1, player2]
    Log(INFO, "Found game between " + games[id])
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
