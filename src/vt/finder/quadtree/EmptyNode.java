package vt.finder.quadtree;


import java.util.ArrayList;
import java.util.Collection;
import vt.finder.quadtree.LeafNode;
import vt.finder.quadtree.QuadNode;
import vt.finder.schedule.Course;
import vt.finder.schedule.Point;
import android.util.Log;

/**
 * QuadNode that is empty, serves ad an indicator for insert, find, remove and
 * all other recursive methods
 * 
 * @author ethan
 * 
 */
public class EmptyNode extends QuadNode {

    // ~Methods----------------------------------------------------------------
    /**
     * checks to see if record's location is within bounds, if so inserts into a
     * new leaf Node at parent via return val. if outside of bounds, return this
     * emptyNode
     * 
     * @param record
     *            the cityData object to insert
     * @param bound
     *            object used to hold bounds, as well as perform partitioning
     * @return returnVal either this QuadNode if out of bounds, or a new
     *         LeafNode if in BOunds
     */
    @Override
    public QuadNode insert(LeafNode node, ChangingBounds bound) {

        QuadNode returnVal;

        // if this node's record is within the bounds
        if (bound.withinBounds(node.getRecord().getCoursePoint())) {
            
            returnVal = node;
        }
        else {
            
            returnVal = this;
        }

        return returnVal;
    }

    /**
     * returns this empty node to caller, there's nothing in this
     * 
     * @param record
     *            the cityData object to insert
     * @param bound
     *            object used to hold bounds, as well as perform partitioning
     * @return this object
     */
    @Override
    public QuadNode find(LeafNode node, ChangingBounds bound) {

        return this;
    }

    /**
     * returns this empty node to caller, there's nothing in this
     * 
     * @param record
     *            the cityData object to insert
     * @param bound
     *            object used to hold bounds, as well as perform partitioning
     * @return this object
     */
    @Override
    public QuadNode remove(LeafNode node, ChangingBounds bound) {

        return this;
    }

    /**
     * returns the number of values here, there's nothing here so, 0
     * 
     * @param x
     *            the x origin of the box
     * @param y
     *            the y origin of the box
     * @param width
     *            the width of the box
     * @param height
     *            the height of the box
     * @param bound
     *            object used to hold bounds, as well as perform partitioning
     * @param found
     *            a Collection used to hold all of the members found within the
     *            rectangular search area
     * @return 0 there's nothing here
     */
    @Override
    public int rFind(int x, int y, int width, int height, ChangingBounds bound,
            Collection<Course> found) {

        return 1;
    }

    /**
     * determines if there are any Course objects within the rectangle
     * determined by two diagonal points from the points Collection<Course>
     * 
     * this is an empty node, so there is no timetable object here, return null
     * 
     * @param points
     *            a Collection<Course> object that is used to determine the
     *            diagonals of a search area
     * @return an empty ArrayList<Course>
     */
    @Override
    public ArrayList<Course> freeFinder(Point start, Point end,
            ChangingBounds worldBounds) {

        return new ArrayList<Course>(0);
    }

    /**
     * prints "empty"
     * 
     * @return "empty"
     */
    @Override
    public String list(ChangingBounds world, String extra) {

        return "\n" + extra + "Empty";
    }
    
    /**
     * returns "nothing" ("") not even a space
     * @return ""
     */
    @Override
    public String toString() {
        
        return "";
    }

    /**
     * determines if a passed in object is an emptyNode all emptyNodes are
     * identical, so if type, then equal used mostly for testing (assertEquals
     * must work!!)
     * 
     * @param thing
     *            the object to check for equality
     * @return value true if type of EmptyNode, false otherwise
     */
    @Override
    public boolean equals(Object thing) {

        boolean value = false;

        if (thing != null) {

            if (thing instanceof EmptyNode) {

                value = true;
            }
        }

        return value;
    }
}