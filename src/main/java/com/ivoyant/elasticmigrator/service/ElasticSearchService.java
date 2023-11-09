package com.ivoyant.elasticmigrator.service;

import com.ivoyant.elasticmigrator.config.ElasticSearchConfig;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.String;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Service
public class ElasticSearchService {

    @Value("${elastic.source.cluster}")
    private String sourceClusterName;

    @Value("${elastic.source.port}")
    private String sourcePort;

    @Value("${elastic.source.scheme}")
    private String sourceScheme;

    @Value("${elastic.source.username}")
    private String sourceUsername;

    @Value("${elastic.source.password}")
    private String sourcePassword;

    @Value("${elastic.target.clusterName}")
    private String targetClusterName;

    @Value("${elastic.target.port}")
    private String targetPort;

    @Value("${elastic.target.scheme}")
    private String targetScheme;

    @Value("${elastic.target.username}")
    private String targetUsername;

    @Value("${elastic.target.password}")
    private String targetPassword;

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    private final Executor asyncExecutor;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);

    public ElasticSearchService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }


    public String migrateElasticData(String indexName) throws IOException {
        RestHighLevelClient sourceClient = elasticSearchConfig.getClient(sourceClusterName, sourcePort, sourceScheme, sourceUsername, sourcePassword);
        RestHighLevelClient targetClient = elasticSearchConfig.getClient(targetClusterName, targetPort, targetScheme, targetUsername, targetPassword);
        if (sourceClient != null && targetClient != null) {
            LOGGER.info("Successfully Instances are Created");

            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source().query(QueryBuilders.matchAllQuery());

            SearchResponse searchResponse = sourceClient.search(searchRequest, RequestOptions.DEFAULT);
            CompletableFuture.runAsync(() -> insertAsync(searchResponse, indexName, targetClient), asyncExecutor);

        }
        return "Successfully Migrated";
    }

    public void insertAsync(SearchResponse searchResponse, String indexName, RestHighLevelClient targetClient) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                String sourceAsString = hit.getSourceAsString();
                IndexRequest indexRequest = new IndexRequest(indexName)
                        .id(hit.getId())
                        .source(sourceAsString, XContentType.JSON);
                LOGGER.info("Successfully Migrated index_Id {} and index {}", indexRequest.id(), indexName);
                bulkRequest.add(indexRequest);
            }
            if (bulkRequest.numberOfActions() > 0) {
                targetClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            LOGGER.error("Error upserting  students asynchronously: {}", e.getMessage());
        }
    }

    public void deleteIndex(String indexName) throws IOException {
        RestHighLevelClient targetClient = elasticSearchConfig.getClient(targetClusterName, targetPort, targetScheme, targetUsername, targetPassword);

        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response = targetClient.indices().delete(request, RequestOptions.DEFAULT);

        if (response.isAcknowledged()) {
            LOGGER.info("{}  index Deleted Successfully", indexName);
        } else {
            LOGGER.info("Failed to Delete index {}", indexName);
        }
    }

    public void deleteAllIndices() throws IOException {
        RestHighLevelClient targetClient = elasticSearchConfig.getClient(targetClusterName, targetPort, targetScheme, targetUsername, targetPassword);

        DeleteIndexRequest request = new DeleteIndexRequest("*");
        AcknowledgedResponse response = targetClient.indices().delete(request, RequestOptions.DEFAULT);

        if (response.isAcknowledged()) {
            LOGGER.info("All indices Deleted Successfully");
        } else {
            LOGGER.info("Failed to Delete All indexRequest");
        }
    }

}