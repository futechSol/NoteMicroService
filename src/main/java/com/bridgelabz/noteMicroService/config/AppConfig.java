package com.bridgelabz.noteMicroService.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/********************************************************************************************
 * Purpose : Contains the application configurations and bean definitions.
 *           
 * @author BridgeLabz/Sudhakar
 * @version 1.0
 * @since 26-02-2019
 *********************************************************************************************/
@Configuration
@EnableSwagger2
public class AppConfig {
	@Value("${elasticsearch.host}")
	private String host;
	@Value("${elasticsearch.port}")
	private String port;
    
	/**
	 * elastic search configuration bean
	 * @return
	 */
	@Bean(destroyMethod = "close")
	public RestHighLevelClient client() {
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
				  RestClient.builder( new HttpHost(host, Integer.parseInt(port), "http")));
		return restHighLevelClient;
	}
	
	/**
	 * ModelMapper to map the DTO to actual model
	 * @return instance of ModelMapper
	 */
	@Bean
	public ModelMapper getModelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}

	/**
	 * BCrypt instance to encode the user password
	 * @return BCrypt instance 
	 */
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * swagger bean
	 * @return
	 */
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.bridgelabz.noteMicroService")).build();
	}
	
}


