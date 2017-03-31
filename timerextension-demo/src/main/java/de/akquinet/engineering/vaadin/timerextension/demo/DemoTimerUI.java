package de.akquinet.engineering.vaadin.timerextension.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import de.akquinet.engineering.vaadin.timerextension.TimerExtension;

import javax.servlet.annotation.WebServlet;

/**
 * @author Axel Meier, akquinet engineering GmbH
 */
@Theme("demo_theme")
public class DemoTimerUI extends UI
{
    private static final long serialVersionUID = -1999493802710152095L;

    private int timerEventCount = 0;

    @Override
    protected void init(final VaadinRequest vaadinRequest)
    {
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        final Label extendedLabel = new Label("extended label");
        final Label label = new Label(getTimerLabelContent(timerEventCount));
        final TimerExtension timerExtension = TimerExtension.create(extendedLabel);
        timerExtension.setIntervalInMs(1000);
        timerExtension.addTimerListener((e) -> label.setValue(getTimerLabelContent(++timerEventCount)));

        final Button startButton = new Button("start", (e) -> timerExtension.start());
        final Button stopButton = new Button("stop", (e) -> timerExtension.stop());
        final TextField intervalTextField = new TextField("Interval");
        final Binder<TimerExtension> binder = new Binder<>();
        binder.forField(intervalTextField)
                .withConverter(new StringToIntegerConverter("not an integer"))
                .bind(TimerExtension::getIntervalInMs, TimerExtension::setIntervalInMs);
        binder.setBean(timerExtension);

        final Button removeExtendedLabelButton = new Button("remove extended component",
                (e) ->
                {
                    if (extendedLabel.isAttached())
                    {
                        verticalLayout.removeComponent(extendedLabel);
                    }
                });
        final Button addExtendedLabelButton = new Button("add extended component",
                (e) ->
                {
                    if (!extendedLabel.isAttached())
                    {
                        verticalLayout.addComponent(extendedLabel);
                    }
                });

        verticalLayout.addComponents(label, startButton, stopButton, intervalTextField,
                extendedLabel, addExtendedLabelButton, removeExtendedLabelButton);

        setContent(verticalLayout);
    }

    private static String getTimerLabelContent(final int timerEventCount)
    {
        return "Timer event count: " + timerEventCount;
    }

    @WebServlet(urlPatterns = "/*", name = "DemoTimerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = DemoTimerUI.class, productionMode = false)
    public static class DemoTimerUIServlet extends VaadinServlet
    {
        private static final long serialVersionUID = -1722811488671415260L;
    }
}
