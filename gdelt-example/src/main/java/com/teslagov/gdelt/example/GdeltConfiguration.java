package com.teslagov.gdelt.example;

import com.teslagov.gdelt.GdeltApi;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kevin Chen
 */
@Configuration
public class GdeltConfiguration
{
	@Bean
	public GdeltApi gdeltApi()
	{
		return new GdeltApi( HttpClientBuilder.create().build() );
	}
}
