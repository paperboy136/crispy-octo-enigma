import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

class syamelsMouseListener extends MouseAdapter implements MouseMotionListener{
    private UIMouseListener controller;

    public syamelsMouseListener(UIMouseListener controller) {
        this.controller = controller;
    }

    public void mousePressed(final MouseEvent e) {
        if (controller != null) {
            controller.mousePerformed("pressed", (double)e.getX(), (double)e.getY());
        }
    }

    public void mouseReleased(final MouseEvent e) {
        if (controller != null) {
            controller.mousePerformed("released", (double)e.getX(), (double)e.getY());
        }
    }

    public void mouseClicked(final MouseEvent e) {
        if (controller != null) {
            if (e.getClickCount() ==1){
                controller.mousePerformed("clicked", (double)e.getX(), (double)e.getY());
            }
            else if (e.getClickCount() > 1){
                controller.mousePerformed("doubleclicked", (double)e.getX(), (double)e.getY());
            }
        }
    }

    public void mouseMoved(final MouseEvent e) {
        if (controller != null) {
            controller.mousePerformed("moved", (double)e.getX(), (double)e.getY());
        }
    }

    public void mouseDragged(final MouseEvent e) {
        if (controller != null) {
            controller.mousePerformed("dragged", (double)e.getX(), (double)e.getY());
        }
    }

}
