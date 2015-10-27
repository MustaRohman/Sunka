# TEAM TUCAN 


* Andrei: 07478343678
* Musta: 07510978119
* Mert: 07775429535
* Konstantin: 07845125471

# Availability During Reading Week?
* Monday - Musta, Mert
* Tuesday - Mert, Musta
* Wednesday - Mert, Musta
* Thursday - Musta
* Friday - Mert

# DEADLINES:
-

# Lectures #

* Monday: 9-12

* Tuesday: 11-2

* Wednesday Free 

* Thursday: 10-12
          1 - 2 (LGT)

* Friday: 9-12
       
# Practice Labs #

* Monday: Free
* Tuesday: 2 - 4  ( Mert & Musta & Konstantin )
* Wednesday: 9 - 11 ( Tsveto ) 
* Thursday: 2 - 4 ( Andrei ) 
* Thursday: 4 - 6 ( Konstantin )
* Friday: 1 -3 ( Tsveto & Merto ) 3 - 5 ( Andrei & Musta ) 


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
    * Stores its 6 crater choices in array.
    The AI algorithm:
    * in Crater class have a "public String viewFuture(ai)" if "ai" then use
      "makeMoveFromHere()" but don't make concrete changes; instead build a
      string of the result and return it.
      ** This should use a thread with no delay.
      ** Method name in Crater is debatable.
    * This would be a state.
    * Generate 6 states.
    * Pick the one that gives you the highest store.
      ** Keep in mind that if a state finishes a game and the ai store is already 
         higher then this is a preferred state.
    * Make the move for it for real.

Feel free to edit the readme with any information