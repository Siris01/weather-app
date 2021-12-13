import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//import org.json.*;
import java.util.Scanner;

class ClientSecrets {
    static String apiKey = "9adfefb504af4f3d8ad182141211212";
}
class RequestBuilder {
    public void getWeatherData(String location) throws IOException {
        URL url = new URL("http://api.weatherapi.com/v1/current.json?key=" + ClientSecrets.apiKey + "&q=" + location + "&aqi=no");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        //int status = con.getResponseCode();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        this.prettify(content);
    }

    public void prettify(StringBuffer rawWeatherData) {
        /*
        JSONObject weatherObj = new JSONObject(rawWeatherData.toString());
        JSONObject loc = weatherObj.getJSONObject("location");
        JSONObject temp = weatherObj.getJSONObject("current");

        System.out.println("Location: " + loc.getString("name") + ", " + loc.getString("country"));
        System.out.println("Weather: " + temp.getFloat("temp_c") + "C / " + temp.getFloat("temp_f") + "F");
        */

        System.out.println(rawWeatherData);
        
    }
}

class Main {
    public static void main (String args[]) {
        RequestBuilder req = new RequestBuilder();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a location: ");
        String loc = sc.nextLine();

        try {
            req.getWeatherData(loc);
        } catch (Exception e) {
            System.out.println(e);
        }

        sc.close();
    }
}