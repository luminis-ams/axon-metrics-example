package eu.luminis.ams.axonmetrics.metrics;

import com.codahale.metrics.MetricRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.Configurer;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.MessageMonitorFactory;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.metrics.CapacityMonitor;
import org.axonframework.metrics.MessageCountingMonitor;
import org.axonframework.metrics.MessageTimerMonitor;
import org.axonframework.monitoring.MultiMessageMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Bean
    public CollectorRegistry collectorRegistry(){
        return new CollectorRegistry();
    }

    @Bean
    public ConfigurerModule metricConfigurer(MetricRegistry metricRegistry, CollectorRegistry collectorRegistry) {
        // Register a collector for Axon's Dropwizard metrics in the Prometheus collector registry bean
        // By doing this we can expose Axon's Dropwizard metrics with Prometheus
        collectorRegistry.register(new DropwizardExports(metricRegistry));

        return configurer -> {
            instrumentEventProcessors(metricRegistry, configurer);
            instrumentCommandBus(metricRegistry, configurer);
        };
    }

    private void instrumentEventProcessors(MetricRegistry metricRegistry, Configurer configurer) {
        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
            CustomPayloadTypeMessageMonitorWrapper<MessageCountingMonitor> messageCounterPerType =
                    new CustomPayloadTypeMessageMonitorWrapper<>(MessageCountingMonitor::new,
                                                                 clazz -> componentName + "_" + clazz.getSimpleName(),
                                                                 metricRegistry);

            CustomPayloadTypeMessageMonitorWrapper<MessageTimerMonitor> messageTimerPerType =
                    new CustomPayloadTypeMessageMonitorWrapper<>(MessageTimerMonitor::new,
                                                                 clazz -> componentName + "_" + clazz.getSimpleName(),
                                                                 metricRegistry);

            CustomPayloadTypeMessageMonitorWrapper<CapacityMonitor> capacityMonitor =
                    new CustomPayloadTypeMessageMonitorWrapper<>(CapacityMonitor::new,
                                                                 clazz -> componentName + "_" + clazz.getSimpleName(),
                                                                 metricRegistry);

            return new MultiMessageMonitor<>(messageCounterPerType, messageTimerPerType, capacityMonitor);
        };
        configurer.configureMessageMonitor(TrackingEventProcessor.class, messageMonitorFactory);
    }

    private void instrumentCommandBus(MetricRegistry metricRegistry, Configurer configurer) {
        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
            CustomPayloadTypeMessageMonitorWrapper<MessageCountingMonitor> messageCounterPerType =
                    new CustomPayloadTypeMessageMonitorWrapper<>(
                            MessageCountingMonitor::new,
                            clazz -> "commandBus_" + clazz.getSimpleName(),
                            metricRegistry);

            CustomPayloadTypeMessageMonitorWrapper<MessageTimerMonitor> messageTimerPerType =
                    new CustomPayloadTypeMessageMonitorWrapper<>(MessageTimerMonitor::new,
                                                                 clazz -> "commandBus_" + clazz.getSimpleName(),
                                                                 metricRegistry);

            CustomPayloadTypeMessageMonitorWrapper<CapacityMonitor> capacityMonitor =
                    new CustomPayloadTypeMessageMonitorWrapper<>(CapacityMonitor::new,
                                                                 clazz -> "commandBus_" + clazz.getSimpleName(),
                                                                 metricRegistry);

            return new MultiMessageMonitor<>(messageCounterPerType, messageTimerPerType, capacityMonitor);
        };
        configurer.configureMessageMonitor(CommandBus.class, messageMonitorFactory);
    }
}
