import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JViewport;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.*;

import java.util.NoSuchElementException;
import java.util.InputMismatchException;

/** 
 * The UI class provides a simple, flexible Graphical User Interface suitable 
 * for small programs.  The UI class will construct a window which can contain 
 * <ul>
 * <li> a menu at the top (with commands for turning the Trace facility on and off, and quitting.
 * <li> a text output pane which can display text output, and read text input
 * <li> a graphics output pane which can display graphical output (anything
 *      output using the draw... methods).
 *      The user can also input positions using the mouse in this region.
 * <LI> an input panel which can contain buttons, textfields,
 *      and sliders.
 * <LI> a message line at the bottom for short messages to the user.
 * </UL>
 * The text pane, graphics pane, and input panel will only be displayed if they are used - if
 * your program only tries to draw on the graphics pane, then only the graphics pane will be visible.
 */

public class UI {
    private JFrame frame;
    private JMenuBar menuBar;
    private JPanel inputPanel;

    // private Box IOarea;
    private syamelsCanvas canvas;
    private syamelsTextArea textPane; 
    private JTextArea messageArea;
    private syamelsMouseListener ml = null;

    private static final String DISP_GRAPHICS = "Graphics";
    private static final String DISP_TEXT = "Text";
    static UI theUI; 
    //    static boolean initialised = false;

    static {initialise();}

    /** Ensure that the User Interface window is initialised */
    public static void initialise(){
        if (theUI==null){
            theUI = new UI();
            Trace.initialise(theUI.menuBar);
        }
    }

