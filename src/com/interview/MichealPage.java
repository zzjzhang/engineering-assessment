package com.interview;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MichealPage {

    private static final String FIELDS_STR = "locationid,Applicant,FacilityType,cnn,LocationDescription,Address,blocklot,block,lot,permit,Status,FoodItems,X,Y,Latitude,Longitude,Schedule,dayshours,NOISent,Approved,Received,PriorPermit,ExpirationDate,Location,Fire Prevention Districts,Police Districts,Supervisor Districts,Zip Codes,Neighborhoods (old)";
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

    public static void main(String[] args) {
        String foodDataPath = args[0];
        List<FoodTruck> foodTrucks = new ArrayList<>();
        try {
            new MichealPage().initFoodTrucks(foodDataPath, foodTrucks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BigDecimal latitude = new BigDecimal(args[1]);
        BigDecimal longitude = new BigDecimal(args[2]);
        FoodTruck foodTruck = new MichealPage().getFoodTruckInMinDistance(latitude, longitude, foodTrucks);
        System.out.println("/* The food truck in min distance listed as below: */");
        System.out.println(foodTruck.getAddress());
        System.out.println(foodTruck.getStatus());
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
        if (a.compareTo(BigDecimal.ZERO) > 0 && b.compareTo(BigDecimal.ZERO) > 0) {
            return a.subtract(b).abs();
        }
        if (a.compareTo(BigDecimal.ZERO) < 0 && b.compareTo(BigDecimal.ZERO) < 0) {
            return a.subtract(b).abs();
        }
        if ((a.compareTo(BigDecimal.ZERO) > 0 && b.compareTo(BigDecimal.ZERO) < 0) ||
                (a.compareTo(BigDecimal.ZERO) < 0 && b.compareTo(BigDecimal.ZERO) > 0)) {
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
