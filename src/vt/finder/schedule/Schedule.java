package vt.finder.schedule;

import java.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * class to hold 6 day objects that represent the days of the week, and one for
 * anyDay of the week to hold empo classes and online classes
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 */
public class Schedule implements Parcelable {

    // ~DataFields----------------------------------------------
    private static final String TAG = "SCHEDULE";

    private Day monday;

    private Day tuesday;

    private Day wednesday;

    private Day thursday;

    private Day friday;

    private Day anyDay;

    /**
     * assigned to the Day object that is today
     */
    private Day today;

    /**
     * the name of the person who this schedule belongs to
     * 
     * mySchedule if user's schedule
     */
    private String whosSchedule;
    
    /**
     * calendar object used to get what today's day is
     */
    private Calendar cal;

    // ~Constructor----------------------------------------------
    /**
     * default constructor for the Schedule class
     * 
     * takes input from the WebCrawler class or the compareTo friends class
     */
    public Schedule() {

        monday = new Day("Monday");
        tuesday = new Day("Tuesday");
        wednesday = new Day("Wednesday");
        thursday = new Day("Thursday");
        friday = new Day("Friday");
        anyDay = new Day("AnyDay");

        whosSchedule = "MySchedule";

        setToday();
    }

    /**
     * constructor for the Schedule class that takes a String to initialize
     * whosSchedule
     * 
     * takes input from the WebCrawler class or the compareTo friends class
     */
    public Schedule(String scheduleOwner) {

        monday = new Day("Monday");
        tuesday = new Day("Tuesday");
        wednesday = new Day("Wednesday");
        thursday = new Day("Thursday");
        friday = new Day("Friday");
        anyDay = new Day("AnyDay");

        whosSchedule = scheduleOwner;

        setToday();
    }

    /**
     * initializes this schedule to a passed in schedule used for receiving sent
     * schedule objects......
     * 
     * @param schedule
     *            a passed in schedule to set this schedule to
     */
    public Schedule(Schedule schedule) {

        monday = schedule.getMonday();
        tuesday = schedule.getTuesday();
        wednesday = schedule.getWednesday();
        thursday = schedule.getThursday();
        friday = schedule.getFriday();
        anyDay = schedule.getAnyDay();

        whosSchedule = schedule.getWhosSchedule();

        setToday();
    }

    // ~Methods-------------------------------------------------------------------------------------
    /**
     * set the owner of this schedule's name
     * 
     * @param name
     *            the name of the owner of the schedule
     */
    public void setWhosSchedule(String name) {

        whosSchedule = name;
    }

    /**
     * get the owner of this schedule's name
     * 
     * @return whosSchedule the name of the owner of this schedule
     */
    public String getWhosSchedule() {

        return whosSchedule;
    }

    /**
     * sets a day by passed in parameter
     * @param name
     *            name of the course
     * @param subjectCode TODO
     * @param courseNumber TODO
     * @param teacherName
     *            name of the teacher of the course
     * @param time
     *            time the class starts
     * @param location
     *            Builing and room number of the course
     * @param day
     *            the one letter character indicating the day the course is
     *            taught
     */
    public void setDay(String days, String name, 
            String subjectCode, String courseNumber, 
            String teacherName, 
            String beginTime, String endTime, 
            String building, String room) {

        String day = "";

        while (days.length() != 0) {

            // set day equal to one character denoting a day
            day = days.substring(0, 1);
            // eliminate the character that day was set to from days
            days = days.replaceFirst(day, "");

            if (day.equals("M")) {

                monday.addCourse(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                        building, room);
            }
            else if (day.equals("T")) {

                tuesday.addCourse(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                        building, room);
            }
            else if (day.equals("W")) {

                wednesday.addCourse(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                        building, room);
            }
            else if (day.equals("R")) {

                thursday.addCourse(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                        building, room);
            }
            else if (day.equals("F")) {

                friday.addCourse(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                        building, room);
            }
            else if (day.equals("TBA")) {

                anyDay.addCourse(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                        building, room);
            }
            else {

                // enter a string thing
            }
        }
    }
    
