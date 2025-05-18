import java.util.Scanner; //import scanner for reading user input

public class RoomAdventure {
    // class variables
    private static Room currentRoom; // the room the player is ucrrently in
    private static String[] inventory = {null, null, null, null, null}; // player inventory slots
    private static String status; // message to display after each action
    private static boolean death = false;

    //constants
    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', and 'take'."; //default error message

    private static void handleGo(String noun) {//Handles moving between rooms
        String[] exitDirections = currentRoom.getExitDirections(); // get available directions
        Room[] exitDestinations = currentRoom.getExitDestinations(); // get rooms in those directions
        status = "I don't see that room."; // default if direction not found
        for (int i = 0; i < exitDirections.length; i++){// loop through directions
            if (noun.equals(exitDirections[i])) {// if user direction matches
                currentRoom = exitDestinations[i]; // change current room
                status = "Changed Room"; //update status   
            }    
        }
    }

    private static void handleLook(String noun) {// handles inspecting items
        String[] items = currentRoom.getItems();// visible items in current room
        String[] itemDescriptions = currentRoom.getItemDescriptions();// descriptions for each item
        status = "I don't see that item."; // default if item not found
        for (int i=0; i < items.length; i++) {//loop through items
            if (noun.equals(items[i])){// if user-noun matches an item
                status = itemDescriptions[i]; // set status to item description    
            }    
        }    
    }

    private static void handleTake(String noun) {// handles picking up items
        String[] grabbables = currentRoom.getGrabbables(); // items that can be taken
        status = "I can't grab that."; // default if not grabbable
        for (String item : grabbables){// loop through grabbable items
            if (noun.equals(item)){// if user-noun matches grabbable
                for (int j = 0; j< inventory.length; j++) { // find empty inventory slot
                    if (inventory[j] == null) { // if slot is empty
                        inventory[j] = noun; //add item to inventory
                        status = "Added it to the inventory"; // update status
                        break; // exit inventory loop
                    }    
                }    
            }    
        }
    }

    private static void handleEat(String noun) {
        String[] edibles = currentRoom.getEdibles();
        status = "I can't eat that, silly goose.";
        for(String item : edibles){
            if (noun.equals(item)){
                status = "You have eaten it. You seem to have died! Too bad.\n";
                death = true;
            }
        }
    }

    private static void setupGame() { // initializes game world
        Room room1 = new Room("Room 1"); // create room 1
        Room room2 = new Room("Room 2"); // create room 2 
        Room room3 = new Room("Room 3");
        
        String[] room1ExitDirections = {"east", "south"}; // room 1 exits
        Room[] room1ExitDestinations = {room2, room3}; // destination rooms for room 1
        String[] room1Items = {"chair", "desk"}; // items in room 1
        String[] room1ItemDescriptions = {
            "It is a chair",
            "It's a desk, there is a key on it."
        };
        String[] room1Grabbables = {"key"}; // items you can take from room 1
        room1.setExitDirections(room1ExitDirections);
        room1.setExitDestinations(room1ExitDestinations);
        room1.setItems(room1Items);
        room1.setItemDescriptions(room1ItemDescriptions);
        room1.setGrabbables(room1Grabbables);

        String[] room2ExitDirections = {"west", "south"}; // room 1 exits
        Room[] room2ExitDestinations = {room1};//, room4}; // destination rooms for room 1
        String[] room2Items = {"fireplace", "rug"}; // items in room 1
        String[] room2ItemDescriptions = {
            "It's on fire.",
            "There is a lump of coal on the rug."
        };
        String[] room2Grabbables = {"coal"}; // items you can take from room 1
        room2.setExitDirections(room2ExitDirections);
        room2.setExitDestinations(room2ExitDestinations);
        room2.setItems(room2Items);
        room2.setItemDescriptions(room2ItemDescriptions);
        room2.setGrabbables(room2Grabbables);

        String[] room3ExitDirections = {"north","east"}; // room 1 exits
        Room[] room3ExitDestinations = {room1};//,room4}; // destination rooms for room 1
        String[] room3Items = {"oven", "cutting-board"}; // items in room 1
        String[] room3ItemDescriptions = {
            "It's cold and dirty.",
            "There is a partially cut, weird smelling lemon and a knife."
        };
        String[] room3Grabbables = {"knife"}; // items you can take from room 1
        String[] room3Edibles = {"lemon"};
        room3.setExitDirections(room3ExitDirections);
        room3.setExitDestinations(room3ExitDestinations);
        room3.setItems(room3Items);
        room3.setItemDescriptions(room3ItemDescriptions);
        room3.setGrabbables(room3Grabbables);
        room3.setEdibles(room3Edibles);

        currentRoom = room1; // start game in room 1
    }
    //@SuppressWarnings("java:S2189") don't need
    public static void main(String[] args){// entry point of the program
        setupGame(); // initialize rooms, items, and starting room
        while(true){// game loop, runs until program is terminated
            System.out.print(currentRoom.toString());// display current room desription
            System.out.print("Inventory: "); // promtot for invetory display
            
            for (int i = 0; i < inventory.length; i++){
                System.out.print(inventory[i] + " ");
            }
            System.out.println("\nWhat would you like to do? ");
            Scanner s = new Scanner(System.in);
            String input = s.nextLine(); // read entire line of input
            String[] words = input.split(" ");

            if (words.length != 2) {
                status = DEFAULT_STATUS;
                continue;

            }
            String verb = words[0]; // first word is the action verb
            String noun = words[1]; // second word is the target noun
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
                default:
                    status = DEFAULT_STATUS; // set status to error message
            }
            System.out.println(status); // print status
            if (death == true){
                System.exit(0);
            }
        }    
    }
}

class Room { // represents a game room
    private String name; //room name
    private String[] exitDirections; // directions you can go
    private Room[] exitDestinations; // rooms reached by each direction
    private String[] items; // items visible in the room
    private String[] itemDescriptions; // descriptions for those items
    private String[] grabbables; // items you can take
    private String[] edibles;

    public Room(String name) { //constructor
        this.name = name; //set the room's name    
    }

    public void setExitDirections(String[] exitDirections){// setter for exits
        this.exitDirections = exitDirections;
    }

    public String[] getExitDirections() {
        return exitDirections;
    }

    public void setExitDestinations(Room[] exitDestinations){// setter for exits
        this.exitDestinations = exitDestinations;
    }

    public Room[] getExitDestinations() {
        return exitDestinations;
    }

    public void setItems(String[] items){// setter for items
        this.items = items;    
    }

    public String[] getItems() {//getter for items
        return items;    
    }

    public void setItemDescriptions(String[] itemDescriptions){// setter for descriptions
        this.itemDescriptions = itemDescriptions;
    }

    public String[] getItemDescriptions() {// getter for descriptions
        return itemDescriptions;    
    }

    public void setGrabbables(String[] grabbables) {// setter for grabbable items
        this.grabbables = grabbables;   
    }

    public String[] getGrabbables() {// getter for grabbable items
        return grabbables;    
    }

    public void setEdibles(String[] edibles) {// setter for grabbable items
        this.edibles = edibles;   
    }

    public String[] getEdibles() {// getter for grabbable items
        return edibles;    
    }

    // @Override don't need
    public String toString(){// custom print for the room
        String result = "\nLocation: " + name; // show room name
        result +="\nYou See: "; // list items
        for (String item: items){// loop items
            result += item + " "; // append each line    
        }
        result += "\nExits: "; // list exits
        for (String direction: exitDirections){// loop exits
            result += direction + " "; // append each direction
        }
        return result + "\n"; // return full description
    }
}
