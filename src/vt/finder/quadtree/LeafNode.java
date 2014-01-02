package vt.finder.quadtree;

import java.util.ArrayList;
import java.util.Collection;
import vt.finder.schedule.Course;
import vt.finder.schedule.Point;

/**
 * extends QuadNode holds a CityData object, allows for insertions removals,
 * finds, and rFinds to be called on it
 * 
 * @author ethan
 * 
 */
public class LeafNode extends QuadNode {

    // ~DataFields-------------------------------------------------------------
    /**
     * CityData object stored at this point in the quad tree
     */
    private Course record;

    // ~Constructors-----------------------------------------------------------
    /**
     * default constructor
     * 
     * used mainly for testing
     */
    public LeafNode() {

    }

    /**
     * constructor that initializes CityData object
     * 
     * @param newRecord
     *            the CityData object to initialize to
     */
    public LeafNode(Course newRecord) {

        setRecord(newRecord);
    }

    // ~Methods----------------------------------------------------------------
    /**
     * inserts a method into this leaf node, if there is already a member,
     * creates a new Internal node and performs an insertion on it
     * 
     * @param node
     *            the node to insert
     * @param bound
     *            the bounds of the world
     * 
     * @return internal node if insertion succeeds, this node if already exists
     */
    @Override
    public QuadNode insert(LeafNode node, ChangingBounds bound) {

        // if the node passed isnt null
        if (node != null) {

            // checks to see if the node to be inserted will perfectly overlap in begin and end times
            if (node.getRecord().getBeginTime().equals(record.getBeginTime()) 
                    && node.getRecord().getEndTime().equals(record.getEndTime())) {
                
                return this;
            }
            // if a record is already here, and it differs from the one in node
            else if (record != null) {

                // create new internal node to store the two nodes and replace
                // this leaf
                InternalNode inside = new InternalNode();

                // insert the node here, and the node passed
                inside.insert(this, bound);
                inside.insert(node, bound);

                // give this new internal node to the parent so it can set its
                // reference to this object equal to the new internal node
                // instead of the leaf node
                return inside;
            }
            else {
                
                // if this leaf node is empty for some reason, set its record
                // equal to the passed node's record
                record = node.getRecord();
                return this;
            }
        }
        else {

            // return empty if the passed node was null, waste of time
            return empty;
        }


    }

    /**
     * searches for a passed in node within this leaf, compares this record to
     * the passed node's record, returns appropriate values depending on
     * equality
     * 
     * @param node
     *            the node holding the value to search for
     * @param bound
     *            the boundaries for this node
     * @return either this node if the node matches, or empty if not found
     */
    @Override
    public QuadNode find(LeafNode node, ChangingBounds bound) {

        // if this node is equal to the one passed, return this
        if (node.getRecord().getCoursePoint().equals(record.getCoursePoint())) {

            return this;
        }
        // if not, the node cannot be found
        else {

            // return empty, to indicate no match
            return empty;
        }
    }

    /**
     * checks to see if this LeafNode holds the same record as the passed node
     * if so, return empty so that internal node or root can set its value to
     * empty otherwise return this
     * 
     * @param node
     *            holds the value you're looking to remove
     * @param bound
     *            the boundaries of this world the LeafNode cover
     * @return either empty if removal, or this if no removal occurred
     */
    @Override
    public QuadNode remove(LeafNode node, ChangingBounds bound) {

        // if the record in this node is equal to the passed node
        if (node.getRecord().getCoursePoint().equals(record.getCoursePoint())) {

            // return empty to the parent to replace the LeafNode reference
            return empty;
        }
        // this node is not the one to be removed
        else {

            // return this node to maintain its place
            return this;
        }
    }

    /**
     * tests if this record is null, if it isnt then it adds the record
     * contained in this node to the collection passed in by the caller. Part of
     * the string of rFinds which searches for all of the nodes within a
     * specified rectangular area
     * 
     * @param x
     *            min x value
     * @param y
     *            min y value
     * @param width
     *            width of rectangle
     * @param height
     *            height of rectangle
     * @param bound
     *            the boundaries of the world
     * @param found
     *            a collection that holds the elements within the bounds
     * @return number of records found
     */
    @Override
    public int rFind(int x, int y, int width, int height, ChangingBounds bound,
            Collection<Course> found) {

        // if the record here isnt null
        if (record != null) {

            // if the record here is within the bounds of "this world"
            if (new ChangingBounds(x + width, y + height, x, y)
                    .withinBounds(record.getCoursePoint())) {

                // add the record to the passed collection
                found.add(record);
            }
        }

        // return one to the parent to increment the count of nodes searched
        return 1;
    }

    /**
     * determines if there are any Course objects stored within the bounds of
     * the rectangle determined by two points from the points collection
     * 
     * @param points
     *            a Collection<Course> that holds the points needed to define
     *            the bounds of the search area
     * 
     * @return the record stored at this location, used at the parent to create
     *         a new search area
     */
    @Override
    public ArrayList<Course> freeFinder(Point start, Point end,
            ChangingBounds worldBounds) {

        ArrayList<Course> returnVal = new ArrayList<Course>(1);

        // check if this record is null
        if (this.getRecord() != null) {

            ChangingBounds freeFindBounds = new ChangingBounds(end.getX(),
                    end.getY(), start.getX(), start.getY());

            // check if this record's coursePoint is within the boundaries
            // check if this recrod's coursePoint is within the
            // ExclusionaryBoundaries of the freeFinder search area
            if ((freeFindBounds.withinBoundsExclusionary(this.getRecord()
                    .getCoursePoint()))
                    && (worldBounds.withinBounds(this.getRecord()
                            .getCoursePoint()))) {

                returnVal.add(this.getRecord());
            }
        }

        return returnVal;
    }

    /**
     * returns a string for the record contained in this leaf node
     * 
     * @return the string rep of the record here
     */
    @Override
    public String list(ChangingBounds world, String extra) {

        // return the string representation of the record held here
        // include "extra" which is the delimiting characters that indicate the
        // depth of the node when printing
        return "\n" + extra + record.getCoursePoint().toString();
    }

    /**
     * returns the string representation of the data stored here
     * 
     * @return the string representation of the record stored in this LEafNode
     */
    @Override
    public String toString() {

        return record.toString();
    }

    /**
     * checks if two LeafNodes are equal
     * 
     * @param other
     *            the LeafNode to check for equality
     * @return value true if equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {

        boolean value = false;

        if (other != null) {

            if (other instanceof LeafNode) {

                if (((LeafNode) other).getRecord().equals(this.getRecord())) {

                    value = true;
                }
            }
        }

        return value;
    }

    /**
     * @param record
     *            the record to set
     */
    public void setRecord(Course record) {

        this.record = record;
    }

    /**
     * @return the record
     */
    public Course getRecord() {

        return record;
    }
}