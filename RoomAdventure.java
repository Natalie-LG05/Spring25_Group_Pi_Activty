import java.util.Scanner; //import scanner for reading user input
import java.util.ArrayList; // import array list for making objects of lists
import java.util.List; //making lists

public class RoomAdventure {
    // class variables
    private static Room currentRoom; // the room the player is currently in
    private static ArrayList<Item> inventory = new ArrayList<>(); // player inventory
    private static String status; // message to display after each action
    private static boolean running = true;
    private static boolean death = false;
    private static boolean win = false;

    //constants
    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', 'take', 'play', 'use', 'inspect', and 'eat'."; //default error message

    private static void handleGo(String noun) {//Handles moving between rooms
        String[] exitDirections = currentRoom.getExitDirections(); // get available directions
        Room[] exitDestinations = currentRoom.getExitDestinations(); // get rooms in those directions
        status = "I don't see that room."; // default if direction not found
        for (int i = 0; i < exitDirections.length; i++) {// loop through directions
            if (noun.equals(exitDirections[i])) {// if user direction matches
                currentRoom = exitDestinations[i]; // change current room
                status = "Changed Room"; //update status   
            }    
        }
    }

    private static void handleLook(String noun) {// handles inspecting items
        status = "I don't see that item."; // default if item not found
        for (Item item : currentRoom.getItems()) {//loop through items
            if (item.toString().equals(noun)) {// if user-noun matches an item
                status = item.getDescription(); // set status to item description    
            }    
        }    
    }

    private static void handleTake(String noun) { // handles picking up items
        status = "I can't grab that."; // default if not grabbable
        for (Item item : currentRoom.getGrabbableItems()) { // loop through grabbable items
            if (item.toString().equals(noun)) { // if user-noun matches grabbable
                inventory.add(item); // add item to inventory
                status = "Added it to the inventory"; // update status
                currentRoom.getItems().remove(item); // removes item from room
                break;
            }    
        }
    }

    private static void handleEat(String noun) {
        status = "I can't eat that, silly goose.";
        for (Item item : currentRoom.getEdibleItems()) {
            if (item.toString().equals(noun)) {
                status = "You have eaten it. You seem to have died! Too bad.\n";
                death = true;
                break;
            }
        }
    }

    private static void handlePlay(String noun){
        status = "um no";
        for (Item item: inventory) {
            if (item != null && noun.equals("harmonica")) {
                if (currentRoom.getRoomName().equals("Room 4")) {
                    status = "You played the harmonica beautifully, the mirror shimmers turning into a door.";
                    for (Item roomItem : currentRoom.getItems()) {
                        if (roomItem.toString().equals("mirror")) {
                            roomItem.setDescription("The mirror turned into a wooden door. There's a keyhole.");
                            roomItem.setItemName("door");
                            break;
                        }
                    }
                } else {
                    status = "The harmonica sounds like the screeching wails of the dead, you are not good at this.";
                }
            }
        }
    }

