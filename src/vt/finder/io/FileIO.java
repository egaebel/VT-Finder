package vt.finder.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import vt.finder.main.ScheduleWaypoint;
import vt.finder.schedule.Course;
import vt.finder.schedule.Date;
import vt.finder.schedule.Day;
import vt.finder.schedule.Schedule;
import android.content.Context;
import android.util.Log;

/**
 * class contains methods for file operations used to write and store data
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 *         last updated 3/22/2012
 */
public class FileIO {

    // ~Data Fields--------------------------------------------
    private final static String TAG = "FILE IO";
    /**
     * file used to store ExamSchedules
     */
    private static File examsFile;
    /**
     * The read in semester value.
     */
    private String semester;
    /**
     * the name of the examsFile
     */
    private static String examsFileName;
    /**
     * file used to store schedules
     */
    private static File schedulesFile;
    /**
     * file name of the schedulesFile
     */
    private static String schedulesFileName;

    // ~Constructors--------------------------------------------
    /**
     * default constructor, blocked from access
     */
    @SuppressWarnings("unused")
    private FileIO() {
        
        //unusable default constructor!!
    }
    
    /**
     * 
     * sets the default file names and PATHS!!!
     */
    public FileIO(Context context) {
        
        schedulesFileName = "test_file.xml";
        schedulesFile = new File(context.getFilesDir() + schedulesFileName);
        
        examsFileName = "exams_file.xml";
        examsFile = new File (context.getFilesDir() + examsFileName);
    }

    /**
     * Constructor, sets fileNames of schedulesFileName and examsFileName to the params
     * @param schedulesFilePath the file path of the schedulesFile
     * @param examsFilePath the file path of the examsFile
     */
    public FileIO(String schedulesFilePath, String examsFilePath) {
        
        if (schedulesFilePath != null) {
            schedulesFileName = schedulesFilePath;
            schedulesFile = new File(schedulesFilePath);
        }
        else {
            schedulesFileName = "test_file.xml";
            schedulesFile = new File("test_file.xml");
        }
        if (examsFilePath != null) {
            examsFileName = examsFilePath;
            examsFile = new File (examsFilePath);
        }
        else {
            examsFileName = "exams_file.xml";
            examsFile = new File(examsFileName);
        }
    }

    /**
     * Constructor, initializes new file to operate on
     */
    public FileIO(File newSchedulesFile, File newExamsFile) {
        
        if (newSchedulesFile != null) {
            schedulesFileName = newSchedulesFile.getName().toString();
            schedulesFile = newSchedulesFile;
        }
        else {
            
            schedulesFileName = "test_file.xml";
            schedulesFile = new File(schedulesFileName);
        }
        if (newExamsFile != null) {

            examsFileName = newExamsFile.getName().toString();
            examsFile = newExamsFile;
        }
        else {

            examsFileName = "exams_file.xml";
            examsFile = new File (examsFileName);
        }
    }

    // ~Methods--------------------------------------------
    //~ SAVING===============================================================================
    /**
     * saves a passed in schedule and buddies ArrayList
     * 
     * @param schedule the user's schedule
     * @param buddies the user's friends' schedules
     * @return true if successful, false otherwise
     */
    public boolean save(Schedule schedule, ArrayList<Schedule> buddies) {
        
        if (schedule != null) {
            
            String saveString = schedule.toXML();
    
            if (buddies != null && buddies.size() != 0) {
    
                for (Schedule friend : buddies) {
    
                    saveString = saveString + "\n" + friend.toXML();
                }
            }
    
            return saveXMLFile(saveString, "Schedules", schedulesFile);
        }
        
        return false;
    }
    
    /**
     * saves all of the "courses" stored in the finalsList to file
     * 
     * @param finalsList the course ArrayList of final exam times/dates
     * @param semester the semester that the passed in finalsList pertains to
     * @return true if successful, false otherwise
     */
    public boolean save(ArrayList<Course> finalsList, String semester) {
        
        if (finalsList != null) {
             
            StringBuilder saveString = new StringBuilder("");
            
            //loop through all courses in finalsList arrayList, adding their xml
            for (Course course : finalsList) {
                
                saveString.append(course.toXML() + "\n");
            }
            
            saveString.append("<Semester>" + semester + "</Semester>\n");
            
            //always append the finalsList
            return saveXMLFile(saveString.toString(), "ExamSchedule", examsFile);
        }
        
        return false;
    }
    

