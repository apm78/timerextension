/**
 * @author Axel Meier, akquinet engineering GmbH
 */

window.de_akquinet_engineering_vaadin_timerextension_TimerExtension = function () {
    var connector = this;

    var intervalId = null;
    var intervalInMs;
    var started = false;

    var eventCount = 0;
    
    var stopImpl = function () {
        if (null !== intervalId) {
            clearTimeout(intervalId);
            intervalId = null;
        }
    };

    var restartImpl = function () {
        stopImpl();

        if (intervalInMs > 0
            && null === intervalId
            && !!started) {
            intervalId = setTimeout(function () {
                ++eventCount;
                console.log("TimerExtension timeout! eventCount=" + eventCount);
                connector.timeout();
            }, intervalInMs);
        }
    };

    connector.triggerNextInterval = function () {
        restartImpl();
    };

    connector.onStateChange = function () {
        var state = this.getState();
        intervalInMs = state.intervalInMs;
        started = state.started;
        console.log("onStateChange: intervalMs=" + intervalInMs
            + ", started=" + started);

        if (!(intervalInMs > 0)
            || !started){
            stopImpl();
        }
    };


};
