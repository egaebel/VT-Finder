package vt.finder.main;

import java.util.ArrayList;
import vt.finder.quadtree.LeafNode;
import vt.finder.quadtree.QuadTree;
import vt.finder.schedule.Course;
import vt.finder.schedule.Day;
import vt.finder.schedule.Schedule;

/**
 * Class that holds two 4 schedule objects, one is the user's, one is his
 * friend's the other two represent a common schedule between them, one for
 * similar classes, one for similar free time
 * 
 * the comparisons are performed in the constructor, because why else do you
 * want this class???
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 *         last updated 3/22/2012
 * 
 */
public class ScheduleComparison {

    // ~Constants
    /**
     * the upper bounds of the quad tree
     */
    private static final int UPPER_BOUND = 240000000;

    /**
     * the lower bounds of the quad tree
     */
    private static final int LOWER_BOUND = 0;

    // ~Data Fields--------------------------------------------
    /**
     * my schedule
     */
    private Schedule mine;

    /**
     * friend's schedule
     */
    private Schedule other;

    /**
     * Schedule object holding the similar courses that the two schedules share
     */
    private Schedule sharedCourses;

    /**
     * Schedule object holding the similar freetime that the two courses share
     */
    private Schedule sharedFreeTime;

    /**
     * true when freeTime schedule is being displayed currently
     * 
     * false when sharedCourses schedule is being displayed currently
     */
    private boolean designator;

    // ~Constructors--------------------------------------------
    /**
     * takes two schedules as arguments and uses them as values for mine and
     * other, also initializes the quad tree to an 800 by 2200 boundary area to
     * represent classes from 8am to 10pm
     * 
     * @param mine
     *            the schedule that is "mine"
     * @param other
     *            the schedule that is not "mine"
     */
    public ScheduleComparison(Schedule mine, Schedule other) {

        this.mine = mine;
        this.other = other;
        
        // set to false initially because commonClasses are shown first
        designator = false;

        // performs the comparisons of the two schedules
        calcCommonClasses();
        calcChillTime();
    }

    // ~Methods--------------------------------------------
    /**
     * takes the two Schedules contained in this class and compares them with
     * each other checking for similar classes
     */
    private void calcCommonClasses() {

        sharedCourses = mine.compareSchedules(other);
    }

    /**
     * takes the two Schedules contained in this class and compares them with
     * each other, checking for blocks of time that are greater than 30 minutes,
     * where both Schedules do not have class
     * 
     * @return common free time between two schedules that is > 30 minutes
     */
    private void calcChillTime() {

        // Return an Array list of Course objects named "Chill Time"
        // with different times and no other information

        // use one quad tree to hold both Schedule's courses, one day at a time

        // go from origin to first point in my schedule, search for course from
        // others schedule
        // if course found, adjust to create rectangle from origin to first
        // point in others schedule, then that area is free time
        // also create rectangle from others schedule to next point in my
        // schedule
        // recurse
        // if no course found, that area is free
        // base case

        // problem: how to get the points in my schedule?
        // solution: schedule list for my schedule, for each day

        // since traversal from point to point in schedule will be done in
        // linear time no matter what the structure, this is max efficiency

        // Schedule object used to hold the resultant free times that are
        // similar between the two schedules
        Schedule shared = new Schedule("Shared");

        // initializes a Day array to the days in shared
        //Day[] days = shared.daysToArray();

        // quad tree object used to compare two people's schedules
        QuadTree quad = new QuadTree(UPPER_BOUND, UPPER_BOUND, LOWER_BOUND,
                LOWER_BOUND);

        // arraylist used to hold the intervals within which to search for free
        // time in each Course list contained in a day object
        ArrayList<Course> intervals = new ArrayList<Course>();

        // array list used to hold the free times, per day
        ArrayList<Course> freeTimes = new ArrayList<Course>();

        // cycles through the days for both my schedule and the others
        // at each day performs a comparison relating free times of day >= 30
        // minutes
        Day myDay;
        Day otherDay;
        int daysCourses;
        // subtract one from daysToArray().length because I don't care about
        // anyDay chilltime
        for (int i = 0; i < (mine.daysToArray().length - 1); i++) {

            myDay = mine.getDay(i);
            otherDay = other.getDay(i);
            
            // sets daysCourses to the larger of the two days course sizes
            daysCourses = myDay.getList().size();
            if (daysCourses < otherDay.getList().size()) {

                daysCourses = otherDay.getList().size();
            }

            // cycles through the course list for the day, adding all courses
            // to the quad tree
            for (int k = 0; k < daysCourses; k++) {

                // check for index out of bound errors on both lists when
                // inserting into quad
                if (k < myDay.getList().size()) {
                    
                    quad.insert(new LeafNode(myDay.getList().get(k)));
                }
                if (k < otherDay.getList().size()) {

                    quad.insert(new LeafNode(otherDay.getList().get(k)));
                }
            }
            
            // sets up intervals object with the day's course objects as well as
            // the beginning and end points of 8am and 10pm
            intervals.addAll((ArrayList<Course>)myDay.getList());
            intervals.add(0, new Course("Start Day", "8:00 am", "8:00 am"));
            intervals.add(intervals.size(), new Course("End Day",
                    "10:00 pm", "10:00 pm"));
            
            // search for free time within the day that this iteration of the
            // for loop
            // designates and add the result to the freeTimes
            // ArrayList<Course>
            freeTimes.addAll(quad.freeFinder(intervals));

            // sets the Course list of the current day in the shared Schedule
            // to the freeTimes ArrayList<Course>
            shared.getDay(i).setList(freeTimes);

            // ---------resets all data structures for next run-----------------
            // resets quadTree for next day of courses
            quad.makeNull();
            // resets the freeTimes arrayList for next run
            // as well as the intervals arraylist
            freeTimes.clear();
            intervals.clear();
        }

        // sets the value of today on the newly created shared Schedule
        shared.setToday();

        // return the freeTimes ArrayList populated with Course objects that
        // represent
        // all of the intervals of time that are "free"
        sharedFreeTime = shared;
    }

    /**
     * gets the sharedCourses Schedule object
     * 
     * @return sharedCourses the Schedule that holds the similar Courses
     */
    public Schedule getSharedCourses() {

        // sets designator to false to indicate sharedCourses called last
        designator = false;

        return sharedCourses;
    }

    /**
     * gets the sharedFreeTime Schedule object
     * 
     * @return sharedFreeTime the Schedule that holds the similar free times
     */
    public Schedule getSharedFreeTime() {

        // sets designator to true to indicate sharedFreeTime called last
        designator = true;

        return sharedFreeTime;
    }

    /**
     * gets the Schedule that is NOW available to the user
     * 
     * designator is true when sharedFreeTime is selected false when
     * sharedCourses is selected
     * 
     * @return sharedCourses OR sharedFreeTime depending on the status of
     *         designator
     */
    public Schedule getCurrentComparison() {

        if (designator) {

            return sharedFreeTime;
        }
        else {

            return sharedCourses;
        }
    }
}