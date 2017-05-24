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
    private final String formatter = "%1$tF %1$tT %2$-30s ⇒ %3$7s: %4$s%n";

    public synchronized String format(LogRecord rec) {
        StringBuilder sb = new StringBuilder();
        Object args[] = new Object[4];
        Date date = new Date();

        // date/time
        date.setTime(rec.getMillis());
        args[0] = date;
        // class name
        String classname;
        if (rec.getSourceClassName() != null) {
            // remove the package name from the class name
            classname = rec.getSourceClassName().split(
                    this.getClass().getPackage().getName(), 2)[1];
        } else {
            classname = rec.getLoggerName();
        }
        // method name
        String methodname;
        if (rec.getSourceMethodName() != null) {
            methodname = rec.getSourceMethodName();
        }
        else {
            methodname = "<none>";
        }
        args[1] = classname + "." + methodname;
        // severity level
        args[2] = rec.getLevel();
        // actual message
        args[3] = formatMessage(rec);

        // build the actual logging string
        sb.append(String.format(formatter, args));

        return sb.toString();
    }
}
