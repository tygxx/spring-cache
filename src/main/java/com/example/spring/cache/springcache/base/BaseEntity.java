package com.example.spring.cache.springcache.base;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@MappedSuperclass
@Data
// 解决子类的用注解创建构造时，找不到父类的属性
// @SuperBuilder 
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    // AUTO(主键由程序控制, 默认值)策略会额外创建一张hibernate_sequences序列表
    // IDENTITY：主键由数据库自动生成（主要是自动增长型）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(columnDefinition = "datetime comment '创建时间'")
    @Column()
    // @CreatedDate（pg不能用）
    @CreationTimestamp
    // 指定json序列化时的格式,所以从controller响应出json格式时,该字段的格式能如下指定
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalTime createdDate;

    // @Column(columnDefinition = "datetime comment '更新时间'")
    @Column()
    // @LastModifiedDate（pg不能用）
    @UpdateTimestamp
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalTime lastUpdatedDate;
}