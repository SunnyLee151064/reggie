package com.lwq.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwq.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

//使用mybatis-plus
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