    /**
     * takes in a string representing to the days that a course object is held in, 
     * as well as a course object itself
     * 
     * @param days a string of characters where each day is represented by a letter
     *      M = monday
     *      T = tuesday
     *      W = wednesday
     *      R = thursday
     *      F = friday
     *      else = anyDay
     */
    public void setCourseInDays(Course course, String days) {

        for (int i = 0; i < days.length(); i++) {
            
            if (days.substring(i, i + 1).equals("M")) {
                monday.addCourse(course);
            }
            else if (days.substring(i, i + 1).equals("T")) {
                tuesday.addCourse(course);
            }
            else if (days.substring(i, i + 1).equals("W")) {
                wednesday.addCourse(course);
            }
            else if (days.substring(i, i + 1).equals("R")) {
                thursday.addCourse(course);
            }
            else if (days.substring(i, i + 1).equals("F")) {
                friday.addCourse(course);
            }
            else {
                anyDay.addCourse(course);
                break;
            }
        }
    }

    /**
     * returns a Day object based on the index passed in
     * 
     * 0 = monday
     * 1 = tuesday
     * 2 = wednesday
     * 3 = thursday
     * 4 = friday
     * 5 = anyDay
     * 
     * @param index the index of the day to select, 
     *      0 = monday
     *      1 = tuesday
     *      2 = wednesday
     *      3 = thursday
     *      4 = friday
     *      5 = anyDay
     * @return the Day object corresponding to the passed in index
     */
    public Day getDay(int index) {
        
        switch(index) {
            
            case 0: return monday;
            case 1: return tuesday; 
            case 2: return wednesday;
            case 3: return thursday;
            case 4: return friday;
            case 5: return anyDay;
            default: return null;
        }
    }
    
    /**
     * sets the today Day object to today's date
     * 
     * find out how to make it go by the time service????
     */
    public void setToday() {

        cal = Calendar.getInstance();

        switch (cal.get(Calendar.DAY_OF_WEEK)) {

            case Calendar.MONDAY:

                today = monday;
                break;

            case Calendar.TUESDAY:

                today = tuesday;
                break;

            case Calendar.WEDNESDAY:

                today = wednesday;
                break;

            case Calendar.THURSDAY:

                today = thursday;
                break;

            case Calendar.FRIDAY:

                today = friday;
                break;

            default:

                if (anyDay.getList().size() > 0) {

                    today = anyDay;
                }
                else {

                    today = monday;
                }
                break;
        }
    }

    /**
     * returns the Day object that represents today
     * 
     * @return today the day that is today
     */
    public Day getToday() {

        return today;
    }

    /**
     * flips today to the "left" of today and returns today
     * 
     * @return today the object that represents the day being displayed in the
     *         ListView
     */
    public Day getLeftOfToday() {

        Day[] days = daysToArray();

        int index = -1;

        // loop through the days array until...
        for (int i = 0; i < days.length; i++) {

            // the day representing today is found
            if (days[i].equals(getToday())) {

                index = i;
                break;
            }
        }

        // if the index is at its minimum
        if (index == 0) {

            // today is the "last" day (anyDay)
            today = days[days.length - 1];
            if (!today.hasCourses()) {
      
                return getLeftOfToday();
            }
        }
        else {

            // today is the day before the old today
            if (index - 1 >= 0) {
                
                today = days[index - 1];
            }
        }

        return today;
    }

    /**
     * flips today to the "right" of today and returns today
     * 
     * @return today the object that represents the day being displayed in the
     *         ListView
     */
    public Day getRightOfToday() {

        Day[] days = daysToArray();

        int index = -1;

        // loop through the days array until...
        for (int i = 0; i < days.length; i++) {

            // the day representing today is found
            if (days[i].equals(getToday())) {

                index = i;
                break;
            }
        }

        // if the index is at its max already
        if (index == (days.length - 1)) {

            // today is the first day of the week
            today = days[0];
        }
        // otherwise
        else {

            // today is the day after the old today
            today = days[index + 1];
            if (!today.hasCourses() && today.getThisDay().equalsIgnoreCase("anyday")) {
                
                return getRightOfToday();
            }
                
        }

        return today;
    }