    private static void handleUse(String noun){
        status = "Why?";
        for (Item item: inventory){
            if (item != null && item.toString().equals("key") && noun.equals("key")) {
                if (currentRoom.getRoomName().equals("Room 4")) {
                    for (Item roomItem: currentRoom.getItems()) {
                        if (roomItem.toString().equals("door")) {
                            status = "You use the key on the door and open it. There is a bright light and you exit the four roomed purgatory. You are free.";
                            win = true;
                            break;
                        }
                    }
                }
            } else if (item != null && item.toString().equals("knife") && noun.equals("knife")) {
                status = "You use the knife on yourself. You stabbed yourself. Why?";
                death = true;
                break;
            } else if (item != null && item.toString().equals("coal") && noun.equals("coal")) {
                if (currentRoom.getRoomName().equals("Room 2")) {
                    status = "You throw the coal into the fire. It gets brighter and hotter. You blow up.";
                    death = true;
                }
                break;
            } else if (item != null && item.toString().equals("bat") && noun.equals("bat")) {
                if (currentRoom.getRoomName().equals("Room 3")) {
                    status = "You put the bat into the oven. I don't know why you did that. You take it back out.";
                }
                break;
            } else if (item != null && item.toString().equals("gear") && noun.equals("gear")) {
                if (currentRoom.getRoomName().equals("Room 5")) {
                    for (Item roomItem : currentRoom.getItems()) {
                        if (roomItem.toString().equals("strange_machine")) {
                            // update the machine's description and the status
                            status = "You put the gear into the machine. Suddenly, it activates, and a pokeball appears!";
                            roomItem.setDescription("The machine is glowing. It seems to make unlimited pokeballs.");

                            Item pokeball = new Item("pokeball"); // create a pokeball item
                            pokeball.setDescription("A pokeball. It could be used to capture something...");
                            inventory.add(pokeball); // add pokeball to inventory

                            inventory.remove(item); // remove gear from inventory
                            break;
                        }
                    }
                }
                break;
            } else if (item != null && item.toString().equals("pokeball") && noun.equals("pokeball")) {
                if (currentRoom.getRoomName().equals("Room 5")) {
                    for (Item roomItem : currentRoom.getItems()) {
                        if (roomItem.getItemName().equals("frog")) {
                            inventory.add(roomItem); // add frog to inventory
                            currentRoom.removeItem(roomItem); // remove frog from the room
                            inventory.remove(item); // Remove the pokeball from inventory
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private static void handleInspect(String noun) {
        status = "I do not have that item."; // Set default status for inspect command
        for (Item item: inventory) {
            if (item != null && item.toString().equals(noun)) {
                status = item.getDescription();
                break;
            }
        }
    }

    private static void setupGame() { // initializes game world
        Room room1 = new Room("Room 1"); // create room 1
        Room room2 = new Room("Room 2"); // create room 2 
        Room room3 = new Room("Room 3");
        Room room4 = new Room("Room 4");
        Room room5 = new Room("Room 5");
        
        // Room 1 setup
        String[] room1ExitDirections = {"east", "south"}; // room 1 exits
        Room[] room1ExitDestinations = {room2, room3}; // destination rooms for room 1

        // Room 1 items
        Item chair = new Item("chair");
        chair.setDescription("It is a chair");
        chair.setIsGrabbable(false);

        Item desk = new Item("desk");
        desk.setDescription("It's a desk, there is a key on it.");
        desk.setIsGrabbable(false);

        Item key = new Item("key");
        key.setDescription("A small rusty key.");
        key.setIsGrabbable(true);

        room1.addItem(chair);
        room1.addItem(desk);
        room1.addItem(key);
        room1.setExitDirections(room1ExitDirections);
        room1.setExitDestinations(room1ExitDestinations);


        // Room 2 setup
        String[] room2ExitDirections = {"west", "south"}; // room 1 exits
        Room[] room2ExitDestinations = {room1, room4}; // destination rooms for room 1

        // Room 2 Items
        Item fireplace = new Item("fireplace");
        fireplace.setDescription("It's on fire.");
        fireplace.setIsGrabbable(false);

        Item rug = new Item("rug");
        fireplace.setDescription("There is a lump of coal on the rug.");
        fireplace.setIsGrabbable(false);

        Item coal = new Item("coal");
        coal.setDescription("Its lumpy and black.");
        coal.setIsGrabbable(true);
        coal.setIsEdible(true);

        room2.addItem(coal);
        room2.addItem(fireplace);
        room2.addItem(rug);
        room2.setExitDirections(room2ExitDirections);
        room2.setExitDestinations(room2ExitDestinations);


        // Room 3 setup
        String[] room3ExitDirections = {"north","east"}; // room 3 exits
        Room[] room3ExitDestinations = {room1, room4}; // destination rooms for room 3

        // Room 3 Items
        Item oven = new Item("oven");
        oven.setDescription("It's cold and dirty.");
        oven.setIsGrabbable(false);

        Item cuttingBoard = new Item("cutting-board");
        cuttingBoard.setDescription("There is a partially cut, weird smelling lemon and a knife.");
        cuttingBoard.setIsGrabbable(false);

        Item lemon = new Item("lemon");
        lemon.setDescription("It smells weird and looks a bit not lemon-y");
        lemon.setIsEdible(true);

        Item knife = new Item("knife");
        knife.setDescription("It's pretty sharp, I should be careful.");
        knife.setIsGrabbable(true);

        room3.addItem(knife);
        room3.addItem(lemon);
        room3.addItem(cuttingBoard);
        room3.addItem(oven);
        room3.setExitDirections(room3ExitDirections);
        room3.setExitDestinations(room3ExitDestinations);


        // Room 4 setup
        String[] room4ExitDirections = {"north","west", "south"}; // room 4 exits
        Room[] room4ExitDestinations = {room2, room3, room5}; // destination rooms for room 4

        // Room 4 Items
        Item mirror = new Item("mirror");
        mirror.setDescription("You can see the room, but you are not in it. You can see a harmonica in the corner now.");
        mirror.setIsGrabbable(false);

        Item bat = new Item("bat");
        bat.setDescription("Is it a bat or a bat? You cannot tell");
        bat.setIsGrabbable(true);

        Item harmonica = new Item("harmonica");
        harmonica.setDescription("It looks functional?");
        harmonica.setIsGrabbable(true);

        room4.addItem(harmonica);
        room4.addItem(bat);
        room4.addItem(mirror);
        room4.setExitDirections(room4ExitDirections);
        room4.setExitDestinations(room4ExitDestinations);

        // Room 5 setup
        String[] room5ExitDirections = {"north"}; // room 1 exits
        Room[] room5ExitDestinations = {room4}; // destination rooms for room 1

        // Room 5 Items
        Item strange_machine = new Item("strange_machine");
        strange_machine.setDescription("A strange, metal contraption of some sort. It looks like it is missing a gear.");
        strange_machine.setIsGrabbable(false);

        Item frog = new Item("frog");
        frog.setDescription("It is a frog.");
        frog.setIsGrabbable(false);

        Item gear = new Item("gear");
        gear.setDescription("A strange metal gear");
        gear.setIsGrabbable(true);

        room5.addItem(strange_machine);
        room5.addItem(frog);
        room5.addItem(gear);
        room5.setExitDirections(room5ExitDirections);
        room5.setExitDestinations(room5ExitDestinations);

        currentRoom = room1; // start game in room 1
    }


    //@SuppressWarnings("java:S2189") don't need
    public static void main(String[] args){// entry point of the program
        setupGame(); // initialize rooms, items, and starting room
        Scanner s = new Scanner(System.in);

        System.out.println("What is your name?: ");
        String playerName = s.nextLine();
        while(running){ // game loop, runs until program is terminated
            System.out.print(currentRoom.toString()); // display current room description
            System.out.print(playerName + "'s inventory: "); // prompt for inventory display

            for (Item item : inventory) {
                if (item != null) {
                    System.out.print(item + " ");
                }
            }
            System.out.println("\nWhat would you like to do, " + playerName + "?");
            String input = s.nextLine(); // read entire line of input
            String[] words = input.split(" ");

            // If the player types quit or q as the first word, exit
            if (words[0].equalsIgnoreCase("quit") || words[0].equalsIgnoreCase("q")) {
                running = false;
                break;
            }

            if (words.length != 2) {
                status = DEFAULT_STATUS;
                System.out.println(status);
                continue;
            }

            String verb = words[0].toLowerCase(); // first word is the action verb
            String noun = words[1].toLowerCase(); // second word is the target noun
            switch (verb){ // decide which action to take
                case "go":
                    handleGo(noun); // move to another room
                    break;
                case "look":
                    handleLook(noun); // describe an item
                    break;
                case "take":
                    handleTake(noun); // pick up an item
                    break;
                case "eat":
                    handleEat(noun);
                    break;
                case "play":
                    handlePlay(noun);
                    break;
                case "use":
                    handleUse(noun);
                    break;
                case "inspect":
                    handleInspect(noun);
                    break;
                default:
                    status = DEFAULT_STATUS; // set status to error message
            }
            System.out.println(status); // print status

            if (death){
                System.out.println("You have perished.");
                running = false;
                System.exit(0);
                break;
            }
            if (win){
                System.out.println("Congratulations!");
                running = false;
                System.exit(0);
                break;
            }
        }    
    }
}


class Room { // represents a game room
    private String name; //room name
    private String[] exitDirections; // directions you can go
    private Room[] exitDestinations; // rooms reached by each direction
    private ArrayList<Item> items; // List of Item objects

    public Room(String name) { //constructor
        this.name = name; //set the room's name
        this.items = new ArrayList<>(); // Initialize item list
    }

    public void setRoomName(String name){
        this.name = name;
    }

    public String getRoomName(){
        return name;
    }

    public void setExitDirections(String[] exitDirections){ // setter for exits
        this.exitDirections = exitDirections;
    }

    public String[] getExitDirections() {
        return exitDirections;
    }

    public void setExitDestinations(Room[] exitDestinations){ // setter for exits
        this.exitDestinations = exitDestinations;
    }

    public Room[] getExitDestinations() {
        return exitDestinations;
    }

    public void addItem(Item item){ // setter for items
        items.add(item);    
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() { //getter for items
        return items;    
    }

    public List<Item> getGrabbableItems() {
        List<Item> grabbables = new ArrayList<>();
        for (Item item : items) {
            if (item.getIsGrabbable()) {
                grabbables.add(item);
            }
        }
        return grabbables;
    }

    public List<Item> getEdibleItems() {
        List<Item> edibles = new ArrayList<>();
        for (Item item : items) {
            if (item.getIsEdible()) {
                edibles.add(item);
            }
        }
        return edibles;
    }

    // @Override don't need
    public String toString(){ // custom print for the room
        String result = "\nLocation: " + name; // show room name
        result +="\nYou See: "; // list items
        for (Item item : items){ // loop items
            result += item + " "; // append each line    
        }
        result += "\nExits: "; // list exits
        for (String direction: exitDirections){ // loop exits
            result += direction + " "; // append each direction
        }
        return result + "\n"; // return full description
    }
}


class Item{

    private String name;
    private String description;
    private boolean isGrabbable = false;
    private boolean isEdible;

    public Item(String name){
        this.name = name;
    }

    //Getters and Setters
    public void setItemName(String name){
        this.name = name;
    }

    public String getItemName(){
        return name;
    }

    public void setDescription(String desc){
        description = desc;
    }

    public String getDescription(){
        return description;
    }

    public void setIsGrabbable(boolean grabbable){
        isGrabbable = grabbable;
    }

    public boolean getIsGrabbable(){
        return isGrabbable;
    }

    public void setIsEdible(boolean edible){
        isEdible = edible;
    }

    public boolean getIsEdible(){
        return isEdible;
    }

    public String toString(){
        return name;
    }
}