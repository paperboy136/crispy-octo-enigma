/** Interface for programs that listen to TextFields on the UI window.
*/

public interface UITextFieldListener {
    /** Respond to text field events.
     * The arguments are the name of the button and the string that the user entered.
     */
  public void textFieldPerformed(String name, String newText);
}
