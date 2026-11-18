import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Starts the Swing GUI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new VerifyApplicationFrame(); // Opens the Verifying Student Application Input Screen
        });
    }
}