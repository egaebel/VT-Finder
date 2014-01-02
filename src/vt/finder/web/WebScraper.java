package vt.finder.web;

import java.util.Calendar;
import java.util.List;
import vt.finder.schedule.Course;
import vt.finder.schedule.Schedule;

/**
 * This class uses the methods of the VT Access API to connect to hokiespa
 * and scrape exam time information as well as schedule information.
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class WebScraper {

    //~Methods-------------------------------------------------
    /**
     * Logs the user into CAS using the passed username and password. Throws a WrongLoginException 
     * if there was an incorrect username or password entered. 
     * 
     * @param username the username.
     * @param password the password.
     * @return true if successful, false otherwise.
     * @throws WrongLoginException indicates an incorrect username or password was entered.
     * @throws vt.finder.web.WrongLoginException 
     * @throws vt.finder.web.WrongLoginException 
     */
    public boolean login(String username, String password, String certFilePath) throws WrongLoginException {
        
        return Cas.login(username, password, certFilePath);
    }
    
    /**
     * Grabs a user's schedule and fills the passed in schedule object with it.
     * 
     * @param schedule the schedule to fill.
     * @param semester the semester code of the semester a schedule is desired for.
     * @return true if successful, false otherwise.
     */
    public boolean scrapeSchedule(Schedule schedule, String semester) {

        boolean value = ScheduleScraper.retrieveSchedule(schedule, getSemesterCode(semester)); 

        return value;
    }
    
    /**
     * Takes in a List of Course objects and fills it with the exam schedule for the user.
     * 
     * @param examList the List<Course> that is to be filled with exams.
     * @param semester the semesterCode for the semester exams are desired for.
     * @return true if successful, false otherwise.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean scrapeExamSchedule(List examList, String semester) {
        
        return ScheduleScraper.retrieveExamSchedule(getSemesterCode(semester), 
                (List<Course>) examList);
    }
    
    /**
     * Checks to see if there is an active CAS session.
     * 
     * @return true if there is an active CAS session, false otherwise.
     */
    public boolean isLoginActive() {
        
        return Cas.isActive();
    }
    
    /**
     * Logs the user out of CAS.
     * 
     * @return true if successful. False otherwise
     */
    public boolean logout() {
        
        return Cas.logout();
    }
    
    /**
     * Gets the year to tack on to the submitted semester.
     * 
     * @return a String that goes <year><semester>
     */
    private String getSemesterCode(String semester) {
        
        Calendar cal = Calendar.getInstance();
        
        int year = cal.get(Calendar.YEAR);
        
        //If the spring semester is the target, and it's November or December.
        if (Integer.valueOf(semester) == 1 && cal.get(Calendar.MONTH) > 10) {
            
            return (year + 1) + semester;
        }
        //if the fall semester is the target, and it's earlier than April.
        else if (Integer.valueOf(semester) == 9 && cal.get(Calendar.MONTH) < 3) {
            System.out.println("HERE IT IS!!!! FUCKKKKINNGG YOUUU OVERRRR!!!");
            System.out.println("So semester is: " + semester + " and Month is: " + cal.get(Calendar.MONTH));
            return (year - 1) + semester;
        }

        return year + semester;
    }
}
