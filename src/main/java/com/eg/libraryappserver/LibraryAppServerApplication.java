package com.eg.libraryappserver;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class LibraryAppServerApplication {

    public static void main(String[] args) throws IOException {
        //为了隐藏配置文件中秘钥等信息，拷贝覆盖
        File src = new ClassPathResource("hide/application.yml").getFile();
        File dest = new ClassPathResource("application.yml").getFile();
        if (src.exists()) {
            FileUtils.copyFile(src, dest);
        }

        SpringApplication.run(LibraryAppServerApplication.class, args);
    }

}
