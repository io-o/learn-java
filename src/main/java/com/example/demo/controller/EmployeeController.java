package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.R;
import com.example.demo.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
     @Autowired
     private EmployeeService employeeService;

     @PostMapping("/login")
     public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
          // get password
          String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

          // search username
          LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
          queryWrapper.eq(Employee::getUsername, employee.getUsername());

          Employee res = employeeService.getOne(queryWrapper);

          if (res == null) return R.error("login error");

          // password check
          if (!res.getPassword().equals(password)) return R.error("login error");

          // search status
          if (res.getStatus() == 0) return R.error("account Disable");

          log.info("id=>{}", res.getId());

          request.getSession().setAttribute("employee", res.getId());

          Long id = (Long) request.getSession().getAttribute("employee");

          log.info("ii={}", id);

          return R.success(employee);
     }
     
     @PostMapping("/logout")
     public R<String> logout(HttpServletRequest request) {
          request.getSession().removeAttribute("employee");

          return R.success("success");
     }


     @PostMapping
     public R<String> sava(HttpServletRequest request, @RequestBody Employee employee) {
          // set password
          employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));

          employee.setCreateTime(LocalDateTime.now());
          employee.setUpdateTime(LocalDateTime.now());

          // (Long) 相当于 ts as string 强制转换类型
          Long id = (Long) request.getSession().getAttribute("employee");
          log.info("save id=>{}", id);
          employee.setCreateUser(id);
          employee.setUpdateUser(id);

          employeeService.save(employee);

          return R.success("success");
     }

     @GetMapping("/page")
     public R<Page> page(int page, int pageSize, String name) {
          log.info("page {}, pageSize {}, name {}", page, pageSize, name);
          // 分页构造器
          Page pageInfo = new Page(page, pageSize);


          // 条件构造器
          LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();

          lambdaQueryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);

          // add 排序条件
          lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

           // 执行查询
          employeeService.page(pageInfo, lambdaQueryWrapper);

          return R.success(pageInfo);
     }


     @PutMapping
     public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
          log.info(employee.toString());

          Long empId = (Long) request.getSession().getAttribute("employee");

          employee.setUpdateTime(LocalDateTime.now());
          employee.setUpdateUser(empId);
          employeeService.updateById(employee);

          return R.success("success");
     }

}
