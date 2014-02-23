/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

/**
 *
 * @author Kage
 */
public interface Cell {
    
    /*
     * Selcts a cell highlighting it
     */
    public void select();
    
    /*
     * Selcts a cell highlighting it
     */
    public void deselect();
    
    /*
     * Opens a cell revealing it's value
     */
    public void open();
    
    /*
     * Mark the cell as mine containing
     */
    public void mark();
    
    /*
     * Marks the cell as simple value containing
     */
    public void unmark();
 
    public boolean isMarked();
    
    public boolean isSelected();
    
    public boolean isOpened();
    
    public int getRow();
    
    public int getCol();
}
