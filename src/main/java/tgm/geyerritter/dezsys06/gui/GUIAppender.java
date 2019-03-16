package tgm.geyerritter.dezsys06.gui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by Mathias on 07.11.15.
 */
public class GUIAppender extends AppenderSkeleton {

    private GUIPrinter guiPrinter;

    public GUIAppender(GUIPrinter guiPrinter) {

        this.guiPrinter = guiPrinter;
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
       this.guiPrinter.print(loggingEvent.getRenderedMessage());
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
