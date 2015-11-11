How to use server:

* Install node for your local machine: https://nodejs.org/en/download/
* The installation should come with npm. If it does not, refer to https://www.npmjs.com/package/npm for more help
* Open a terminal and run 'cd path/to/the/project'
* Call `npm install`
* Call `ifconfig` on Linux/OSx and see your local address/inet address in the wlan network card (e.g. 192.168.... or 10.1....)
* Call `ipconfig` in Windows and see `IPv4 Address`
* Open `OnlineGame.java` and edit the `SERVER_ADRESS` variable with the one from the console
* Call `node server/server.js`
* You should have an running server in your terminal now

# TEAM TUCAN 


* Andrei: 07478343678
* Musta: 07510978119
* Mert: 07775429535
* Konstantin: 07845125471


# DEADLINES:
-

# Lectures/Tutorial #

* Monday: 9-12

* Tuesday: 11-2

* Wednesday Free 

* Thursday: 10-12
          1 - 2 (LGT)

* Friday: 3:40 - 4 (Group Tutorial with Valeri Katerinchuk)
       
# Practice Labs Week 10 (Database System) #

* Monday: 2 - 4 (Mert) , 4-6 (Tsveto, Musta)
* Tuesday: 9 - 11 (Konstantin)
* Wednesday: 
* Thursday: 
* Friday: 

# Practice Labs Week 11 (Object-Oriented Design)
* Monday: 3 - 4 (Mert and Tsveto), 4 - 5 (Konstantin), 5 - 6 (Musta)
* Tuesday: 
* Wednesday: 
* Thursday: 
* Friday: 

# MEET UPS #

* Monday 12:15-12:30 to 4:30-5
* Wednesday 12:00 to 4-5
* Thursday TBD
* Friday TBD

# Ideas after Minimum Viable Product is done 
* Validate Player Names 
* AI vs AI?

# One possible AI idea #
    
    AI extends Player.
    
    The AI parameters:
    * Stores its 6(*7?) crater choices in array.
    The AI algorithm:
    * in Crater class have a "public String viewFuture(ai)" if "ai" then use
      "makeMoveFromHere()" but don't make concrete changes; instead build a
      string of the result and return it.
      ** This should use a thread with no delay.
      ** Method name in Crater is debatable.
    * This would be a state.
    * Generate 6(*7?) states.
    * Pick the one that gives you the highest store.
      ** Keep in mind that if a state finishes a game and the ai store is already 
         higher then this is a preferred state.
    * Make the move for it for real.

    Harder AI
    - Keep track of the score for both players
    - Consider how to create extra turns for itself
    (assume extra turns give you a higher % chance of winning)
    - Consider creating steal opportunities for itself
    (assume steals give you an increase % chance of winning)
    - Consider opponents board and prevent extra turns or steals
    (same evaluation of board state used for the AI's side can be 
    used to determine the opposing board)
    (or both sides could be determined at the same time)

Feel free to edit the readme with any information