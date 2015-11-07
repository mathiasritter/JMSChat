package tgm.geyerritter.dezsys06.gui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.List;

/**
 * Created by Mathias on 07.11.15.
 */
public class GUIAppender extends AppenderSkeleton {

    private List<GUIPrinter> windows;

    public GUIAppender(List<GUIPrinter> chatWindows) {
        this.windows = chatWindows;
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {

        for (GUIPrinter guiPrinter : windows)
            guiPrinter.print(loggingEvent.getRenderedMessage());
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
