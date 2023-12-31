# OSRS-Fishing-Bot---Runelite-Plugin
 Custom plugin for the Old School RuneScape (OSRS) game client, built with RuneLite, to automate fly fishing at Barbarian fishing spots. Currently supports 3 fishing spots to train your account from levels 1 to 99 (shown below).
 
![interface](https://github.com/Tong1233/OSRS-Fishing-Automation-Plugin-for-Runelite---Undetectable-and-Efficient/assets/74699244/f7603b2e-d4b1-4ddb-84f8-b9f0c79c82b5)

**Features:**
+ Automates the process of fly fishing at Barbarian fishing spots in OSRS.
+ Allows users to customize fishing locations and drop conditions
+ Built in debug features and informative GUI such as showing a heat map of all mouse click locations after prolonged use of the bot
+ Extensively tested to ensure that it can run stably (over 10 hours) and that antiban features prevent botting detection. Antiban features include:
realistic mouse movement and clicks, highly customizable click patterns following modified Gaussian distribution curves, moves the mouse off-screen when not required

**Technologies and Libraries**
+ Developed in Java and built using the RuneLite framework, an open-source client to run OSRS.
+ Utilizes an event-driven architecture to respond to various in-game events such as item container changes, game state changes, and interactions.
+ Effective thread handling for managing concurrent tasks and ensuring a smooth automation process acknowledging the single-threaded nature of the RuneLite framework
+ Integrates the MouseMotionFactory library for natural mouse movement, enhancing the realism of mouse interactions.

**Demo (Sped up, mouse movement is more natural in real time, red dots records click locations)**

![Demogif](https://github.com/Tong1233/OSRS-Fishing-Automation-Plugin-for-Runelite---Undetectable-and-Efficient/assets/74699244/7f58812e-73b7-4351-90bb-b56c32d5b4f5)
