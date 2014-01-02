package vt.finder.quadtree;

import java.util.ArrayList;
import java.util.Collection;
import vt.finder.schedule.Course;
import vt.finder.schedule.Point;

/**
 * abstract class that serves as a base for all of the nodes within the QuadTree
 * 
 * allows for declaring QuadNode types, and setting them to emptyNode,
 * internalNode, or LeafNode
 * 
 * contains an empty node to provide access to it in all nodes that extend this
 * clas
 * 
 * @author ethan
 * 
 * last updated 3/22/2012
 * 
 */
public abstract class QuadNode {

    // ~Data Fields-------------------------------------------------------------
    /**
     * emptyNode which can fulfill all needs of an emptyNode everywhere in the
     * program
     */
    protected static QuadNode empty = new EmptyNode();

    // ~Methods----------------------------------------------------------------
    /**
     * inserts a CityData object into the quad tree organizing it by x,y cords
     * 
     * @param record
     *            the CityData object to insert into the tree
     * @param bound
     *            the bounds of the current "world" being checked
     * @return
     */
    public abstract QuadNode insert(LeafNode node, ChangingBounds bound);

    /**
     * finds a CityData object specified by an x,y cord
     * 
     * @param record
     *            a CityData object containing only x,y coordinates, no other
     *            data used to search the quadTree for a matching object
     * @param bound
     *            the bounds of the current world being checked
     * @return
     */
    public abstract QuadNode find(LeafNode node, ChangingBounds bound);

    /**
     * removes a CityData object specified by an x,y cord
     * 
     * @param record
     *            a CityData object containing only x,y coordinates, no other
     *            data used to search the quadTree for a matching object
     * @param bound
     *            the bounds of the current world being checked
     * @return
     */
    public abstract QuadNode remove(LeafNode node, ChangingBounds bound);

    /**
     * prints all cities within the specified "box", and returns the number of
     * cities in the box
     * 
     * @param x
     *            the x cord of the origin of the box
     * @param y
     *            the y cord of the origin of the box
     * @param width
     *            the width of the box
     * @param height
     *            the height of the box
     * @param bound
     *            the bounds of the world currently being checked
     * @return the number of cities within the box!
     */
    public abstract int rFind(int x, int y, int width, int height,
            ChangingBounds bound, Collection<Course> found);

    /**
     * method that takes a Collection of TimeTable objects, and searches the
     * area between a point in the first TimeTable object and the point in the
     * second TimeTable object if a TimeTable object is found within this area,
     * change the search area to be between the first point and the point stored
     * in this newly found timetable object
     * 
     * when there is no TimeTable object found in a rectangle with diagonals at
     * these two points then add a TimeTable object that only has a name
     * "ChillTime" and points representing the two diagonals of the free area
     * 
     * @param worldBounds
     * @param points
     *            a Collection of TimeTable objects used to "draw" searchable
     *            rectangles between its memebers
     * 
     * @return a Collection of TimeTable objects that holds the areas that did
     *         not contain a TimeTable object
     */
    public abstract ArrayList<Course> freeFinder(Point start, Point end,
            ChangingBounds worldBounds);

    /**
     * prints the value associated with the specific quadNode --internal Node,
     * all subnodes --empty node, empty --leaf node, record in the leaf
     * 
     * @return the String representation of the Node
     */
    public abstract String list(ChangingBounds world, String extra);
    
    /**
     * prints out a string representation of the node, including children
     * 
     * used so that I can see the value easily in the debugger under variables!!!
     */
    @Override
    public abstract String toString();
}
