# Lorenzo il Magnifico

**by Mattia Rizzolo, Luca Pierri, Alessandro Vannoni**

This is the source code for our Software Engineering project, a videogame adaptation of the board
game **Lorenzo il Magnifico**.

The UML Class Diagram is located in the root directory of the project.

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
   
The other configuration files loaded whenever a game starts are:
 - **cards.json**: contains the development cards and their properties
 - **bonusTile.json**: contains the bonus Tiles and their bonuses
 - **council.json**: contains the available council privileges
 - **excommunication.json**: contains the excommunications
 - **faithTrack.json**: contains the rewards given to the player when supporting the Church, depending on the position
 on the faith
 points track
 - **market.json**: contains the bonuses given by the market booths
 - **towers.json**: contains the floor bonuses given when claiming a floor
 - **cardsModel.json**: contains a general model for the elements in cards.json (kept for reference purposes, but never
 loaded)
 

### How to play
To play one must at first run **ServerImpl**, then run the desired number of **Client**.
The Client will be able to choose:
 1. Name
 1. Colour (note that this parameter is case sensitive)
 1. Type of connection (*socket* or *rmi*, only if the parameter **mode** in settings.json is set to *ask*)
 
A connection will be established and the player will be able to choose which game to join, if more then one available.
If the player is the first to join a game,  he will be able to choose:
 1. Number of players
 1. Type of rules (*basic* or *advanced*, only if the parameter **rules** in settings.json is set to *ask*)

The **command line interface** will show a console and, when needed, a textual version of the board in a separate window,
through the use of the Lanterna library.
To proceed, one must close the Lanterna window and choose the action that he wants to perform; he will be asked whether 
he wants to increase the value of the chosen Family Member or not.

It is to note that, when choosing a card during a Floor action, the player must input the name in **Italian**.

Before finishing his turn, the player will be able to confirm or discard his actions. In case of confirmation, the
queued actions are performed and the turn ends; in case of rejection the changes will be ignored and the player is able
to start the turn over.

If the player does not confirm its actions before the timeout, he will be kicked out of the game and the other
participants will be able to continue playing; the player's subsequent turns will be automatically skipped.

The **graphical user interface** will show a window containing the board and various information relevant to the player.
The text area in the bottom left corner acts like the console in the CLI while the text field is only used when the 
player inserts his name or if he wants to write the name of the card he chooses, instead of double clicking on it.
The rules explained earlier regarding the CLI apply here as well.

The actions are performed through the buttons appearing as needed under the text area. If the user wants to see the 
bonus tile, the excommunications or the current occupation of the council, he can click on the relevant button.

It is to note that, when choosing a card during a Floor action, the player can double click on the Card to make his 
choice.

If the player wants to zoom on a card present in the game board on in his own personal board, he can click on it and it
will be displayed in the Information Pane. The same stands for the buttons above the current resources.