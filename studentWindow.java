import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class studentWindow {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("student Window");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);


    public studentWindow() {

        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }


    private String getInfo() {
        return JOptionPane.showInputDialog(
            frame,
            "Give your details",
            "Format: rollno name seat_no",
            JOptionPane.PLAIN_MESSAGE);
    }

    private String getInfo2() {
        return JOptionPane.showInputDialog(
            frame,
            "Something's wrong, try again.",
            "Format: rollno name seat_no",
            JOptionPane.PLAIN_MESSAGE);
    }

    private void showSuccess() {
      JOptionPane.showMessageDialog(
        frame,
        "Thanks, take your seat, recline and have fun."
      );
      // close the studentWindow
      frame.setVisible(false);
      frame.dispose();
      System.exit(0);
    }


    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = "127.0.0.1";
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        boolean isFirstTry = true;
        while (true) {
            String line = in.readLine();
            if (line == null) return;
            if (line.startsWith("LOGIN") && isFirstTry) {
              out.println("STUDENT " + getInfo());
              isFirstTry = false;
            } else if (line.startsWith("LOGIN") && !isFirstTry) {
              out.println("STUDENT " + getInfo2());
            } else if (line.startsWith("SUCCESS")) {
              showSuccess();
            }
        }
    }
    public static void createStudentWindow() throws Exception {
      studentWindow client = new studentWindow();
      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      client.frame.setVisible(true);
      client.run();
    }
    public static void main(String[] args) throws Exception {
      createStudentWindow();
    }
}
