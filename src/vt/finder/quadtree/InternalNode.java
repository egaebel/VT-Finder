package vt.finder.quadtree;

import java.util.ArrayList;
import java.util.Collection;
import vt.finder.schedule.Course;
import vt.finder.schedule.Point;

/**
 * "Waypoint" node, governs over 4 areas of the 2d graph, can contain either
 * LeafNodes, Empty Nodes, or other InternalNodes
 * 
 * @author ethan
 * 
 */
public class InternalNode extends QuadNode {

    // ~Constants
    /**
     * integer representing NW used for switch statements
     */
    private final static int NW = 0;

    /**
     * integer representing NE used for switch statements
     */
    private final static int NE = 1;

    /**
     * integer representing SW used for switch statements
     */
    private final static int SW = 2;

    /**
     * integer representing SE used for switch statements
     */
    private final static int SE = 3;

    /**
     * String used as a delimiter for the list function, used for showing depth
     */
    private final static String DELIM = "....";

    // ~DataFields-------------------------------------------------------------
    /**
     * the quadNode to the "northwest
     */
    private QuadNode nw;

    /**
     * the quadNode to the "northeast
     */
    private QuadNode ne;

    /**
     * the quadNode to the "southeast
     */
    private QuadNode se;

    /**
     * the quadNode to the "southwest
     */
    private QuadNode sw;
    
    // ~Constructors-----------------------------------------------------------
    /**
     * default constructor, initializes all of the QuadNodes to empty nodes
     */
    public InternalNode() {

        nw = empty;
        sw = empty;
        ne = empty;
        se = empty;
    }

    // ~Methods----------------------------------------------------------------

    /**
     * inserts a node at whichever position it needs to be inserted at _______ |
     * 0| 1 | |------| | 2| 3 | --------
     * 
     * @param node
     *            the node to be inserted
     * @param bound
     *            the boundaries of the current world
     * @return the node that was inserted
     */
    @Override
    public QuadNode insert(LeafNode node, ChangingBounds bound) {

        // switch to determine which quadrant to go to
        switch (bound.placement(node)) {

            case 0:

                nw = nw.insert(node, new ChangingBounds(bound.calcNW()));
                break;
            case 1:

                ne = ne.insert(node, new ChangingBounds(bound.calcNE()));
                break;
            case 2:

                sw = sw.insert(node, new ChangingBounds(bound.calcSW()));
                break;
            case 3:

                se = se.insert(node, new ChangingBounds(bound.calcSE()));
                break;
        }

        return this;
    }

    /**
     * routes the find method to the correct node in the internal node based on
     * the result of bound.placement
     * 
     * @param node
     *            holds the value to search for in comparison
     * @param bound
     *            the boundaries for the world that this internal NOde
     *            represents
     * @return the node found by find or empty
     */
    @Override
    public QuadNode find(LeafNode node, ChangingBounds bound) {

        switch (bound.placement(node)) {

            case 0:
                return nw.find(node, new ChangingBounds(bound.calcNW()));
            case 1:
                return ne.find(node, new ChangingBounds(bound.calcNE()));
            case 2:
                return sw.find(node, new ChangingBounds(bound.calcSW()));
            case 3:
                return se.find(node, new ChangingBounds(bound.calcSE()));
        }
        return empty;
    }

    /**
     * removes a node from the QuadTree that matches the parameters in node
     * 
     * @param node
     *            holds the values to look for to remove
     * @param bound
     *            the boundaries of the current world
     * @return either this Internal Node if no retraction is needed or the
     *         single leaf node contained in this if a retraction IS needed
     */
    @Override
    public QuadNode remove(LeafNode node, ChangingBounds bound) {

        switch (bound.placement(node)) {

            case 0:
                nw = nw.remove(node, new ChangingBounds(bound.calcNW()));
                break;
            case 1:
                ne = ne.remove(node, new ChangingBounds(bound.calcNE()));
                break;
            case 2:
                sw = sw.remove(node, new ChangingBounds(bound.calcSW()));
                break;
            case 3:
                se = se.remove(node, new ChangingBounds(bound.calcSE()));
                break;
        }

        return retract();
    }

    /**
     * checks all of the QuadNodes contained in this INternal NOde and if there
     * is one LeafNode and three empty nodes, return
     * 
     * @return a QuadNode that is either a leaf if retraction is needed or this
     *         INternal node otherwise
     */
    private QuadNode retract() {

        int leaves = 0;
        int empties = 0;

        if (nw instanceof LeafNode) {

            leaves++;
        }
        if (ne instanceof LeafNode) {

            leaves++;
        }
        if (sw instanceof LeafNode) {

            leaves++;
        }
        if (se instanceof LeafNode) {

            leaves++;
        }

        // if theres only one leaf node
        if (leaves == 1) {

            if (nw instanceof EmptyNode) {

                empties++;
            }
            if (ne instanceof EmptyNode) {

                empties++;
            }
            if (sw instanceof EmptyNode) {

                empties++;
            }
            if (se instanceof EmptyNode) {

                empties++;
            }

            // Contract to leaf node, via return
            if (leaves == 1 && empties == 3) {

                if (nw instanceof LeafNode) {

                    return nw;
                }
                else if (ne instanceof LeafNode) {

                    return ne;
                }
                else if (sw instanceof LeafNode) {

                    return sw;
                }
                else {

                    return se;
                }
            }
        }

        return this;
    }

