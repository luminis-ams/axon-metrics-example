package eu.luminis.ams.axonmetrics.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.Configurer;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.config.MessageMonitorFactory;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.micrometer.CapacityMonitor;
import org.axonframework.micrometer.MessageCountingMonitor;
import org.axonframework.micrometer.MessageTimerMonitor;
import org.axonframework.micrometer.PayloadTypeMessageMonitorWrapper;
import org.axonframework.monitoring.MultiMessageMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@SuppressWarnings("Duplicates")
public class MetricConfig {

    @Bean
    public ConfigurerModule metricConfigurer(MeterRegistry meterRegistry){
        return configurer -> {
            instrumentEventProcessors(meterRegistry, configurer);
            instrumentCommandBus(meterRegistry, configurer);
        };
    }

    private void instrumentEventProcessors(MeterRegistry meterRegistry, Configurer configurer) {
        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
            PayloadTypeMessageMonitorWrapper<MessageCountingMonitor> messageCounterPerType =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> MessageCountingMonitor.buildMonitor(monitorName, meterRegistry),
                                                           clazz ->  componentName + "_" + clazz.getSimpleName());

            PayloadTypeMessageMonitorWrapper<MessageTimerMonitor> messageTimerPerType =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> MessageTimerMonitor.buildMonitor(monitorName, meterRegistry),
                                                           clazz ->  componentName + "_" + clazz.getSimpleName());

            PayloadTypeMessageMonitorWrapper<CapacityMonitor> capacityMonitor1Minute =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> CapacityMonitor.buildMonitor(monitorName, meterRegistry, 1, TimeUnit.MINUTES),
                                                           clazz ->  componentName + "_" + clazz.getSimpleName() + "_1m");

            PayloadTypeMessageMonitorWrapper<CapacityMonitor> capacityMonitor10Minutes =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> CapacityMonitor.buildMonitor(monitorName, meterRegistry, 10, TimeUnit.MINUTES),
                                                           clazz ->  componentName + "_" + clazz.getSimpleName() + "_10m");


            return new MultiMessageMonitor<>(messageCounterPerType, messageTimerPerType, capacityMonitor1Minute,
                                             capacityMonitor10Minutes);
        };
        configurer.configureMessageMonitor(TrackingEventProcessor.class, messageMonitorFactory);
    }

    private void instrumentCommandBus(MeterRegistry meterRegistry, Configurer configurer) {
        MessageMonitorFactory messageMonitorFactory = (configuration, componentType, componentName) -> {
            PayloadTypeMessageMonitorWrapper<MessageCountingMonitor> messageCounterPerType =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> MessageCountingMonitor.buildMonitor(monitorName, meterRegistry),
                                                           clazz ->  componentName + "_" + clazz.getSimpleName());

            PayloadTypeMessageMonitorWrapper<MessageTimerMonitor> messageTimerPerType =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> MessageTimerMonitor.buildMonitor(monitorName, meterRegistry),
                                                           clazz -> componentName + "_" + clazz.getSimpleName());

            PayloadTypeMessageMonitorWrapper<CapacityMonitor> capacityMonitor =
                    new PayloadTypeMessageMonitorWrapper<>(monitorName -> CapacityMonitor.buildMonitor(monitorName, meterRegistry),
                                                           clazz -> componentName + "_" + clazz.getSimpleName());

            return new MultiMessageMonitor<>(messageCounterPerType, messageTimerPerType, capacityMonitor);
        };
        configurer.configureMessageMonitor(CommandBus.class, messageMonitorFactory);
    }

}
