# Finomena Dance

### Installation
For installation you just need to copy and paste the apk in the main respository directory to your phone and install it.

### About the Game
This is an Android Game(Finomena Dance) which tests your fingers flexibity and ability to dance :P. Following are the rules of the game:
  - The first screen of the game contains 4 options for the user to select, lets say the user selects N.
  - The next screen contains a board having N*N tiles.
  - This is a two player game, at first the game starts with a random tile highlighted and player1 has to press any of his finger on the tile failing which he would loose.
  - When player1 presses immediately the next tile pops up with a different color for player2.
  - Every player has 5 seconds to press their highlighted tiles failing which they loose.
  - During the game no player is allowed to release any of the previously pressed tile, otherwise that player will loose.
  - If during the course of the game, player moves any of his pressed finger out of the tile area then also he looses.
  - If by chance both the player succeed in pressing all the present tiles then the distance of the press from the center of the button is calculated and added for each player. The player with larger value of this distance looses in the end.

### Architecture
This game is supported for Android version 16+ (Jellybean and above) and doesn't uses any third party library. The grid in the GameActivity.java is dynamically created using a linearlayout horizontal inside a linearlayout vertical. OnTouchListeners are set on each and every buttons. The game contain three Activtiy Screens all of them are fully immersive. A thread is run in the GameActivity which checks the currentTimeMillis with the latest downTime, if it is greater than 5 sec then the activity is stopped and the next activity GameOverActivity is called along with the winner. The Android device must support multi-touch for running this game. I searched for any way to find out the maximum number for multi touch supported by the device, but couldn't find any proper way of doing it. We can tell if the android device support multi-touch or not but no way of finding out the number.