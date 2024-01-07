
// Change to JTextField.
// Needs to then change the TextListener to an ActionListener

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

class syamelsTextField extends JTextField {
    private String name;
    private UITextFieldListener controller;
    private boolean newEntry = false;
    private boolean redrawing = false;

    public syamelsTextField(String nm, int cols, UITextFieldListener contrl) {
        super(cols);
        //setBackground(Color.white);
        this.name = nm;
        this.controller = contrl;
        getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e){}

                public void insertUpdate(DocumentEvent e){newEntry = true;}

                public void removeUpdate(DocumentEvent e){newEntry = true;}
            });
        addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newEntry = false;
                    if (controller != null) {
                        //Trace.printf("JTextField %s: enter event\n", name);
                        new Thread(new Runnable(){public void run(){
                                    controller.textFieldPerformed(name, getText());
                                }}).start();
                    }
                }});
        addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    if (!e.isTemporary() && newEntry && controller != null) {
                        newEntry = false;
                        //Trace.printf("JTextField %s: focus lost event\n", name);
                        new Thread(new Runnable(){public void run(){
                                    controller.textFieldPerformed(name, getText());
                                }}).start();
                    }
                }});

    }

}