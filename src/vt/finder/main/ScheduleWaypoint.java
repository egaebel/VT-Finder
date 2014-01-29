package vt.finder.main;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import java.util.Observable;
import vt.finder.io.FileIO;
import vt.finder.schedule.Course;
import vt.finder.schedule.Schedule;
import vt.finder.schedule.UserMadeCourse;
import vt.finder.web.WebScraper;
import vt.finder.web.WrongLoginException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
//text the xml data to friends, automatically receive by program, then delete text
//read the xml data in via this!!!!!! INGENIOUS
/**
 * holds the user's schedule as well as an ArrayList of all of the user's
 * friend's schedules
 * 
 * contains fileOperations for reading an xml string and placing the values into
 * the user's schedule and ArrayList
 * 
 * @author Ethan Gaebel (egaebel)
 */
public class ScheduleWaypoint extends Observable implements Parcelable {

    // ~Data Fields--------------------------------------------
    /**
     * TAG to identify Log.i values in LogCat
     */
    public static final String TAG = "MODEL";
    /**
     * the Schedule for the user
     */
    private Schedule schedule;
    /**
     * SChedule object holding the user's free time Schedule.
     */
    private Schedule freeTime;
    /**
     * list of schedules that holds friend's schedules that have been sent to
     * you
     * 
     * check for name of schedule when receiving new schedule..... keep ordered
     * perhaps for binary search
     */
    private ArrayList<Schedule> buddies;
    /**
     * instance of FileIO class used to write schedule values to file
     */
    private FileIO readWrite;
    /**
     * instance of the WebScraper class, used to grab Schedule values from
     * hokieSpa
     */
    private WebScraper webcrawler;
    /**
     * a list of course objects that represent each final exam the user has for
     * whatever semester they pulled it for. In sorted order
     */
    private ArrayList<Course> finalsList;
    /**
     * the term that the currently stored finalsList is connected to
     * 
     * saved to avoid repetitive web scraping
     */
    private String finalsTerm;

    // ~Constructors--------------------------------------------
    /**
     * the default constructor for the model, initializes objects
     */
    public ScheduleWaypoint(Context context) {

        schedule = new Schedule();
        freeTime = new Schedule();
        buddies = new ArrayList<Schedule>();
        readWrite = new FileIO(context);
        webcrawler = new WebScraper();
        
        finalsTerm = " ";
    }

    /**
     * parameterized constructor for the model, takes in a file as an argument
     * to initialize myFile in FileIO class readWrite
     * 
     * @param schedulesFile the file that contains the schedule
     * @param examsFile the file that contains the exam schedule
     */
    public ScheduleWaypoint(File schedulesFile, File examsFile) {

        schedule = new Schedule();
        freeTime = new Schedule();
        buddies = new ArrayList<Schedule>();
        webcrawler = new WebScraper();
        
        finalsTerm = " ";
        
        readWrite = new FileIO(schedulesFile, examsFile);
    }
    
    /**
     * ScheduleWaypoint constructor that takes another ScheduleWaypoint and
     * uses its values for itself.
     * 
     * @param modelCopy a copy of ScheduleWaypoint, whose values are
     *          to be used for this ScheduleWaypoint
     */
    public ScheduleWaypoint (ScheduleWaypoint modelCopy) {
        
        schedule = modelCopy.getSchedule();
        freeTime = modelCopy.getFreeTime();
        buddies = modelCopy.getBuddiesSchedules();
        readWrite = modelCopy.getReadWrite();
        webcrawler = new WebScraper();
        finalsList = modelCopy.getFinalsList();
        setFinalsTerm(modelCopy.getFinalsTerm());
    }

    // ~Methods-------------------------------------------------
    /**
     * Sets a UserMadeCourse in the schedule in the days indicated by daysString. 
     * 
     * @param userMadeCourse an instance of a UserMadeCourse that you wish to
     *          be put in the schedule stored in this object. 
     * @param daysString a string with one letter indicators of the days of the week.
     *          E.X: MWF (monday wednesday friday) or MR (monday thursday)
     */
    public void addUserMadeCourse(UserMadeCourse userMadeCourse, String daysString) {
        schedule.setCourseInDays(userMadeCourse, daysString);
        Log.i(TAG, "THE SCHEDULE AFTER ADDING THE USERMADE COURSE");
        Log.i(TAG, schedule.toXML());
    }
    
