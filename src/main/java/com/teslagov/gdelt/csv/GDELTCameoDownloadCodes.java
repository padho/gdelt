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

	public static boolean containsCameo( String root )
	{
		return root.equals( EngageInMaterialCooperation.getRootCameoCode() ) ||
			root.equals( ProvideAid.getRootCameoCode() ) ||
			root.equals( Threaten.getRootCameoCode() ) ||
			root.equals( Protest.getRootCameoCode() ) ||
			root.equals( Coerce.getRootCameoCode() ) ||
			root.equals( Assault.getRootCameoCode() ) ||
			root.equals( Fight.getRootCameoCode() ) ||
			root.equals( EngageInUnconventionalMassViolence.getRootCameoCode() );
	}

	private final String rootCameoCode;
}
