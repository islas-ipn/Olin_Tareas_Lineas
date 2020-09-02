package mx.com.pendulum.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class CustomExceptionHandler  implements Thread.UncaughtExceptionHandler{

    private Thread.UncaughtExceptionHandler defaultUEH;


    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CustomExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {

        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();


        writeToFile(stacktrace);

        defaultUEH.uncaughtException(t, e);
    }



    private void writeToFile(String stacktrace) {
        Tools.appendLog(stacktrace);
    }
}
