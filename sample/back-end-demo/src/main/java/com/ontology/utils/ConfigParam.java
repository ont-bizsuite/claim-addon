package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${payer.addr}")
	public String PAYER_ADDRESS;

	@Value("${signing.server.url}")
	public String SIGNING_SERVER_URL;

	@Value("${callback.url}")
	public String CALLBACK_URL;

	@Value("${payer.wif}")
	public String PAYER_WIF;
}