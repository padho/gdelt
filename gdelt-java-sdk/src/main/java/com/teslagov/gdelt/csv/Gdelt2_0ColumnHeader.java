package com.teslagov.gdelt.csv;

/**
 * The only difference between this and {@link Gdelt1_0ColumnHeader} are the 3 ADM2 codes.
 *
 * @author Kevin Chen
 */
public enum Gdelt2_0ColumnHeader {
	GLOBALEVENTID,
	SQLDATE,
	MonthYear,
	Year,
	FractionDate,
	Actor1Code,
	Actor1Name,
	Actor1CountryCode,
	Actor1KnownGroupCode,
	Actor1EthnicCode,
	Actor1Religion1Code,
	Actor1Religion2Code,
	Actor1Type1Code,
	Actor1Type2Code,
	Actor1Type3Code,
	Actor2Code,
	Actor2Name,
	Actor2CountryCode,
	Actor2KnownGroupCode,
	Actor2EthnicCode,
	Actor2Religion1Code,
	Actor2Religion2Code,
	Actor2Type1Code,
	Actor2Type2Code,
	Actor2Type3Code,
	IsRootEvent,
	EventCode,
	EventBaseCode,
	EventRootCode,
	QuadClass,
	GoldsteinScale,
	NumMentions,
	NumSources,
	NumArticles,
	AvgTone,
	Actor1Geo_Type,
	Actor1Geo_FullName,
	Actor1Geo_CountryCode,
	Actor1Geo_ADM1Code,
	Actor1Geo_ADM2Code,
	Actor1Geo_Lat,
	Actor1Geo_Long,
	Actor1Geo_FeatureID,
	Actor2Geo_Type,
	Actor2Geo_FullName,
	Actor2Geo_CountryCode,
	Actor2Geo_ADM1Code,
	Actor2Geo_ADM2Code,
	Actor2Geo_Lat,
	Actor2Geo_Long,
	Actor2Geo_FeatureID,
	ActionGeo_Type,
	ActionGeo_FullName,
	ActionGeo_CountryCode,
	ActionGeo_ADM1Code,
	ActionGeo_ADM2Code,
	ActionGeo_Lat,
	ActionGeo_Long,
	ActionGeo_FeatureID,
	DATEADDED,
	SOURCEURL;

	private static final int size;

	private static final String[] names;

	static {
		// initialize size
		size = Gdelt2_0ColumnHeader.values().length;

		// initialize names
		names = new String[size];
		int index = 0;
		for (Gdelt2_0ColumnHeader s : Gdelt2_0ColumnHeader.values()) {
			names[index++] = s.name();
		}
	}

	public static String[] getNames() {
		return names;
	}

	public static int getSize() {
		return size;
	}
}
