package net.txsla.proxychat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class log {
    static File logFile;
    static String path;
    static String logFileName;
    static boolean enabled;
    public static void add(String message) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true) );
        out.write(
                "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] "
                + message + "\n"
        );
        out.close();
    }
    public static void loadLogFile() throws IOException {
        path = System.getProperty("user.dir") + File.separator + ProxyChat.dir + File.separator +  "logs";

        // make sure folders exists :skull:
        new File(path).mkdirs();

        // load file (basic file stuff)
        logFileName = "log_" + LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        System.out.println( path + File.separator + logFileName);
        logFile = new File(path + File.separator + logFileName );
        try {
            if (logFile.createNewFile()) {
                System.out.println("[ProxyChat] [log] new log file created: " + logFileName);
            }else {
                System.out.println("[ProxyChat] [log] log file loaded: " + logFileName);
            }
        }
        catch (Exception e) {
            System.out.println("[ProxyChat] [log] Failed to load log file!!!");
            e.printStackTrace();
            System.out.println("[ProxyChat] [log] Failed to load log file!!!");
        }
    }
}
