package com.example.demo.mapper;

import com.example.demo.entity.Employee;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends MppBaseMapper<Employee> {
}
