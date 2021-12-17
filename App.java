import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.awt.*;    

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Scanner;

class ClientSecrets {
    static String apiKey = "9adfefb504af4f3d8ad182141211212";
}

class App extends Frame {    
    App() {  
        Label l = new Label("Location:");  
        Button b = new Button("Search");  
        TextField t = new TextField();  
        Choice c = new Choice(); 

        l.setBounds(20, 30, 80, 30);  
        t.setBounds(20, 60, 160, 30);  
        b.setBounds(200, 60, 80, 30);  
        c.setBounds(180, 60, 20, 30);
        
        add(b); 
        add(l); 
        add(t);  

        c.add("°C");
        c.add("°F");

        add(c);
   
        setSize(800, 400);  
   
        setTitle("Weather Dashboard");   
           
        setLayout(null);   
        setVisible(true);  
    }
}    

class RequestBuilder {
    public void getWeatherData(String location) throws IOException, JSONException {
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

    public void prettify(StringBuffer rawWeatherData) throws JSONException {
        
        JSONObject weatherObj = new JSONObject(rawWeatherData.toString());
        JSONObject loc = weatherObj.getJSONObject("location");
        JSONObject temp = weatherObj.getJSONObject("current");
        JSONObject cond = temp.getJSONObject("condition");

        System.out.println("Location: " + loc.getString("name") + ", " + loc.getString("country"));
        System.out.println("Weather: " + temp.getDouble("temp_c") + "C / " + temp.getDouble("temp_f") + "F " + cond.getString("text"));
        System.out.println("Time: " + loc.getString("localtime"));
    }
}

class Main {
    public static void main (String args[]) {
        RequestBuilder req = new RequestBuilder();
        Scanner sc = new Scanner(System.in);

        App ui = new App();    

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