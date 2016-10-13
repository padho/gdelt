package com.teslagov.gdelt.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Country codes are stored as FIPS 10-4.
 *
 * @author hanymorcos
 */
@Data
public class GDELTEventResource implements Serializable
{
	private int globalEventID;

	private String actor1Code;

	private String actor1Name;

	private String actor1CountryCode;

	private String actor1KnownGroupCode;

	private String actor1EthnicCode;

	private String actor1Religion1Code;

	private String actor1Religion2Code;

	private String actor1Type1Code;

	private String actor1Type2Code;

	private String actor1Type3Code;

	private String actor2Code;

	private String actor2Name;

	private String actor2CountryCode;

	private String actor2KnownGroupCode;

	private String actor2EthnicCode;

	private String actor2Religion1Code;

	private String actor2Religion2Code;

	private String actor2Type1Code;

	private String actor2Type2Code;

	private String actor2Type3Code;

	private Boolean isRootEvent;

	private String eventCode;

	private String eventBaseCode;

	private String eventRootCode;

	private int quadClass;

	private Double goldsteinScale;

	private int numMentions;

	private int numSources;

	private int numArticles;

	private Double avgTone;

	private int actor1GeoType;

	private String actor1GeoFullName;

	private String actor1GeoCountryCode;

	private String actor1GeoADM1Code;

	private String actor1GeoADM2Code;

	private double actor1GeoLat;

	private double actor1GeoLong;

	private String actor1GeoFeatureID;

	private int actor2GeoType;

	private String actor2GeoFullName;

	private String actor2GeoCountryCode;

	private String actor2GeoADM1Code;

	private String actor2GeoADM2Code;

	private double actor2GeoLat;

	private double actor2GeoLong;

	private String actor2GeoFeatureID;

	private int actionGeoType;

	private String actionGeoFullName;

	private String actionGeoCountryCode;

	private String actionGeoADM1Code;

	private String actionGeoADM2Code;

	private double actionGeoLat;

	private double actionGeoLong;

	private String actionGeoFeatureID;

	private String dateAdded;

	private String sourceUrl;

	private Date eventDate;
}
