# high-orbit-ion-cannons

This is refractoring projet of a school project of a fun game called High orbit ion cannons. Oh, and the game itself is a implementation of `battleships`. This project was extracted form a overarching project that had a Lego Mindstorm robot and a UI to talk with the robot, I myself creating the game logic.

#### Rules

instead of ships-of-the-line, we are targetting bunkers that are 2d in shape. Each player has following bunkers to plonk on the map:  
  X       X X     X          X
X X X     X X     X  X       X
  X
Each player in turn fire at a position, and will receive feedback whether they hit or missed. Player to first reach 15 points = destroy all forts wins.
  
### Goal

I work to refractor this app into a presentable form after it being hastily created for a course in Metropolia AMK. This is the last item on the backburner so it might take some time though.

#### targets

- Game logic that can beat opponent on average faster than completely random selection
- Neatly packaged files that comply with MVC
- *Readable code*
- Some UI to play the game, currently playing in terminal is painfull
- Tested code
- Javadocks


### Tech
Like 3 billion devices, this project runs on Java. Testing is done with JUnit and UI will be built with `insert tech here`
