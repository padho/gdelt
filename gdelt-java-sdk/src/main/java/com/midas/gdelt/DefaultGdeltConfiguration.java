package com.midas.gdelt;

/**
 * @author Kevin Chen
 */
public class DefaultGdeltConfiguration implements GdeltConfiguration
{
    @Override
    public String getGdeltBaseURL()
    {
        return "http://data.gdeltproject.org";
    }

    @Override
    public String getGdeltV2URL()
    {
        return getGdeltBaseURL() + "/gdeltv2";
    }

    @Override
    public String getGdeltLastUpdateURL()
    {
        return getGdeltV2URL() + "/lastupdate.txt";
    }
}
