package com.teslagov.gdelt;

/**
 * @author Kevin Chen
 */
public class GdeltFileNameFormatter {

	private final GdeltConfiguration gdeltConfiguration;

	public GdeltFileNameFormatter(GdeltConfiguration gdeltConfiguration) {
		this.gdeltConfiguration = gdeltConfiguration;
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
}
