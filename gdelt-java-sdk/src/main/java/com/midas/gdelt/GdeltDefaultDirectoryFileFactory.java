package com.midas.gdelt;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @author Kevin Chen
 */
public class GdeltDefaultDirectoryFileFactory
{
    static File getDefaultDirectory()
    {
        return new File(System.getProperty("user.home") + File.separator + "gdelt");
    }

    static File getDirectory(File parentDestinationDir, String url)
    {
        LocalDateTime time = GdeltUrlTimeParser.parseTimeFromUrl(url);
        return getDirectory(parentDestinationDir, time.getYear(), time.getMonth().getValue(), time.getDayOfMonth());
    }

    static File getDirectory(File parentDestinationDir, int year, int month, int dayOfMonth)
    {
        if (parentDestinationDir == null)
        {
            throw new IllegalArgumentException("Cannot provide null directory File");
        }
        return new File(parentDestinationDir.getAbsolutePath() + File.separator +
            year + File.separator +
            GdeltFileNameFormatter.padToTwoDigits(month) + File.separator +
            GdeltFileNameFormatter.padToTwoDigits(dayOfMonth));
    }
}
