package io.confluent.connect.jira;

import io.confluent.connect.operations.Operation;
import io.confluent.connect.operations.OperationOrder;
import java.time.Duration;
import java.util.Comparator;
import java.util.Objects;

public class JiraOperationOrder {
    public static Comparator<Operation<?>> lowestDelayHighestPriorityEarliestFirst(Duration maxStartTimeVariance) {
        long maxTimeVarianceMillis = ((Duration)Objects.<Duration>requireNonNull(maxStartTimeVariance)).toMillis();
        if (maxTimeVarianceMillis < 0L)
            throw new IllegalArgumentException("The maximum start time variance may not be negative");
        if (maxTimeVarianceMillis == 0L)
            return OperationOrder.highestPriority();
        return (o1, o2) -> {
            long timeDelta = o1.startMillis() - o2.startMillis();
            if (Math.abs(timeDelta) > maxTimeVarianceMillis)
                return (int)timeDelta;
            timeDelta = OperationOrder.highestPriority().compare(o1, o2);
            return (timeDelta == 0L) ? OperationOrder.earliestOperation().compare(o1, o2) : (int)timeDelta;
        };
    }
}