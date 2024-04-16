import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class Room implements Serializable {
    private String roomName;
    private int noOfPersons;
    private String area;
    private int stars;
    private int noOfReviews;
    private String roomImage;
    private int price;
    private List<Available_Date> availability;
    private String owner;

    // Constructor
    public Room(String roomName, int noOfPersons, String area, int stars, int noOfReviews, String roomImage, int price, List<Available_Date> availability, String owner) {
        this.roomName = roomName;
        this.noOfPersons = noOfPersons;
        this.area = area;
        this.stars = stars;
        this.noOfReviews = noOfReviews;
        this.roomImage = roomImage;
        this.price = price;
        this.availability = availability;
        this.owner = owner;
    }
   
    // for testing
    public List<Available_Date> getAvailability() {
        return availability;
    }

    // for testing
    public static String getOwner(Room room){
        return room.owner;
    }

    public  String getArea(Room room){
        return room.area;
    }

    public String getRoomName(){
        return roomName;
    }

    public int getStars(){
        return stars;
    }


    public void addBooking(Request req){
        // looks like [roomName,FirstDayOfStay,LastDayOfStay]
        String[] details = req.details.split(","); 
        String FirstDay = details[1];
        String LastDay = details[2];

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate FD = LocalDate.parse(FirstDay,df);
        LocalDate LD = LocalDate.parse(LastDay,df);

        Available_Date wanted = new Available_Date(FD,LD);

        if (wanted.isAvailable(availability)){
            availability = wanted.RemoveFrom(availability);
        }else{
            System.out.println("Sorry, this date is not available at " + details[0] + ".");
        }
    }

    // (Manager) Adds new available dates
    public void addAvailability(Request req){

        String[] details = req.details.split(",");
        List<Available_Date> newDates = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        if (details.length % 2 == 0){
            System.out.println("Incorrect input. Please try again");
        }else{
            int i = 1; // skipping 0 because details[0] is the name of the room
            while (i<=details.length-1){
                LocalDate newFirstDay = LocalDate.parse(details[i],df);  
                LocalDate newLastDay = LocalDate.parse(details[i+1],df);
                Available_Date desiredDate = new Available_Date(newFirstDay,newLastDay);
                i=i+2;
                newDates.add(desiredDate);
            }

            boolean overlapping = false;    // assuming the dates do not overlap

            doubleLoop:
            for (Available_Date existingDate : availability){
                for (Available_Date newDate : newDates){
                    if (newDate.OverlapsWith(existingDate)){
                        System.out.println("The dates are overlapping. Please try again.");
                        overlapping = true;
                        break doubleLoop;
                    }
                    for (Available_Date newDate2 : newDates){
                        if (newDate2 != newDate && newDate2.OverlapsWith(newDate)){
                            System.out.println("The given dates are overlapping. Please try again.");
                            overlapping = true;
                            break doubleLoop;
                        }
                    }
                }
            }

            // If there are truly no overlapping dates
            if (!overlapping){
                for(Available_Date newDate : newDates){
                    availability = newDate.safeAdd(availability);
                }
            }
        }
    }

    // Changes the rating and the noOfReviews of the room
    public void ratingChanges(Request req){
        String[] details = req.details.split(",");
        int newStarRating = Integer.parseInt(details[1]);
        noOfReviews +=1;
        stars = (stars * (noOfReviews - 1) + newStarRating) / noOfReviews;
    }

    // Filters a list of rooms
    public List<Room> filterRooms(List<Room> givenRooms, Request req) {
        List<Room> matchingRooms = new ArrayList<>();

        String[] arrayFilter = req.details.split(",");
        // [Location, First Day Of Stay, Last Day Of Stay, NumOfPeople, Price, Stars]

        for (Room r: givenRooms) {
            if(r.filterByArea(arrayFilter[0]) && 
             r.filterByPersons(arrayFilter[3]) && 
             r.filterByPrice(arrayFilter[4]) && 
             r.filterByStars(arrayFilter[5]) && 
             r.filterByDates(arrayFilter[1], arrayFilter[2])){
                matchingRooms.add(r);
            }
        }
        return matchingRooms;
    }

    public boolean filterByArea(String desiredArea){
        if (desiredArea.equals("x")){
            return true;
        }else{
            return area.equals(desiredArea);
        }
    }

    public boolean filterByPersons(String desiredNoOfPersons){
        if (desiredNoOfPersons.equals("x")){
            return true;
        }else{
            return noOfPersons == Integer.parseInt(desiredNoOfPersons);
        }
    }

    public boolean filterByPrice(String desiredPrice){
        if (desiredPrice.equals("x")){
            return true;
        }else{
            return price <= Integer.parseInt(desiredPrice);
        }
    }

    public boolean filterByStars(String desiredStars){
        if (desiredStars.equals("x")){
            return true;
        }else{
            return stars >= Integer.parseInt(desiredStars);
        }
    }

    public boolean filterByDates(String FirstDay, String LastDay){
        if (FirstDay.equals("x") || LastDay.equals("x")){
            return true;
        }else{
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate FD = LocalDate.parse(FirstDay,df);  
            LocalDate LD =  LocalDate.parse(LastDay,df);
            Available_Date desiredDate = new Available_Date(FD, LD);
            return desiredDate.isAvailable(availability);
        }
    }

    // Returns the room with the desired roomName
    public static Room findRoomByName(String rName, List<Room> RoomList){
        for (Room r : RoomList){
            if(rName.equals(r.roomName)){
                return r;
            }
        }
        System.out.println("There was no room with the name" + rName);
        return null;
    }

    public static List<Room> roomsOfFolder(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();

        List<Room> rooms = new ArrayList<>();

        for (File f : files){
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(f));
                JSONObject JSONobj= (JSONObject) obj;

                String roomName = (String) JSONobj.get("roomName");
                int noOfPersons = ((Long) JSONobj.get("noOfPersons")).intValue();
                String area = (String) JSONobj.get("area");
                int stars = ((Long) JSONobj.get("stars")).intValue();
                int noOfReviews = ((Long) JSONobj.get("noOfReviews")).intValue();
                String roomImage = (String) JSONobj.get("roomImage");
                int price = ((Long) JSONobj.get("price")).intValue();
                String owner = (String) JSONobj.get("owner");

                List<Available_Date> dates = new ArrayList<>();

                JSONArray available_dates = (JSONArray) JSONobj.get("availability");
                for (Object Obj : available_dates){
                    JSONObject jsonOB = (JSONObject) Obj;
                    String FirstDayString = (String) jsonOB.get("start_date");
                    String LastDayString = (String) jsonOB.get("end_date");
                    
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate FirstDayDate = LocalDate.parse(FirstDayString,df);  
                    LocalDate LastDayDate =  LocalDate.parse(LastDayString,df);
                    

                    dates.add(new Available_Date(FirstDayDate,LastDayDate));
                }

                Room room = new Room(roomName, noOfPersons, area, stars, noOfReviews, roomImage, price, dates, owner);
                rooms.add(room);

            }catch (Exception e){
                System.out.println("Exception:" + e);
            }   
        }
        return rooms;
    }


    // TESTING
    public static void main(String[] args) {
        String folderPath = "bin/rooms";
        List<Room> rooms = roomsOfFolder(folderPath);

        /* TESTING addAvailability 

        Request rAvailability = new Request("x", folderPath, "Thalassi Room,2024/07/03,2024/07/08", 0);

        Room room = findRoomByName("Thalassi Room",rooms);
        System.out.println("BEFORE BOOKING");
        for (Available_Date r :room.availability){
            System.out.println(r.getTimePeriod());
        }

        room.addAvailability(rAvailability);

        System.out.println("\nAFTER BOOKING");
        for (Available_Date r : room.availability){
            System.out.println(r.getTimePeriod());
        }
        System.out.println();

        */

        
        /*  TESTING addBooking
        Request rBooking = new Request("x", folderPath, "Thalassi Room,2024/06/15,2024/06/30", 0);
        Room room = findRoomByName("Thalassi Room",rooms);
        System.out.println("BEFORE BOOKING");
        for (Available_Date r :room.availability){
            System.out.println(r.getTimePeriod());
        }
        room.addBooking(rBooking);
        System.out.println("\nAFTER BOOKING");
        for (Available_Date r :room.availability){
            System.out.println(r.getTimePeriod());
        }
        System.out.println();

        */


        /*  TESTING ratingChanges
        Room room = findRoomByName("Thalassi Room",rooms);
        System.out.println(room.stars);
        String details = "Thalassi Room,1";
        Request r = new Request(details, folderPath, details, 0);
        room.ratingChanges(r);
        System.out.println(room.stars);

        */
        

        /* prepei na kaneis th filterRooms static gia na treksei

        List<Room> filtered = filterRooms(rooms, r);
        for (Room f : filtered){
            System.out.println(f.getRoomName());
        }

        */
    }

}