    /**
     * saves passed in text to a passed in file returns a boolean whether the
     * save was successful or not
     * 
     * @param textToAdd
     *            the text to be written to the file
     * @param tagName
     * @param fileToAdd
     *            the file to write to
     * @return true if successful false otherwise
     */
    public static boolean saveXMLFile(String textToAdd, String tagName, File fileToAdd) {

        boolean success = false;

        // print xml formatting string and the xml to add
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<" + tagName + ">\n" + textToAdd + "\n</" + tagName + ">";
        
        try {

            // creates an output stream for the FILEIO's myFile
            OutputStream os = new FileOutputStream(fileToAdd);

            // read bytes into data byte array
            os.write(text.getBytes());

            // close down stream
            os.close();
            
            success = true;
        }
        catch (IOException e) {

            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.i(TAG, "Error writing " + fileToAdd, e);
        }

        return success;
    }

    //~ LOADING===============================================================================
    /**
     * loads a schedule from a string of xml and returns it, 
     * 
     * 
     * @param xmlSchedule
     *            string in XML rep
     * @param model
     *            the model of this program
     * 
     * @return true if successful, false otherwise
     */
    public Schedule loadSchedules(String xmlSchedule, ScheduleWaypoint model) {

        try {
            // translate string into a Document object
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputter = new InputSource(
                    new StringReader(xmlSchedule));
            Document doc = builder.parse(inputter);

            // normalize the text representation
            doc.getDocumentElement().normalize();

            return loadSchedules(doc, model);
        }
        catch (IOException e) {
            
            e.printStackTrace();
            Log.i(TAG, "IOEXCEPTION");
        }
        catch (ParserConfigurationException e) {
            
            e.printStackTrace();
            Log.i(TAG, "PARSERCONFIGURATIONEXCEPTION");
        }
        catch (SAXException e) {
            
            e.printStackTrace();
            Log.i(TAG, "saxEXCEPTION");
        }
        Log.i(TAG, "null is returned");
        return null;
    }

