# ReEngine2

LibGDX based engine focusing on ease of development. This is for personal use and such has no documentation. 
This repo is also the public facing (and very old build) of my game Garden Souls. That project has had steady progress since splitting from this repo improving upon all aspects. 
The up-to-date version has numerous editors to aid in content generation. 
* Level Editor
  * layered system 
  * Unique and fast control schemes
  * Undo/Redo
  * Scripting system for level specifc entities
* Tile Editor
  * Mapping to define what type of terrain or ground type a tile is. For collision and audio referencing. 
* Animation Editor
  * Allows quick and easy control over offsets for complex animations
  * Scripting built in to allow animations to control things like particles and other animation commands
* Object Editor
  * Way too complex to describe in detail. Allows you to define all behaviors and parameters for creating objects in game. Enemy ai, reactions, physics parameters, etc
* Audio Editor
  * Basic utility for allowing quick edit access to audio data in game. 
* Particle Editor (Coming)

Each of these tools edit json files that the engine loads during gameplay. 

Much more with custom physics implimentation, gamestate based engine behavior, deep scripting system, networking, and more
