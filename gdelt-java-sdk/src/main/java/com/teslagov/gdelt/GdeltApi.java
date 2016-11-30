package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.CsvProcessor;
import com.teslagov.gdelt.csv.GDELTReturnResult;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;

/**
 * @author Kevin Chen
 */
public class GdeltApi {
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

	/**
	 * Downloads a GDELT CSV file.
	 *
	 * @param destinationDir The directory to download files to.
	 * @param time           The time indicating which CSV file to grab. GDELT files are published every 15 minutes.
	 * @param unzip          Whether the downloaded file will be unzipped.
	 * @param deleteZip      If you choose to unzip the CSV file, this method will not delete the zip file.
	 * @return a GDELT CSV file.
	 */
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
		String url = formatGdeltUrl(year, month, dayOfMonth, hour, minute);
		return downloadGdeltFile(url, destinationDir, unzip, deleteZip);
	}

	String formatGdeltUrl(int year, int month, int dayOfMonth, int hour, int minute) {
		return String.format(
			"%s/%d%02d%02d%02d%02d00.export.CSV.zip",
			gdeltConfiguration.getBaseURL(),
			year, month, dayOfMonth, hour, minute
		);
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
		File destinationDir = new File(System.getProperty("user.home") + File.separator + "gdelt");
		return downloadLastUpdate(destinationDir, true, false);
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