    /**
     * interface to the retrieveSchedule method of the WebScraper class
     * 
     * @param username
     *            the hokiespa username of the user
     * @param password
     *            the hokiespa password of the user
     * @param context
     *            the context of the current application
     */
    public boolean scrapeSchedule(String username, String password, String semester, String certFilePath) {

        schedule = new Schedule();

        boolean result = false;

        try {
            result = webcrawler.login(username, password, certFilePath);
        }
        catch (WrongLoginException e) {
            e.printStackTrace();
            return false;
        }

        if (result) {

            result = false;

            result = webcrawler.scrapeSchedule(schedule, semester);

            webcrawler.logout();

            // sorts all days in the schedule object by start time, so they
            // appear in the listview in the order that they occur
            schedule.sortDays();

            // save the newly scraped together schedule to file
            readWrite.save(schedule, buddies);

            // update observers
            setChanged();
            notifyObservers();

            return result;
        }
        else {

            return false;
        }
    }

    /**
     * takes a string of the semester to check the exam schedule for, and passes
     * that and the finalsList ArrayList<Course> into the webcrawler's
     * retrieveExamSchedule method
     * 
     * @param semester
     *            the semester to get the finals schedule for
     * @return true or false depending on if IO errors were encountered in the
     *         webcrawler method, or login issues
     */
    public boolean scrapeExamSchedule(String username, String password,
            String semester, String certFilePath) {

        //represents result of logging in
        boolean result = false;
        //represents result of scraping examSchedule
        boolean value = false;

        try {
            result = webcrawler.login(username, password, certFilePath);
        }
        catch (WrongLoginException e) {
            e.printStackTrace();
            return false;
        }

        // check if login was successful, if not returns false
        if (result) {

            // must initialize in here, otherwise a blank screen will be
            // displayed
            // if login was wrong, FOREVER!!!
            finalsTerm = semester;
            finalsList = new ArrayList<Course>();

            value = webcrawler.scrapeExamSchedule(finalsList, semester);

            sortFinalsList();
            
            webcrawler.logout();

            // append the new finalsList to file
            readWrite.save(finalsList, semester);
        }

        return value;
    }

    /**
     * sets the finalsList, ArrayList<Course> that is the finals schedule for
     * the user.
     * 
     * @param newFinalsList
     *            the ArrayList<Course> that finalsList is to be set to
     */
    public void setFinalsList(ArrayList<Course> newFinalsList) {

        if (newFinalsList != null) {

            finalsList = newFinalsList;
        }
    }

    /**
     * gets the finalsList, ArrayList<Course> that is the finals schedule for
     * the user. returns the finalsList or null if it is null
     * 
     * @return finalsList the ArrayList<Course> that represents the final exam
     *         schedule for the user
     */
    public ArrayList<Course> getFinalsList() {

        if (finalsList != null) {

            return finalsList;
        }
        else {

            return null;
        }
    }

    /**
     * performs an insertion sort on the finalsList ArrayList<Course>, the sort
     * is performed by doing a compareTo on the date objects contained in each
     * course object in the finalsList ArrayList<course>
     */
    public void sortFinalsList() {
        
        Course course = null;
        int j = 0;
        for (int i = 1; i < finalsList.size(); i++) {

            j = i;
            // checks to see if the start time of the element j is less than the
            // previous element, and if j > 0
            while (j > 0) {

                // if the current element, is less than the previous, then....
                if (finalsList.get(j).getDate()
                        .compareTo(finalsList.get(j - 1).getDate()) < 0) {

                    // if so, swap the two
                    course = finalsList.get(j);
                    finalsList.set(j, finalsList.get(j - 1));
                    finalsList.set(j - 1, course);
                }
                else if (finalsList.get(j).getDate()
                        .compareTo(finalsList.get(j - 1).getDate()) == 0) {
                    
                    if (compareTimes(finalsList.get(j).getBeginTime(), finalsList.get(j - 1).getBeginTime()) < 0) {
                        
                        // if so, swap the two
                        course = finalsList.get(j);
                        finalsList.set(j, finalsList.get(j - 1));
                        finalsList.set(j - 1, course);
                    }
                }

                // decrement j
                j--;
            }
        }
    }
    
