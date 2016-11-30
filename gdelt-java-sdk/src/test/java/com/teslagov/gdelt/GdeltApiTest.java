package com.teslagov.gdelt;

import com.teslagov.gdelt.csv.GDELTReturnResult;
import com.teslagov.gdelt.models.GdeltEventResource;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Kevin Chen
 */
public class GdeltApiTest {
	private static final Logger logger = LoggerFactory.getLogger(GdeltApiTest.class);

	enum GdeltCameoDownloadCodes {
		EngageInMaterialCooperation("06"),
		ProvideAid("07"),
		Threaten("13"),
		Protest("14"),
		Coerce("17"),
		Assault("18"),
		Fight("19"),
		EngageInUnconventionalMassViolence("20");

		public String getRootCameoCode() {
			return rootCameoCode;
		}

		GdeltCameoDownloadCodes(String cameoCode) {
			this.rootCameoCode = cameoCode;
		}

		public static boolean containsCameo(String eventRootCode) {
			return eventRootCode.equals(EngageInMaterialCooperation.getRootCameoCode()) ||
				eventRootCode.equals(ProvideAid.getRootCameoCode()) ||
				eventRootCode.equals(Threaten.getRootCameoCode()) ||
				eventRootCode.equals(Protest.getRootCameoCode()) ||
				eventRootCode.equals(Coerce.getRootCameoCode()) ||
				eventRootCode.equals(Assault.getRootCameoCode()) ||
				eventRootCode.equals(Fight.getRootCameoCode()) ||
				eventRootCode.equals(EngageInUnconventionalMassViolence.getRootCameoCode());
		}

		private final String rootCameoCode;
	}

	private GdeltApi gdeltApi;

	@Before
	public void init() {
		gdeltApi = new GdeltApi();
	}

	@Test
	public void testFormatUrl() {
		String url;

		url = gdeltApi.formatGdeltUrl(2016, 7, 15, 8, 30);

		assertEquals("http://data.gdeltproject.org/gdeltv2/20160715083000.export.CSV.zip", url);
	}

	@Test
	public void testSpecificDownload() {
		File file0 = gdeltApi.downloadUpdate(new File("src/test/resources"), true, false, 2016, 8, 5, 13, 15);
		File file1 = gdeltApi.download().atTime(OffsetDateTime.of(2016, 8, 5, 13, 30, 0, 0, ZoneOffset.UTC)).execute();
		File file2 = gdeltApi.download().atTime(LocalDateTime.now().minusHours(1).withMinute(0)).execute();
		System.out.println(file2.length());
	}

	@Test
	public void testDownload() {
		// Download LastUpdate CSV
		File csvFile = gdeltApi.downloadLastUpdate();
		assertNotNull(csvFile);
		logger.info("Download a GDELT CSV file to: {}", csvFile.getAbsolutePath());
	}

	@Test
	public void testParseCsvInputStream() throws IOException {
		// TODO csv processing works with file but not with InputStream
		InputStream inputStream = GdeltApiTest.class.getClassLoader().getResourceAsStream("20161013151500.export.CSV");
		logger.info("Input Stream: {}", IOUtils.toString(inputStream, StandardCharsets.UTF_8));
		GDELTReturnResult gdeltReturnResult = gdeltApi.parseCsv(inputStream);
		assertCsv(gdeltReturnResult);
	}

	@Test
	public void testParseCsvFile() {
		File file = new File(GdeltApiTest.class.getClassLoader().getResource("20161013151500.export.CSV").getFile());
		GDELTReturnResult gdeltReturnResult = gdeltApi.parseCsv(file);
		assertCsv(gdeltReturnResult);

		List<GdeltEventResource> gdeltEvents = gdeltReturnResult.getGdeltEventList();
		GdeltEventResource first = gdeltEvents.get(0);
		assertEquals(588604779, first.getGlobalEventID());
		assertEquals("", first.getActor1Code());
		assertEquals("DEU", first.getActor2Code());
		assertEquals("HALLE", first.getActor2Name());
		assertEquals("DEU", first.getActor2CountryCode());
		assertEquals(false, first.getRootEvent());
		assertEquals("040", first.getEventCode());
		assertEquals("040", first.getEventBaseCode());
		assertEquals("04", first.getEventRootCode());
		assertEquals(1, first.getQuadClass());
	}

	private void assertCsv(GDELTReturnResult gdeltReturnResult) {
		long start, end;
		gdeltReturnResult.getGdeltEventList().forEach(e -> logger.trace(e.toString()));

		List<GdeltEventResource> gdeltEvents = gdeltReturnResult.getGdeltEventList();
		start = System.currentTimeMillis();
		long count = gdeltEvents.stream().filter(event -> GdeltCameoDownloadCodes.containsCameo(event.getEventRootCode())).count();
		end = System.currentTimeMillis();
		logger.debug("Took {} ms to filter", end - start);
		logger.debug("Loaded {} events", count);
		assertEquals(653, count);
	}
}
