package com.example.spring.cache.springcache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.alibaba.fastjson.JSONObject;
import com.example.spring.cache.springcache.dao.DepartmentDao;
import com.example.spring.cache.springcache.dao.EmployeeDao;
import com.example.spring.cache.springcache.po.Department;
import com.example.spring.cache.springcache.po.Employee;
import com.example.spring.cache.springcache.service.RedisService;
import com.github.wenhao.jpa.Specifications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@SpringBootTest
class SpringCacheApplicationTests {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private RedisService redisService;

    /**
     * 添加部门
     */
    @Test
    public void testDepartmentAdd() {
        // Department department = new Department();
        // department.setDeptName("市场部");
        // departmentDao.save(department);
    }

    /**
     * 添加员工并设置外键
     */
    @Test
    public void testEmployeeAdd() {
        Employee employee = new Employee();
        employee.setEmpName("非法");
        employee.setEmpJob("地革");

        Department department = departmentDao.findByDeptName("市场部");
        if (department == null) {
            System.out.println("部门id不存在");
            return;
        }
        employee.setDepartment(department);
        employeeDao.save(employee);
    }

    /**
     * 查询部门
     */
    @Test
    public void testFindDepartment() {
        // Optional<Department> departmentOptional = departmentDao.findById(1L);
        // System.out.println("部门:" + departmentOptional.get());
        // Department department = departmentDao.findByDeptNameQ("市场部");
        Department department = redisService.findByDeptNameQ("市场部");
        System.out.println("部门:" + department.toString());
    }

    /**
     * 查询员工所属部门
     */
    @Test
    public void testFindEmployeeById() {
        Optional<Employee> employeeOptional = redisService.findEmployeeById(2L);

        System.out.println("员工:" + employeeOptional.get());
        System.out.println("所属部门:" + employeeOptional.get().getDepartment());// 懒加载
    }

    /**
     * 查询员工所属部门
     */
    @Test
    public void getListMapTest() {
        List<Map<String, Object>> list = employeeDao.getListMap();
        for (Map<String, Object> map : list) {
            System.out.println(JSONObject.toJSONString(map));
        }
    }

    /**
     * 动态查询-JpaSpecificationExecutor
     */
    @Test
    public void dyFindSpec() {
        // 查询参数
        String employeeName = "";

        // 分页参数
        Integer page = 0;
        Integer size = 10;

        Sort sort = Sort.by(Direction.DESC, "id");

        PageRequest pageable = PageRequest.of(page, size, sort);// springData2.0之前 用new PageRequest(0,1)

        // 构建Specification,通过JpaSpecificationExecutor查询时的一个入参
        Specification<Employee> spec = new Specification<Employee>() {
            /**
             * @param root  将User 对象映射为 Root 可以通过它的对象获取对象中的各种字段
             * @param query 查询条件的容器，存放查询条件;存放单个条件或者条件数组
             * @param cb    构造查询条件，大于/小于/等于/like，比较对象
             * @return predicate 定义查询条件；条件和比较的对象
             */
            @Override
            public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.hasText(employeeName)) {
                    predicates.add(cb.like(root.get("employeeName"), employeeName));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        Page<Employee> result = employeeDao.findAll(spec, pageable);
        List<Employee> resultList = result.getContent();
        for (Employee employee : resultList) {
            System.out.println("部门:" + employee.getEmpName());
        }
    }

    /**
     * 动态查询-封装相关类在specification包中,主要封装Specification<T>的生成 也可以直接添加依赖 <dependency>
     * <groupId>com.github.wenhao</groupId> <artifactId>jpa-spec</artifactId>
     * <version>3.2.5</version> </dependency>
     */
    @Test
    public void dyFindFz() {

        // 查询参数
        // 员工名称
        String employeeName = "地";
        // 员工id
        List<Long> idsList = Arrays.asList(2L, 3L);
        // 员工注册日期
        Date startDate = new Date(1617354009000L);
        Date endDate = new Date();

        // 是否抓取部门
        Boolean fetchDepartment = false;
        Long departmentId = 1L;

        // 分页参数
        Integer page = 0;
        Integer size = 2;

        Sort sort = Sort.by(Direction.DESC, "id");

        PageRequest pageable = PageRequest.of(page, size, sort);// springData2.0之前 用new PageRequest(0,1)
        Specification<Employee> spec = Specifications.<Employee>and()
                // .like(StringUtils.hasText(employeeName), "empName", employeeName + "%")
                // .in(!CollectionUtils.isEmpty(idsList), "id", idsList).ge(startDate != null, "createdDate", startDate)
                // .lt(endDate != null, "createdDate", endDate)
                .predicate(fetchDepartment, (root, query, cb) -> { // 有关联查询
                    Join department = (Join) root.fetch("department", JoinType.LEFT);
                    // if (Long.class != query.getResultType()) {
                    // department = (Join) root.fetch("department", JoinType.LEFT);
                    // } else {
                    // department = root.join("department", JoinType.LEFT);
                    // }

                    List<Predicate> predicates = new ArrayList<>();
                    // 有关联查询的条件
                    if (departmentId != null) {
                        predicates.add(cb.equal(department.get("id"), departmentId));
                    }
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }).build();

        Page<Employee> result = employeeDao.findAll(spec, pageable);
        System.out.println("总数:" + result.getTotalElements());
        // System.out.println("分页的size:" + result.getSize());
        List<Employee> resultList = result.getContent();
        for (Employee employee : resultList) {
            System.out.println("员工名称:" + employee.getEmpName());
            // 如果没有关联查询department时，序列化会报错，所以需要将其置为null
            employee.setDepartment(null);
            System.out.println("员工详情:" + JSONObject.toJSONString(employee));
            Department department = employee.getDepartment();
            String departmentDesc =  department != null ? JSONObject.toJSONString(department) : "找不到";
            System.out.println("部门:" + departmentDesc);
        }
    }

}
