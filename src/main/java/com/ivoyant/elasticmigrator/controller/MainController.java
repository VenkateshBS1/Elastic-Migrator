package com.ivoyant.elasticmigrator.controller;

import java.io.IOException;

import com.ivoyant.elasticmigrator.entity.ElasticConnection;;
import com.ivoyant.elasticmigrator.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {
    @Autowired
    private MigrationService migrationService;

    @GetMapping("/connection")
    public String showForm(Model model) {
        ElasticConnection elasticconnection  = new ElasticConnection();
        model.addAttribute("elasticconnection", elasticconnection);

        return "elasticMigrator";
    }
    @PostMapping("/connection")
    public String submitForm(@ModelAttribute("elasticconnection") ElasticConnection elasticconnection) {
        try {
            migrationService.migrateElasticData(elasticconnection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(elasticconnection);
        return "migration_Success";
    }

}