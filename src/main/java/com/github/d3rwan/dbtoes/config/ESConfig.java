package com.github.d3rwan.dbtoes.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.github.d3rwan.dbtoes.common.Constants;
import com.github.d3rwan.dbtoes.exceptions.ConfigException;

/**
 * ES Config 
 * 
 * @author d3rwan
 * 
 */
@Configuration
public class ESConfig {

	@Autowired
	private Environment environment;

	private TransportClient client;

	@Bean(destroyMethod="close")
	public Client esClient() throws ConfigException {
		try {
			Settings settings = ImmutableSettings
					.settingsBuilder().put("cluster.name", environment.getProperty(Constants.CONFIG_ES_CLUSTERNAME))
					.build();
			client = new TransportClient(settings);
			String esHost = environment.getProperty(Constants.CONFIG_ES_HOST);
			String[] hosts = esHost.split(",");
			for (String host : hosts) {
				String[] hostnameAndPort = host.split(":");
				String hostname = hostnameAndPort[0];
				Integer port = Integer.parseInt(hostnameAndPort[1]);
				client = client.addTransportAddress(new InetSocketTransportAddress(hostname, port));
			}
			if (client.connectedNodes().size() == 0) {
				throw new ConfigException(
						"No ES nodes avalaible !! Check the config or the ES cluster. ");
			}
			return client;
		} catch (Exception ex) {
			throw new ConfigException(ex.getMessage(), ex.getCause());
		}
	}
}
