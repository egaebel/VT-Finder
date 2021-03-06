package vt.finder.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.util.Log;
import vt.finder.schedule.Course;
import vt.finder.schedule.Schedule;

/**
 * ScheduleScraper class that can be used to scrape the 
 * user currently logged into the CAS object's Schedule, and ExamSchedule,
 * from passed in scheduleCodes (yyyymm). The year then the month that the
 * user is concerned with.
 * 
 * Note: The CAS object must have a user logged into it for any of the methods contained 
 *          in here to work. They will return values indicating an error if there is not. (null)
 * 
 * @author Ethan Gaebel (egaebel)
 *
 */
public class ScheduleScraper {

    //~Constants--------------------------------------------
	private static final String TAG = "SCHEDULE SCRAPER";
    /**
     * The URL for the page before the HOKIESPA constant's URL where the cookies are updated.
     */                                      
    private static final String HOKIESTOP = "https://banweb.banner.vt.edu/ssb/prod/hzskstat.P_Popup?link_in=hzskschd.P_CrseSchdDetl&term_in=";
    /**                                      
     * The URL for the Courses tab on HokieSpa, minus the term (Term must be added to the end, in format yyyymm).
     */
    private static final String HOKIESPA = "https://banweb.banner.vt.edu/ssb/prod/hzskschd.P_DispCrseSchdDetl?term_in=";
    /**
     * The portion of the URL for the HokieSpa URL that goes at the end, the
     * term date is sandwiched in between, the main URL portion and this one.
     */
    private static final String ENDOFURL = "&disp_header=N";
    /**
     * Goes at the end of the HokieSpa URL, used to indicate that the Schedule
     * is to be displayed in a print friendly manner.
     * 
     * Course CRNS are only scrap-able if this is used.
     */
    private static final String PRINT_FRIENDLY = "&print_friendly=Y";
    /**
     * The users agents to pass along with the response.
     */
    private static final String AGENTS = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0";
    
    // ~Exam Time Constants---------------------------------------------------------------
    // The following static strings together with the correct variables make up
    // the appropriate URL to get exam times.
    /**
     * Final Exam URL part 1.
     * Portion of the URL that is followed by the Course's CRN
     */
    private static final String BEFORE_CRN = "https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_ProcExamTime?CRN=";
    /**
     * Final Exam URL part 2.
     * Portion of the URL that is followed by the Course's subject.
     */
    private static final String BEFORE_SUBJECT = "&SUBJECT=";
    /**
     * Final Exam URL part 3.
     * Portion of the URL that is followed by the Course's number.
     */
    private static final String BEFORE_CRSE_NUM = "&CRSE_NUM=";
    /**
     * Final Exam URL part 4.
     * Portion of the URL that is followed by the Course's term.
     */
    private static final String BEFORE_TERM = "&TERM=";
    /**
     * Final Exam URL part 5.
     * Portion of the URL that is followed by the Course's year.
     */
    private static final String BEFORE_YEAR = "&YEAR=";
    /**
     * Final Exam URL part 1.
     * Portion of the URL that is followed by the Exam's identification number.
     */
    private static final String BEFORE_EXAMNUM = "&EXAMNUM=";

    //~Constructors--------------------------------------------

    //~Methods--------------------------------------------
    /**
     * Tests a char to see if it is safe to convert into an int.
     * 
     * @param character the char to test.
     * @return true if the char is a number, false otherwise.
     */
    private static boolean isNumber(char character) {

        boolean value;

        String tester = String.valueOf(character);

        if (tester.contains("0") || tester.contains("1")
                || tester.contains("2") || tester.contains("3")
                || tester.contains("4") || tester.contains("5")
                || tester.contains("6") || tester.contains("7")
                || tester.contains("8") || tester.contains("9")) {

            value = true;
        }
        else {

            value = false;
        }

        return value;
    }
    
