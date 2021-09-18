package com.example.spring.cache.springcache.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "tb_emp") // 将实体和表进行映射,不然用@Query查询时会找不到对应关系
// @org.hibernate.annotations.Table(appliesTo = "tb_emp", comment = "员工表") // 增加表注释
@Data
// @Builder // 不用再去new创建对象了(Builder创建的构造函数只会有该实体定义属性)，该注解会导致jpa查询有问题
// @EqualsAndHashCode(exclude = { "department" }) // 不使用employees字段参与hashCode/equals的重写
// @ToString(exclude = { "department" }) // 不使用employees字段参与toString的重写
// @EntityListeners(AuditingEntityListener.class) // 有了@EntityListeners(AuditingEntityListener.class)这个注解，@CreatedBy、@CreatedDate、@LastModifiedBy 、@LastModifiedDate才生效
public class Employee implements Serializable {

    private static final long serialVersionUID = 3454573140752653513L;

    @Id
    // AUTO(主键由程序控制, 默认值)策略会额外创建一张hibernate_sequences序列表
    // IDENTITY：主键由数据库自动生成（主要是自动增长型）
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(columnDefinition = "VARCHAR(64) unique comment '名字'")
    @Column()
    private String empName;

    // @Column(columnDefinition = "VARCHAR(64) comment '职位'")
    @Column()
    private String empJob;

    // @Column(columnDefinition = "datetime comment '创建时间'")
    @Column()
    // @CreatedDate（pg不能用）
    @CreationTimestamp
    // 指定json序列化时的格式,所以从controller响应出json格式时,该字段的格式能如下指定
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    // @Column(columnDefinition = "datetime comment '更新时间'")
    @Column()
    // @LastModifiedDate（pg不能用）
    @UpdateTimestamp
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastUpdatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    // targetEntity：先保存targetEntity，设置外键后，再保存sourceEntity。
    // FetchType.LAZY: 开启懒加载 需要在配置文件配置
    // spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
    // @JoinColumn(name = "dept_id", columnDefinition = "bigint(32) comment '部门id'") // 会自动根据配置在数据表生成dept_id字段
    @JoinColumn(name = "dept_id") // 会自动根据配置在数据表生成dept_id字段
    private Department department;
}