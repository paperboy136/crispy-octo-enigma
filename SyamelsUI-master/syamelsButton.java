import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class syamelsButton extends JButton implements ActionListener {
    private UIButtonListener controller;

    /*  public void setButtonListener(UIButtonListener controller) {
    this.controller = controller;
    }*/

    public syamelsButton(String name, UIButtonListener controller) {
        super(name);
        this.controller = controller;
        addActionListener(this);
    }

    public void actionPerformed(final ActionEvent e) {
        if (controller != null) {
            new Thread(new Runnable(){public void run(){
                        controller.buttonPerformed(e.getActionCommand()); 
                    }}).start();
        }
    }
}