    /**
     * Accesses HokieSpa and retrieves the schedule of the user currently logged in.
     * 
     * @param schedule
     *            the Schedule object which will be filled from the web.
     * @param semester
     *            the numerical identifier of the semester, that is to be
     *            implanted into the URL, along with the year. (ex. YYYYMM)
     * @return true if the schedule retrieval succeeds, false if there was an error.
     */
    public static boolean retrieveSchedule(Schedule schedule, String semesterCode) {
        
        //If the Cas object has cookies
        if (Cas.isActive() && Cas.getCookies() != null && schedule != null) {
            try {

                Map<String, String> cookies = Cas.getCookies();

                if (cookies.get("IDMSESSID") != null) {

                    // to get the cookies that are updated with the click
                    Response hokieResp = Jsoup
                            .connect(HOKIESTOP + semesterCode + ENDOFURL)
                            .cookies(cookies)
                            .userAgent(AGENTS)
                            .method(Method.GET)
                            .execute();
        
                    cookies.putAll(hokieResp.cookies());

                    // go to the detailed schedule page
                    Document hokieDoc = Jsoup.connect(HOKIESPA + semesterCode + PRINT_FRIENDLY)
                            .cookies(cookies)
                            .userAgent(AGENTS)
                            .referrer(HOKIESTOP + semesterCode + ENDOFURL)
                            .post();

                    Elements courseTable = hokieDoc.select("body center table tbody");
                    Elements rows = courseTable.select("tr");

                    Elements cols;
                    Course course;
                    String tempCourseCode = "";
                    String[] courseCodeParts = null;
                    String[] timeString = new String[2];
                    String tempString;
                    String locationString = "";
                    String tempName = "";
                    for (int i = 2; i < (rows.size() - 1); i++) {
        
                        cols = rows.get(i).select("td");
        
                        course = new Course();
        
                        // the row is weird, it doesn't have course name, and column
                        // numbers are off
                        if (cols.get(2).text().equals("* Additional Times *")) {
        
                            // course name
                            course.setName(tempName);
                            
                            //ensure that course code was formatted properly
                            if (courseCodeParts != null && courseCodeParts.length == 2) {

                                course.setSubjectCode(courseCodeParts[0]);
                                course.setCourseNumber(courseCodeParts[1]);
                            }
        
                            // times
                            timeString[0] = cols.get(4).text();
                            timeString = timeString[0].split("-");
        
                            course.setBeginTime(timeString[0].trim());
                            course.setEndTime(timeString[1].trim());
        
                            // Building and room number
                            tempString = cols.get(6).text();
        
                            // loop through all of the characters in tempString
                            for (int j = 0; j < tempString.length(); j++) {
        
                                // if a number is reached, grab the remainder of the
                                // string,
                                // and break
                                if (isNumber(tempString.charAt(j))) {
        
                                    course.setRoom(tempString.substring(j,
                                            tempString.length()));
                                    break;
                                }
                                // otherwise add the character to the location (building
                                // id)
                                // string
                                else {
        
                                    locationString += tempString.charAt(j);
                                }
                            }
        
                            // set the course's building
                            course.setBuilding(locationString.trim());
        
                            // clear location string for next go around
                            locationString = "";
        
                            // Teacher name
                            course.setTeacherName(cols.get(7).text());
        
                            // days
                            schedule.setCourseInDays(course, cols.get(5).text());
                        }
                        // otherwise the row is normal, proceed
                        else {
        
                            // course name
                            tempName = cols.get(2).text();
                            course.setName(tempName);
        
                            //get course code
                            tempCourseCode = cols.get(1).text();
                            courseCodeParts = Course.splitCourseCode(tempCourseCode);
                            
                            //ensure that course code was formatted properly
                            if (courseCodeParts.length == 2) {

                                course.setSubjectCode(courseCodeParts[0]);
                                course.setCourseNumber(courseCodeParts[1]);
                            }
        
                            // times
                            timeString[0] = cols.get(5).text();
        
                            // check to see if the timeString is not an actual time
                            // (likely an online class)
                            if (!timeString[0].equals("TBA")
                                    && !timeString[0].contains("ARR")) {
        
                                timeString = timeString[0].split("-");
        
                                course.setBeginTime(timeString[0].trim());
                                course.setEndTime(timeString[1].trim());
                            }
                            else {
        
                                // no set start or end time
                                course.setBeginTime("N/A");
                                course.setEndTime("N/A");
                            }
                            // Building and room number
                            tempString = cols.get(7).text();
        
                            // loop through all of the characters in tempString
                            for (int j = 0; j < tempString.length(); j++) {
        
                                // if a number is reached, grab the remainder of the
                                // string,
                                // and break
                                if (isNumber(tempString.charAt(j))) {
        
                                    course.setRoom(tempString.substring(j,
                                            tempString.length()));
                                    break;
                                }
                                // otherwise add the character to the location (building
                                // id)
                                // string
                                else {
        
                                    locationString += tempString.charAt(j);
                                }
                            }
        
                            // set the course's building
                            course.setBuilding(locationString.trim());
        
                            // clear location string for next go around
                            locationString = "";
        
                            // Teacher name
                            course.setTeacherName(cols.get(8).text());
        
                            // days
                            if (cols.get(6).text().equals("(ARR)")
                                    || cols.get(6).text().equals("TBA")) {
                                // if days are tba or ARR (MOST LIKELY ONLINE CLASS!!!)
                                schedule.setCourseInDays(course, "AnyDay");
                            }
                            else {
        
                                // normal days
                                schedule.setCourseInDays(course, cols.get(6).text());
                            }
                        }
                    }
                }
                else {
                    
                	Cas.logout();
                    Log.i(TAG, "Retrieve schedule, no IDMSESSID cookie failure");
                    return false;
                }
    
                Cas.logout();
                Log.i(TAG, "Retrieve schedule SUCCESS");
                return true;
            }
            catch (FileNotFoundException e) {
            	Log.i(TAG, "FileNotFoundException");
                e.printStackTrace();
            }
            catch (SecurityException e) {
            	Log.i(TAG, "SecurityException");
                e.printStackTrace();
            }
            catch (UnknownHostException e) {
            	Log.i(TAG, "UnknownHostException");
                e.printStackTrace();
            }
            catch (IOException e) {
            	Log.i(TAG, "IOException");
                e.printStackTrace();
            }
        }
        
        Log.i(TAG, "Retrieve schedule FAILURE");
        return false;
    }
    
