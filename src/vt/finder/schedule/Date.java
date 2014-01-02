package vt.finder.schedule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * date object which holds both string and int representations of a month, day
 * and year. Basic wrapper class for a date of format
 * 
 * MM/DD/YYYY
 * 
 * @author Ethan Gaebel (egaebel)
 * 
 */
public class Date implements Parcelable{

    //~COnstants------------------
    
    @SuppressWarnings("unused")
    private static final String TAG = "DATE--Course";
    
    // ~Data Fields--------------------------------------------
    private int month;

    private int day;

    private int year;

    private String strMonth;

    private String strDay;

    private String strYear;

    // ~Constructors--------------------------------------------
    /**
     * takes a string representing a date in the format of (MM/DD/YYYY) and sets
     * all the variables in this Date object to the values obtained from the
     * passed string
     * 
     * @param date
     *            the date on the format mm/dd/yyyy
     */
    public Date(String date) {

        if (isShortDate(date)) {
       
            String[] dateBlocks = date.split("/");
    
            if (dateBlocks.length == 3) {
                strMonth = dateBlocks[0];
                strDay = dateBlocks[1];
                strYear = dateBlocks[2];
        
                month = Integer.parseInt(strMonth);
                day = Integer.parseInt(strDay);
                year = Integer.parseInt(strYear);
            }
            else {
    
                strMonth = "No set date";
                strDay = "";
                strYear = "";
    
                month = 0;
                day = 0;
                year = 0;
            }
        }
        else {
            
            if (date.trim().equalsIgnoreCase("No set date")) {
                
                strMonth = "No set date";
                strDay = "";
                strYear = "";
    
                month = 0;
                day = 0;
                year = 0;
            }
        }
    }

    /**
     * takes a string month, string day, and string year and sets all of the
     * variables using it
     * 
     * @param newMonth
     * @param newDay
     * @param newYear
     */
    public Date(String newMonth, String newDay, String newYear) {

        strMonth = newMonth;
        strDay = newDay;
        strYear = newYear;

        month = Integer.parseInt(strMonth);
        day = Integer.parseInt(strDay);
        year = Integer.parseInt(strYear);
    }

    /**
     * default constructor, empty strings for all strings, 0s for all ints
     */
    public Date() {

        strMonth = "";
        strDay = "";
        strYear = "";

        month = 0;
        day = 0;
        year = 0;
    }

    /**
     * takes a date in long form: (typed out month) day, year as well as a
     * dateType specifier, that does not require a valid value it is present to
     * distinguish this constructor from the standard format Date constructor
     * (MM/DD/YYYY)
     */
    public Date(String longDate, String dateType) {
        
        if (longDate.length() != 0) {
        
            String[] dateBlock = longDate.split(" ");
            
            if (dateBlock.length == 3) {
                
                dateBlock[1] = dateBlock[1].replaceFirst(",", "");
        
                strMonth = interpretLongMonth(dateBlock[0]);
                month = Integer.parseInt(strMonth);
        
                strDay = dateBlock[1];
                day = Integer.parseInt(dateBlock[1]);
        
                strYear = dateBlock[2];
                year = Integer.parseInt(dateBlock[2]);
            }
            else {
                
                strMonth = "No set date";
                month = 0;
                
                strDay = "";
                day = 0;
                
                strYear = "";
                year = 0;
            }
        }
        else {
            
            strMonth = "No set date";
            month = 0;
            
            strDay = "";
            day = 0;
            
            strYear = "";
            year = 0;
        }
    }

    // ~Methods--------------------------------------------
    /**
     * takes in a fully spelled out month and spits out the numerical
     * representation of the month in String form (january = 01 etc...)
     * 
     * @param month
     *            the long version of the month (ex. December)
     * @return the String of the number corresponding to the month, returns a 0
     *         if invalid string was passed in
     */
    private String interpretLongMonth(String month) {

        // the switch is the fully spelled out month
        // ex. December, december, dEcEmbeR
        if (month.equalsIgnoreCase("january")) {

            return "01";
        }
        else if (month.equalsIgnoreCase("february")) {

            return "02";
        }
        else if (month.equalsIgnoreCase("march")) {

            return "03";
        }
        else if (month.equalsIgnoreCase("april")) {

            return "04";
        }
        else if (month.equalsIgnoreCase("may")) {

            return "05";
        }
        else if (month.equalsIgnoreCase("june")) {

            return "06";
        }
        else if (month.equalsIgnoreCase("july")) {

            return "07";
        }
        else if (month.equalsIgnoreCase("august")) {

            return "08";
        }
        else if (month.equalsIgnoreCase("september")) {

            return "09";
        }
        else if (month.equalsIgnoreCase("october")) {

            return "10";
        }
        else if (month.equalsIgnoreCase("november")) {

            return "11";
        }
        else if (month.equalsIgnoreCase("december")) {

            return "12";
        }
        else {

            return "0";
        }
    }

