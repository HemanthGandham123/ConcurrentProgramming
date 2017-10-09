import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.FileReader;
import java.util.Arrays;


public class MyServer {
  private static final int PORT = 9001;
  // Cached seat arrangement on server side.
  private static int[][] seats = new int[5][6];
  private static HashMap<String, String> rollNumbersToNames = new HashMap<String, String>();
  private static PrintWriter teacherOut;

  private static void bootStrapServer() {
    try {
      FileReader fr = new FileReader("data/rollNumbersToNames.txt");
      BufferedReader br = new BufferedReader(fr);
      String s;

      while((s = br.readLine()) != null) {
        String[] parsedInfo = s.split("\\s+");
        if (parsedInfo.length != 2)
          System.out.println("Wrongly formatted data.");
        rollNumbersToNames.put(parsedInfo[0], parsedInfo[1]);
      }
    } catch(Exception e) {

    }

    for(int i=0; i<5; i++) {
      for(int j=0; j<6; j++) {
        seats[i][j] = 0;
      }
    }
  }

  public static void startServer() throws Exception {
    bootStrapServer();
    System.out.println("The Server is running");
    ServerSocket listener = new ServerSocket(PORT);
    try {
      while (true) {
        new Handler(listener.accept()).start();
      }
    } catch(Exception e) {

    } finally {
      listener.close();
    }
  }

  public static void main(String[] args) throws Exception {
    startServer();
  }

  private static class Handler extends Thread {
    private String info;
    private String rollNumber;
    private String name;
    private String seatInString;
    private int seatNumber = 0;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Handler(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      try {
        // Create character streams for the socket.
        in = new BufferedReader(new InputStreamReader(
                                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        boolean teacherOnline = false;
        while (true) {
          if (!teacherOnline)
            out.println("LOGIN");
          info = in.readLine();
          if (info == null) {
            return;
          }
          if (info.startsWith("STUDENT")) {
            try {
              // Format : rollNumber name seatNumber
              String[] parsedInfo = info.split("\\s+");
              rollNumber = parsedInfo[1];
              name = parsedInfo[2];
              seatInString = parsedInfo[3];
              seatNumber = Integer.parseInt(seatInString);
            } catch (Exception e) {
              // handle
            }
            if (rollNumbersToNames.containsKey(rollNumber)) {
              if (rollNumbersToNames.get(rollNumber).equals(name)) {

                if (seatNumber <= 30 && seatNumber > 0) {

                  int row = seatNumber/6;
                  int col = (seatNumber-1)%6;
                  if (seats[row][col] == 0) {
                    seats[row][col] = 1;
                    try {
                      teacherOut.println(info);
                    } catch (Exception e) {
                      System.out.println("Teacher not online");
                    }
                    out.println("SUCCESS");
                    break;
                  }
                }
              }
            }
          } else if (info.startsWith("TEACHER")) {
            // Acknowledge the teacher client.
            teacherOut = out;
            teacherOnline = true;
          }
        }
      } catch (IOException e) {
        System.out.println(e);
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          // handle it?
        }
      }
    }
  }
}
