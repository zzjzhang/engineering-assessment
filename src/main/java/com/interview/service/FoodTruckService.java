package com.interview.service;

import com.interview.entity.FoodTruck;
import com.interview.request.FoodRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class FoodTruckService {

    private static final URL FOOD_DATA_PATH = FoodTruckService.class.getClassLoader().getResource("Mobile_Food_Facility_Permit.csv");
    private static final String FIELDS_STR = "locationid,Applicant,FacilityType,cnn,LocationDescription,Address,blocklot,block,lot,permit,Status,FoodItems,X,Y,Latitude,Longitude,Schedule,dayshours,NOISent,Approved,Received,PriorPermit,ExpirationDate,Location,Fire Prevention Districts,Police Districts,Supervisor Districts,Zip Codes,Neighborhoods (old)";
    private static final List<FoodTruck> FOOD_TRUCKS = new ArrayList<>();
    private static final Map<Integer, String> FIELDS_MAP = new HashMap<>();

    static {
        String[] fields = FIELDS_STR.split(",");
        Integer index = 0;
        for (String field : fields) {
            FIELDS_MAP.put(index, field
                    .replaceAll(" ", "_")
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "")
            );
            index++;
        }
    }

    @PostConstruct
    public void init() {
        try {
            initFoodTrucks(FOOD_DATA_PATH.getPath(), FOOD_TRUCKS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FoodTruck getInMinDistance(FoodRequest foodRequest) {
        BigDecimal latitude = new BigDecimal(foodRequest.getLatitude());
        BigDecimal longitude = new BigDecimal(foodRequest.getLongitude());
        FoodTruck foodTruck = getFoodTruckInMinDistance(latitude, longitude, FOOD_TRUCKS);
        System.out.println("/* The food truck with min distance listed as below: */");
        System.out.println("locationid: " + foodTruck.getLocationid());
        System.out.println("LocationDescription: " + foodTruck.getLocationDescription());
        System.out.println("Status: " + foodTruck.getStatus());
        return foodTruck;
    }


    private FoodTruck getFoodTruckInMinDistance(BigDecimal latitude, BigDecimal longitude, List<FoodTruck> foodTrucks) {
        double minDistance = -1;
        FoodTruck ft = null;
        for (FoodTruck foodTruck : foodTrucks) {
            BigDecimal la = new BigDecimal(foodTruck.getLatitude());
            BigDecimal lo = new BigDecimal(foodTruck.getLongitude());
            BigDecimal laDistance = getDistance(latitude, la);
            BigDecimal loDistance = getDistance(longitude, lo);
            BigDecimal disMulti = laDistance.multiply(laDistance).add(loDistance.multiply(loDistance));
            double distance = Math.sqrt(disMulti.doubleValue());
            if (minDistance == -1) {
                minDistance = distance;
                ft = foodTruck;
            }
            if (distance < minDistance) {
                minDistance = distance;
                ft = foodTruck;
            }
        }
        return ft;
    }

    private BigDecimal getDistance(BigDecimal a, BigDecimal b) {
        if (a.compareTo(BigDecimal.ZERO) >= 0 && b.compareTo(BigDecimal.ZERO) >= 0) {
            return a.subtract(b).abs();
        }
        if (a.compareTo(BigDecimal.ZERO) <= 0 && b.compareTo(BigDecimal.ZERO) <= 0) {
            return a.subtract(b).abs();
        }
        if ((a.compareTo(BigDecimal.ZERO) >= 0 && b.compareTo(BigDecimal.ZERO) <= 0) ||
                (a.compareTo(BigDecimal.ZERO) <= 0 && b.compareTo(BigDecimal.ZERO) >= 0)) {
            return a.abs().add(b.abs());
        }
        return BigDecimal.ZERO;
    }


    private void initFoodTrucks(String foodDataPath, List<FoodTruck> foodTrucks) throws IOException, NoSuchFieldException, IllegalAccessException {
        Path dataPath = Paths.get(foodDataPath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataPath.toFile()));
        String line = "";
        int index = 0;
        while ((line = bufferedReader.readLine()) != null) {
            if (index == 0) {
                index++;
                continue;
            }
            Map<Integer, String> valueMap = getFoodTrucksData(line);
            generateFoodTrucks(valueMap, foodTrucks);
        }
        bufferedReader.close();
    }


    private Map getFoodTrucksData(String line) {
        String[] values = line.split(",");
        Integer valueIndex = 0;
        Map<Integer, String> valueMap = new HashMap<>();
        String temp = "";
        boolean closeFlag = Boolean.FALSE;
        for (String value : values) {
            if (value.contains("\"") && !closeFlag) {
                temp = value;
                closeFlag = true;
                continue;
            }
            if (value.contains("\"") && closeFlag) {
                value = temp + value;
                closeFlag = false;
            }
            valueMap.put(valueIndex, value);
            valueIndex++;
        }
        return valueMap;
    }

    private void generateFoodTrucks(Map<Integer, String> valueMap, List<FoodTruck> foodTrucks) throws NoSuchFieldException, IllegalAccessException {
        FoodTruck foodTruck = new FoodTruck();
        int size = valueMap.size();
        for (int i = 0; i < size; i++) {
            String fieldName = FIELDS_MAP.get(i);
            String fieldValue = valueMap.get(i);
            Field field = FoodTruck.class.getDeclaredField(fieldName);
            field.setAccessible(Boolean.TRUE);
            field.set(foodTruck, fieldValue);
        }
        foodTrucks.add(foodTruck);
    }

}
