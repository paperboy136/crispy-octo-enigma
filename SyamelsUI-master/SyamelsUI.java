import java.awt.Color;
import javax.swing.JOptionPane;

/**
 * @author (Syamel Sera) 
 * @version (0.1)
 **/

public class SyamelsUI implements UIMouseListener, UIButtonListener, UITextFieldListener, UISliderListener{

    public static void main(String[] arguments){
        new SyamelsUI().test();
    }

    public void test(){
        test3();
    }

    public void test3 (){
        //Trace.setVisible();
        //UI.sleep(500);
        UI.println("Welcome to SyamelsUI");
        /*
        String name = UI.askString("what is your name");
        int age = UI.askInt("How old are you, "+name);
        UI.printf("Happy %d birthday, %s\n", age, name);
        UI.println();
        UI.print("Enter remaining words (end with boo):");
        String tok = "";
        do {
        tok = UI.next();
        UI.println(">"+tok);
        }while (!tok.equals("boo"));
        UI.println("DONE");
        if (UI.askBoolean("Quit now")){
        UI.quit();
        return;
        }
         */
        UI.setColor(Color.blue);
        UI.drawLine(90, 30.5, 100, 30.5);
        UI.drawLine(100, 30.5, 100, 20.5);
        UI.drawLine(150, 52.5, 160, 52.5);
        UI.drawLine(150, 52.5, 150, 62.5);
        UI.setColor(Color.black);
        UI.drawRect(100, 30.5, 50, 22);

        UI.printMessage("there should be a rectangle.....");
        UI.setMouseListener(this);
        UI.addButton("black", this);
        UI.addButton("red", this);
        UI.addButton("white", this);
        UI.addButton("oval", this);
        UI.addTextField("name", this);
        UI.addTextField("age", this);
        UI.addSlider("size", 0, 100, this);
        UI.addSlider("repetitions", 10, 20, 15, this);
        UI.addButton("clear", this);
        UI.addButton("hello", this);
    }

    public void test2 (){
        UI.setMouseListener(this);
        UI.println("Welcome to SyamelsUI");
        UI.sleep(2000);
        JOptionPane.showMessageDialog(UI.getFrame(), "now draw?");
        UI.drawRect(100, 30.5, 50, 22);
        UI.printMessage("there should be a rectangle");
        UI.sleep(5000);
        UI.addButton("black", this);
        UI.addButton("red", this);
        UI.addButton("white", this);
        JOptionPane.showMessageDialog(UI.getFrame(), "now text?");
        Trace.setVisible(true);
        for (int i=0; i<100; i++){
            Trace.println("now doing it: "+i);
            Trace.setVisible(i<20 || i>60);
        }
    }

    private double lastx, lasty;
    public void mousePerformed(String action, double x, double y){
        if (action.equals("pressed")){
            lastx = x; lasty = y;
        }
        else if  (action.equals("released")){
            UI.drawLine(lastx, lasty, x, y);
        }
    }

    public void buttonPerformed(String button){
        if (button.equals("hello")){
            int age = UI.askInt("how old");
            String name = UI.askString("name");
            UI.println("Hello "+ name);
            UI.println("You are "+ age + " years old");
        }
        if (button.equals("black")){
            UI.setColor(Color.black);
            UI.fillRect(100, 30.5, 50, 22);
        }
        else if (button.equals("red")){
            UI.setColor(Color.red);
            UI.fillRect(160, 30.5, 50, 22);
        }
        if (button.equals("white")){
            UI.eraseRect(100, 30.5, 50, 22);
            UI.setColor(Color.black);
            UI.drawRect(220, 30.5, 50, 22);
        }
        if (button.equals("oval")){
            UI.setColor(Color.green);
            UI.drawOval(100, 30.5, 50, 22);
            UI.eraseOval(160, 30.5, 50, 22);
        }
        if (button.equals("clear")){
            UI.clearGraphics();
            UI.clearText();
        }
    }

    public void textFieldPerformed(String fieldname, String value){
        if (fieldname.equals("name")){
            UI.println("You entered "+value);
        }
        else if  (fieldname.equals("age")){
            UI.drawString(value, 100, 100);
        }
    }

    public void sliderPerformed(String name, double value){
        if (name.equals("size")){
            UI.eraseRect(200, 200, 400, 400);
            UI.setColor(Color.green);
            UI.fillRect(200, 200, value, value);
            UI.setColor(Color.black);
        }
        else if (name.equals("repetitions")){
            UI.println("-----------------");
            for (int i=0; i<value; i++){
                UI.printf("now doing it: %d .\n", i);
            }
        }
    }
}