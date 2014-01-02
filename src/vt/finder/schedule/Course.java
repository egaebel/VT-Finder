package vt.finder.schedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A course object that contains the courseName, teacherName, location, and
 * beginning time, and ending time, and a Point object that holds formatted
 * beginning and ending times
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 *         updated 3/22/2012
 */
public class Course implements Parcelable {

    // ~Constants----------------------------------------------------
    protected int FACTOR = 10000;
    
    // ~Data Fields-------------------------------------------------
    /**
     * the time the course begins
     */
    protected String beginTime;

    /**
     * the time the course ends
     */
    protected String endTime;

    /**
     * a Point object that contains the adjusted time values for the beginning
     * time and ending time
     */
    protected Point coursePoint;

    /**
     * the name of the course
     */
    protected String name;

    /**
     * the teacher's name
     */
    protected String teacherName;

    /**
     * the building of the course
     */
    protected String building;

    /**
     * the room number of the course
     */
    protected String room;
    
    /**
     * The subject code of this Course.
     */
    protected String subjectCode;
    
    /**
     * The 4-digit course number of this Course. ex. 1105.
     */
    protected String courseNumber;

    /**
     * the date object that represents the day this course lies on
     * 
     * rarely used
     */
    protected Date date;

    // ~Constructors-------------------------------------------------
    /**
     * default constructor, initializes empty coursePoint object, and empty Date
     * object
     */
    public Course() {

        coursePoint = new Point();

        date = null;
    }
    
    /**
     * Course constructor that is used to denote something that is not a typical
     * course object. (for instance, free time).
     * 
     * @param name the name of this different event
     * @param begint the time this event begins
     * @param end the time thie event ends.
     */
    public Course(String name, String begin, String end) {
        
        setName(name);
        setTeacherName("");

        this.setBuilding("");
        this.setRoom("");

        coursePoint = new Point();
        setBeginTime(begin);
        setEndTime(end);
    }

    /**
     * the constructor for the Course, takes a beginTime, as well as the name,
     * teacherName, and location, and a string in long date format used to
     * initialize the date object (ex. December 18, 1988)
     * 
     * @param name
     *            the name of the course
     * @param teacherName
     *            the teacher's name of the course
     * @param begin
     *            the beginning time of the course
     * @param end
     *            the ending time of the course
     * @param newDate
     *            the long format of the date in String form (ex. December 18,
     *            2054) used to initialize the date object
     * @param building
     *            the building that the class is in
     * @param room
     *            the room that the class is in
     */
    public Course(String name, String teacherName, String begin, String end,
            String newDate, String building, String room) {

        setName(name);
        setTeacherName(teacherName);

        this.setBuilding(building);
        this.setRoom(room);

        coursePoint = new Point();
        setBeginTime(begin);
        setEndTime(end);

        // sets the date object using the long string newDate
        date = new Date(newDate, "long date format");
    }
    
    /**
     * the constructor for the Course, takes a beginTime, as well as the name,
     * teacherName, and location, and a string in long date format used to
     * initialize the date object (ex. December 18, 1988)
     * 
     * @param name
     *            the name of the course
     * @param teacherName
     *            the teacher's name of the course
     * @param begin
     *            the beginning time of the course
     * @param end
     *            the ending time of the course
     * @param theDate
     *            Date object that this course's date object will be set equal to
     * @param building
     *            the building that the class is in
     * @param room
     *            the room that the class is in
     */
    public Course(String name, String teacherName, String begin, String end,
            Date theDate, String building, String room) {

        setName(name);
        setTeacherName(teacherName);

        this.setBuilding(building);
        this.setRoom(room);

        coursePoint = new Point();
        setBeginTime(begin);
        setEndTime(end);

        // sets the date object using the long string newDate
        date = theDate;
    }
    
    /**
     * Basic course constructor, has all the essentials.
     * 
     * @param name the name of the course.
     * @param subjectCode the subjectCode of the course.
     * @param courseNumber the courseNumber of the course.
     * @param teacherName the name of the teacher for the course.
     * @param begin the time that the course begins at.
     * @param end the time that the course ends at.
     * @param building the building that the course is taught in.
     * @param room the room that the course is taught in.
     */
    public Course (String name, String subjectCode, String courseNumber, 
            String teacherName, String begin, String end, String building, String room) {
        
        setName(name);
        setTeacherName(teacherName);

        this.setBuilding(building);
        this.setRoom(room);

        coursePoint = new Point();
        setBeginTime(begin);
        setEndTime(end);

        setSubjectCode(subjectCode);
        setCourseNumber(courseNumber);
    }
    
