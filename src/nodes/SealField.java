/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import mygame.Main;

/**
 *
 * @author Kage
 */
public class SealField extends Node {

    /*
     * Actions
     */
    public static final String ACTION_MARK = "Mark";
    public static final String ACTION_SELECT = "Select";
    /*
     * Seal grid params
     */
    public static final float spacing = 0.5f;
    private int size;
    private int[][] matrix;
    private boolean[][] flagLocations;
    private int flags = 0;
    /*
     * Delegates
     */
    private ActionListener markingListener;
    private ActionListener openingListener;
    private Camera camera;
    private AssetManager assetManager;
    private InputManager inputManager;

    public SealField(AssetManager assetManager, InputManager inputManager, Camera camera, int size) {

        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.camera = camera;
        this.size = size;

        markingListener = new MarkingListener();

        inputManager.addMapping(ACTION_MARK,
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // trigger 1: left-button click

        inputManager.addListener(markingListener, ACTION_MARK);

        openingListener = new OpenListener();
        inputManager.addMapping(ACTION_SELECT,
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 1: left-button click

        inputManager.addListener(openingListener, ACTION_SELECT);

        /*
         * Dadd field cells
         */
        MinefieldMatrixFactory factory = new MinefieldMatrixFactory();

        flagLocations = new boolean[size][size];
        matrix = factory.getMinefieldMatrix(size, size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                attachChild(new SealCell("Cell", i * SealCell.width + i * SealField.spacing,
                        1f, j * SealCell.width + j * SealField.spacing, assetManager, i, j, matrix[i][j], this));
            }
        }
    }

    public class MarkingListener implements ActionListener {

        private Cell markedCell;

        public void onAction(String name, boolean keyPressed, float tpf) {

            if (!keyPressed) {
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(camera.getLocation(), camera.getDirection());
                // 3. Collect intersections between Ray and Shootables in results list.
                SealField.this.collideWith(ray, results);
                // 4. Print the results
//                System.out.println("----- Collisions? " + results.size() + "-----");
//                for (int i = 0; i < results.size(); i++) {
//                    // For each hit, we know distance, impact point, name of geometry.
//                    float dist = results.getCollision(i).getDistance();
//                    Vector3f pt = results.getCollision(i).getContactPoint();
//                    String hit = results.getCollision(i).getGeometry().getName();
//                    System.out.println("* Collision #" + i);
//                    System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//                }
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    // Let's interact - we mark the hit with a red dot.

                    if (closest.getGeometry().getName().equals("Mark")) {
                        markedCell = (Cell) closest.getGeometry().getParent();
                    } else {
                        markedCell = (Cell) closest.getGeometry().getParent().getParent().getParent();
                    }

                    if (markedCell.isOpened()) {
                        return;
                    }

                    if (markedCell.isMarked()) {
                        flags--;
                        flagLocations[markedCell.getRow()][markedCell.getCol()] = false;
                        markedCell.unmark();
                    } else if (flags < matrix.length) {
                        flags++;
                        flagLocations[markedCell.getRow()][markedCell.getCol()] = true;
                        markedCell.mark();
                    }
                }
            }
        }
    }

    public class SelectionListener implements ActionListener {

        private Cell lastSelectedCell;