    /**
     * Gets the index of the day that today is.
     * 
     * @return the index of the day that today is.
     */
    public int getTodayIndex() {
        
        if (today.equals(monday)) {
            return 0;
        }
        else if (today.equals(tuesday)) {
            return 1;
        }
        else if (today.equals(wednesday)) {
            return 2;
        }
        else if (today.equals(thursday)) {            
            return 3;
        }
        else if (today.equals(friday)) {            
            return 4;
        }
        else if (today.equals(anyDay)) {
            return 5;
        }
        else {
            return -1;
        }
    }
    

    /**
     * Gets the index of the passed Day object. 
     * 
     * @param theDay the Day to get the index of.
     * @return the index of the day passed. -1 if the Day passed
     *          was not one of the days in this Schedule.
     */
    public int getIndexFromDay(Day theDay) {
        
        if (theDay.equals(monday)) {
            
            return 0;
        }
        else if (theDay.equals(tuesday)) {
            
            return 1;
        }
        else if (theDay.equals(wednesday)) {
            
            return 2;
        }
        else if (theDay.equals(thursday)) {
            
            return 3;
        }
        else if (theDay.equals(friday)) {
            
            return 4;
        }
        else if (theDay.equals(anyDay)) {
            
            return 5;
        }
        else {
            
            return -1;
        }
    }
    
    /**
     * returns if the schedule has any classes in it or not
     * 
     * @return value true if the list is empty, false otherwise
     */
    public boolean isEmpty() {

        boolean value;

        if (monday.getList().isEmpty() && tuesday.getList().isEmpty()
                && wednesday.getList().isEmpty()
                && thursday.getList().isEmpty() && friday.getList().isEmpty()
                && anyDay.getList().isEmpty()) {

            value = true;
        }
        else {

            value = false;
        }

        return value;
    }

    /**
     * returns an Day array of all of the day objects in this class
     * 
     * @return days an array of all the day objects
     */
    public Day[] daysToArray() {

        Day[] days = { monday, tuesday, wednesday, thursday, friday, anyDay };

        return days;
    }

    /**
     * takes an array of Days and updates the Day objects with them
     * 
     * @param days
     *            array of all of the days to be translated into the day objects
     */
    public void arrayToDays(Day[] days) {

        monday = days[0];
        tuesday = days[1];
        wednesday = days[2];
        thursday = days[3];
        friday = days[4];
        anyDay = days[5];
    }

    /**
     * runs the insertion sort on all of the days of the week
     */
    public void sortDays() {
        
        monday.sortCourses();
        tuesday.sortCourses();
        wednesday.sortCourses();
        thursday.sortCourses();
        friday.sortCourses();
    }
    
    /**
     * toString method for schedule object.
     * 
     * outputs schedule in xml format: <Schedule> <Owner></Owner> <Monday>
     * <Course></Course> <Course></Course> <Course></Course> </Monday> .....
     * </Schedule>
     * 
     * @return returnString the String representation of the schedule object in
     *         the above form
     */
    public String toXML() {

        String returnString = "<Schedule>" + "\n<Owner>" + whosSchedule
                + "</Owner>" + monday.toXML() + tuesday.toXML()
                + wednesday.toXML() + thursday.toXML() + friday.toXML()
                + anyDay.toXML() + "\n</Schedule>";

        return returnString;
    }

    @Override
    public boolean equals(Object other) {

        boolean value = false;

        if (other instanceof Schedule) {

            for (int i = 0; i < 6; i++) {

                if (this.daysToArray()[i].equals(((Schedule) other)
                        .daysToArray()[i])) {

                    value = true;
                }
                else {

                    value = false;
                    break;
                }
            }
        }

        return value;
    }

    /**
     * takes another schedule and compares it to this schedule to see which
     * courses are in common
     * 
     * returns a Schedule  of all courses that the two schedules have in common
     * 
     * @param other
     *            the Schedule to compare this one to
     * @return shared the Schedule holding all of the courses that are in
     *         common
     */
    public Schedule compareSchedules(Schedule other) {

        Schedule shared;

        Day[] days;

        if (other == null) {

            shared = null;
            days = null;
        }
        else {

            shared = new Schedule();
            days = shared.daysToArray();

            // compares each day to see if there are similar courses
            for (int i = 0; i < daysToArray().length; i++) {

                // compares the courses for this day, adds them to the
                // shared Schedule object
                days[i] = this.daysToArray()[i].compareDays(other.daysToArray()[i]);
            }

            shared.arrayToDays(days);
        }
        
        //sets 'Today' within the shared Schedule, because the days have NOW all been set
        shared.setToday();
        
        //returns the shared Schedule
        return shared;
    }
    