    /**
     * Final exam constructor.
     * 
     * @param name the name of the course.
     * @param subjectCode the subjectCode of the course.
     * @param courseNumber the courseNumber of the course.
     * @param begin the beginning time of the course.
     * @param end the ending time of the course.
     * @param newDate the date, in string form of the exam/course.
     */
    public Course (String name, String subjectCode, String courseNumber, 
            String begin, String end, String newDate) {
        
        setName(name);
        setTeacherName("");

        this.setBuilding("");
        this.setRoom("");

        coursePoint = new Point();
        setBeginTime(begin);
        setEndTime(end);

        setSubjectCode(subjectCode);
        setCourseNumber(courseNumber);
        
        // sets the date object using the long string newDate
        date = new Date(newDate, "long date format");
    }
    
    /**
     * Final exam constructor.
     * 
     * @param name the name of the course.
     * @param subjectCode the subjectCode of the course.
     * @param courseNumber the courseNumber of the course.
     * @param begin the beginning time of the course.
     * @param end the ending time of the course.
     * @param newDate the date, in a date object of the exam/course.
     */
    public Course(String name, String subjectCode, String courseNumber, 
            String begin, String end, Date newDate) {
        
        setName(name);
        setTeacherName("");

        this.setBuilding("");
        this.setRoom("");

        coursePoint = new Point();
        setBeginTime(begin);
        setEndTime(end);

        setSubjectCode(subjectCode);
        setCourseNumber(courseNumber);
        
        // sets the date object using the passed date object
        date = newDate;
    }

    // ~Methods------------------------------------------------------
    /**
     * setter for the beginTIme
     * 
     * @param begin
     *            the beginning time of the course
     */
    public void setBeginTime(String begin) {
        if (begin != null) {
            
            beginTime = begin;
        }
        else {
            
            beginTime = " ";
        }
        
        // sets the Point's X value in this Course object to the value of begin
        // after formatting
        if ((begin != null) && !(begin.equals("N/A"))
                && !(begin.contains("ARR")) && !(begin.equalsIgnoreCase("TBA"))
                && !(begin.equals(""))) {
            
            // split the time string apart by the colon
            // ex. 1:30 pm --> {1, 30 pm}
            String[] temp = begin.split(":");

            // rejoin the split strings, into a string the same without the
            // colon
            String amPm;
            //in case there wasn't a time separated by a semi-colon
            //if semi-colon
            if (temp.length == 2) {

                //proceed
                amPm= temp[0] + temp[1];
                
                // split the time string apart by the space
                // ex. 130 pm --> {130, pm}
                String[] temp2 = amPm.split(" ");
                
                //check to see if the am or pm was stuck to the end of the
                //time or not... (stuck to example. 3:00PM)
                if (temp2.length != 2) {
                    
                    //place temp 2 in temp22
                    String[] temp22 = temp2;
                    
                    //reset temp2 and make it 2 large
                    temp2 = new String[2];
                    //place the cut up temp22 back into temp 2 to work with everything else
                    temp2[1] = temp22[0].substring(temp22[0].length() - 2, temp22[0].length());
                    temp2[0] = temp22[0].substring(0, temp22[0].length() - 2);
                }

                int adjustedTime = Integer.parseInt(temp2[0]);
                adjustedTime *= FACTOR;

                if (((temp2[1].contains("PM")) || (temp2[1].contains("pm")))
                        && (!(temp[0].equals("12")))) {
                    
                    adjustedTime += (1200 * FACTOR);
                }
                
                coursePoint.setX(adjustedTime);
                
            }
            else {
                //if no semi-colon make the coursePoint default
                coursePoint.setX(0);
            }
        }
    }

    /**
     * setter for the endTime
     * 
     * @param end
     *            the ending time of the course
     */
    public void setEndTime(String end) {

        if (end != null) {
            
            endTime = end;
        }
        else {
            
            endTime = " ";
        }

        // sets the Point in this Course object's Y value to end after
        // formatting
        if ((end != null) && !(end.equals("N/A")) 
                && !(end.contains("ARR")) && !(end.equalsIgnoreCase("TBA"))
                && !(end.equals(""))) {
            
            // split the time string apart by the colon
            // ex. 1:30 pm --> {1, 30 pm}
            String[] temp = end.split(":");

            //in case there wasn't a time separated by a semi-colon
            //if semi-colon
            if (temp.length == 2) {
                // rejoin the split strings, into a string the same without the
                // colon
                String amPm = temp[0] + temp[1];
    
                // split the time string apart by the space
                // ex. 130 pm --> {130, pm}
                String[] temp2 = amPm.split(" ");
    
                //check to see if the am or pm was stuck to the end of the
                //time or not... (stuck to example. 3:00PM)
                if (temp2.length != 2) {
                    
                    //place temp 2 in temp22
                    String[] temp22 = temp2;
                    
                    //reset temp2 and make it 2 large
                    temp2 = new String[2];
                    //place the cut up temp22 back into temp 2 to work with everything else
                    temp2[1] = temp22[0].substring(temp22[0].length() - 2, temp22[0].length());
                    temp2[0] = temp22[0].substring(0, temp22[0].length() - 2);
                }
                
                int adjustedTime = Integer.parseInt(temp2[0]);
                adjustedTime *= FACTOR;
    
                // check if the time is am or pm
                if (((temp2[1].contains("PM")) || (temp2[1].contains("pm")))
                        && (!(temp[0].equals("12")))) {
    
                    adjustedTime += 1200 * FACTOR;
                }
                
                coursePoint.setY(adjustedTime);
            }
            else {
                //if no semi-colon make the coursePoint default
                coursePoint.setY(0);
            }
        }
    }

