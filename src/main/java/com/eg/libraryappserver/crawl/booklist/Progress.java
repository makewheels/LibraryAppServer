package com.eg.libraryappserver.crawl.booklist;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @time 2020-04-23 10:12
 */
@Data
@Document
public class Progress {
    @Id
    private String _id;

    private String key;
    private int page;

    private Date createTime;
    private Date updateTime;
}
