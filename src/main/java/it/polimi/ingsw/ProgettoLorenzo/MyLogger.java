package it.polimi.ingsw.ProgettoLorenzo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;


public final class MyLogger {
    private final static Level LEVEL = Level.FINEST;  // the default log level
    private final static Logger logger = Logger.getLogger("");
    private final static ConsoleHandler ch = new ConsoleHandler();

    public static void setup() {
        // don't try to use handlers inherited from who-knows-where
        logger.setUseParentHandlers(false);
        for(Handler handler : logger.getHandlers()) {
            if(handler.getClass() == ConsoleHandler.class)
                logger.removeHandler(handler);
        }

        // Console Handler
        ch.setFormatter(new MyConsoleFormatter());
        ch.setLevel(LEVEL);

        // the actual Logger
        logger.addHandler(ch);
        logger.setLevel(LEVEL);
    }
}


class MyConsoleFormatter extends Formatter {
    // log things in an actually readable way
    private final String formatter = "%1$tF %1$tT %2$s.%3$s ⇒ %4$7s: %5$s%n";

    public synchronized String format(LogRecord rec) {
        StringBuilder sb = new StringBuilder();
        Object args[] = new Object[5];
        Date date = new Date();

        // date/time
        date.setTime(rec.getMillis());
        args[0] = date;
        // class name
        if (rec.getSourceClassName() != null) {
            // remove the package name from the class name
            args[1] = rec.getSourceClassName().split(
                    this.getClass().getPackage().getName(), 2)[1];
        } else {
            args[1] = rec.getLoggerName();
        }
        // method name
        if (rec.getSourceMethodName() != null) {
            args[2] = rec.getSourceMethodName();
        }
        else {
            args[2] = "<none>";
        }
        // severity level
        args[3] = rec.getLevel().getLocalizedName();
        // actual message
        args[4] = formatMessage(rec);

        // build the actual logging string
        sb.append(String.format(formatter, args));

        // in case we're logging an exception…
        if (rec.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                rec.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }

        return sb.toString();
    }
}