    /**
     * Creates a schedule of free time based on the time blocks occupied in this Schedule object.
     * 
     * Note if this method is used on a schedule that is being used to hold free time, then you
     * will get busy time.
     * 
     * @return freeTime a Schedule object that holds Course objects which represent free time.
     */
    public Schedule findFreeTime() {
        
        Schedule freeTime = new Schedule();
        
        //Loop through each day in this schedule and the freeTime schedule.
        for (int i = 0; i < 5; i++) {
            
            freeTime.setDay(i, this.getDay(i).findFreeTime());
        }
        
        return freeTime;
    }
    
    /**
     * Sets the day object denoted by the passed in index to the passed in Day object.
     * 
     * @param index the index of the day to set.
     * @param day the Day to set to.
     * @return true if index was valid, false otherwise
     */
    public boolean setDay(int index, Day day) {
        
        switch(index) {
            
            case 0: monday = day; break;
            case 1: tuesday = day; break;
            case 2: wednesday = day; break;
            case 3: thursday = day; break;
            case 4: friday = day; break;
            case 5: anyDay = day; break;
            default: return false;
        }
        
        return true;
    }

    // ~PARCELABLE
    // ~STUFF----------------------------------------------------------------------
    // ~----------------------------------------------------------------------------------------

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(monday, flags);
        dest.writeParcelable(tuesday, flags);
        dest.writeParcelable(wednesday, flags);
        dest.writeParcelable(thursday, flags);
        dest.writeParcelable(friday, flags);
        dest.writeParcelable(anyDay, flags);
        dest.writeParcelable(today, flags);
        dest.writeString(whosSchedule);
    }

    /**
     * used to regenerate the Schedule upon receiving it
     */
    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {

        public Schedule createFromParcel(Parcel in) {

            return new Schedule(in);
        }

        public Schedule[] newArray(int size) {

            return new Schedule[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated
    // with it's values
    private Schedule(Parcel in) {

        monday = in.readParcelable(Schedule.class.getClassLoader());
        tuesday = in.readParcelable(Schedule.class.getClassLoader());
        wednesday = in.readParcelable(Schedule.class.getClassLoader());
        thursday = in.readParcelable(Schedule.class.getClassLoader());
        friday = in.readParcelable(Schedule.class.getClassLoader());
        anyDay = in.readParcelable(Schedule.class.getClassLoader());
        today = in.readParcelable(Schedule.class.getClassLoader());
        whosSchedule = in.readString();
    }

    // ~----------------------------------------------------------------------------------------


    // ~THE DAYS OF THE WEEK SETTER AND
    // GETTERS------------------------------------------------------------------------
    /**
     * @param monday
     *            the monday to set
     */
    public void setMonday(Day monday) {

        this.monday = monday;
    }

    /**
     * @return the monday
     */
    public Day getMonday() {

        return monday;
    }

    /**
     * @param tuesday
     *            the tuesday to set
     */
    public void setTuesday(Day tuesday) {

        this.tuesday = tuesday;
    }

    /**
     * @return the tuesday
     */
    public Day getTuesday() {

        return tuesday;
    }

    /**
     * @param wednesday
     *            the wednesday to set
     */
    public void setWednesday(Day wednesday) {

        this.wednesday = wednesday;
    }

    /**
     * @return the wednesday
     */
    public Day getWednesday() {

        return wednesday;
    }

    /**
     * @param thursday
     *            the thursday to set
     */
    public void setThursday(Day thursday) {

        this.thursday = thursday;
    }

    /**
     * @return the thursday
     */
    public Day getThursday() {

        return thursday;
    }

    /**
     * @param friday
     *            the friday to set
     */
    public void setFriday(Day friday) {

        this.friday = friday;
    }

    /**
     * @return the friday
     */
    public Day getFriday() {

        return friday;
    }

    /**
     * @param anyDay
     *            the anyDay to set
     */
    public void setAnyDay(Day anyDay) {

        this.anyDay = anyDay;
    }

    /**
     * @return the anyDay
     */
    public Day getAnyDay() {

        return anyDay;
    }
}