    /** Construct a new UI object, with its associated window.  <BR>
     * If a String argument is provided, the window will be given a title of the specified string.
     */
    private UI() {
        frame = new JFrame();
        frame.setSize(600,30);

        menuBar = initMenuBar();
        frame.setJMenuBar(menuBar);

        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setSize(40, 600);
        frame.add(inputPanel, BorderLayout.WEST);

        messageArea = new JTextArea(1, 80);
        messageArea.setEditable(false);
        frame.add(messageArea, BorderLayout.SOUTH);

        canvas = new syamelsCanvas();
        textPane = new syamelsTextArea(0,60);
        textPane.setFont(Font.decode("Monospaced"));

        JScrollPane textSP = new JScrollPane(textPane);
        JScrollPane graphicsSP = new JScrollPane(canvas,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        graphicsSP.getViewport().setBackground(Color.white);
        graphicsSP.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        frame.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, textSP, graphicsSP),
            BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { quit();}
            });

        frame.pack();
        frame.setVisible(true);  // Cause frame to be laid out
        sleep(300);  // Give everything a chance to be displayed (may not help)
    }

    /*--- Menu Bar --------------------------------------------------------------*/

    private JMenuBar initMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu menuItems = new JMenu("MENU");

        JMenuItem file = new JMenuItem("File");
        JMenuItem traceOnOff = new JMenuItem("Trace On/Off");
        JMenuItem quit = new JMenuItem("Quit");

        file.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {UIFileChooser.open();}});
        traceOnOff.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Trace.setVisible(!Trace.isVisible());}});
        quit.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {quit();}});

        menuItems.add(file);
        menuItems.add(traceOnOff);
        menuItems.add(quit);
        mb.add(menuItems);

        return mb;
    }

    /* ======= UTILITIES ======================================================== */

    static void checkInitialised() {
        if (theUI==null)
            throw new RuntimeException("The UI was not initialised or had been quit");
    }

    static void ensureGraphics() {
        if (theUI==null) checkInitialised();
    }

    static void ensureText() {
        if (theUI==null) checkInitialised();
    }

    /*-----------------------------------------------------------------*/

    private void dispose() {
        if (frame != null) {
            frame.dispose(); 
            frame = null;
        }
    }

    /*-----------------------------------------------------------------*/

    static JFrame getFrame() {
        if (theUI!=null) return theUI.frame;
        return null;
    }

    /*-----------------------------------------------------------------*/

    private void reweightLastComponent(Container c) {
        int ncomps = c.getComponentCount();
        if (ncomps > 0) {
            GridBagLayout lm = (GridBagLayout)c.getLayout();
            Component comp = inputPanel.getComponent(ncomps-1);
            GridBagConstraints lc = lm.getConstraints(comp);
            lc.weighty = 0;
            lm.setConstraints(comp,lc);
        }
    }

    /* ================================ DRAWING on the GRAPHICS PANE ===================================== */

    /** Clear the contents of the graphics output region
    With argument of false, do not redisplay the graphics pane yet*/
    public static void clearGraphics() {
        ensureGraphics();
        theUI.canvas.clear(true);
    }

    public static void clearGraphics(boolean redisplay) {
        ensureGraphics();
        theUI.canvas.clear(redisplay);
    }

    /** Repaint the contents of the graphics output region.  */
    public static void repaintGraphics() {
        ensureGraphics();
        theUI.canvas.display();
    }

    public static void setColor(Color col){
        checkInitialised();
        theUI.canvas.setColor(col);
    }

    public static void setForeGround(Color col){
        checkInitialised();
        theUI.canvas.setColor(col);
    }

    /** Get the Graphics object so that programs can do more complicated 
    operations on the image than are provided by this class. */
    public Graphics getCanvas() {
        return canvas.getBackingGraphics();
    }

    /*---- Draw Line---------------------------------------------------------*/

    /** Draw a line in the graphics output region from (x1, y1) to (x2, y2)<BR>
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */
    public static void drawLine(double x1, double y1, double x2, double y2) {
        drawLine(x1, y1, x2, y2, true);
    }

    public static void drawLine(double x1, double y1, double x2, double y2,  boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, redraw);
    }

    /*---- Erase Line-----------------------------------------------------*/

    public static void eraseLine(double x1, double y1, double x2, double y2) {
        eraseLine(x1, y1, x2, y2, true);
    }

    /** Erase the line in the graphics output region from (x1, y1) to (x2, y2)
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void eraseLine(double x1, double y1, double x2, double y2,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseLine((int)x1, (int)y1, (int)x2, (int)y2, redraw);
    }

    /*----Invert Line-----------------------------------------------------*/

    public static void invertLine(double x1, double y1, double x2, double y2) {
        invertLine(x1, y1, x2, y2, true);
    }

    /** Invert the line in the graphics output region from (x1, y1) to (x2, y2)
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void invertLine(double x1, double y1, double x2, double y2,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.invertLine((int)x1, (int)y1, (int)x2, (int)y2, redraw);
    }

    /*---- Draw Rectangle-----------------------------------------------------*/

    /** Draw the outline of a rectangle in the graphics output region with
     * a top-left corner at (x, y) and of the specified width and height.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */
    public static void drawRect(double x, double y, double width, double height) {
        drawRect(x, y, width, height, true);
    }

    public static void drawRect(double x, double y, double width, double height,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawRect((int)x, (int)y, (int)width, (int)height, redraw);
    }

    /*---- Fill Rectangle-----------------------------------------------------*/

    /** Draw a solid rectangle in the graphics output region with
     * a top-left corner at (x, y) and of the specified width and height.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */
    public static void fillRect(double x, double y, double width, double height) {
        fillRect(x, y, width, height, true);
    }

    public static void fillRect(double x, double y, double width, double height,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.fillRect((int)x, (int)y, (int)width, (int)height, redraw);
    }

    /*---- Erase Rect-----------------------------------------------------*/

    public static void eraseRect(double x, double y, double width, double height) {
        eraseRect(x, y, width, height, true);
    }

    /** Erase the rectangular region in the graphics output region with
     * a top-left corner at (x, y) and of the specified width and height.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void eraseRect(double x, double y, double width, double height,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseRect((int)x, (int)y, (int)width, (int)height, true);
    }

    /*---- Invert Rect-----------------------------------------------------*/

    public static void invertRect(double x, double y, double width, double height) {
        invertRect(x, y, width, height, true);
    }

    /** Invert the rectangular region in the graphics output region with
     * a top-left corner at (x, y) and of the specified width and height.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void invertRect(double x, double y, double width, double height,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.invertRect((int)x, (int)y, (int)width, (int)height, true);
    }

    /*---- Draw String-----------------------------------------------------*/

    /** Draw the given string in the graphics output region at the position (x, y) .
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */
    public static void drawString(String s, double x, double y) {
        drawString(s, x, y, true);
    }

    public static void drawString(String s, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawString(s, (int)x, (int)y, redraw);
    }

    /*---- Erase String---------------------------------------------------*/

    /** Erase the region covered given string in the graphics output region at the position (x, y).
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void eraseString(String s, double x, double y) {
        eraseString(s, x, y, true);
    }

    public static void eraseString(String s, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseString(s, (int)x, (int)y, redraw);
    }
    /*---- Invert String---------------------------------------------------*/

    /** Erase the region covered given string in the graphics output region at the position (x, y).
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void invertString(String s, double x, double y) {
        invertString(s, x, y, true);
    }

    public static void invertString(String s, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.invertString(s, (int)x, (int)y, redraw);
    }

    /*---- Draw Oval-----------------------------------------------------*/
    /** Draw the outline of an oval in the graphics output region with the left edge of
     * the oval at x, the top of the oval at y, and having the specified width and height.
     * With an final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */

    public static void drawOval(double x, double y, double width, double height) {
        drawOval(x, y, width, height, true);
    }

    public static void drawOval(double x, double y, double width, double height, boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawOval((int)x, (int)y, (int)width, (int)height, redraw);
    }

    /*---- Fill Oval-----------------------------------------------------*/
    /** Draw a solid oval in the graphics output region with the left edge of the
     * oval at x, the top of the oval at y, and having the specified width and height.
     * With a final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */

    public static void fillOval(double x, double y, double width, double height) {
        fillOval(x, y, width, height, true);
    }

    public static void fillOval(double x, double y, double width, double height, boolean redraw) {
        ensureGraphics();
        theUI.canvas.fillOval((int)x, (int)y, (int)width, (int)height, redraw);
    }

    /*---- Erase Oval-----------------------------------------------------*/
    /** Erase an oval region in the graphics output region with the left edge of the
     * oval at x, the top of the oval at y, and having the specified width and height.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */

    public static void eraseOval(double x, double y, double width, double height) {
        eraseOval(x, y, width, height, true);
    }

    public static void eraseOval(double x, double y, double width, double height, boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseOval((int)x, (int)y, (int)width, (int)height, redraw);
    }

    /*----Invert Oval-----------------------------------------------------*/
    /** Invert an oval region in the graphics output region with the left edge of the
     * oval at x, the top of the oval at y, and having the specified width and height.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */

    public static void invertOval(double x, double y, double width, double height) {
        invertOval(x, y, width, height, true);
    }

    public static void invertOval(double x, double y, double width, double height, boolean redraw) {
        ensureGraphics();
        theUI.canvas.invertOval((int)x, (int)y, (int)width, (int)height, redraw);
    }

    /*---- Draw Arc-----------------------------------------------------*/
    /** Draw the outline of an arc in the graphics output region.  An arc is a segment of
     * an oval, and is specified by giving the left edge of the
     * oval (x), the top of the oval (y), the width and height of the oval, and the angle 
     * (anticlockwise from the x-axis) that the arc starts, and the angle of the arc.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */

    public static void drawArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle) {
        drawArc(x, y, width, height, startAngle, arcAngle, true);
    }

    public static void drawArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle, boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawArc((int)x, (int)y, (int)width, (int)height, 
            (int)startAngle, (int)arcAngle, redraw);
    }

    /*---- Fill Arc-----------------------------------------------------*/

    /** Draw a solid arc in the graphics output region.  An arc is a segment of
     * an oval, and is specified by giving the left edge of the
     * oval (x), the top of the oval (y), the width and height of the oval, and the angle 
     * (anticlockwise from the x-axis) that the arc starts, and the angle of the arc.
     * With a final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */
    public static void fillArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle) {
        fillArc(x, y, width, height, startAngle, arcAngle, true);
    }

    public static void fillArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle, boolean redraw) {
        ensureGraphics();
        theUI.canvas.fillArc((int)x, (int)y, (int)width, (int)height, 
            (int)startAngle, (int)arcAngle, redraw);
    }

    /*---- Erase Arc-----------------------------------------------------*/
    /** Erase an arc-shaped region of the graphics output region.  An arc is a 
     * segment of an oval, and is specified by giving the left edge of the
     * oval (x), the top of the oval (y), the width and height of the oval, and the angle 
     * (anticlockwise from the x-axis) that the arc starts, and the angle of the arc.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void eraseArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle) {
        eraseArc(x, y, width, height, startAngle, arcAngle, true);
    }

    public static void eraseArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle, boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseArc((int)x, (int)y, (int)width, (int)height, (int)startAngle, (int)arcAngle, redraw);
    }

    /*----Invert Arc-----------------------------------------------------*/
    /** Invert an arc-shaped region of the graphics output region.  An arc is a 
     * segment of an oval, and is specified by giving the left edge of the
     * oval (x), the top of the oval (y), the width and height of the oval, and the angle 
     * (anticlockwise from the x-axis) that the arc starts, and the angle of the arc.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void invertArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle) {
        invertArc(x, y, width, height, startAngle, arcAngle, true);
    }

    public static void invertArc(double x, double y, double width, double height, 
    double startAngle, double arcAngle, boolean redraw) {
        ensureGraphics();
        theUI.canvas.invertArc((int)x, (int)y, (int)width, (int)height, (int)startAngle, (int)arcAngle, redraw);
    }

    /*---- Draw Image-----------------------------------------------------*/
    /** Draw an image in the graphics output region.  
     * The image may be specified by giving a file name, or providing the Image object.
     * The 2nd and 3rd arguments are where the left, top corner of the image should be placed.
     * Optional 4th and 5th arguments are the width and height to scale the image; if
     * not provided, the image will have its natural size
     * With a final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */

    public static void drawImage(String fileName, double x, double y, double width, double height) {
        drawImage(fileName, x, y, width, height, true);
    }

    public static void drawImage(String fileName, double x, double y, double width, double height,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawImage(fileName, (int)x, (int)y, (int)width, (int)height, redraw);
    }

    public static void drawImage(String fileName, double x, double y) {
        drawImage(fileName, x, y, true);
    }

    public static void drawImage(String fileName, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawImage(fileName, (int)x, (int)y, redraw);
    }

    public static void drawImage(Image img, double x, double y, double width, double height) {
        drawImage(img, x, y, width, height, true);
    }

    public static void drawImage(Image img, double x, double y, double width, double height,
    boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawImage(img, (int)x, (int)y, (int)width, (int)height, redraw);
    }

    public static void drawImage(Image img, double x, double y) {
        drawImage(img, x, y, true);
    }

    public static void drawImage(Image img, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.drawImage(img, (int)x, (int)y, redraw);
    }

    /*---- Erase Image-----------------------------------------------------*/
    /** Erase an unscaled image, specified by a file name or an Image object,
     * With a final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */
    public static void eraseImage(String fileName, double x, double y) {
        eraseImage(fileName, x, y, true);
    }

    public static void eraseImage(String fileName, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseImage(fileName, (int)x, (int)y, redraw);
    }

    public static void eraseImage(Image img, double x, double y) {
        eraseImage(img, x, y, true);
    }

    public static void eraseImage(Image img, double x, double y, boolean redraw) {
        ensureGraphics();
        theUI.canvas.eraseImage(img, (int)x, (int)y, redraw);
    }

    /*---- Draw Polygon-----------------------------------------------------*/
    /** Draw the outline of an polygon in the graphics output region.
     * The polygon is specified by two arrays containing the x coordinates and the y coordinates
     * of the series of vertices of the polygon, and the number of vertices.
     * With an final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */

    public static void drawPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        drawPolygon(xPoints, yPoints, nPoints, true);
    }

    public static void drawPolygon(double[] xPoints, double[] yPoints, int nPoints, boolean redraw) {
        ensureGraphics();
        int[] xs = new int[nPoints];
        int[] ys = new int[nPoints];
        for (int i=0; i<nPoints; i++){
            xs[i] = (int) xPoints[i];
            ys[i] = (int) yPoints[i];
        }
        theUI.canvas.drawPolygon(xs, ys, nPoints, redraw);
    }

    /*---- Fill Polygon-----------------------------------------------------*/
    /** Draw a filled polygon in the graphics output region.
     * The polygon is specified by two arrays containing the x coordinates and the y coordinates
     * of the series of vertices of the polygon, and the number of vertices.
     * With a final boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     * (Drawing a number of shapes without redisplaying, then calling the <TT>repaintGraphics()</TT>
     * method produces faster and smoother animation, but is more complicated.)
     */

    public static void fillPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        fillPolygon(xPoints, yPoints, nPoints, true);
    }

    public static void fillPolygon(double[] xPoints, double[] yPoints, int nPoints, boolean redraw) {
        ensureGraphics();
        int[] xs = new int[nPoints];
        int[] ys = new int[nPoints];
        for (int i=0; i<nPoints; i++){
            xs[i] = (int) xPoints[i];
            ys[i] = (int) yPoints[i];
        }
        theUI.canvas.fillPolygon(xs, ys, nPoints, redraw);
    }

    /*---- Erase Polygon-----------------------------------------------------*/
    /** Erase an polygon region in the graphics output region.
     * The polygon is specified by two arrays containing the x coordinates and the y coordinates
     * of the series of vertices of the polygon, and the number of vertices.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */

    public static void erasePolygon(double[] xPoints, double[] yPoints, int nPoints) {
        erasePolygon(xPoints, yPoints, nPoints, true);
    }

    public static void erasePolygon(double[] xPoints, double[] yPoints, int nPoints, boolean redraw) {
        ensureGraphics();
        int[] xs = new int[nPoints];
        int[] ys = new int[nPoints];
        for (int i=0; i<nPoints; i++){
            xs[i] = (int) xPoints[i];
            ys[i] = (int) yPoints[i];
        }
        theUI.canvas.erasePolygon(xs, ys, nPoints, redraw);
    }

    /*----Invert Polygon-----------------------------------------------------*/
    /** Invert an polygon region in the graphics output region.
     * The polygon is specified by two arrays containing the x coordinates and the y coordinates
     * of the series of vertices of the polygon, and the number of vertices.
     * With an additional boolean argument of <TT>false</TT>, do not redisplay the graphics pane yet.
     */

    public static void invertPolygon(double[] xPoints, double[] yPoints, int nPoints) {
        invertPolygon(xPoints, yPoints, nPoints, true);
    }

    public static void invertPolygon(double[] xPoints, double[] yPoints, int nPoints, boolean redraw) {
        ensureGraphics();
        int[] xs = new int[nPoints];
        int[] ys = new int[nPoints];
        for (int i=0; i<nPoints; i++){
            xs[i] = (int) xPoints[i];
            ys[i] = (int) yPoints[i];
        }
        theUI.canvas.invertPolygon(xs, ys, nPoints, redraw);
    }

    /*==== OUTPUT to TEXT PANE=============================================*/
    /** Clear the contents of the text output region  */
    public static void clearText() {
        ensureText();
        theUI.textPane.clear();
    }

    /** Print a String to the text pane
     */
    public static void print(String s) {
        ensureText();
        if (s == null) 
            theUI.textPane.outputString("NULL");
        else 
            theUI.textPane.outputString(s);
    }

    /** Print a boolean to the text pane
     */
    public static void print(boolean b) {
        ensureText();
        print(String.valueOf(b));
    }

    /** Print a character  to the text pane
     */
    public static void print(char c) {
        print(String.valueOf(c));
    }

    /** Print a number to the text pane
     */
    public static void print(double d) {
        print(String.valueOf(d));
    }

    /** Print an integer to the text pane
     */
    public static void print(int i) {
        print(String.valueOf(i));
    }

    /** Print an object to the text pane
    Note, it calls the toString() method on the object, and prints the result.
    This may or may not be useful.
     */
    public static void print(Object o) {
        print(String.valueOf(o));
    }

    /** Start a new line on the text pane.
     */
    public static void println() {
        print("\n");
    }

    /** Print a string to the text pane and start a new line.
     */
    public static void println(String s) {
        print(s+"\n");
    }

    /** Print a boolean to the text pane and start a new line.
     */
    public static void println(boolean b) {
        print(String.valueOf(b)+"\n");
    }

    /** Print a character to the text pane and start a new line.
     */
    public static void println(char c) {
        print(c+"\n");
    }

    /** Print a number to the text pane and start a new line.
     */
    public static void println(double d) {
        print(String.valueOf(d)+"\n"); 
    }

    /** Print an integer to the text pane and start a new line.
     */
    public static void println(int i) {
        print(String.valueOf(i)+"\n"); 
    }

    /** Print an object to the text pane and start a new line.
    Note, it calls the toString() method on the object, and prints the result.
    This may or may not be useful.
     */
    public static void println(Object o) {
        print(String.valueOf(o)+"\n");
    }

    /*------printing with a format string ------------------------------*/

    /** The <TT>printf()</TT> method requires a format string (which
     *  will contain "holes" specified with %'s) and additional arguments
     *  which will be placed "in the holes", using the specified formatting.
     */
    public static void printf(String format, Object... args){ 
        print(String.format(format, args));
    }

    /*====== INPUT from TEXT PANE ==============================*/

    // needs to act like a Scanner, but will block until input
    // (perhaps it should put "Enter input..." in the meassage area if blocked)
    // entering text will have no effect until return is typed, then the text will be
    // put into a field of theUI, from which these methods will get the tokens
    // actually, we need two fields - the current scanner, and the StringBuilder waiting to be processed
    // these methods will get a value out of the scanner if it has a token/line.
    // if there is no scanner or the scanner doesn't have a token/line, then it will make a new
    // Scanner on the outstanding StringBuilder. if the stringBuilder is empty, then it will
    // put a message prompting for input, and wait??? (could be a problem if the keylistener
    // is in the same thread!! - may need to put the call to the read in another thread

    private static void prompt(String question){
        theUI.textPane.clearInputBuffer();
        question = question.trim();
        if (!(question.endsWith("?") || question.endsWith(":")))
            question = question+": ";
        else
            question = question+" ";
        theUI.textPane.outputString(question);
    }

    /** Prints the question and waits for the user to answer.
    Returns the first token in their answer as a String.
    Removes any pending user input before asking the question.
     */
    public static String askToken(String question){
        checkInitialised();
        prompt(question);
        return theUI.textPane.next();
    }

    /** Prints the question and waits for the user to answer
    Returns their answer as a String.
    Removes any pending user input before asking the question.
     */
    public static String askString(String question){
        checkInitialised();
        prompt(question);
        return theUI.textPane.nextLine();
    }

    /** Prints the question and waits for the user to answer with an integer
    Returns their answer as an int
    Removes any pending user input before asking the question.
     */
    public static int askInt(String question){
        checkInitialised();
        prompt(question);
        while (true){
            try{
                int ans = theUI.textPane.nextInt();
                return ans;
            }
            catch (InputMismatchException e){}
            prompt(question+ " (must be an integer)");
        }
    }

    /** Prints the question and waits for the user to answer with a number
    Returns their answer as a double.
    Removes any pending user input before asking the question.
     */
    public static double askDouble(String question){
        checkInitialised();
        prompt(question);
        while (true){
            try{
                return theUI.textPane.nextDouble();
            }
            catch (InputMismatchException e){}
            prompt(question+ " (must be a number)");
        }
    }

    /** Prints the question and waits for the user to answer with yes/no or true/false.
    Returns their answer as a boolean.
    Removes any pending user input before asking the question.
     */
    public static boolean askBoolean(String question){
        checkInitialised();
        prompt(question);
        while (true){
            try{
                return theUI.textPane.nextBoolean();
            }
            catch (InputMismatchException e){}
            prompt(question+ " (must be a boolean)");
        }
    }

    /** Read the next token of the user's input and return it. 
     * Waits for user input if there isn't any yet.
     */
    public static String next(){
        checkInitialised();
        return theUI.textPane.next();
    }

    /** Read the next token of the user's input and
     * return it as an int if it is an integer. 
     * Throws an exception if it is not an integer. 
     * Waits for user input if there isn't any yet.
     */
    public static int nextInt(){
        checkInitialised();
        return theUI.textPane.nextInt();
    }

    /** Read the next token of the user's input and
     * return it as a double if it is a number. 
     * Throws an exception if it is not a number. 
     * Waits for user input if there isn't any yet.
     */
    public static double nextDouble(){
        checkInitialised();
        return theUI.textPane.nextDouble();
    }

    /** Read the next token of the user's input and
     * return true if it is "yes", "y", or "true",
     * return false if it is "no", "n", or "false" (case insensitive).
     * Throws an exception if it is anything else.
     * Waits for user input if there isn't any yet.
     */
    public static boolean nextBoolean(){
        checkInitialised();
        return theUI.textPane.nextBoolean();
    }

    /** Read the remaining characters of the user's input up to (but not including) the next end-of-line
     * and return them as a string. Reads and throws away the end-of-line character.
     * If there are no characters on the line, then it returns an empty string ("").
     * Waits for user input if there isn't any yet.
     */
    public static String nextLine(){
        checkInitialised();
        return theUI.textPane.nextLine();
    }

    /** Returns true if there is any user input, but waits for the
     * user to type something if there isn't any yet.
     * (ie, always returns true).
     */
    public static boolean hasNext(){
        checkInitialised();
        return theUI.textPane.hasNext();
    }

    /** Returns true if the next token in the user input is an integer.
    waits for the user to type something if there isn't any yet.
     */
    public static boolean hasNextInt(){
        checkInitialised();
        return theUI.textPane.hasNextInt();
    }

    /** Returns true if the next token in the user input is a number.
    waits for the user to type something if there isn't any yet.
     */
    public static boolean hasNextDouble(){
        checkInitialised();
        return theUI.textPane.hasNextDouble();
    }

    /** Returns true if the next token in the user input is a yes/no or true/false.
    waits for the user to type something if there isn't any yet.
     */
    public static boolean hasNextBoolean(){
        checkInitialised();
        return theUI.textPane.hasNextBoolean();
    }

    /** Returns true if there is any user input, but waits for the
    user to type somethign if there isn't any yet.
    (ie, always returns true).*/
    public static boolean hasNextLine(){
        checkInitialised();
        return theUI.textPane.hasNextLine();
    }

    /*==== MESSAGE OUTPUT =====================================================*/
    /** Print a message to the message line.*/
    public static void printMessage(String s) {
        checkInitialised();
        theUI.messageArea.setText(s);
    }

    /* ==== BUTTONS and INPUT FIELDS =============================================*/

    /* This needs to fix up the input panel, make sure it is visible and
    has been redrawn.  It doesn't work yet!!! */
    private void fixInputPanel(){
        theUI.inputPanel.revalidate();
        //theUI.inputPanel.repaint();
        //theUI.frame.invalidate();
        theUI.frame.validate();
        theUI.frame.pack();
    }

    /** Add a button to the input panel on the left side of the gui window.
     * The arguments are the name that should appear on the button and the object
     * that will handle the button event.  <P>
     * Typically, the object will be <TT>this</TT> - the object that is putting the button
     * on the gui window, but it need not be the same object. <P>
     * The object that handles the button event must implement the <TT>UIButtonListener</TT>
     * interface which means that it must provide the method:<QUOTE><TT>
     *  public void buttonPerformed(String name)  </TT></QUOTE>
     * which will be passed the name of the button that was pressed.
     */
    public static void addButton(String name, UIButtonListener controller) {
        checkInitialised();

        theUI.reweightLastComponent(theUI.inputPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(5,3,5,3);
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1.0;
        c.ipady = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        theUI.inputPanel.add(new syamelsButton(name, controller), c);

        theUI.fixInputPanel();
    }

    /** Add a text field to the input panel on the left side of the gui window.<P>
     * A text field allows the user to enter a string value by typing the string
     * into the field. <P>
     * The arguments are the name of the text field and the object
     * that will handle the event when a string is entered into the text field.  <P>
     * Typically, the object will be <TT>this</TT> - the object that is putting the text field
     * on the gui window, but it need not be the same object. <P>
     * The object that handles the text field event must implement the <TT>UITextFieldListener</TT>
     * interface which means that it must provide the method:<QUOTE><TT>
     *   public void  textFieldPerformed(String name, String newText) </TT></QUOTE>
     * which will be passed two strings: <UL><LI>
     * the name of the text field into which a value was entered,<LI>
     * the value that was entered into the field.</UL>
     */
    public static void addTextField(String s, UITextFieldListener obj) {
        checkInitialised();

        theUI.reweightLastComponent(theUI.inputPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(7,3,2,3);
        c.anchor = GridBagConstraints.WEST;
        theUI.inputPanel.add(new JLabel(s), c);

        c.insets = new Insets(2,3,7,3);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 1.0;
        theUI.inputPanel.add(new syamelsTextField(s, 10, obj), c);

        theUI.fixInputPanel();
    }

    // I think we don't want this.
    /** Add a number field to the input panel on the left side of the gui window.<P>
     * A number field allows the user to enter a numeric value by typing the number
     * into the field. <P>
     * The arguments are the name of the number field and the object
     * that will handle the event when a number is entered into the number field.  <P>
     * Typically, the object will be <TT>this</TT> - the object that is putting the number field
     * on the gui window, but it need not be the same object. <P>
     * The object that handles the number field event must implement the <TT>UINumberFieldListener</TT>
     * interface which means that it must provide the method:<QUOTE><TT>
     *   public void numberFieldPerformed(String name, double num) </TT></QUOTE>
     * which will be passed two values: <UL><LI>
     * the name of the number field into which a value was entered,<LI>
     * the value that was entered into the field (as a double)</UL>
    public static void addNumberField(String s, UINumberFieldListener obj) {
    checkInitialised();

    reweightLastComponent(inputPanel);

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.insets = new Insets(7,3,2,3);
    c.anchor = GridBagConstraints.WEST;
    inputPanel.add(new Label(s), c);

    c.insets = new Insets(2,3,7,3);
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weighty = 1.0;
    inputPanel.add(new Comp102NumberField(this, s, 20, obj), c);

    theUI.frame.pack();
    }
     */

    /** Add a slider to the input panel on the left side of the gui window.<P>
     * A slider allows the user to enter a numeric value by moving the slider knob
     * along the slider. <P>
     * The arguments are the name of the slider, the minimum and maximum vales of the slider
     * (integers) and the object
     * that will handle the event when the slider is moved.  <P>
     * Typically, the object will be <TT>this</TT> - the object that is putting the slider
     * on the gui window, but it need not be the same object. <P>
     * The object that handles the slider event must implement the <TT>UISliderListener</TT>
     * interface which means that it must provide the method:<QUOTE><TT>
     *  public void sliderPerformed(String name, double num) </TT></QUOTE>
     * which will be passed two values: <UL><LI>
     * the name of the slider that was moved,<LI>
     * the current value of the slider (as a double)</UL>
     */

    public static void addSlider(String s, double min, double max, UISliderListener obj) {
        addSlider(s, new syamelsSlider(s, (int)min, (int)max, obj));
    }

    public static void addSlider(String s, double min, double max, double initial, UISliderListener obj) {
        addSlider(s, new syamelsSlider(s, (int)min, (int)max, (int)initial, obj));
    }

    private static void addSlider(String s, syamelsSlider sl) {
        checkInitialised();
        theUI.reweightLastComponent(theUI.inputPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(7,3,2,3);
        c.anchor = GridBagConstraints.WEST;
        theUI.inputPanel.add(new JLabel(s), c);

        c.insets = new Insets(2,3,7,3);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 1.0;
        theUI.inputPanel.add(sl, c);

        theUI.fixInputPanel();
    }

    /*----Mouse Input-------------------------------------------------------*/

    /** Enable the user to use the mouse to use the mouse on the graphics
     * pane on the right side of the gui window.  The program will respond to the user <UL><LI>
     *  pressing the mouse button,<LI>
     *  releasing the mouse button<LI>
     *  clicking the mouse button (pressing and releasing without moving)</UL>
     * but will not notice movements of the mouse.<P>
     * The argument is the object that will handle the events when the mouse is
     * pressed, released, or clicked in the graphics pane.  <BR>
     * Typically, the object will be <TT>this</TT> - the object that is invoking 
     * <TT>setMouseListener</TT>, but it need not be the same object. <P>
     * The object that handles the mouse events must implement the <TT>UIMouseListener</TT>
     * interface which means that it must provide the method:<PRE>
     *     public void mousePerformed(String action, double x, double y);</PRE>
     * which will be passed three values: <UL><LI>
     * a string specifying what the user did: "pressed", "released", or "clicked", or "doubleclicked" <LI>
     * the coordinates (two doubles) of the mouse when the event happened.</UL>
     * Note also that if the user clicks the mouse there will be three events - a "pressed",
     * a "released", and a "clicked".
     */
    public static void setMouseListener(UIMouseListener obj) {
        checkInitialised();
        if (theUI.ml != null){
            theUI.canvas.removeMouseListener(theUI.ml);
            theUI.canvas.removeMouseMotionListener(theUI.ml);
        }
        theUI.ml = new syamelsMouseListener(obj);
        theUI.canvas.addMouseListener(theUI.ml);
    }

    /** Enable the user to use the mouse to use the mouse on the graphics
     * pane on the right side of the gui window.
     * This is identical to the <TT>setMouseListener()</TT>
     * method (see above) except that the program will also respond to the user <UL><LI>
     *  moving the mouse (moving it with the button up), and <LI>
     *  dragging the mouse (moving it with the button down) </UL>
     * The argument is the object that will handle the events when the mouse is
     * pressed, released, or clicked in the graphics pane.  <BR>
     * Typically, the object will be <TT>this</TT> - the object that is invoking 
     * <TT>setMouseMotionListener()</TT>, but it need not be the same object. <P>
     * The object that handles the mouse events must implement the <TT>UIMouseListener</TT>
     * interface which means that it must provide the methods:<PRE>
     *    public void mousePerformed(String action, double x, double y,);</PRE>
     * which will be passed three values: <UL><LI>
     * a string specifying what the user did: "pressed", "released", "clicked", "doubleclicked",
     *   "moved", or "dragged"  <LI>
     * the coordinates (two doubles) of the mouse when the event happened.</UL>
     */
    public static void setMouseMotionListener(UIMouseListener obj) {
        checkInitialised();
        if (theUI.ml != null){
            theUI.canvas.removeMouseListener(theUI.ml);
            theUI.canvas.removeMouseMotionListener(theUI.ml);
        }
        theUI.ml = new syamelsMouseListener(obj);
        theUI.canvas.addMouseListener(theUI.ml);
        theUI.canvas.addMouseMotionListener(theUI.ml);
    }

    /* ==== SLEEP and QUIT =========================================================*/
    /** Causes the program to pause for a specified number of milliseconds.
     * This is useful to control graphical animations - do a sleep between consecutive
     * draw... methods to control the speed of the animation.
     */
    public static void sleep(double millis) {
        try { Thread.sleep((long)millis); }
        catch (InterruptedException e) { }
    }

    /** Get rid of the interface windows.
     * If these are the only open windows, then the program should quit */
    public static void quit() {
        if (theUI!=null){
            theUI.dispose();
            Trace.dispose();
            theUI=null;
        }
        System.exit(0); 
    }

}
