import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.InputMismatchException;
//import java.util.StringBuilder;

class syamelsTextArea extends JTextArea {

    private int inputPoint = 0;
    private StringBuilder inputBuffer = new StringBuilder();
    private Scanner inputScanner;

    private static final Pattern booleanPattern =
        Pattern.compile("\\A([yY][eE][sS]|[tT][rR][uU][eE]|[yY]|[tT]|[Nn][Oo]|[fF][aA][lL][sS][eE]|[nN]|[fF])\\Z");
    private static final Pattern trueBooleanPattern =
        Pattern.compile("\\A([yY][eE][sS]|[tT][rR][uU][eE]|[yY]|[tT])\\Z");
    private static final Pattern falseBooleanPattern =
        Pattern.compile("\\A([Nn][Oo]|[fF][aA][lL][sS][eE]|[nN]|[fF])\\Z");

    public syamelsTextArea(int rows, int cols) {
        super(rows, cols);
        setDocument(new SyamelsDocument());
        setEditable(true);
    }

    private class SyamelsDocument extends PlainDocument {

        @Override
        public void remove(int offset, int len) throws BadLocationException {
            if (offset+len >= inputPoint) {
                if (offset < inputPoint){
                    len = offset+len - inputPoint;
                    offset = inputPoint;
                }
                super.remove(offset, len);    
            }
        }

        @Override
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            if (offset >= inputPoint) {
                super.insertString(offset, str, a);
                int lastReturn = str.lastIndexOf("\n");
                if (lastReturn >  -1 ){
                    int lengthOfInput = offset+lastReturn+1-inputPoint;
                    inputBuffer.append(getText(inputPoint, lengthOfInput));
                    inputPoint += lengthOfInput;
                }
            }
        }

        void insertOutput(String str) throws BadLocationException {
            super.writeLock();
            super.insertString(inputPoint<getLength()?inputPoint:getLength(), str, null);
            inputPoint += str.length();
            super.writeUnlock();
        }

    }

    /* ==== OUTPUT ===== */

    public void outputString(String str){
        try{
            ((SyamelsDocument)getDocument()).insertOutput(str);
            if (getCaretPosition()<inputPoint) setCaretPosition(inputPoint);
        }catch (BadLocationException e){}
        UI.getFrame().validate();
    }

    /*    public void outputString(String str){
    try{
    ((SyamelsDocument)getDocument()).doWriteLock();
    outputting = true;
    insert(str, inputPoint);
    if (getCaretPosition()<inputPoint) setCaretPosition(inputPoint);
    outputting = false;
    } finally { ((SyamelsDocument)getDocument()).doWriteUnlock();}
    }*/

    public void clear(){
        inputPoint=0;
        setCaretPosition(0);
        clearInputBuffer();
        setText("");
    }

    /* ==== INPUT Buffer ===== */

    void clearInputBuffer(){
        inputScanner = null;
        inputBuffer.delete(0,inputBuffer.length());
    }

    /** Ensures that InputScanner has a non-empty Scanner.
     *  First tries to get more text from the inputBuffer stringBuffer
     *  If nothing there, then will wait until there is.*/
    void ensureInputToken(){
        ensureInput(true);
    }

    void ensureInput(){
        ensureInput(false);
    }

    void ensureInput(boolean needsToken){
        requestFocusInWindow();
        boolean messagePosted = false;
        while (true){
            if (inputScanner!=null &&
            ((needsToken && inputScanner.hasNext()) ||
                (!needsToken && inputScanner.hasNextLine()))){
                break;
            }
            if (inputBuffer.length()>0){
                inputScanner = new Scanner(inputBuffer.toString());
                inputBuffer.delete(0,inputBuffer.length());
                ensureInput(needsToken);
                break;
            }
            if (UI.theUI==null) break;
            UI.printMessage("Waiting for input");
            messagePosted = true;
            try { Thread.sleep(200); } catch (InterruptedException e) { }
        }
        if (messagePosted) UI.printMessage("");
        return;
    }

    /* ==== INPUT Methods ===== */

    public String next(){
        try{
            ensureInputToken();
            return inputScanner.next();
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}
    }

    public String nextLine(){
        try{
            ensureInput();
            return inputScanner.nextLine();
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    }

    public int nextInt() {
        try{
            ensureInputToken();
            if (!inputScanner.hasNextInt())
                throw new InputMismatchException("The Text Pane says 'not an integer'");
            return inputScanner.nextInt();
        }
        catch(InputMismatchException e){throw e;}
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    }

    public double nextDouble(){
        try{
            ensureInputToken();
            if (!inputScanner.hasNextDouble())
                throw new InputMismatchException("The Text Pane says 'not a number'");
            return inputScanner.nextDouble();
        }
        catch(InputMismatchException e){throw e;}
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    } 

    public boolean nextBoolean() {
        try{
            ensureInputToken();
            if (inputScanner.hasNext(booleanPattern)){
                String token = inputScanner.next();
                return trueBooleanPattern.matcher(token).matches();
            }
            else
                throw new InputMismatchException("The Text Pane says 'not a boolean'");
        }
        catch(InputMismatchException e){throw e;}
        catch(Exception e){throw new RuntimeException("Input buffer broken");}
    }

    public boolean hasNext() {
        try{
            ensureInputToken();
            return inputScanner.hasNext();
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    }

    public boolean hasNextLine() {
        try{
            ensureInput();
            return inputScanner.hasNextLine();
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}
    }

    public boolean hasNextInt() {
        try{
            ensureInputToken();
            return inputScanner.hasNextInt();
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    }

    public boolean hasNextDouble() {
        try{
            ensureInputToken();
            return inputScanner.hasNextDouble();
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    }

    public boolean hasNextBoolean(){
        try{
            ensureInputToken();
            return inputScanner.hasNext(booleanPattern);
        }
        catch(Exception e){throw new RuntimeException("Input buffer broken");}

    } 
}