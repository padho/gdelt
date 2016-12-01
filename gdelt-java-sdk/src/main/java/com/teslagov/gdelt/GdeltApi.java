package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import com.teslagov.gdelt.csv.GDELTReturnResult;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kevin Chen
 */
public class GdeltApi {
	private static final Logger logger = LoggerFactory.getLogger(GdeltApi.class);

	private final HttpClient httpClient;

	private final GdeltConfiguration gdeltConfiguration;

	private final GdeltLastUpdateFetcher gdeltLastUpdateFetcher;

	private final GdeltLastUpdateDownloader gdeltLastUpdateDownloader;

	private final GdeltLastUpdateUnzipper gdeltLastUpdateUnzipper;

	private final GdeltFileNameFormatter gdeltFileNameFormatter;

	private final CsvProcessor csvProcessor;

	public GdeltApi() {
		this(HttpClientBuilder.create().build());
	}

	public GdeltApi(HttpClient httpClient) {
		this.httpClient = httpClient;
		this.gdeltConfiguration = new DefaultGdeltConfiguration();
		this.gdeltLastUpdateFetcher = new GdeltLastUpdateFetcher();
		this.gdeltLastUpdateDownloader = new GdeltLastUpdateDownloader();
		this.gdeltLastUpdateUnzipper = new GdeltLastUpdateUnzipper();
		this.gdeltFileNameFormatter = new GdeltFileNameFormatter(gdeltConfiguration);
		this.csvProcessor = new CsvProcessor();
	}

	public GdeltDownloadConfiguration download() {
		return new GdeltDownloadConfiguration(this);
	}

	public GdeltLastUpdateDownloadConfiguration downloadLastUpdate() {
		return new GdeltLastUpdateDownloadConfiguration(this);
	}

	/**
	 * @param parentDestinationDir The directory to download files to.
	 * @param unzip                Whether the downloaded file will be unzipped.
	 * @param deleteZip            If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @param year                 The year (e.g., 1979-2017) of the GDELT CSV to download.
	 * @param month                The month (e.g., 01-12) of the GDELT CSV to download.
	 * @param dayOfMonth           The day of month (e.g,. 1-30) of the GDELT CSV to download.
	 * @param hour                 The hour (e.g., 00-23) of the GDELT CSV to download.
	 * @param minute               The minute (e.g., 00-59) of the GDELT CSV to download.
	 * @return a GDELT CSV file.
	 */
	File downloadUpdate(File parentDestinationDir, boolean unzip, boolean deleteZip, int year, int month, int dayOfMonth, int hour, int minute) {
		File destinationDir = GdeltDefaultDirectoryFileFactory.getDirectory(parentDestinationDir, year, month, dayOfMonth);
		if (destinationDir.exists()) {
			File[] files = destinationDir.listFiles();
			if (files != null) {
				List<String> csvFileNames = Arrays.stream(files)
					.filter(File::isFile)
					.map(File::getName)
					.filter(n -> n.endsWith(".CSV"))
					.collect(Collectors.toList());

				// we found the csv, just return it
				String csvFileName = gdeltFileNameFormatter.formatGdeltCsvFilename(year, month, dayOfMonth, hour, minute);
				if (csvFileNames.contains(csvFileName)) {
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
				if (zippedCsvFileNames.contains(zippedCsvFileName)) {
					logger.debug("Found zipped CSV file for: {}", zippedCsvFileName);
					File zippedCsv = new File(destinationDir.getAbsolutePath() + File.separator + zippedCsvFileName);
					return unzipCsv(zippedCsv, false);
				}
			}
		} else {
			destinationDir.mkdirs();
		}

		String url = gdeltFileNameFormatter.formatGdeltUrl(year, month, dayOfMonth, hour, minute);
		return downloadGdeltFile(url, destinationDir, unzip, deleteZip);
	}

	/**
	 * Returns the GDELT last updates CSV file (zipped or unzipped).
	 *
	 * @param parentDestinationDir The directory to download files to.
	 * @param unzip          Whether the downloaded file will be unzipped.
	 * @param deleteZip      If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file
	 */
	File downloadLastUpdate(File parentDestinationDir, boolean unzip, boolean deleteZip) {
		String lastUpdateUrl = gdeltLastUpdateFetcher.getGDELTLastUpdate(httpClient, gdeltConfiguration);
		File destinationDir = GdeltDefaultDirectoryFileFactory.getDirectory(parentDestinationDir, lastUpdateUrl);
		destinationDir.mkdirs();
		return downloadGdeltFile(lastUpdateUrl, destinationDir, unzip, deleteZip);
	}

	private File downloadGdeltFile(String url, File destinationDir, boolean unzip, boolean deleteZip) {
		File zippedCsvFile = gdeltLastUpdateDownloader.downloadGDELTZipFile(httpClient, destinationDir, url);

		if (unzip) {
			return unzipCsv(zippedCsvFile, deleteZip);
		}

		return zippedCsvFile;
	}

	/**
	 * Unzips a GDELT CSV file (to the same directory as the given zip file).
	 *
	 * @param zippedCsvFile The file to unzip
	 * @param deleteZip     Whether to delete the zip file afterwards
	 * @return an unzipped CSV file.
	 */
	public File unzipCsv(File zippedCsvFile, boolean deleteZip) {
		return gdeltLastUpdateUnzipper.unzip(zippedCsvFile, deleteZip);
	}

	/**
	 * Parses a GDELT CSV file.
	 *
	 * @param file The CSV file.
	 * @return the GDELT CSV records as POJOs.
	 */
	public GDELTReturnResult parseCsv(File file) {
		return csvProcessor.processCSV(file);
	}

	// TODO make public once the test passes
	GDELTReturnResult parseCsv(InputStream inputStream) {
		return csvProcessor.processCSV(inputStream);
	}
}
