package interview;

import java.net.URL;

public class MichealPageTest {

    public static void main(String[] args) {
        String[] arguments = new String[3];
        URL url = MichealPageTest.class.getResource("");
        arguments[0] = url.getPath().replaceAll("interview/", "Mobile_Food_Facility_Permit.csv");
        arguments[1] = "100";
        arguments[2] = "150";
        MichealPage michealPage = new MichealPage();
        michealPage.run(arguments);
    }

}
