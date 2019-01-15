package eu.luminis.ams.axonmetrics.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import org.axonframework.messaging.Message;
import org.axonframework.monitoring.MessageMonitor;

import java.util.Map;

public class CastingMessageMonitorDecorator<U extends Message<?>, T extends MessageMonitor<U> & MetricSet> implements MessageMonitor<Message<?>>, MetricSet {

    private final T wrapperMessageMonitor;

    public CastingMessageMonitorDecorator(T wrapperMessageMonitor) {
        this.wrapperMessageMonitor = wrapperMessageMonitor;
    }

    @Override
    public MonitorCallback onMessageIngested(Message<?> message) {
        return wrapperMessageMonitor.onMessageIngested((U) message);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return wrapperMessageMonitor.getMetrics();
    }
}
