package kryonetwork;

public class User { //Everything you should need to render and calculate any player or ai through the network
    public int ID; //Unique connection identifier
    public String username;

    public float x = 0;
    public float y = 0;

    public float angle = 0;

    public String text = "___"; // This can be utilized to send commands or chat through the server

    public String toString() {
        return "User [ " + username + ", " + ID + ", " + x + ", " + y + ", " + angle + "]";
    }
}