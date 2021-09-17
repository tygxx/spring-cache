package com.example.spring.cache.springcache.dao;

import com.example.spring.cache.springcache.po.Department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentDao extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    // 通过方法名称命名规则查询
    Department findByDeptName(String deptName);

    // 通过注解查询
    @Query(value = "SELECT d FROM Department d WHERE d.deptName = ?1")
    Department findByDeptNameQ(String deptName);

}