        public void onAction(String name, boolean keyPressed, float tpf) {

            if (!keyPressed) {
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(camera.getLocation(), camera.getDirection());
                // 3. Collect intersections between Ray and Shootables in results list.
                SealField.this.collideWith(ray, results);
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    // Let's interact - we mark the hit with a red dot.
                    if (closest.getGeometry().getName().equals("Mark")) {
                        lastSelectedCell = (Cell) closest.getGeometry().getParent();
                    } else {
                        lastSelectedCell = (Cell) closest.getGeometry().getParent().getParent().getParent();
                    }
                    lastSelectedCell.select();

                    System.out.println(lastSelectedCell.toString());
                } else if (lastSelectedCell != null) {
                    lastSelectedCell.deselect();
                    lastSelectedCell = null;
                }
            }
        }
    }

    public class OpenListener implements ActionListener {

        public void onAction(String name, boolean keyPressed, float tpf) {

            if (!keyPressed) {
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(camera.getLocation(), camera.getDirection());
                // 3. Collect intersections between Ray and Shootables in results list.
                SealField.this.collideWith(ray, results);
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    // Let's interact - we mark the hit with a red dot.

                    Cell openedCell;
                    if (closest.getGeometry().getName().equals("Mark")) {
                        openedCell = (Cell) closest.getGeometry().getParent();
                    } else {
                        openedCell = (Cell) closest.getGeometry().getParent().getParent().getParent();
                    }

                    if (!openedCell.isMarked()) {

                        openedCell.open();

                    }

                }
            }
        }
    }

    public void openedCell(int row, int col) {

        matrix[row][col] = -2;

    }

    public void openedEmptyCell(int row, int col) {

        matrix[row][col] = -2;

        openIfEmpty(row - 1, col - 1);
        openIfEmpty(row - 1, col);
        openIfEmpty(row - 1, col + 1);
        openIfEmpty(row, col - 1);
        openIfEmpty(row, col + 1);
        openIfEmpty(row + 1, col - 1);
        openIfEmpty(row + 1, col);
        openIfEmpty(row + 1, col + 1);

    }

    public void openIfEmpty(int row, int col) {

        if (row > -1 && row < matrix.length
                && col > -1 && col < matrix.length) {

            if (matrix[row][col] > -1) {

                Cell cellToOpen = (Cell) getChild("cell" + "." + row + "." + col);

                cellToOpen.open();
            }

        }

    }

    public void checkCompletion() {

        if (isCompleted()) {
            NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                    assetManager, inputManager, Main.getApp().getAudioRenderer(), Main.getApp().getGuiViewPort());
            /**
             * Create a new NiftyGUI object
             */
            Nifty nifty = niftyDisplay.getNifty();
            /**
             * Read your XML and initialize your custom ScreenController
             */
            nifty.fromXml("Interface/gameWon.xml", "start");
            // nifty.fromXml("Interface/helloworld.xml", "start", new MySettingsScreen(data));
            // attach the Nifty display to the gui view port as a processor
            Main.getApp().getGuiViewPort().addProcessor(niftyDisplay);
// disable the fly cam
            Main.getApp().getFlyByCamera().setDragToRotate(true);

        }

    }

    public void bombFound() {


//        final Explosion exp = new Explosion(assetManager);
//        
//        attachChild(exp);
//
//        new Thread(new Runnable() {
//
//            public void run() {
//            
//                int time = 50;
//                
//                    exp.explode(10000);
////                for(int i = 0; i < 8; i++){
////                    exp.explode(time);
////                    try {
////                        Thread.sleep(50);
////                        time += 50;
////                    } catch (InterruptedException ex) {
////                        Logger.getLogger(SealField.class.getName()).log(Level.SEVERE, null, ex);
////                    }
////                }
//                
//            }
//        }).start();

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, Main.getApp().getAudioRenderer(), Main.getApp().getGuiViewPort());
        /**
         * Create a new NiftyGUI object
         */
        Nifty nifty = niftyDisplay.getNifty();
        /**
         * Read your XML and initialize your custom ScreenController
         */
        nifty.fromXml("Interface/gameOver.xml", "start");
        // nifty.fromXml("Interface/helloworld.xml", "start", new MySettingsScreen(data));
        // attach the Nifty display to the gui view port as a processor
        Main.getApp().getGuiViewPort().addProcessor(niftyDisplay);
// disable the fly cam
        Main.getApp().getFlyByCamera().setDragToRotate(true);

    }

    public boolean isCompleted() {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {

                if (matrix[i][j] == -1) {
                    if (!flagLocations[i][j]) {
                        return false;
                    }
                } else if (matrix[i][j] != -2) {
                    return false;
                }

            }
        }

        return true;
    }
}
