/**
 *
 */
package com.demo.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

public class TestUtils {


    public static void cleanUpData(DataSource datasource, boolean cleanup) {
        if (cleanup) {
            deleteDefaultData(datasource);
        }
    }

    private static void deleteDefaultData(DataSource datasource) {
        Resource resource = new ClassPathResource("dml-delete-data-script.sql");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(datasource);
    }
}
