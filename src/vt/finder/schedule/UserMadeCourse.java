package vt.finder.schedule;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author Ethan Gaebel (egaebel)
 *
 */
public class UserMadeCourse extends Course implements Parcelable {

    //~Constants----------------------------------------------


    //~Data Fields-------------------------------------------
    
    
    //~Constructors---------------------------------------------
    /**
     * Not to be used.
     */
    @SuppressWarnings("unused")
    private UserMadeCourse() {}
    
    /**
     * Only constructor, creates a UserMadeCourse object.
     * 
     * @param name the name of the course.
     * @param teacherName the name of the teacher for the course.
     * @param subjectCode the code of the subject, ex. CS MATH
     * @param courseNumber the number of the course, ex. 4175
     * @param beginTime the time that the course begins at.
     * @param endTime the time that the course ends at.
     * @param building the building that the course occurs in.
     * @param roomNumber the roomNumber of the course.
     */
    public UserMadeCourse(String name, String teacherName,
            String subjectCode, String courseNumber,  
            String beginTime, String endTime,
            String building, String roomNumber) {
        
        this.name = name;
        this.subjectCode = subjectCode;
        this.courseNumber = courseNumber;
        
        coursePoint = new Point();
        setBeginTime(beginTime);
        setEndTime(endTime);
        
        this.building = building;
        this.room = roomNumber;
    }
    
    //~Methods-------------------------------------------------
    
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
    public static final Parcelable.Creator<UserMadeCourse> CREATOR = new Parcelable.Creator<UserMadeCourse>() {

        public UserMadeCourse createFromParcel(Parcel in) {

            return new UserMadeCourse(in);
        }

        public UserMadeCourse[] newArray(int size) {

            return new UserMadeCourse[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated
    // with it's values
    private UserMadeCourse(Parcel in) {

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
}
