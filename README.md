# TASmod
A mod to record and play back your inputs!
## What is TASsing?
TASsing or Tool-Assisted-Speedrunning is a speedrun using tools, to get the fastest possible speedrun, which is oftentimes impossible to accomplish as a human... The purpose of TASsing is for entertainment and to find new strategies for RTA (Real-Time-Attack) Speedrunning.  

**Is this cheating?**
No it's not, it's a category on it's own and is not comparable to RTA speedrunning... But can use the tools for TASsing to fake an RTA speedrun! That is considered cheating...  
  
TASses are oftentimes made in *emulators*... But you can take the inputs and play them back on a real console to achieve the same effect.  
  
  Some of the tools for creating a TAS are:
  -  **Slowdown:** The game is slowed down to make more precise inputs... To a point where you can advance the frames individually  
  - **Savestates:** Create a snapshot of the game which you can reload at any time
  - **Playback:** The emulator records your inputs to a file which can be played back... If you realod a savestate, the inputs will continue from that savestate  
  
## What is Minecraft TASsing?
Basically the same :D  
But as you know Minecraft doesn't run in an emulator and therefore we don't have these common tools available...  
There is a program that supports PC games called LibTAS which is only available on Linux, but Minecraft has problems with that, since it doesn't run on a consistent framerate and it runs on a tick system (20 ticks/s)
So we use mods to accomplish our goals.  
There has been a [TickrateChanger](https://github.com/Guichaguri/TickrateChanger) mod to slow down the game and a new [TASTools](https://github.com/ScribbleLP/MC-TASTools) for savestates.  
And with this, we want to create a playback tool to complete all the tools used for creating TASses (especially playing them back)  
In addition to that, famous1622 is working on a mod to make [RNG](https://github.com/famous1622/KillTheRNG) consistent which is pretty helpful if you consider that it can desync if mobs don't spawn where you want it...
## Commands
Arguments written like this are `<required>` and like this is `[optional]`  

`/tasmod help` A list of commands ingame, if you are stuck  
`/tasmod folder` Folder where your TASfiles get saved  
`/tasmod gui` Disables the Gui  
`/record ,/rec,/r [filename]` Starts recording your inputs to a file. If no filename is specified it will save as *recording_[random number]*  
`/record` while a recording is running will stop the recording  
`/play, /p <filename>` Plays back a recording, entering it again will abort playing it back  
`/tastp <filename>` Will teleport you to the start of the recording  

## Examples
Checkout [TASvideos.org](http://tasvideos.org/) for cool emulator TASses

Here are examples from an earlier version of the mod [https://youtu.be/qbCoTa5p52w](https://youtu.be/qbCoTa5p52w) or here [https://youtu.be/AYlqu3ExpnQ](https://youtu.be/AYlqu3ExpnQ)

And here is some stuff I done with only the Slowdown mod, but sped up to match realtime speed. [https://www.youtube.com/watch?v=t4ucnRRkKLY](https://www.youtube.com/watch?v=t4ucnRRkKLY)
