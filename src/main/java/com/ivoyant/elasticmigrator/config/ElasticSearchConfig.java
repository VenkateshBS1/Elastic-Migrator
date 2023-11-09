package com.ivoyant.elasticmigrator.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.lang.String;

@Service
@ComponentScan(basePackages = {"com.ivoyant.elasticmigrator"})
public class ElasticSearchConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfig.class);

    public RestHighLevelClient getClient(String clusterName, String port, String scheme, String username, String password) {
        RestHighLevelClient client = null;
        if ("HTTP".equalsIgnoreCase(scheme)) {
            try {
                client = new RestHighLevelClient(
                        RestClient.builder(new HttpHost(clusterName, Integer.parseInt(port), scheme))
                );
                LOGGER.info("Connected to ES Cluster");
            } catch (Exception e) {
                LOGGER.error("Unable to Connect to ES Cluster : {}", e.getMessage());
            }
        } else {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            RestClientBuilder builder = RestClient.builder(HttpHost.create(clusterName))
                    .setHttpClientConfigCallback(httpClientBuilder -> {
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        LOGGER.info("Connected to ES Cluster");
                        return httpClientBuilder;
                    });
            client = new RestHighLevelClient(builder);
        }
        return client;
    }
}
