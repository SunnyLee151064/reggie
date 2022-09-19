package com.lwq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwq.reggie.common.R;
import com.lwq.reggie.entity.Employee;
import com.lwq.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

//    登录方法
//    将requestbody中的json数据转换为employee对象
//    request将登陆成功之后employee的id存储一份在session中
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将页面提交的密码进行MD5加密处理
        String password = employee.getPassword();

        String username = employee.getUsername();

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //如果没有查询到则返回错误信息
        if(emp == null){
            return R.error("登陆失败，用户不存在");

        }
        //查询到了结果进行密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //密码比对成功，查看员工的状态
        if(emp.getStatus() == 0 ){
            return R.error("该用户已经被禁用！");
        }
        //登录成功，将员工id放进session中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    //退出登录方法
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工的方法
    //使用调试看一下提交给的路径
    //员工的数据是在requestbody中提交的
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工");
        //设置初始的密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //创建人
//        employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    //进行分页查询的方法
    //因为前端需要返回的数据是page类型的
    //这里是简单的kv形式的参数，所以按照最简单的方式接收即可
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {}, pageSize = {},name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        //当name不等于空的时候添加
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    //根据id修改员工的信息
    //因为是接收到的json格式的数据，所以要用@requestbody
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        log.info("2   当前线程的id是{}",Thread.currentThread().getId());
        Long id = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(id);
        employeeService.updateById(employee);
        //将更新的时间以及更新的用户改掉

        return R.success("员工信息修改成功");
    }
    //根据id查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工");
        Employee employee = employeeService.getById(id);
        if(employee!=null)
            return R.success(employee);
        return R.error("没有查询到对象");
    }


}