    /**
     * takes in another Date object and compares these two to see if this one is
     * greater than, equal to, or less than the other one
     * 
     * greater than is defined as being later in the year than other less than
     * is defined as being earlier in the year than other
     * 
     * ;; this > other = 1;; this < other = -1;; this = other = 0;;
     * 
     * @param other
     *            the date object being compared to this one
     */
    public int compareTo(Date other) {

        if (this.getMonth() == 0 && other.getMonth() == 0) {
            
            return 0;
        }
        else if (this.getMonth() == 0) {
            
            return 1;
        }
        else if (other.getMonth() == 0) {
            
            return -1;
        }
        else {
         
            if (other.getYear() < year) {
    
                return 1;
            }
            else if (other.getYear() > year) {
    
                return -1;
            }
            else {
    
                if (other.getMonth() < month) {
    
                    return 1;
                }
                else if (other.getMonth() > month) {
    
                    return -1;
                }
                else {
    
                    if (other.getDay() < day) {
    
                        return 1;
                    }
                    else if (other.getDay() > day) {
    
                        return -1;
                    }
                    else {
    
                        return 0;
                    }
                }
            }
        }
    }

    /**
     * prints out a string representation of the date (MM/DD/YYYY)
     * 
     * @return the string representation of the date object
     */
    @Override
    public String toString() {
        
        return strDay == null || strYear == null || strMonth.equals("No set date") ? strMonth : strMonth + "/" + strDay + "/" + strYear;
    }

    // ~Getters/Setters----------------------------------------------
    /**
     * @param month
     *            the month to set
     */
    public void setMonth(int month) {

        this.month = month;
    }

    /**
     * @return the month
     */
    public int getMonth() {

        return month;
    }

    /**
     * @param day
     *            the day to set
     */
    public void setDay(int day) {

        this.day = day;
    }

    /**
     * @return the day
     */
    public int getDay() {

        return day;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(int year) {

        this.year = year;
    }

    /**
     * @return the year
     */
    public int getYear() {

        return year;
    }

    /**
     * @param strMonth
     *            the strMonth to set
     */
    public void setStrMonth(String strMonth) {

        this.strMonth = strMonth;
    }

    /**
     * @return the strMonth
     */
    public String getStrMonth() {

        return strMonth;
    }

    /**
     * @param strDay
     *            the strDay to set
     */
    public void setStrDay(String strDay) {

        this.strDay = strDay;
    }

    /**
     * @return the strDay
     */
    public String getStrDay() {

        return strDay;
    }

    /**
     * @param strYear
     *            the strYear to set
     */
    public void setStrYear(String strYear) {

        this.strYear = strYear;
    }

    /**
     * @return the strYear
     */
    public String getStrYear() {

        return strYear;
    }
    
    // ~PARCELABLE
    // STUFF----------------------------------------------------------------------
    // ~----------------------------------------------------------------------------------------

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(year);
        dest.writeString(strMonth);
        dest.writeString(strDay);
        dest.writeString(strYear);
    }

    /**
     * used to regenerate the Schedule upon receiving it
     */
    public static final Parcelable.Creator<Date> CREATOR = new Parcelable.Creator<Date>() {

        public Date createFromParcel(Parcel in) {

            return new Date(in);
        }

        public Date[] newArray(int size) {

            return new Date[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated
    // with it's values
    private Date(Parcel in) {

        month = in.readInt();
        day = in.readInt();
        year = in.readInt();
        strMonth = in.readString();
        strDay = in.readString();
        strYear = in.readString();
    }
    // ~----------------------------------------------------------------------------------------
    /**
     * Static method that takes in a string and determines if it is a date in the format
     * xx/yy/zzzz.
     * 
     * @param date the string to check.
     * @return true if the date matches the above format, false otherwise.
     */
    public static boolean isShortDate(String date) {
        
        //check for nullness and valid length
        if (date != null && date.trim().length() == 10) {
            
            for (int i = 0; i < 10; i++) {
                
                if (i == 2 || i == 5) {
                    
                    if (date.charAt(i) != '/') {
                        
                        return false;
                    }
                }
                else {
                    
                    if (!isNumber(date.charAt(i))) {
                        
                        return false;
                    }
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Takes in a string and determines if its a number.
     * 
     * @param num the string to check
     * @return ture if a number, false otherwise.
     */
    public static boolean isNumber(String num) {
       
        if (num != null) {
            
            for (int i = 0; i < num.length(); i++) {
                
                if (!isNumber(num.charAt(i))) {
                    
                    return false;
                }
            }
        }
        else {
            return false;
        }
        
        return true;
    }
    
    /**
     * Takes in a character checks to see if it is a number
     * 
     * @param num the character to check
     * @return true if a number, false otherwise.
     */
    public static boolean isNumber(char num) {
        
        if (num == '0' || num == '1' || num == '2' 
                || num == '3' || num == '4' || num == '5'
                || num == '6' || num == '7' || num == '6'
                || num == '7' || num == '8' || num == '9') {
            
            return true;
        }
        
        return false;
    }
}
