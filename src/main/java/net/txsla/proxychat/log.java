package net.txsla.proxychat;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class log {
    static File logFile;
    static String logFileName;
    public static void loadLogFile() {
        logFileName = "log_" + LocalDate.now().format( DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        logFile = new File(ProxyChat.dir + File.separator +  "logs" + File.separator + logFileName );
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