    /**
     * searches a range specified by x, y, width and height, returns the number
     * of nodes searched also adds members within the range to the Collection
     * found
     * 
     * @param x
     *            the minimum x cord
     * @param y
     *            the minimum y cord
     * @param width
     *            the width of the rectangle
     * @param height
     *            the height of the rectangle
     * @param bound
     *            the boundaries of this world
     * @param
     * @return the number of nodes found
     */
    @Override
    public int rFind(int x, int y, int width, int height, ChangingBounds bound,
            Collection<Course> found) {

        // =1 because count this node
        int total = 1;

        // determine if each quadrant falls within the bounds of the rectangle
        if (bound.intersects(x, y, width, height, NW)) {

            total += nw.rFind(x, y, width, height,
                    new ChangingBounds(bound.calcNW()), found);
        }
        if (bound.intersects(x, y, width, height, NE)) {

            total += ne.rFind(x, y, width, height,
                    new ChangingBounds(bound.calcNE()), found);
        }
        if (bound.intersects(x, y, width, height, SW)) {

            total += sw.rFind(x, y, width, height,
                    new ChangingBounds(bound.calcSW()), found);
        }
        if (bound.intersects(x, y, width, height, SE)) {

            total += se.rFind(x, y, width, height,
                    new ChangingBounds(bound.calcSE()), found);
        }

        return total;
    }

    /**
     * routes control for the freeFinder method.
     * 
     * if it is found that a LeafNode contains a record, then must add that
     * record to the points Collection, to split the search area into two search
     * areas
     * 
     * if there are no Course objects within the search area, then return a
     * Course object that has the points designated by the search area
     * 
     * @param points
     *            a Collection of Course objects that determines the search
     *            areas
     * 
     * @return an ArrayList of Course objects holding
     */
    @Override
    public ArrayList<Course> freeFinder(Point start, Point end,
            ChangingBounds worldBounds) {
        
        //initialize new ArrayList to return
        ArrayList<Course> varTimes = new ArrayList<Course>();

        // if a child isnt a LeafNode and if it intersects with the rectangle
        // drawn by the diagnoals start and end
        // then recurse into specified quadrant
        // and add the result to varTimes to return up chain
        if (!(nw instanceof EmptyNode)
                && worldBounds.intersects(start.getX(), start.getY(),
                        end.getX() - start.getX(), end.getY() - start.getY(),
                        NW)) {

            varTimes.addAll(nw.freeFinder(start, end, new ChangingBounds(
                    worldBounds.calcNW())));
        }
        if (!(ne instanceof EmptyNode)
                && worldBounds.intersects(start.getX(), start.getY(),
                        end.getX() - start.getX(), end.getY() - start.getY(),
                        NE)) {

            varTimes.addAll(ne.freeFinder(start, end, new ChangingBounds(
                    worldBounds.calcNE())));
        }
        if (!(sw instanceof EmptyNode)
                && worldBounds.intersects(start.getX(), start.getY(),
                        end.getX() - start.getX(), end.getY() - start.getY(),
                        SW)) {

            varTimes.addAll(sw.freeFinder(start, end, new ChangingBounds(
                    worldBounds.calcSW())));
        }
        if (!(se instanceof EmptyNode)
                && worldBounds.intersects(start.getX(), start.getY(),
                        end.getX() - start.getX(), end.getY() - start.getY(),
                        SE)) {

            varTimes.addAll(se.freeFinder(start, end, new ChangingBounds(
                    worldBounds.calcSE())));
        }
   
        return varTimes;
    }

    /**
     * calls all list methods on nodes contained within
     * 
     * @param world
     *            the bounds of the current world
     * @param extra
     *            the number of dashes to include
     * @return the string representation of this internal node
     */
    public String list(ChangingBounds world, String extra) {

        return "\n" + extra + "Internal " + world.getCenter().toString()
                + nw.list(new ChangingBounds(world.calcNW()), extra + DELIM)
                + ne.list(new ChangingBounds(world.calcNE()), extra + DELIM)
                + sw.list(new ChangingBounds(world.calcSW()), extra + DELIM)
                + se.list(new ChangingBounds(world.calcSE()), extra + DELIM);
    }
    
    /**
     * 
     */
    @Override
    public String toString() {
        
        return "\nNW " + nw.toString() + "\nNE " + ne.toString() + "\nSW " + sw.toString() + "\nSE " + se.toString();
    }

    /**
     * determines if this internal node is equal to another internal node used
     * mainly for testing purposes (assertEquals must work!)
     * 
     * @param other
     *            the internal node to compare to this one
     * @return value true if equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {

        boolean value = false;

        if (other != null) {

            if (other instanceof InternalNode) {

                if (((InternalNode) other).getNW().equals(nw)
                        && ((InternalNode) other).getNE().equals(ne)
                        && ((InternalNode) other).getSE().equals(se)
                        && ((InternalNode) other).getSW().equals(sw)) {

                    value = true;
                }
            }
        }

        return value;
    }

    // ~Getters/Setters-----------------------------------------------
    /**
     * @return nw
     */
    public QuadNode getNW() {

        return nw;
    }

    /**
     * @return ne
     */
    public QuadNode getNE() {

        return ne;
    }

    /**
     * @return se
     */
    public QuadNode getSE() {

        return se;
    }

    /**
     * @return sw
     */
    public QuadNode getSW() {

        return sw;
    }

    /**
     * sets the value of the nw node to a passed in QuadNode used for testing
     * 
     * @param node
     */
    public void setNW(QuadNode node) {

        nw = node;
    }

    /**
     * sets the value of NE to a passed in quadNode used for testing
     * 
     * @param node
     */
    public void setNE(QuadNode node) {

        ne = node;
    }

    /**
     * sets the value of SE to a passed in QUad NOde used for testing
     * 
     * @param node
     */
    public void setSE(QuadNode node) {

        se = node;
    }

    /**
     * sets the value of the SW to a passed in quad nODe used for testing
     * 
     * @param node
     */
    public void setSW(QuadNode node) {

        sw = node;
    }
}