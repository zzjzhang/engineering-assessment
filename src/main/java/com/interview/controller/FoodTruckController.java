package com.interview.controller;

import com.interview.entity.FoodTruck;
import com.interview.request.FoodRequest;
import com.interview.service.FoodTruckService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("food-truck")
public class FoodTruckController {

    @Resource
    private FoodTruckService foodTruckService;

    @PostMapping("min-distance")
    public FoodTruck getInMinDistance(@RequestBody FoodRequest foodRequest) {
        return foodTruckService.getInMinDistance(foodRequest);
    }

}
