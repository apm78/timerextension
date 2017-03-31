package de.akquinet.engineering.vaadin.timerextension;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Axel Meier, akquinet engineering GmbH
 */
@JavaScript("timer_connector.js")
public class TimerExtension extends AbstractJavaScriptExtension
{
    private static final long serialVersionUID = 3285441855446400186L;

    public interface TimerListener
    {
        void timeout(final TimerEvent timerEvent);
    }

    public static class TimerEvent
    {
        private final TimerExtension source;

        public TimerEvent(final TimerExtension source)
        {
            this.source = source;
        }

        public TimerExtension getSource()
        {
            return source;
        }
    }

    private static class TimerDetachListener implements DetachListener
    {
        private static final long serialVersionUID = -9116297509283898153L;

        private final TimerExtension timerExtension;

        TimerDetachListener(final TimerExtension timerExtension)
        {
            this.timerExtension = timerExtension;
        }

        @Override
        public void detach(final DetachEvent detachEvent)
        {
            timerExtension.stop();
        }
    }

    private static class TimeoutJavaScriptFunction implements JavaScriptFunction
    {
        private static final long serialVersionUID = -7853145359399605119L;

        private final TimerExtension timerExtension;

        TimeoutJavaScriptFunction(final TimerExtension timerExtension)
        {
            this.timerExtension = timerExtension;
        }

        @Override
        public void call(final JsonArray arguments)
        {
            if (timerExtension.isStarted())
            {
                for (final TimerListener listener : timerExtension.timerListeners)
                {
                    listener.timeout(new TimerEvent(timerExtension));
                }
                timerExtension.triggerNextInterval();
            }
        }
    }

    private final List<TimerListener> timerListeners = new ArrayList<TimerListener>();

    public TimerExtension(final AbstractClientConnector target)
    {
        super(target);
        addDetachListener(new TimerDetachListener(this));
        extend(target);

        addFunction("timeout", new TimeoutJavaScriptFunction(this));
    }

    private void triggerNextInterval()
    {
        callFunction("triggerNextInterval");
    }

    public void addTimerListener(final TimerListener timerListener)
    {
        timerListeners.add(timerListener);
    }

    public void removeTimerListener(final TimerListener timerListener)
    {
        timerListeners.remove(timerListener);
    }

    public void setIntervalInMs(final int intervalInMs)
    {
        getState().intervalInMs = intervalInMs;
    }

    public int getIntervalInMs()
    {
        return getState().intervalInMs;
    }

    public void start()
    {
        getState().started = true;

        triggerNextInterval();
    }

    public void stop()
    {
        getState().started = false;
    }

    public boolean isStarted()
    {
        return getState().started;
    }

    @Override
    protected TimerState getState()
    {
        return (TimerState) super.getState();
    }
}
