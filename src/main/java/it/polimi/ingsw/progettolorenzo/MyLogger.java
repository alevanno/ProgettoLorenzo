package it.polimi.ingsw.progettolorenzo;

import java.util.Date;
import java.util.logging.*;


public final class MyLogger {
    private static final Level LEVEL = Level.FINE;  // the default log level
    private static final Logger logger = Logger.getLogger("");
    private static final ConsoleHandler ch = new ConsoleHandler();

    private MyLogger() {
        throw new IllegalStateException("Logger class");
    }

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

        // add a filter to remove sun.rmi stuff from outor logs
        ch.setFilter((LogRecord rec) -> {
            String n = rec.getSourceClassName();
            return (!n.startsWith("sun.rmi") &&
                !n.startsWith("sun.awt") && !n.startsWith("java.awt")
            && !n.startsWith("sun.lwawt"));
        });

        // the actual Logger
        logger.addHandler(ch);
        logger.setLevel(LEVEL);
    }
}


class MyConsoleFormatter extends Formatter {
    // log things in an actually readable way
    /**
     * Formatted used to print messages.  The formatter format is:
     *      %[argument_index$][flags][width][.precision]conversion
     * therefore this formatter means:
     * <p><ul>
     *     <li>
     *         {@code %1$tF} → index: first, conversion: datetime as ISO 8601 complete date formatted as "%tY-%tm-%td"
     *         <br> the date
     *     </li>
     *     <li>
     *         {@code %1$tT} → index: first, conversion: datetime as 24-hour clock as "%tH:%tM:%tS"
     *         <br> the hour
     *     </li>
     *     <li>
     *         {@code %2$-30s} → index: second, flags: left justified, width: 30, conversion: string
     *         <br> the class and method name, truncated to the 30th character
     *     </li>
     *     <li>
     *         {@code %3$7s} → index: third, width: 7, conversion: string
     *         <br> the log level
     *     </li>
     *     <li>
     *         {@code %4$s} → index: fourth, conversion: string
     *         <br> the actual log message
     *     </li>
     *     <li>
     *         {@code %n} → a newline
     *     </li>
     *
     * </ul></p>
     */
    private static final String FORMATTER = "%1$tF %1$tT %2$-30s ⇒ %3$7s: %4$s%n";

    public synchronized String format(LogRecord rec) {
        StringBuilder sb = new StringBuilder();
        Object[] args = new Object[4];
        Date date = new Date();

        // date/time
        date.setTime(rec.getMillis());
        args[0] = date;
        // class name
        String classname;
        if (rec.getSourceClassName() != null) {
            String[] classnameSplit = rec.getSourceClassName().split(
                this.getClass().getPackage().getName(), 2
            );
            if (classnameSplit.length == 2) {
                classname = classnameSplit[1];
            } else {
                classname = rec.getSourceClassName();
            }
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
        sb.append(String.format(FORMATTER, args));

        return sb.toString();
    }
}
