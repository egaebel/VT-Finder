package vt.finder.schedule;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contains an ArrayList of courses that represent a student's course-load for
 * any given day
 * 
 * an arraylist is used to ease the passing of values into the ListView in the
 * activity
 * 
 * @author Ethan Gaebel(egaebel)
 * 
 */
public class Day implements Parcelable {

    // ~Data Fields------------------------------------------------------------
    /**
     * holds a list of Courses in a given day
     */
    private ArrayList<Course> daily;

    /**
     * the String designating what day of the week this day is
     */
    private String thisDay;

    // ~Constructors------------------------------------------------------------    
    /**
     * initializes the ArrayList daily and sets the String value of thisDay
     */
    public Day(String thisDay) {
        
        daily = new ArrayList<Course>();
        this.thisDay = thisDay;
    }

    // ~Methods------------------------------------------------------------
    /**
     * adds a course to the day's arrayList
     * 
     * @param added
     *            the course to be added to the arrayList
     */
    public void addCourse(String name, String subjectCode, String courseNumber, String teacherName, String beginTime,
            String endTime, String building, String room) {

        Course addingCourse = new Course(name, subjectCode, courseNumber, teacherName, beginTime, endTime,
                building, room);

        daily.add(addingCourse);
    }

    /**
     * adds the passed course to this object's daily course list
     * 
     * @param newCourse
     *            the course to add to the daily list
     */
    public void addCourse(Course newCourse) {

        daily.add(newCourse);
    }

    /**
     * removes the passed in course from the arraylist
     * 
     * @param remove
     *            course to be removed
     */
    public void removeCourse(Course remove) {

        daily.remove(remove);
    }

    /**
     * Checks to see if the daily course list has any members in it.
     * Returns true if there are courses, false if there are none.
     * 
     * @return true if there are courses, false if there aren't.
     */
    public boolean hasCourses() {
        
        return !daily.isEmpty();
    }
    
    /**
     * gets the course of a specified index
     * 
     * @param index
     *            the index of the course to get
     * @return the course at the specified index
     */
    public Course getCourse(int index) {

        if (daily.size() > index) {
            
            return daily.get(index);
        }
        
        return null;
        
    }

    /**
     * performs an insertion sort. Sorted by start time on the ArrayList<Course>
     */
    public void sortCourses() {

        Course course = null;
        int j = 0;
        for (int i = 1; i < size(); i++) {
            
            j = i; 
            //checks to see if the start time of the element j is less than the previous element, and if j > 0
            while (j > 0) {
                
                if ((daily.get(j).getCoursePoint().getX() < daily.get(j - 1).getCoursePoint().getX())) {
                
                    //if so, swap the two
                    course = daily.get(j);
                    daily.set(j, daily.get(j - 1));
                    daily.set(j - 1, course);
                }
                
                //decrement j
                j--;
            }
        }
    }

    /**
     * setter method for the daily arrayList of courses
     * 
     * @param theList
     *            the ArrayList<Course> to set daily equal to
     */
    public void setList(ArrayList<Course> theList) {

        daily = new ArrayList<Course>(theList);
    }

    /**
     * getter method for the daily ArrayList of courses
     * 
     * @return daily the arrayList with the courses of the day
     */
    public ArrayList<Course> getList() {

        return daily;
    }


    /**
     * sets the thisDay object which designated what day this Day object
     * represents
     * 
     * @param thisDay
     *            the String designating which day of the week thisDay is
     */
    public void setThisDay(String thisDay) {

        this.thisDay = thisDay;
    }
    
    /**
     * Getter method for the thisDay String which identifies the name of 
     * the Day. (ex. Monday, tuesday etc)
     * @return thisDay the name of this day
     */
    public String getThisDay() {
        
        return thisDay;
    }

    /**
     * tells what day of the week this day object represents
     * 
     * @return thisDay the String representing this day
     */
    @Override
    public String toString() {

        return thisDay;
    }

    /**
     * returns the size of the daily arrayList
     */
    public int size() {

        return daily.size();
    }

