package com.teslagov.gdelt.csv;

/**
 * @author hanymorcos
 */
public enum GDELTCameoDownloadCodes
{
	EngageInMaterialCooperation( "06" ),
	ProvideAid( "07" ),
	Threaten( "13" ),
	Protest( "14" ),
	Coerce( "17" ),
	Assault( "18" ),
	Fight( "19" ),
	EngageInUnconventionalMassViolence( "20" );

	public String getRootCameoCode()
	{
		return rootCameoCode;
	}

	private GDELTCameoDownloadCodes( String cameoCode )
	{
		this.rootCameoCode = cameoCode;
	}

	public static boolean containsCameo( String eventRootCode )
	{
		return eventRootCode.equals( EngageInMaterialCooperation.getRootCameoCode() ) ||
			eventRootCode.equals( ProvideAid.getRootCameoCode() ) ||
			eventRootCode.equals( Threaten.getRootCameoCode() ) ||
			eventRootCode.equals( Protest.getRootCameoCode() ) ||
			eventRootCode.equals( Coerce.getRootCameoCode() ) ||
			eventRootCode.equals( Assault.getRootCameoCode() ) ||
			eventRootCode.equals( Fight.getRootCameoCode() ) ||
			eventRootCode.equals( EngageInUnconventionalMassViolence.getRootCameoCode() );
	}

	private final String rootCameoCode;
}
