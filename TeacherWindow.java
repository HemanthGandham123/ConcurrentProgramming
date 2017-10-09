import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;


public class TeacherWindow {
    public JLabel[][] seats = new JLabel[6][6];
    BufferedReader in;
    PrintWriter out;

    private static void createInitialTeacherWindow() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Hey Teach!");
        TeacherWindow teachWin = new TeacherWindow();
        frame.setContentPane(teachWin.createContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
          teachWin.goOnline();
        } catch (Exception e) {
          System.out.println(e);
        }
    }

    public JPanel createContentPane() {

        JPanel totalGUI = new JPanel();

        // We create a JPanel with the GridLayout.
        JPanel mainPanel = new JPanel(new GridLayout(0, 6, 10, 30));

        ImageIcon imageIcon = new ImageIcon(new ImageIcon("imgs/empty.jpg").getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));

        for (int i = 0; i < 5; i++) {
          for (int j = 0; j < 6; j++) {
            JLabel imgLabel = new JLabel(imageIcon);
            seats[i][j] = imgLabel;
            imgLabel.setText("Empty Seat");
            imgLabel.setVerticalTextPosition(JLabel.BOTTOM);
            imgLabel.setHorizontalTextPosition(JLabel.CENTER);
            mainPanel.add(imgLabel);
          }
        }

        totalGUI.add(mainPanel);
        totalGUI.setOpaque(true);
        return totalGUI;
    }

    public void processRequest(String request) {
      String[] parsedInfo = request.split("\\s+");
      String rollNumber = parsedInfo[1];
      String name = parsedInfo[2];
      String seatInString = parsedInfo[3];
      try{
        Thread.sleep(10000);
      } catch(InterruptedException e) {
        System.out.println("Error while sleeping thread for " + name + " " + e);
      }
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      updateSeatGivenRollNumber(seatInString, name, rollNumber);
      System.out.println(name + " updated at " + timestamp);
    }

    public void updateSeatGivenRollNumber(String seatInString, String name, String rollNumber) {
      int seatNumber = Integer.parseInt(seatInString);
      int row = (seatNumber-1)/6;
      int col = (seatNumber-1)%6;
      seats[row][col].setText(name);
      ImageIcon imageIcon = new ImageIcon(new ImageIcon("imgs/" + rollNumber + ".jpg").getImage().
                                getScaledInstance(100, 100, Image.SCALE_DEFAULT));
      seats[row][col].setIcon(imageIcon);
    }

    private void goOnline() throws IOException {
      // Make connection and initialize streams
      String serverAddress = "127.0.0.1";
      Socket socket = new Socket(serverAddress, 9001);
      in = new BufferedReader(new InputStreamReader(
          socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);

      // Process all messages from server, according to the protocol.
      boolean isOnline = false;
      while (true) {
        String request = in.readLine();

        if (request.startsWith("LOGIN")) {
          out.println("TEACHER");
          isOnline = true;
        } else {
          processRequest(request);
        }
      }
    }

    public static void main(String[] args) {
      createInitialTeacherWindow();
    }
}
