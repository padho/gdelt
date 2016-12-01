package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import com.teslagov.gdelt.csv.GDELTReturnResult;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
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

	private final CsvProcessor csvProcessor;

	public GdeltApi() {
		this(HttpClientBuilder.create().build());
	}

	public GdeltApi(HttpClient httpClient) {
		this.httpClient = httpClient;
		this.gdeltConfiguration = new GdeltConfiguration() {
			@Override
			public String getBaseURL() {
				return "http://data.gdeltproject.org/gdeltv2";
			}

			@Override
			public String getV2ServerURL() {
				return getBaseURL() + "/lastupdate.txt";
			}
		};
		this.gdeltLastUpdateFetcher = new GdeltLastUpdateFetcher();
		this.gdeltLastUpdateDownloader = new GdeltLastUpdateDownloader();
		this.gdeltLastUpdateUnzipper = new GdeltLastUpdateUnzipper();
		this.csvProcessor = new CsvProcessor();
	}

	public GdeltDownloadConfiguration download() {
		return new GdeltDownloadConfiguration(this);
	}

	/**
	 * Downloads a GDELT CSV file.
	 *
	 * @param destinationDir The directory to download files to.
	 * @param time           The time indicating which CSV file to grab. GDELT files are published every 15 minutes.
	 * @param unzip          Whether the downloaded file will be unzipped.
	 * @param deleteZip      If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file.
	 */
	// TODO remove
	public File downloadUpdate(File destinationDir, OffsetDateTime time, boolean unzip, boolean deleteZip) {
		if (time.getMinute() % 15 != 0) {
			throw new IllegalArgumentException("Given dateTime must be multiple of 15 minutes. Received: " + time);
		}

		if (time.isAfter(OffsetDateTime.now())) {
			throw new IllegalArgumentException("Given dateTime must be before now. Received: " + time);
		}

		return downloadUpdate(destinationDir, unzip, deleteZip,
			time.getYear(), time.getMonth().getValue(), time.getDayOfMonth(), time.getHour(), time.getMinute());
	}

	// TODO remove
	public File downloadUpdate(OffsetDateTime time) {
		return downloadUpdate(getDefaultDirectory(), time, true, false);
	}

	/**
	 * @param destinationDir The directory to download files to.
	 * @param unzip          Whether the downloaded file will be unzipped.
	 * @param deleteZip      If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @param year           The year (e.g., 1979-2017) of the GDELT CSV to download.
	 * @param month          The month (e.g., 01-12) of the GDELT CSV to download.
	 * @param dayOfMonth     The day of month (e.g,. 1-30) of the GDELT CSV to download.
	 * @param hour           The hour (e.g., 00-23) of the GDELT CSV to download.
	 * @param minute         The minute (e.g., 00-59) of the GDELT CSV to download.
	 * @return a GDELT CSV file.
	 */
	public File downloadUpdate(File destinationDir, boolean unzip, boolean deleteZip, int year, int month, int dayOfMonth, int hour, int minute) {
		File[] files = destinationDir.listFiles();
		if (files != null) {
			List<String> csvFileNames = Arrays.stream(files)
				.filter(File::isFile)
				.map(File::getName)
				.filter(n -> n.endsWith(".CSV"))
				.collect(Collectors.toList());

			// we found the csv, just return it
			String csvFileName = formatGdeltCsvFilename(year, month, dayOfMonth, hour, minute);
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
			String zippedCsvFileName = formatGdeltZippedCsvFilename(year, month, dayOfMonth, hour, minute);
			if (zippedCsvFileNames.contains(zippedCsvFileName)) {
				logger.debug("Found zipped CSV file for: {}", zippedCsvFileName);
				File zippedCsv = new File(destinationDir.getAbsolutePath() + File.separator + zippedCsvFileName);
				return unzipCsv(zippedCsv, false);
			}
		}

		String url = formatGdeltUrl(year, month, dayOfMonth, hour, minute);
		return downloadGdeltFile(url, destinationDir, unzip, deleteZip);
	}

	String formatGdeltTime(int year, int month, int dayOfMonth, int hour, int minute) {
		return String.format("%d%02d%02d%02d%02d00", year, month, dayOfMonth, hour, minute);
	}

	String formatGdeltUrl(int year, int month, int dayOfMonth, int hour, int minute) {
		return String.format(
			"%s/%s.export.CSV.zip",
			gdeltConfiguration.getBaseURL(),
			formatGdeltTime(year, month, dayOfMonth, hour, minute)
		);
	}

	String formatGdeltCsvFilename(int year, int month, int dayOfMonth, int hour, int minute) {
		return String.format("%s.export.CSV", formatGdeltTime(year, month, dayOfMonth, hour, minute));
	}

	String formatGdeltZippedCsvFilename(int year, int month, int dayOfMonth, int hour, int minute) {
		return String.format("%s.zip", formatGdeltCsvFilename(year, month, dayOfMonth, hour, minute));
	}

	/**
	 * Downloads a GDELT CSV file (unzipped), and does not delete the zip file.
	 *
	 * @param destinationDir The directory to download files to.
	 * @return a GDELT CSV file.
	 */
	public File downloadLastUpdate(File destinationDir) {
		return downloadLastUpdate(destinationDir, true, false);
	}

	/**
	 * Downloads a GDELT CSV file (unzipped) to {@code ~/gdelt}. This method does not delete the zip file afterward.
	 *
	 * @return a GDELT CSV file.
	 */
	public File downloadLastUpdate() {
		File destinationDir = getDefaultDirectory();
		return downloadLastUpdate(destinationDir, true, false);
	}


	File getDefaultDirectory() {
		return new File(System.getProperty("user.home") + File.separator + "gdelt");
	}

	/**
	 * Downloads a GDELT CSV file (zipped or unzipped).
	 *
	 * @param destinationDir The directory to download files to.
	 * @param unzip          If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file
	 */
	public File downloadLastUpdate(File destinationDir, boolean unzip) {
		return downloadLastUpdate(destinationDir, unzip, false);
	}

	/**
	 * Returns the GDELT last updates CSV file (zipped or unzipped).
	 *
	 * @param destinationDir The directory to download files to.
	 * @param unzip          Whether the downloaded file will be unzipped.
	 * @param deleteZip      If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file
	 */
	public File downloadLastUpdate(File destinationDir, boolean unzip, boolean deleteZip) {
		String lastUpdateUrl = gdeltLastUpdateFetcher.getGDELTLastUpdate(httpClient, gdeltConfiguration);
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
