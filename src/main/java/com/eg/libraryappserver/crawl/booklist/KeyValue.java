package com.eg.libraryappserver.crawl.booklist;

import com.eg.libraryappserver.autoincrease.AutoIncrement;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @time 2020-04-23 20:49
 */
@Data
@Document
public class KeyValue {
    @Id
    private String id;
    @AutoIncrement
    private long index;

    private String key;
    private Object value;
}