    /**
     * Get the final exam schedule for the specified semester and fill the
     * passed in List<Course> with Course objects representing the final
     * exam dates, and names. returns false if there's an error, true otherwise
     * 
     * @param semesterCode
     *            the numerical identifier of the semester, that is to be
     *            implanted into the URL, along with the year. (ex. YYYYMM)
     * @param examList
     *            the ArrayList>Course> to fill up for the user
     * @return true if no errors, false if there are any IOExceptions
     */
    public static boolean retrieveExamSchedule(String semesterCode,
            List<Course> examList) {

        if (Cas.isActive()) {
        
            Map<String, String> cookies = Cas.getCookies();
            
            Course additive;

            try {
    
                //covers odd case where wrong password gets through.....
                if (cookies.get("IDMSESSID") != null) {
                    
                    //TODO this is where the NULL cookie error is! WTF IS CAUSING IT!?
                        //IDMSESSID is not present....
                    //if (cookies.get("IDMSESSID") != null) {
                    // to get the cookies that are updated with the click
                    Response hokieResp = Jsoup
                            .connect(HOKIESTOP + semesterCode + ENDOFURL)
                            .cookies(cookies)
                            .userAgent(AGENTS)
                            .method(Method.GET)
                            .execute();
        
                    cookies.put("SESSID", hokieResp.cookies().get("SESSID"));
        
                    // go to the detailed schedule page
                    Document hokieDoc = Jsoup
                            .connect(HOKIESPA + semesterCode + PRINT_FRIENDLY)
                            .cookies(cookies)
                            .userAgent(AGENTS)
                            .referrer(HOKIESTOP + semesterCode + ENDOFURL)
                            .post();
        
                    // the Elements that will get down to the needed fields
                    Elements table = hokieDoc.select("body table");
                    Elements rows = table.get(1).select("tr");
                    Elements cols;
        
                    // the strings that will be used to construct the url for the pages
                    // holding the exam information
                    String name;
                    String crn;
                    String[] course;
                    String examID;
        
                    // the first 2 rows hold formatting information
                    // therefore course information stats in the 3rd row (2)
                    for (int i = 2; i < (rows.size() - 1); i++) {
        
                        cols = rows.get(i).select("td");
    
                        // the row is weird, it doesn't have course name, and column
                        // numbers are off
                        if (cols.get(2).text().equals("* Additional Times *")) {
                        
                            name = "";
                        }
                        else {
                            
                            name = cols.get(2).text();
                        
                            crn = cols.get(0).text();
                            course = cols.get(1).text().split(" ");
                            if (cols.size() >= 10) {
                                
                                examID = cols.get(9).text();
                                
                                // get the course from the retrieveExamTimes method
                                additive = retrieveExamTimes(name, crn, course[0], course[1],
                                        semesterCode, examID);
                                
                                // check for null course
                                if (additive != null) {
                                    examList.add(additive);
                                }
                            }
                        }
                    }
        
                    Log.i(TAG, "Retrieve exam schedule SUCCESS");
                    return true;
                }
                else {
                    
                    Cas.logout();
                    Log.i(TAG, "Retrieve exam schedule, no IDMSESSID cookie failure");
                    return false;
                }
            }
            catch (IOException e) {
    
                e.printStackTrace();
    
                Log.i(TAG, "Retrieve exam schedule IOException failure");
                return false;
            }
        }
        
        Log.i(TAG, "Retrieve exam schedule general failure");
        return false;
    }

