package com.example.spring.cache.springcache.dao;

import java.util.List;
import java.util.Map;

import com.example.spring.cache.springcache.po.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/*
 *@Description: 使用的是SpringDataJpa中自带的类库
 *@ClassAuthor: tengYong
 *@Date: 2021-05-07 12:58:58
*/
public interface EmployeeDao extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    // new map中需要用as起别名作为key，不然会默认key为0,1,2
    @Query(value = "SELECT new map(e.id as id, e.empName as empName, e.createdDate as createdDate) FROM Employee e")
    List<Map<String, Object>> getListMap();
}