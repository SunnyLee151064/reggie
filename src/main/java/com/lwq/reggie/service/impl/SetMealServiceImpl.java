package com.lwq.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwq.reggie.entity.Setmeal;
import com.lwq.reggie.mapper.SetMealMapper;
import com.lwq.reggie.service.SetMealService;
import org.springframework.stereotype.Service;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
}