    /**
     * Takes in two String objects and compares them to see which one comes when.
     * 
     * @param time1 the first time.
     * @param time2 the second time.
     * @return -1 if time1 < time2, 0 if time1 == time2, 1 if time1 > time2
     *              1 if time1 exists and time2 doesn't, -1 if time1 doesn't exist and time2 does
     */
    private int compareTimes(String time1, String time2) {
        
        String[] timeOneSplit = time1.trim().split(":");
        String[] timeTwoSplit = time2.trim().split(":");
        
        
        if (timeOneSplit.length == 2 && timeTwoSplit.length == 2) {
            
            String[] minutesAmPmSplitOne = new String[2];
            minutesAmPmSplitOne[1] = timeOneSplit[1].trim().substring(0, timeOneSplit[1].length() - 2);
            minutesAmPmSplitOne[1] = timeOneSplit[1].trim().substring(timeOneSplit[1].length() - 2, timeOneSplit[1].length());

            String[] minutesAmPmSplitTwo = new String[2];
            minutesAmPmSplitTwo[1] = timeTwoSplit[1].trim().substring(0, timeTwoSplit[1].length() - 2);
            minutesAmPmSplitTwo[1] = timeTwoSplit[1].trim().substring(timeTwoSplit[1].length() - 2, timeTwoSplit[1].length());
            
            int hourOne = Integer.parseInt(timeOneSplit[0]);
            int hourTwo = Integer.parseInt(timeTwoSplit[0]);
            
            //increment hours depending on am or pm
            if (minutesAmPmSplitOne[1].trim().equalsIgnoreCase("pm")) {
                hourOne += 12;
            }
            if (minutesAmPmSplitTwo[1].trim().equalsIgnoreCase("pm")) {
                hourTwo += 12;
            }
            
            if (hourOne < hourTwo) {
                
                return -1;
            }
            else if (hourOne > hourTwo) {
                
                return 1;
            }
            else {
                
                int minutesOne = Integer.parseInt(minutesAmPmSplitOne[0]);
                int minutesTwo = Integer.parseInt(minutesAmPmSplitTwo[0]);
                
                if (minutesOne < minutesTwo) {
                    
                    return -1;
                }
                else if (minutesOne > minutesTwo) {
                    
                    return 1;
                }
                else {
                    
                    return 0;
                }
            }
        }
        //time1 exists, time 2 doesn't, time 1 comes first!
        else if (timeOneSplit.length == 2 && timeTwoSplit.length != 2) {
            return -1;
        }
        else {
            return 1;
        }
    }

    /**
     * sets the value of the schedule object, and writes the new schedule to
     * file
     * 
     * @param newSchedule
     *            a schedule object to set schedule to
     */
    public void setSchedule(Schedule newSchedule) {

        schedule = newSchedule;
    }

    /**
     * returns the value of the schedule object
     * 
     * @return schedule the schedule of the user
     */
    public Schedule getSchedule() {

        return schedule;
    }

    /**
     * adds a friends schedule from an xml stream
     * 
     * @param name
     *            the name of a friend
     * @param friendsSchedule
     *            xml String of a friend's schedule
     */
    public void addFriend(String name, String friendsSchedule) {

        // loads the friend's schedule from the passed in friendsSchedule xml
        // stream
        // writes to the model
        Schedule temp = readWrite.loadSchedules(friendsSchedule, this);
        Log.i(TAG, "temp is: " + temp);
        buddies.add(temp);

        Log.i(TAG, "buddies.size() - 1 is: " + (buddies.size() - 1));
        Log.i(TAG, "buddies.size() - 1 is: " + buddies.get(buddies.size() - 1));
        // gets the just added friend's schedule and changes its name to
        // the passed in value
        // necessary because all schedules are sent as "MySchedule"
        buddies.get(buddies.size() - 1).setWhosSchedule(name);


        // update observers
        setChanged();
        notifyObservers();
    }

    /**
     * getter for the buddies ArrayList<Schedules>
     * 
     * @return buddies an ArrayList of schedules of friends you've compared
     *         schedules with
     */
    public ArrayList<Schedule> getBuddiesSchedules() {

        return new ArrayList<Schedule>(buddies);
    }

    /**
     * setter for the buddies ArrayList<Schedules>
     * 
     * takes in an arrayList of schedules and sets buddies to it
     * 
     * @param newBuddies
     *            the ArrayList<Schedule> to set buddies to
     */
    public void setBuddeisSchedules(ArrayList<Schedule> newBuddies) {

        buddies = new ArrayList<Schedule>();

        buddies.addAll(newBuddies);
    }

