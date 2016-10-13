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
public class GDELTEventDB implements Serializable
{
	private int GLOBALEVENTID;

	private String Actor1Code;

	private String Actor1Name;

	private String Actor1CountryCode;

	private String Actor1KnownGroupCode;

	private String Actor1EthnicCode;

	private String Actor1Religion1Code;

	private String Actor1Religion2Code;

	private String Actor1Type1Code;

	private String Actor1Type2Code;

	private String Actor1Type3Code;

	private String Actor2Code;

	private String Actor2Name;

	private String Actor2CountryCode;

	private String Actor2KnownGroupCode;

	private String Actor2EthnicCode;

	private String Actor2Religion1Code;

	private String Actor2Religion2Code;

	private String Actor2Type1Code;

	private String Actor2Type2Code;

	private String Actor2Type3Code;

	private Boolean IsRootEvent;

	private String EventCode;

	private String EventBaseCode;

	private String EventRootCode;

	private int QuadClass;

	private Double GoldsteinScale;

	private int NumMentions;

	private int NumSources;

	private int NumArticles;

	private Double AvgTone;

	private int Actor1Geo_Type;

	private String Actor1Geo_FullName;

	private String Actor1Geo_CountryCode;

	private String Actor1Geo_ADM1Code;

	private String Actor1Geo_ADM2Code;

	private double Actor1Geo_Lat;

	private double Actor1Geo_Long;

	private String Actor1Geo_FeatureID;

	private int Actor2Geo_Type;

	private String Actor2Geo_FullName;

	private String Actor2Geo_CountryCode;

	private String Actor2Geo_ADM1Code;

	private String Actor2Geo_ADM2Code;

	private double Actor2Geo_Lat;

	private double Actor2Geo_Long;

	private String Actor2Geo_FeatureID;

	private int ActionGeo_Type;

	private String ActionGeo_FullName;

	private String ActionGeo_CountryCode;

	private String ActionGeo_ADM1Code;

	private String ActionGeo_ADM2Code;

	private double ActionGeo_Lat;

	private double ActionGeo_Long;

	private String ActionGeo_FeatureID;

	private String DATEADDED;

	private GDELTURLDB SOURCEURL;

	private Date eventDate;

	private Boolean ignoreCompletely = false;

	private Date lastmodified;
}
