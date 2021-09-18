package com.example.spring.cache.springcache.service;

import java.util.Optional;

import com.example.spring.cache.springcache.dao.DepartmentDao;
import com.example.spring.cache.springcache.dao.EmployeeDao;
import com.example.spring.cache.springcache.po.Department;
import com.example.spring.cache.springcache.po.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "test:cache")
public class RedisService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Cacheable(key = "#id", unless = "#result == null ")
    public Optional<Employee> findEmployeeById(Long id) {
        Optional<Employee> optional = employeeDao.findById(id);
        // optional.get().setDepartment(null);
        return optional;
    }

    @Cacheable(key = "#root.methodName", unless = "#result == null ")
    public Department findByDeptNameQ(String name) {
        return departmentDao.findByDeptNameQ(name);
    }

}