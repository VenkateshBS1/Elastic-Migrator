package com.ivoyant.elasticmigrator.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElasticConnection {
    private String sourceclustername;
    private String sourceport;
    private String sourcescheme;
    private String sourceusername;
    private String sourcepassword;
    private String targetclustername;
    private String targetport;
    private String targetscheme;
    private String targetusername;
    private String targetpassword;
    private String indexname;

    @Override
    public String toString() {
        return "ElasticConnection{" +
                "sourceclustername='" + sourceclustername + '\'' +
                ", sourceport='" + sourceport + '\'' +
                ", sourcescheme='" + sourcescheme + '\'' +
                ", sourceusername='" + sourceusername + '\'' +
                ", sourcepassword='" + sourcepassword + '\'' +
                ", targetclustername='" + targetclustername + '\'' +
                ", targetport='" + targetport + '\'' +
                ", targetscheme='" + targetscheme + '\'' +
                ", targetusername='" + targetusername + '\'' +
                ", targetpassword='" + targetpassword + '\'' +
                ", indexname='" + indexname + '\'' +
                '}';
    }
}
