package com.auction.client.Models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;

public class ChartTimeLabelFormatter extends StringConverter<Number> {
    private NumberAxis xAxis;

    // format definitions
    private final DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter longFormat = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public ChartTimeLabelFormatter(NumberAxis xAxis) {
        this.xAxis = xAxis;
    }

    @Override
    public String toString(Number object) {
        long epochSecond = object.longValue();
        // instant is not bound to timezone.
        // TODO: use instant across all time variables
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault());

        double lowerBound = xAxis.getLowerBound();
        double upperBound = xAxis.getUpperBound();
        double timeRange = upperBound - lowerBound;

        // if time in second in whole chart is higher than 1 day
        if (timeRange > 86400) {
            return time.format(longFormat); // display dd/MM hh:mm
        } else {
            return time.format(shortFormat); // display hh:mm:ss
        }
    }

    // unused, implement just to fulfill abstract requirements
    @Override
    public Number fromString(String string) {
        return 0;
    }
}
