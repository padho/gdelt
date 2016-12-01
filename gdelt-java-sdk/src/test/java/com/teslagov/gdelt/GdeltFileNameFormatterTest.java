package com.teslagov.gdelt;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kevin Chen
 */
public class GdeltFileNameFormatterTest {
	private GdeltFileNameFormatter gdeltFileNameFormatter;

	@Before
	public void init() {
		gdeltFileNameFormatter = new GdeltFileNameFormatter(new DefaultGdeltConfiguration());
	}

	@Test
	public void testFormatGdeltTime() {
		assertEquals("20160715083000", gdeltFileNameFormatter.formatGdeltTime(2016, 7, 15, 8, 30));
	}

	@Test
	public void testFormatUrl() {
		String url;

		url = gdeltFileNameFormatter.formatGdeltUrl(2016, 7, 15, 8, 30);

		assertEquals("http://data.gdeltproject.org/gdeltv2/20160715083000.export.CSV.zip", url);
	}

	@Test
	public void testFormatGdeltCsvFilename() {
		assertEquals("20160715083000.export.CSV", gdeltFileNameFormatter.formatGdeltCsvFilename(2016, 7, 15, 8, 30));
	}

	@Test
	public void testFormatGdeltZippedCsvFilename() {
		assertEquals("20160715083000.export.CSV.zip", gdeltFileNameFormatter.formatGdeltZippedCsvFilename(2016, 7, 15, 8, 30));
	}
}