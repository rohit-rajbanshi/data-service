package com.gslab.solar;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


/**
 * @author Rohit Rajbanshi
 */


@SpringBootApplication
@ImportResource({"classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml"})
public class DataServiceApplication {

    //public static InputStream input;

    @Value("${spring.profiles.active:local}")
    String profile;

    public static void main(String[] args) {
        SpringApplication.run(DataServiceApplication.class, args);
//		String filename = "application.properties";
//		input = DataServiceApplication.class.getClassLoader().getResourceAsStream(filename);
    }

}
