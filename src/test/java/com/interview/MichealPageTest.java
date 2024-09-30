package com.interview;

import com.interview.request.FoodRequest;
import com.interview.service.FoodTruckService;


public class MichealPageTest {

    public static void main(String[] args) {
        FoodRequest foodRequest = new FoodRequest();
        foodRequest.setLatitude("37.77687638877653");
        foodRequest.setLongitude("-122.40025957520209");
        new FoodTruckService().getInMinDistance(foodRequest);
    }

}
