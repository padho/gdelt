package com.teslagov.gdelt;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * @author Kevin Chen
 */
public class GdeltUrlTimeParserTest
{
    @Test
    public void parseTimeFromUrl() throws Exception
    {
        LocalDateTime time = GdeltUrlTimeParser.parseTimeFromUrl("http://data.gdeltproject.org/gdeltv2/20161201040000.export.CSV.zip");
        assertEquals(2016, time.getYear());
        assertEquals(12, time.getMonth().getValue());
        assertEquals(1, time.getDayOfMonth());
    }
}