package com.lwq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwq.reggie.common.CustomException;
import com.lwq.reggie.entity.Category;
import com.lwq.reggie.entity.Dish;
import com.lwq.reggie.entity.Setmeal;
import com.lwq.reggie.mapper.CategoryMapper;
import com.lwq.reggie.service.CategoryService;
import com.lwq.reggie.service.DishService;
import com.lwq.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    @Override
    //看分类是否关联了菜品或者套餐，如果已经关联，抛出异常
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        //添加查询条件，根据条件进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //使用已经定义的查询条件进行查询
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            //有关联的菜品，抛出业务异常
            throw new CustomException("该分类关联了菜品信息，无法删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            throw new CustomException("该分类关联了套餐信息，无法删除");
        }
        super.removeById(id);
    }
}