    /**
     * getter for the beginTIme
     * 
     * @return beginTime
     */
    public String getBeginTime() {

        return beginTime;
    }

    /**
     * getter for the endTime
     * 
     * @return endTime
     */
    public String getEndTime() {

        return endTime;
    }

    /**
     * returns a Point object that contains the adjusted beginning and end times
     * for this object
     * 
     * @return coursePoint an object holding the start and end times for this
     *         object
     */
    public Point getCoursePoint() {

        return coursePoint;
    }

    /**
     * setter method for the name of the course
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {

        if (name != null && !name.equals("")) {
            name = name.replaceAll("&", "and");
            this.name = name;
        }
        else {
            
            name = "UNKNOWN";
        }
    }

    /**
     * getter method for the name of the course
     * 
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * @return the subjectCode
     */
    public String getSubjectCode() {

        return subjectCode;
    }

    /**
     * @param subjectCode the subjectCode to set
     */
    public void setSubjectCode(String subjectCode) {

        this.subjectCode = subjectCode;
    }

    /**
     * @return the courseNumber
     */
    public String getCourseNumber() {

        return courseNumber;
    }

    /**
     * @param courseNumber the courseNumber to set
     */
    public void setCourseNumber(String courseNumber) {

        this.courseNumber = courseNumber;
    }

    /**
     * @param teacherName
     *            the teacherName to set
     */
    public void setTeacherName(String teacherName) {

        if (teacherName != null && !teacherName.equals("") && !teacherName.equals("null")) {
            
            this.teacherName = teacherName;
        }
        else {
            
            this.teacherName = " ";
        }
    }

    /**
     * @return the teacherName
     */
    public String getTeacherName() {

        return teacherName;
    }

    /**
     * @param building
     *            the building to set
     */
    public void setBuilding(String building) {

        if (building != null && !building.equals("")) {
            
            this.building = building;
        }
        else {
            
            this.building = " ";
        }
        
    }

    /**
     * @return the building
     */
    public String getBuilding() {

        return building;
    }

    /**
     * @param room
     *            the room to set
     */
    public void setRoom(String room) {

        if (room != null && !room.equals("") && !room.equals("null")) {

            this.room = room;
        }
        else {

            this.room = "N/A";
        }

    }

    /**
     * @return the room
     */
    public String getRoom() {

        return room;
    }

