package com.ivoyant.elasticmigrator.controller;

import com.ivoyant.elasticmigrator.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/migrate/")
public class ElasticSearchController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchController.class);

    @PostMapping("loadAll/{indexName}")
    public String loadAll(@PathVariable String indexName) {
        try {
            return elasticSearchService.migrateElasticData(indexName);
        } catch (IOException e) {
            LOGGER.info("The following Exception Occurred {}", e.getMessage());
        }
        return null;
    }

    @DeleteMapping("deleteIndex/{indexName}")
    public void deleteIndex(@PathVariable String indexName) {
        try {
            elasticSearchService.deleteIndex(indexName);
        } catch (IOException e) {
            LOGGER.info("The following Exception Occurred {}", e.getMessage());
        }
    }

    @DeleteMapping("deleteAllIndices")
    public void deleteAllIndices() {
        try {
            elasticSearchService.deleteAllIndices();
        } catch (IOException e) {
            LOGGER.info("The following Exception Occurred {}", e.getMessage());
        }
    }

}
