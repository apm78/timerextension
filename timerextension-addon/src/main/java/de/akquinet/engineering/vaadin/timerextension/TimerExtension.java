package de.akquinet.engineering.vaadin.timerextension;

import com.vaadin.annotations.JavaScript;
import com.vaadin.event.ConnectorEvent;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Axel Meier, akquinet engineering GmbH
 */
@JavaScript("timer_connector.js")
public class TimerExtension extends AbstractJavaScriptExtension
{
    private static final long serialVersionUID = 3285441855446400186L;

    public interface TimerListener extends Serializable
    {
        void timeout(final TimerEvent timerEvent);
    }

    public static class TimerEvent extends ConnectorEvent
    {
        public TimerEvent(final TimerExtension source)
        {
            super(source);
        }

        public TimerExtension getTimerExtension()
        {
            return (TimerExtension) getSource();
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
        public void detach(final DetachEvent event)
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

    protected TimerExtension()
    {
    }

    protected void init(final AbstractClientConnector target)
    {
        addDetachListener(new TimerDetachListener(this));
        extend(target);

        addFunction("timeout", new TimeoutJavaScriptFunction(this));
    }

    public static TimerExtension create(final AbstractClientConnector target)
    {
        final TimerExtension timerExtension = new TimerExtension();
        timerExtension.init(target);
        return timerExtension;
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
