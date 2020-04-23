package com.eg.libraryappserver.autoincrease;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;

/**
 * 该 pojo 类主要为每个集合记录自增的序列
 */
@Data
@Document(collection = "sequence")
public class SequenceId {
    @Id
    private String _id;          //主键
    @Field("collName")
    private String collName;    //集合名称
    @Field("seqId")
    private long seqId;         //序列值
}