    /**
     * loads schedules from xml file
     * 
     * @param assets
     *            location of assets of program
     * @param model
     *            the model of this program
     * @return value true if successful, false otherwise
     */
    public Schedule loadSchedules(File file, ScheduleWaypoint model) {

        try {

            // translate string into a Document object
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new FileInputStream(file));

            // if the file isn't empty
            if (doc.getDocumentElement() != null) {

                // normalize the text representation
                doc.getDocumentElement().normalize();

                // returns result of load
                return loadSchedules(doc, model);
            }
        }
        catch (IOException e) {
            
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            
            e.printStackTrace();
        }
        catch (SAXException e) {
            
            e.printStackTrace();
        }
        Log.i(TAG, "null is returned");
        // return null if error thrown, or if file DNE
        return null;
    }

    /**
     * loads all schedules contained in the passed in doc
     * 
     * passes the schedules to the model as either My Schedule or buddies
     * schedules
     * 
     * load each element in schedule by xml tag
     * 
     * @param doc the document from which to read the schedules from
     * @param model the model object used to enter friends schedules in file into
     *              as well as the user's exam schedule (if either are present)
     * @return loadedSchedule, the schedule that SHOULD be "yours" 
     */
    private Schedule loadSchedules(Document doc, ScheduleWaypoint model) {

        Schedule loadedSchedule = new Schedule();

        // Find base tag of <Schedules>
        NodeList theSchedules = doc.getElementsByTagName("Schedules");

        Node theSchedulesNode = theSchedules.item(0);
        Element theSchedulesEl = (Element) theSchedulesNode;

        // get all of the Schedule tagged objects (get schedule)
        NodeList schedules = theSchedulesEl.getElementsByTagName("Schedule");

        // record number of schedules for testing
        int numScheds = schedules.getLength();

        // set user's schedule values//----------------------------------
        Node scheduleNode = schedules.item(0);

        Element scheduleEl = (Element) scheduleNode;

        // set the owner of this schedule
        NodeList oneOwnerList = scheduleEl.getElementsByTagName("Owner");
        Element ownerEl = (Element) oneOwnerList.item(0);

        NodeList ownerFNList = ownerEl.getChildNodes();

        String ownerName = ownerFNList.item(0).getNodeValue().trim().toString();

        loadedSchedule.setWhosSchedule(ownerName);

        // ~VARIABLES USED WITHIN BIG LOOP BELOW----------------

        // holds list of elements tagged by "Course"
        NodeList courseList;
        int numCourses;
        Element courseEl = null;

        // Day setting
        NodeList dayList;
        Element dayEl;

        // creates an array with the String values of the days of the week
        // used as input for .getElementsByTagName
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "AnyDay" };

        // gets array representation of the days of the week
        // values here will be changed, then values will be updated after
        // looping
        Day[] theDay;

        theDay = loadedSchedule.daysToArray();

        // values used to create a new course object
        // NodeList used for Name, teacher, room, time, building element lists
        // Element used for grabbing the correct element from the NodeList
        // (always 0)
        NodeList theList = null;
        Element theEl = null;

        // ---------------------------------------------------
        String name = "";
        String teacher = "";
        String beginTime = "";
        String endTime = "";
        String building = "";
        String room = "";
        String subjectCode = "";
        String courseNumber = "";
        // -----------------------------------------------------//

        // loops through the array theDay
        // gets courses from that day
        // then loops through the arraylist of courses retrieving courseName,
        // teacherName etc
        for (int k = 0; k < theDay.length; k++) {

            // Day setting
            dayList = scheduleEl.getElementsByTagName(days[k]);
            dayEl = (Element) dayList.item(0);

            // Course searches
            courseList = dayEl.getElementsByTagName("Course");
            numCourses = courseList.getLength();

            // if there are indeed courses on the current day
            if (numCourses > 0) {

                // loops through current course object extracting name, teacher,
                // time, building etc....
                for (int i = 0; i < courseList.getLength(); i++) {

                    // selects the correct course in the day via i
                    courseEl = (Element) courseList.item(i);

                    // Checks to see if there is in fact a course here
                    if (((Element) courseEl.getElementsByTagName("Name")
                            .item(0)).getChildNodes().getLength() != 0) {

                        // Finds each element in a course by Tag and assigns it
                        // to a variable
                        // reuses variables
                        theList = courseEl.getElementsByTagName("Name");
                        theEl = (Element) theList.item(0);

                        name = theEl.getChildNodes().item(0).getNodeValue()
                                .trim().toString();

                        theList = courseEl.getElementsByTagName("SubjectCode");
                        theEl = (Element) theList.item(0);
                        if (theEl != null) {
                            subjectCode = theEl.getChildNodes().item(0).getNodeValue()
                                    .trim().toString();
                        }
                        
                        theList = courseEl.getElementsByTagName("CourseNumber");
                        theEl = (Element) theList.item(0);
                        if (theEl != null) {
                            courseNumber = theEl.getChildNodes().item(0).getNodeValue()
                                    .trim().toString();
                        }
                        
                        theList = courseEl.getElementsByTagName("Teacher");
                        theEl = (Element) theList.item(0);

                        teacher = theEl.getChildNodes().item(0).getNodeValue()
                                .trim().toString();

                        theList = courseEl.getElementsByTagName("BeginTime");
                        theEl = (Element) theList.item(0);

                        beginTime = theEl.getChildNodes().item(0)
                                .getNodeValue().trim().toString();

                        theList = courseEl.getElementsByTagName("EndTime");
                        theEl = (Element) theList.item(0);

                        endTime = theEl.getChildNodes().item(0).getNodeValue()
                                .trim().toString();

                        theList = courseEl.getElementsByTagName("Building");
                        theEl = (Element) theList.item(0);

                        building = theEl.getChildNodes().item(0).getNodeValue()
                                .trim().toString();

                        theList = courseEl.getElementsByTagName("Room");
                        theEl = (Element) theList.item(0);

                        room = theEl.getChildNodes().item(0).getNodeValue()
                                .trim().toString();
                        
                        theDay[k].addCourse(name, subjectCode, courseNumber, teacher, beginTime, endTime,
                                building, room);
                    }
                    else {

                        continue;
                    }


                }// end loop through courses for day
            }
            else {

                Log.i(TAG, "No courses added from xml on " + days[k]);
            }

        }// end loop through days

        // update the schedule with theDay array that contains changes
        loadedSchedule.arrayToDays(theDay);

        // if there is more than one schedule in the passed doc....
        if (numScheds > 1) {

            loadFriendsSchedulesFromFile(theSchedulesEl, model, numScheds);
        }

        // returns the first schedule in the Document
        return loadedSchedule;
    }

    /**
     * helper method, loops through the remainder of theSchedulesEl Element loading all of the other schedules
     * into the model ScheduleWaypoint object
     * 
     * @param theSchedulesEl the schedule Element that holds all of the friendsSchedules
     * @param model the ScheduleWaypoint object that all of the friends schedules are put in
     * @param numScheds the number of schedules
     */
    private void loadFriendsSchedulesFromFile(Element theSchedulesEl,
            ScheduleWaypoint model, int numScheds) {

        // *************************************************************************************
        // *************************************************************************************
        // FRIEND'S SCHEDULE RETRIEVAL//
        // IF there are more schedules to
        // get//--------------------------------------------
        // get them and add them to the list in the same fashion
        // as the user's schedule was
        if (numScheds > 1) {

            // get all of the Schedule tagged objects (get schedule)
            NodeList schedules = theSchedulesEl
                    .getElementsByTagName("Schedule");

            // set up variables to hold users schedules values (used in j loop)
            Node scheduleNode;
            Element scheduleEl;

            // holds list of elements tagged by "Course"
            NodeList courseList;
            int numCourses;
            Element courseEl = null;

            // Day setting
            NodeList dayList;
            Element dayEl;

            // creates an array with the String values of the days of the week
            // used as input for .getElementsByTagName
            String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday",
                    "Friday", "AnyDay" };

            // gets array representation of the days of the week
            // values here will be changed, then values will be updated after
            // looping
            Day[] theDay;

            theDay = model.getSchedule().daysToArray();

            // values used to create a new course object
            // NodeList used for Name, teacher, room, time, building element
            // lists
            // Element used for grabbing the correct element from the NodeList
            // (always 0)
            NodeList theList = null;
            Element theEl = null;

            // ---------------------------------------------------
            String name = "";
            String teacher = "";
            String beginTime = "";
            String endTime = "";
            String building = "";
            String room = "";
            String subjectCode = "";
            String courseNumber = "";

            // allocates space for new schedule object
            Schedule friendSched;

            // loops through all schedules
            for (int j = 1; j < numScheds; j++) {

                // switches scheduleEl to new schedule object on each iteration
                scheduleNode = schedules.item(j);
                scheduleEl = (Element) scheduleNode;

                // creates new schedule object and resets theDay array to refer
                // to the friend's schedule
                friendSched = new Schedule();

                // set the owner of this schedule
                NodeList oneOwnerList = scheduleEl
                        .getElementsByTagName("Owner");
                Element ownerEl = (Element) oneOwnerList.item(0);
                NodeList ownerFNList = ownerEl.getChildNodes();

                // sets the name of the friendSched object to the proper name
                //String ownerName = "a friend";// ;
                friendSched.setWhosSchedule(ownerFNList.item(0).getNodeValue().trim().toString());

                theDay = friendSched.daysToArray();

                // loops through the array theDay
                // gets courses from that day
                // then loops through the arraylist of courses retrieving
                // courseName, teacherName etc
                for (int k = 0; k < theDay.length; k++) {

                    // Day setting
                    dayList = scheduleEl.getElementsByTagName(days[k]);
                    dayEl = (Element) dayList.item(0);

                    // Course searches
                    courseList = dayEl.getElementsByTagName("Course");
                    numCourses = courseList.getLength();

                    // if there are indeed courses on the current day
                    if (numCourses > 0) {

                        // loops through current course object extracting name,
                        // teacher, time, building etc....
                        for (int i = 0; i < courseList.getLength(); i++) {

                            // selects the correct course in the day via i
                            courseEl = (Element) courseList.item(i);

                            // Checks to see if there is in fact a course here
                            if (((Element) courseEl
                                    .getElementsByTagName("Name").item(0))
                                    .getChildNodes().getLength() != 0) {

                                // Finds each element in a course by Tag and
                                // assigns it to a variable
                                // reuses variables
                                theList = courseEl.getElementsByTagName("Name");
                                theEl = (Element) theList.item(0);

                                name = theEl.getChildNodes().item(0)
                                        .getNodeValue().trim().toString();

                                theList = courseEl.getElementsByTagName("SubjectCode");
                                theEl = (Element) theList.item(0);
                                if (theEl != null) {
                                    subjectCode = theEl.getChildNodes().item(0).getNodeValue()
                                            .trim().toString();
                                }
                                
                                theList = courseEl.getElementsByTagName("CourseNumber");
                                theEl = (Element) theList.item(0);
                                if (theEl != null) {
                                    courseNumber = theEl.getChildNodes().item(0).getNodeValue()
                                            .trim().toString();
                                }
                                
                                theList = courseEl
                                        .getElementsByTagName("Teacher");
                                theEl = (Element) theList.item(0);

                                teacher = theEl.getChildNodes().item(0)
                                        .getNodeValue().trim().toString();

                                theList = courseEl
                                        .getElementsByTagName("BeginTime");
                                theEl = (Element) theList.item(0);

                                beginTime = theEl.getChildNodes().item(0)
                                        .getNodeValue().trim().toString();

                                theList = courseEl
                                        .getElementsByTagName("EndTime");
                                theEl = (Element) theList.item(0);

                                endTime = theEl.getChildNodes().item(0)
                                        .getNodeValue().trim().toString();

                                theList = courseEl
                                        .getElementsByTagName("Building");
                                theEl = (Element) theList.item(0);

                                building = theEl.getChildNodes().item(0)
                                        .getNodeValue().trim().toString();

                                theList = courseEl.getElementsByTagName("Room");
                                theEl = (Element) theList.item(0);

                                room = theEl.getChildNodes().item(0)
                                        .getNodeValue().trim().toString();

                                theDay[k].addCourse(name, subjectCode, courseNumber, teacher, beginTime,
                                        endTime, building, room);
                            }
                            else {

                                continue;
                            }
                        }// end loop through courses for day
                    }
                    else {

                        Log.i(TAG, "No courses added from xml on " + days[k]);
                    }
                }// end loop through days

                // update the schedule with theDay array that contains changes
                friendSched.arrayToDays(theDay);
                
                // add friendSched to buddies ArrayList<Schedule>
                model.getBuddiesSchedules().add(friendSched);

            }// end loop through all schedules
        }// end if more than 1 schedule
    }
    
    /**
     * Reads in a file and converts it to a doc object. Passes the doc object onto the loadExams(doc) method
     * where an ArrayList<Course> is filled with final exams and is returned through this method
     * 
     * @param file the file to read from
     * @return the ArrayList<Course> of the user's final exams
     */
    public ArrayList<Course> loadExams(File file) {
        
        if (file != null) {
            try {
    
                // translate string into a Document object
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new FileInputStream(file));
    
                // if the file isn't empty
                if (doc != null && doc.getDocumentElement() != null) {
    
                    // normalize the text representation
                    doc.getDocumentElement().normalize();
    
                    // returns result of load
                    return loadExams(doc);
                }
            }
            catch (IOException e) {
                
                e.printStackTrace();
            }
            catch (ParserConfigurationException e) {
                
                e.printStackTrace();
            }
            catch (SAXException e) {
                
                e.printStackTrace();
            }
        }

        // return null if error thrown, or if file DNE
        return null;
    }
    
    /**
     * helper method, loads the user's exam schedule from the file
     */
    private ArrayList<Course> loadExams (Document doc) {
     
        ArrayList<Course> finalsList = new ArrayList<Course>();
        
        NodeList examSchedule = doc.getElementsByTagName("ExamSchedule");
        
        //distill down to a list of XML course objects
        Node examScheduleNode = examSchedule.item(0);
        Element examEl = (Element) examScheduleNode;
        NodeList examList = examEl.getElementsByTagName("Course");
        
        NodeList semesterList = examEl.getElementsByTagName("Semester");

        if(semesterList.getLength() > 0 && semesterList.item(0).getNodeValue() != null) {

            semester = semesterList.item(0).getTextContent();
        }
        
        //~Loop Elements===========================
        // values used to create a new course object
        // NodeList used for Name, teacher, room, time, building element lists
        // Element used for grabbing the correct element from the NodeList
        // (always 0)
        NodeList theList = null;
        Element theEl = null;
        // ---------------------------------------------------
        String name = "";
        String courseNumber = "";
        String subjectCode = "";
        String beginTime = "";
        String endTime = "";
        String date = "";
        // -----------------------------------------------------//
        
        //loop through the examList, setting all courses in finalsList
        for (int i = 0; i < examList.getLength(); i++) {
            
            examEl = (Element) examList.item(i);
            
            // Checks to see if there is in fact a course here
            if (((Element) examEl.getElementsByTagName("Name")
                    .item(0)).getChildNodes().getLength() != 0) {
                
                // Finds each element in a course by Tag and assigns it
                // to a variable
                // reuses variables
                theList = examEl.getElementsByTagName("Name");
                theEl = (Element) theList.item(0);
                name = theEl.getChildNodes().item(0).getNodeValue()
                        .trim().toString();

                theList = examEl.getElementsByTagName("SubjectCode");
                theEl = (Element) theList.item(0);
                if (theEl != null) {
                    subjectCode = theEl.getChildNodes().item(0).getNodeValue()
                            .trim().toString();
                }
                
                theList = examEl.getElementsByTagName("CourseNumber");
                theEl = (Element) theList.item(0);
                if (theEl != null) {
                    courseNumber = theEl.getChildNodes().item(0).getNodeValue()
                            .trim().toString();
                }
                
                theList = examEl.getElementsByTagName("BeginTime");
                if (theList != null) {
                    
                    theEl = (Element) theList.item(0);
                    
                    if (theEl != null 
                            && theEl.getChildNodes() != null 
                            && theEl.getChildNodes().item(0) != null
                            && theEl.getChildNodes().item(0).getNodeValue() != null) {
                        
                        beginTime = theEl.getChildNodes().item(0)
                                .getNodeValue().trim().toString();
                    }
                }

                theList = examEl.getElementsByTagName("EndTime");
                if (theList != null) {
                    
                    theEl = (Element) theList.item(0);
                    
                    if (theEl != null
                            && theEl.getChildNodes() != null 
                            && theEl.getChildNodes().item(0) != null
                            && theEl.getChildNodes().item(0).getNodeValue() != null) {

                        endTime = theEl.getChildNodes().item(0).getNodeValue()
                                .trim().toString();
                    }
                }

                theList = examEl.getElementsByTagName("Date");
                theEl = (Element) theList.item(0);
                
                if (theEl != null && theEl.getChildNodes() != null 
                        && theEl.getChildNodes().item(0) != null 
                        && theEl.getChildNodes().item(0).getNodeValue() != null
                        && theEl.getChildNodes().item(0).getNodeValue().trim().toString() != null
                        && !theEl.getChildNodes().item(0).getNodeValue().trim().toString().trim().equals("null")
                        && !theEl.getChildNodes().item(0).getNodeValue().trim().toString().equals("No set date")) {

                    date = theEl.getChildNodes().item(0).getNodeValue()
                            .trim().toString();
                }
                else {
                    date = "No set date";
                }
                
                //add the read in course to the finalsList
                
                finalsList.add(new Course(name, subjectCode, courseNumber, 
                        beginTime, endTime, new Date(date)));
            }
        }
        
        return finalsList;
    }

    /**
     * @return the semester
     */
    public String getSemester() {

        return semester;
    }
}