    /**
     * toString method for Day object
     */
    public String toXML() {

        String returnString = "";
        String courseList = "";

        for (Course course : daily) {

            courseList = courseList + course.toXML();
        }

        returnString = "\n<" + thisDay + ">" + courseList + "\n</" + thisDay
                + ">";

        return returnString;
    }

    /**
     * compares this day object to a passed in day object, returns an ArrayList
     * of all of the Courses that the two days share
     * 
     * @param other
     *            the passed in day to compare this day to
     * @return shared the ArrayList of courses the two days have in common
     */
    public Day compareDays(Day other) {

        Day shared;

        // if the passed in day is null, return null
        if (other == null) {

            shared = null;
        }
        else {

            // initialized shared with the string representation of "this" day
            shared = new Day(thisDay);

            // loops through this course list
            for (int i = 0; i < this.getList().size(); i++) {

                // loops through other course List
                for (int j = 0; j < other.getList().size(); j++) {

                    // if two days have the same course, then add them to the
                    // shared Day
                    if (this.getList().get(i).equals(other.getList().get(j))) {

                        shared.addCourse(this.getList().get(i));
                    }
                }
            }
        }

        return shared;
    }
    
    /**
     * Finds the blocks of free time between the courses in this Day.
     * 
     * @return freeTime a Day object which holds courses representing
     *          free time slots.
     */
    public Day findFreeTime() {

        Day freeTime = new Day(this.getThisDay());
        
        Course prev = new Course("Free Time", "8:00 AM", "8:00 AM");
        //Loop over all courses and find the breaks between them.
        for (Course course : daily) {

            //If next thing DOESNT begin at the same time the previous ends
            if (!prev.getEndTime().equals(course.getBeginTime())) {
                
                //Create a new Course object spanning the time between the two courses, add to freeTime day
                freeTime.addCourse(new Course("Free Time", prev.getEndTime(), course.getBeginTime()));
            }
            
            prev = course;
        }
        //add a free time chunk going from the end of the last course, to 10pm
        freeTime.addCourse(new Course("Free Time", prev.getEndTime(), "10:00 PM"));

        return freeTime;
    }

    @Override
    public boolean equals(Object other) {

        boolean value = false;

        // if other is null
        if (other == null) {

            value = false;
        }
        // otherwise
        else {

            // if other isnt a Day object
            if (!(other instanceof Day)) {

                value = false;
            }
            // otherwise
            else {

                if (this.toString().equals(((Day) other).toString())) {

                    // if the sizes of the courseLists arent the same, false
                    if (this.size() != ((Day) other).size()) {

                        value = false;
                    }
                    // if the sizes are both 0, true
                    else if (this.size() == 0) {

                        value = true;
                    }
                    // otherwise cycle through courses
                    else {

                        // cycle through course lists of both days
                        for (int i = 0; i < this.size(); i++) {

                            // if there is a course in other that doesnt equal
                            // the corresponding course in this,
                            // then break loop, value = false
                            if (!(this.getCourse(i).equals(((Day) other)
                                    .getCourse(i)))) {

                                value = false;
                                break;
                            }
                            // otherwise
                            else {

                                value = true;
                            }
                        }
                    }
                }
                // otherwise
                else {

                    value = false;
                }
            }
        }

        return value;
    }

    // ~PARCELABLE
    // STUFF----------------------------------------------------------------------
    // ~----------------------------------------------------------------------------------------

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeList(daily);
        dest.writeString(thisDay);
    }

    /**
     * used to regenerate the Schedule upon receiving it
     */
    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {

        public Day createFromParcel(Parcel in) {

            return new Day(in);
        }

        public Day[] newArray(int size) {

            return new Day[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated
    // with it's values
    private Day(Parcel in) {

        daily = new ArrayList<Course>();

        in.readList(daily, Course.class.getClassLoader());
        thisDay = in.readString();
    }
    // ~----------------------------------------------------------------------------------------

}