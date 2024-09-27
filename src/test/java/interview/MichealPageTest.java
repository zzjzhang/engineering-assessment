package interview;

import java.net.URL;

public class MichealPageTest {

    public static void main(String[] args) {
        String[] arguments = new String[3];
        URL url = MichealPageTest.class.getClassLoader().getResource("Mobile_Food_Facility_Permit.csv");
        arguments[0] = url.getPath();
        arguments[1] = "37.77687638877653";
        arguments[2] = "-122.40025957520209";
        new MichealPage().run(arguments);
    }

}