    /**
     * Takes in all necessary information needed to create the URL which leads
     * to the exam information for a course.
     * 
     * Returns a course object that holds the information needed to display and
     * sort it. In particular the Course object has a Date object.
     * 
     * @param crn
     *            course's CRN.
     * @param courseID
     *            the courses 2-4 digit subject code.
     * @param courseNum
     *            the course's number.
     * @param semesterCode
     *            the numerical identifier of the semester, that is to be
     *            implanted into the URL, along with the year. (ex. YYYYMM)
     * @param examID
     *            the exam id code that VT uses.
     * @return course a course object representing the exam date, time, name.
     *            Null if there are any errors.
     */
    public static Course retrieveExamTimes(String name, String crn, String courseID,
            String courseNum, String semesterCode, String examID) {

        if (Cas.getCookies() != null) {
        
            Map<String, String> cookies = Cas.getCookies();
            Course course;
    
            try {
                //for some reason hokiespa uses theXXX for CTE..in the url
                if (examID.equalsIgnoreCase("CTE")) {
                    
                    examID = "XXX";
                }

                Document examDoc = Jsoup
                        .connect(
                                BEFORE_CRN + crn + BEFORE_SUBJECT + courseID
                                        + BEFORE_CRSE_NUM + courseNum + BEFORE_TERM
                                        + semesterCode.substring(4, 6) + BEFORE_YEAR 
                                        + semesterCode.substring(0, 4)
                                        + BEFORE_EXAMNUM + examID)
                        .cookies(cookies)
                        .referrer(HOKIESTOP + semesterCode + ENDOFURL)
                        .get();
    
                Elements rows = examDoc.select("body table tr");
    
                String[] courseCode = Course.splitCourseCode(rows.get(2).text().substring(7).trim());
                
                // create the new course object
                // name, subject code, course number, begin, end, date
                course = new Course(name, courseCode[0], courseCode[1], rows.get(4).text().substring(11).trim(),
                        rows.get(5).text().substring(9).trim(), rows.get(3).text().substring(10).trim());
            }
            catch (IOException e) {
                e.printStackTrace();
    
                // set course to null, must check for this when calling method,
                // include in javadoc
                course = null;
            }
    
            return course;
        }
        
        return null;
    }
}
