# Lorenzo il Magnifico

This is the source code for our Software Engineering project, a videogame adaptation of the board game **Lorenzo il Magnifico**.

### Configuration
The configuration of the game is stored in **settings.json**:

 - the **server** section is comprised of
   - socket: sever socket connection config
   - rmi: server rmi connection config


 - the **client** section is comprised of 
   - mode: specifies the type of connection
     - *ask* → the player will be prompted about this choice after starting the client
     - *socket*
     - *rmi*
   - interface: specifies the interaction method
     - *cli*
     - *gui*
   - socket: client socket connection config
   - rmi: client rmi connection config
     
     
- the **game** section is read sever-side during the initial loading, and is comprised of
   - rules:
     - *ask* → the first player to join a game will be prompted about this choice
     - *basic*
     - *advanced*
   - timeout: specifies the maximum duration of a player's turn  in seconds
   

### How to play
To play one must at first run **ServerImpl**, then run the desired number of **Client**.
The Client will be able to choose:
 1. Name
 1. Colour
 1. Type of connection (only if the parameter **mode** in settings.json is set to ask)
 
A connection will be established and the player will be able to choose which game to join, if more then one is one available
If the player is the first to join a game,  he will be able to choose:
 1. Number of players
 1. Type of rules (*basic* or *advanced*)
