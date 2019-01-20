package com.midas.gdelt;

import com.midas.gdelt.csv.CsvProcessor;
import com.midas.gdelt.csv.GDELTReturnResult;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kevin Chen
 */
public class GdeltApi
{
    private static final Logger logger = LoggerFactory.getLogger(GdeltApi.class);

    private final HttpClient httpClient;

    private final GdeltConfiguration gdeltConfiguration;

    private final GdeltLastUpdateFetcher gdeltLastUpdateFetcher;

    private final GdeltLastUpdateDownloader gdeltLastUpdateDownloader;

    private final GdeltLastUpdateUnzipper gdeltLastUpdateUnzipper;

    private final GdeltFileNameFormatter gdeltFileNameFormatter;

    private final CsvProcessor csvProcessor;

    public GdeltApi()
    {
        this(HttpClientBuilder.create().build());
    }

    public GdeltApi(HttpClient httpClient)
    {
        this.httpClient = httpClient;
        this.gdeltConfiguration = new DefaultGdeltConfiguration();
        this.gdeltLastUpdateFetcher = new GdeltLastUpdateFetcher();
        this.gdeltLastUpdateDownloader = new GdeltLastUpdateDownloader();
        this.gdeltLastUpdateUnzipper = new GdeltLastUpdateUnzipper();
        this.gdeltFileNameFormatter = new GdeltFileNameFormatter(gdeltConfiguration);
        this.csvProcessor = new CsvProcessor();
    }

    public GdeltDownloadConfiguration download()
    {
        return new GdeltDownloadConfiguration(this);
    }

    public GdeltLastUpdateDownloadConfiguration downloadLastUpdate()
    {
        return new GdeltLastUpdateDownloadConfiguration(this);
    }

    public GdeltMultipleDownloadsConfiguration downloadAllSince(LocalDateTime since)
    {
        return new GdeltMultipleDownloadsConfiguration(this, since);
    }

    public GdeltMultipleDownloadsConfiguration downloadAllBetween(LocalDateTime since, LocalDateTime until)
    {
        return new GdeltMultipleDownloadsConfiguration(this, since, until);
    }

    Optional<File> tryDownloadUpdate(File parentDestinationDir, boolean unzip, boolean deleteZip, int year, int month, int dayOfMonth, int hour, int minute)
    {
        try
        {
            return Optional.of(downloadUpdate(parentDestinationDir, unzip, deleteZip, year, month, dayOfMonth, hour, minute));
        }
        catch (Exception e)
        {
            logger.warn("Error downloading file for {}/{}/{}/{}/{}", year, month, dayOfMonth, hour, minute);
            logger.error("Error download file", e);
        }
        return Optional.empty();
    }

    File downloadUpdate(File parentDestinationDir, boolean unzip, boolean deleteZip, int year, int month, int dayOfMonth, int hour, int minute)
    {
        File destinationDir = GdeltDefaultDirectoryFileFactory.getDirectory(parentDestinationDir, year, month, dayOfMonth);
        if (destinationDir.exists())
        {
            File[] files = destinationDir.listFiles();
            if (files != null)
            {
                List<String> csvFileNames = Arrays.stream(files)
                    .filter(File::isFile)
                    .map(File::getName)
                    .filter(n -> n.endsWith(".CSV"))
                    .collect(Collectors.toList());

                // we found the csv, just return it
                String csvFileName = gdeltFileNameFormatter.formatGdeltCsvFilename(year, month, dayOfMonth, hour, minute);
                if (csvFileNames.contains(csvFileName))
                {
                    logger.debug("Found CSV file for: {}", csvFileName);
                    return new File(destinationDir.getAbsolutePath() + File.separator + csvFileName);
                }

                List<String> zippedCsvFileNames = Arrays.stream(files)
                    .filter(File::isFile)
                    .map(File::getName)
                    .filter(n -> n.endsWith(".CSV.zip"))
                    .collect(Collectors.toList());

                // we found the zipped csv, just unzip it and return the unzipped file
                String zippedCsvFileName = gdeltFileNameFormatter.formatGdeltZippedCsvFilename(year, month, dayOfMonth, hour, minute);
                if (zippedCsvFileNames.contains(zippedCsvFileName))
                {
                    logger.debug("Found zipped CSV file for: {}", zippedCsvFileName);
                    File zippedCsv = new File(destinationDir.getAbsolutePath() + File.separator + zippedCsvFileName);
                    return unzipCsv(zippedCsv, false);
                }
            }
        }
        else
        {
            destinationDir.mkdirs();
        }

        String url = gdeltFileNameFormatter.formatGdeltUrl(year, month, dayOfMonth, hour, minute);
        return downloadGdeltFile(url, destinationDir, unzip, deleteZip);
    }

    File downloadLastUpdate(File parentDestinationDir, boolean unzip, boolean deleteZip)
    {
        String lastUpdateUrl = gdeltLastUpdateFetcher.getGDELTLastUpdate(httpClient, gdeltConfiguration);
        File destinationDir = GdeltDefaultDirectoryFileFactory.getDirectory(parentDestinationDir, lastUpdateUrl);
        destinationDir.mkdirs();
        return downloadGdeltFile(lastUpdateUrl, destinationDir, unzip, deleteZip);
    }

    private File downloadGdeltFile(String url, File destinationDir, boolean unzip, boolean deleteZip)
    {
        File zippedCsvFile = gdeltLastUpdateDownloader.downloadGDELTZipFile(httpClient, destinationDir, url);

        if (unzip)
        {
            return unzipCsv(zippedCsvFile, deleteZip);
        }

        return zippedCsvFile;
    }

    private File unzipCsv(File zippedCsvFile, boolean deleteZip)
    {
        return gdeltLastUpdateUnzipper.unzip(zippedCsvFile, deleteZip);
    }

    /**
     * Parses a GDELT CSV file.
     *
     * @param file The CSV file.
     * @return the GDELT CSV records as POJOs.
     */
    public GDELTReturnResult parseCsv(File file)
    {
        return csvProcessor.processCSV(file);
    }

    // TODO make public once the test passes
    GDELTReturnResult parseCsv(InputStream inputStream)
    {
        return csvProcessor.processCSV(inputStream);
    }
}
