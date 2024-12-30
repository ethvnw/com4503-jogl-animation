COM4503 Assignment 1
Created by Ethan Watts

VIDEO LINK TO ANIMATION
=======================
https://www.youtube.com/watch?v=l_HJTAgfLSI

CODE STRUCTURE
==============
Cube, Sphere, TwoTriangles, TwoTrianglesRepeating, TwoTrianglesWindowCutout.java
----
Static classes containing indices and vertices for creating basic shapes. TwoTriangles has two modified versions: Repeating for the wall with a repeating texture, WindowCutout to make a custom shape for the window on the left.

AnimationController.java
----
Class to handle animations, allows for smooth progress when an animation is paused and resumed.

DancingRobot.java
----
Class for robot 1, contains all the code for creating the robot and animating it.

Globe.java
----
Class for the globe, contains all the code for creating the globe and animating it.

MovingRobot.java
----
Class for robot 2, contains all the code for creating the robot and animating it.

Room.java
----
Class for the room, contains all the code for creating the room (walls, floor, ceiling, window).

Skybox.java
----
Class for the skybox, contains all the code for creating the skybox. Uses the skybox shaders.

Spotlight.java
----
Class that inherits from Light, contains the code for creating the spotlight. Allows for the spotlight to be moved and rotated.

SpotlightNode.java
----
Class that inherits from SGNode, contains the code for creating the spotlight node. Allows for the spotlight to be attached to a scene graph.

Utilities.java
----
Class containing utility functions for getting the current time, creating models and loading the appropriate shader.

Camera, Light, Material, Mesh, Model, ModelNode, NameNode, SGNode, Shader, TextureLibrary, TransformNode.java
----
Classes that are given in the tutorial code.

Spacecraft.java
----
Main class for the program.

Spacecraft_EventListener.java
----
Class for creating the scene and rendering it.