    /**
     * gets a friend's schedule from the buddies ArrayList<Schedule> by the
     * passed in index
     * 
     * makes my code shorter elsewhere
     * 
     * @param index
     *            the index of the friend to get from buddies
     * @return the schedule of the friend specified by the index
     */
    public Schedule getBuddy(int index) {

        return buddies.get(index);
    }

    /**
     * searches through buddies list of schedules for passed in name to see if
     * buddies contains a schedule for the friend's name you passed in
     * 
     * @param name
     *            friend's name whose schedule you want
     * @return value true if found, false otherwise
     */
    public int searchFriends(String name) {

        int index = -1;

        if (name != null) {

            Schedule sched;
            for (int i = 0; i < buddies.size(); i++) {

                sched = buddies.get(i);

                if (name.equals(sched.getWhosSchedule())) {
                    
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    /**
     * saves the user's schedule to the file via the FILEIO class, also saves
     * other schedules contained in the buddies ArrayList<Schedule>, and the
     * user's finalsList
     */
    public void saveAll() {

        Log.i(TAG, "saveAll called");
        if (schedule != null) {

            readWrite.save(schedule, buddies);
        }
        if (finalsList != null) {

            readWrite.save(finalsList, finalsTerm);
        }
    }

    /**
     * loads the user's schedule from file via the FileIO class and other
     * schedules contained in the xml, including the final exam schedule
     * 
     * @param file
     *            the file to load from
     * @return the Schedule loaded
     */
    public Schedule loadSchedules(File file) {

        return readWrite.loadSchedules(file, this);
    }

    /**
     * loads the user's schedule from the xml string passed in and returns the
     * schedule object resulting
     * 
     * NOTE: this does not set ScheduleWaypoint's schedule field.But it does set ScheduleWaypoint's buddies
     * field if applicable
     * 
     * @param xmlString
     *            the xml rep of a schedule
     * @return the resultant schedule object
     */
    public Schedule loadSchedules(String xmlString) {

        return readWrite.loadSchedules(xmlString, this);
    }

    /**
     * loads the user's exam list from the file passed in, and returns the
     * ArrayList<Course> resulting
     * 
     * NOTE: this does not set ScheduleWaypoint's finalsList field
     * 
     * @param file
     *            the file to load from
     * @return the loaded ArrayList<Course>
     */
    public ArrayList<Course> loadExams(File file) {

        if (finalsTerm == null) {
            
            finalsTerm = " ";
        }
        
        ArrayList<Course> returnList = readWrite.loadExams(file);
        finalsTerm = readWrite.getSemester();
                
        return returnList;
    }
    
    /**
     * 
     * @return the readWrite FilIO object
     */
    public FileIO getReadWrite() {
        
        return readWrite;
    }

    // ~Parcelable
    // Stuff---------------------------------------------------------------------------------------
    // ------------------------------------------//
    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(schedule, flags);
        dest.writeList(buddies);
    }

    /**
     * used to regenerate the Schedule upon receiving it
     */
    public static final Parcelable.Creator<ScheduleWaypoint> CREATOR = new Parcelable.Creator<ScheduleWaypoint>() {

        public ScheduleWaypoint createFromParcel(Parcel in) {

            return new ScheduleWaypoint(in);
        }

        public ScheduleWaypoint[] newArray(int size) {

            return new ScheduleWaypoint[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated
    // with it's values
    private ScheduleWaypoint(Parcel in) {

        buddies = new ArrayList<Schedule>();

        in.readParcelable(Schedule.class.getClassLoader());
        in.readList(buddies, Course.class.getClassLoader());
    }

    /**
     * @return the finalsTerm
     */
    public String getFinalsTerm() {

        return finalsTerm;
    }
    
    /**
     * @param finalsTerm the finalsTerm to set
     */
    public void setFinalsTerm(String finalsTerm) {

        this.finalsTerm = finalsTerm;
    }

    /**
     * @return the freeTime
     */
    public Schedule getFreeTime() {

        return freeTime;
    }

    /**
     * @param freeTime the freeTime to set
     */
    public void setFreeTime(Schedule freeTime) {

        this.freeTime = freeTime;
    }
}