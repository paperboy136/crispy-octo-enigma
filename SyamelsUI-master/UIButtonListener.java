/** Interface for programs that listen to Buttons on the UI window.
*/
public interface UIButtonListener {

    /** Respond to button events.
     * The argument is the name of the button.
     */
    public void buttonPerformed(String name);
}
