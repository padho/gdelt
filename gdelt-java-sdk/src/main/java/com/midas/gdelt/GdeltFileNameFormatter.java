package com.midas.gdelt;

/**
 * @author Kevin Chen
 */
public class GdeltFileNameFormatter
{

    private final GdeltConfiguration gdeltConfiguration;

    public GdeltFileNameFormatter(GdeltConfiguration gdeltConfiguration)
    {
        this.gdeltConfiguration = gdeltConfiguration;
    }

    static String padToTwoDigits(int i)
    {
        return String.format("%02d", i);
    }

    String formatGdeltTime(int year, int month, int dayOfMonth, int hour, int minute)
    {
        return String.format(
            "%d%s%s%s%s00",
            year,
            padToTwoDigits(month),
            padToTwoDigits(dayOfMonth),
            padToTwoDigits(hour),
            padToTwoDigits(minute)
        );
    }

    String formatGdeltUrl(int year, int month, int dayOfMonth, int hour, int minute)
    {
        return String.format(
            "%s/%s.export.CSV.zip",
            gdeltConfiguration.getGdeltV2URL(),
            formatGdeltTime(year, month, dayOfMonth, hour, minute)
        );
    }

    String formatGdeltCsvFilename(int year, int month, int dayOfMonth, int hour, int minute)
    {
        return String.format("%s.export.CSV", formatGdeltTime(year, month, dayOfMonth, hour, minute));
    }

    String formatGdeltZippedCsvFilename(int year, int month, int dayOfMonth, int hour, int minute)
    {
        return String.format("%s.zip", formatGdeltCsvFilename(year, month, dayOfMonth, hour, minute));
    }
}
