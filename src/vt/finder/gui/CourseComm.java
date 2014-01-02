package vt.finder.gui;

import vt.finder.schedule.Course;

/**
 * Interface used for passing a Course from a Fragment to an activity.
 * 
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public interface CourseComm {

    /**
     * Takes a course object and sets some data field to the course.
     * 
     * @param course the Course to set the field to.
     */
    public void setCourse(Course course);
}