    /**
     * @return the date
     */
    public Date getDate() {

        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {

        if (date != null) {
            
            this.date = date;
        }
        else {
            
            date = new Date();
        }
    }

    /**
     * equals method for course, determines if two Course objects are equal
     * 
     * @param thing
     *            the course to compare this one to
     * @return value true if they're equal, false otherwise
     */
    @Override
    public boolean equals(Object thing) {

        boolean value = false;

        if (thing == null) {

            value = false;
        }
        else if (thing instanceof Course) {

            Course other = (Course) thing;

            if (name == null || building == null || room == null
                    || beginTime == null) {

                value = false;
            }
            else if (name.equals(other.getName())
                    && building.equals(other.getBuilding())
                    && room.equals(other.getRoom())
                    && beginTime.equals(other.getBeginTime())) {

                value = true;
            }
        }

        return value;
    }

    /**
     * toString method for the Course object returns an xml representation of
     * the course as below:
     * 
     * <Course> <Name></Name> <Teacher></Teacher> <Time></Time>
     * <Building></Building> <Room></Room> </Course>
     * 
     * @return returnString the xml string rep of the course
     */
    public String toXML() {

        String returnString = "";

        String dateXML = "";
        
        if (date != null) {
            
            dateXML = "\n<Date>" + date.toString() + "</Date>";
        }
        
        returnString = "\n<Course>" + "\n<Name>" + name + "</Name>"
                + "\n<SubjectCode>" + subjectCode + "</SubjectCode>" 
                + "\n<CourseNumber>" + courseNumber + "</CourseNumber>"
                + "\n<Teacher>" + teacherName + "</Teacher>" + "\n<BeginTime>"
                + beginTime + "</BeginTime>" + "\n<EndTime>" + endTime
                + "</EndTime>" + "\n<Building>" + building + "</Building>"
                + "\n<Room>" + room + "</Room>" + dateXML + "\n</Course>";
        
        return returnString;
    }

    @Override
    public String toString() {

        //Set dateString conditionally
        String dateString = "";
        if (date != null) {

            dateString = "\n" + date.toString();
        }

        //set courseCodeString conditionally
        String courseCodeString = "";
        if (subjectCode != null && !subjectCode.trim().equals("") && courseNumber != null && !courseNumber.trim().equals("")) {
            
            courseCodeString = "\n" + subjectCode + "-" + courseNumber;
        }
        
        //set timeString conditionally
        String timeString = "";
        if (beginTime != null && beginTime.length() > 2 && !beginTime.equalsIgnoreCase("N/A")) {
            
            timeString = "\n" + beginTime + " - " + endTime;
        }
        
        //set locationString conditionally
        String locationString = "";
        if (building != null && room != null && !building.equalsIgnoreCase("TBA") && !room.equalsIgnoreCase("N/A")) {
            
            locationString = "\n" + building + " " + room;
        }
        
        //set teacherString conditionally
        String teacherString = "";
        if (teacherName != null && !teacherName.trim().equalsIgnoreCase("")) {
            
            teacherString = "\n" + teacherName;
        }
        
        return name + courseCodeString + teacherString + timeString + locationString + dateString;
    }

    // ~PARCELABLE
    // STUFF----------------------------------------------------------------------
    // ~----------------------------------------------------------------------------------------

    public int describeContents() {

        return 0;
    }

    /**
     * writes the contents of Course to the passed in parcel with flags on the objects.
     * @param dest the Parcel to write to
     * @param flags the flags to mark objects with.
     */
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(subjectCode);
        dest.writeString(courseNumber);
        dest.writeString(beginTime);
        dest.writeString(endTime);
        dest.writeString(name);
        dest.writeString(teacherName);
        dest.writeString(building);
        dest.writeString(room);
        dest.writeParcelable(coursePoint, flags);
        dest.writeParcelable(date, flags);
    }

    /**
     * used to regenerate the Schedule upon receiving it
     */
    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {

        public Course createFromParcel(Parcel in) {

            return new Course(in);
        }

        public Course[] newArray(int size) {

            return new Course[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated
    // with it's values
    private Course(Parcel in) {

        subjectCode = in.readString();
        courseNumber = in.readString();
        beginTime = in.readString();
        endTime = in.readString();
        name = in.readString();
        teacherName = in.readString();     
        building = in.readString();
        room = in.readString();
        coursePoint = in.readParcelable(Course.class.getClassLoader());
        date = in.readParcelable(Course.class.getClassLoader());
    }
    // ~----------------------------------------------------------------------------------------
    
    /**
     * Takes in a course code in the format: XX->XXXX-nnnn or XX->XXXX nnnn and splits it into
     * a two element String array where the subject code (the first portion) 
     * is the first element, and the course number (second portion) is the 
     * second element. 
     * 
     * Course code must have its subjectCode and courseNumber separated by either a dash "-"
     * or a space " ".
     * 
     * XX->XXXX signifies that there are 2 to 4 characters.
     * nnnn signifies that there are 4 numbers. 
     * 
     * @param courseCode the course code in the format: XX->XXXX-nnnn.
     *              If this format is not followed, null is returned.
     * @return courseCodes 2 element String array holding the subject code and
     *              course number, or null if the format specified was not followed.
     */
    public static String[] splitCourseCode(String courseCode) {
        
        String[] courseCodes = courseCode.split("-");
        
        if (courseCodes.length == 2) {
            //if the subject code was entered properly
            if(courseCodes[0].length() >= 2 && courseCodes[0].length() <= 4) {
                
                //if the course number was entered properly
                if (courseCodes[1].length() == 4) {
                    
                    return courseCodes;
                }
            }
        }
        else {
            
            courseCodes = courseCode.split(" ");
            
            if (courseCodes.length == 2) {
                //if the subject code was entered properly
                if(courseCodes[0].length() >= 2 && courseCodes[0].length() <= 4) {
                    
                    //if the course number was entered properly
                    if (courseCodes[1].length() == 4) {
                        
                        return courseCodes;
                    }
                }
            }
        }
        
        return null;
    }
}