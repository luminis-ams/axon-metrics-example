package eu.luminis.ams.axonmetrics.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import org.axonframework.messaging.Message;
import org.axonframework.monitoring.MessageMonitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Adapted from {@link org.axonframework.metrics.PayloadTypeMessageMonitorWrapper}
 *
 * A {@link MessageMonitor} implementation which creates a new MessageMonitor for every {@link Message} payload type
 * ingested by it. The PayloadTypeMessageMonitorWrapper keeps track of all distinct payload types it has created
 * and only creates a new one if there is none present.
 *
 * If a new metric is created it's automatically registered with the given {@link MetricRegistry}
 *
 * The type of MessageMonitor which is created for every payload type is configurable, as long as it implements
 * MessageMonitor and {@link MetricSet}.
 *
 * @param <T> The type of the MessageMonitor created for every payload type.Must implement both {@link MessageMonitor}
 *           and {@link MetricSet}
 */
public class CustomPayloadTypeMessageMonitorWrapper<T extends MessageMonitor<Message<?>> & MetricSet>
        implements MessageMonitor<Message<?>> {

    private final Supplier<T> monitorSupplier;
    private final Function<Class<?>, String> monitorNameBuilder;
    private final Map<String, T> payloadTypeMonitors;
    private final MetricRegistry metricRegistry;

    /**
     * Create a PayloadTypeMessageMonitorWrapper which builds monitors through a given {@code monitorSupplier} for
     * every message payload type encountered and sets the monitor name as specified by the {@code
     * monitorNameBuilder}, additionally registers the metric with the given metric registry.
     *
     * @param monitorSupplier    A Supplier of MessageMonitors of type {@code T} for every encountered payload type
     * @param monitorNameBuilder A Function where the payload type is the input (of type {@code Class<?>}) and
     *                           output
     *                           is the desired name for the monitor (of type {@code String})
     * @param metricRegistry     The metric registry to register the metric to
     *
     */
    public CustomPayloadTypeMessageMonitorWrapper(Supplier<T> monitorSupplier,
                                                  Function<Class<?>, String> monitorNameBuilder,
                                                  MetricRegistry metricRegistry) {
        this.monitorSupplier = monitorSupplier;
        this.monitorNameBuilder = monitorNameBuilder;
        this.metricRegistry = metricRegistry;
        this.payloadTypeMonitors = new ConcurrentHashMap<>();

    }

    @Override
    public MonitorCallback onMessageIngested(Message<?> message) {
        String monitorName = monitorNameBuilder.apply(message.getPayloadType());

        MessageMonitor<Message<?>> messageMonitorForPayloadType =
                payloadTypeMonitors.computeIfAbsent(monitorName, payloadType -> {
                    T monitor = this.monitorSupplier.get();

                    // Register the monitor before we start using it
                    metricRegistry.register(monitorName, monitor);

                    return monitor;
                });

        return messageMonitorForPayloadType.onMessageIngested(message);
    }
}

