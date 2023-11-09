package com.ivoyant.elasticmigrator;

import com.ivoyant.elasticmigrator.controller.ElasticSearchController;
import com.ivoyant.elasticmigrator.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

@WebMvcTest(ElasticSearchController.class)
public class ElasticSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElasticSearchService elasticSearchService;
    Logger logger = LoggerFactory.getLogger(ElasticSearchControllerTest.class);

    @Test
    void shouldReturnSuccessfullyMigrated(){
        String indexName = "student";
        String expectedResponse = "Successfully Migrated";
        try {
            when(elasticSearchService.migrateElasticData(anyString())).thenReturn(expectedResponse);
            logger.info(" trying to Test  contoller class of post method of Load all ");
        }
        catch (Exception ignored){
        }
        try {
            MvcResult result = mockMvc.perform(post("/migrate/loadAll/" + indexName
                    ))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedResponse))
                    .andReturn();
            logger.info("Test Passed ");
        }
        catch (Exception ignored){
        }

    }
}
