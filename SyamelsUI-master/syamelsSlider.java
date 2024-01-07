import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Dimension;

class syamelsSlider extends JSlider {
    private String name; // 
    private UISliderListener controller;

    public syamelsSlider(String nm, int min, int max, UISliderListener ctrl) { // 
        this(nm, min, max, (min+max)/2, ctrl);
    }

    public syamelsSlider(String nm, int min, int max, int initial, UISliderListener ctrl) { // 
        super(min, max, initial);
        name = nm;
        controller = ctrl;
        setMajorTickSpacing((max-min)/2);
        setPaintLabels(true);
        setPreferredSize(new Dimension(150,35));
        addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    if (!getValueIsAdjusting()) {
                        new Thread(new Runnable(){public void run(){
                                    controller.sliderPerformed(name, getValue()); 
                                }}).start();
                    }
                }});
    }
}