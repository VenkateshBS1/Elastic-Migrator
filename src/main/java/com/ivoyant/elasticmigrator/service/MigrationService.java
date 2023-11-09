package com.ivoyant.elasticmigrator.service;

import com.ivoyant.elasticmigrator.config.ElasticSearchConfig;
import com.ivoyant.elasticmigrator.entity.ElasticConnection;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
@Service
public class MigrationService {
    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

    private final Executor asyncExecutor;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchService.class);

    public MigrationService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }


    public String migrateElasticData(ElasticConnection elasticConnection) throws IOException {
        RestHighLevelClient sourceClient = elasticSearchConfig.getClient(elasticConnection.getSourceclustername(),
                elasticConnection.getSourceport(),elasticConnection.getSourcescheme(),elasticConnection.getSourceusername(),elasticConnection.getSourcepassword());
        RestHighLevelClient targetClient = elasticSearchConfig.getClient(elasticConnection.getTargetclustername(),elasticConnection.getTargetport(),elasticConnection.getTargetscheme(),elasticConnection.getTargetusername(),elasticConnection.getTargetpassword());
        if (sourceClient != null && targetClient != null) {
            LOGGER.info("Successfully Instances are Created");

            SearchRequest searchRequest = new SearchRequest(elasticConnection.getIndexname());
            searchRequest.source().query(QueryBuilders.matchAllQuery());

            SearchResponse searchResponse = sourceClient.search(searchRequest, RequestOptions.DEFAULT);
            CompletableFuture.runAsync(() -> insertAsync(searchResponse, elasticConnection.getIndexname(), targetClient), asyncExecutor);

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
}
