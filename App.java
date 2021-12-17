import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

import java.awt.*;    
import java.awt.event.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ClientSecrets {
    static String apiKey = "9adfefb504af4f3d8ad182141211212";
}

class APIException extends Exception {
    int code;
    APIException (String msg, int code) {
        super(msg);
        this.code = code;
    }

    public void printMessage(Frame f) {
        if (this.code == 0) return;
        
        if (this.code == 401) Print.error("Invalid API Key", f);
        else if (this.code == 400) Print.error("Invalid Location", f);
        else if (this.code == 403) Print.error("API Key exhausted", f);
    }
}

class Utils {
    public static String getHourly (JSONArray data, String unit) {
        String result = "Hourly Data: ";

        try {
            for (int i = 0; i < 5; ++i) {
                JSONObject d = data.getJSONObject(i);
                String time = d.getString("time");
                String temp = Double.toString(unit == "°C"? d.getDouble("temp_c"): d.getDouble("temp_f"));
                result = result + time.split(" ")[1] + "-->" + temp + (unit == "°C"? "°C": "°F") + " | ";
            }
            return result;
        } catch (JSONException e) {
            return result;
        }
    }
}

class Print {
    public static void current (StringBuffer rawWeatherData, String unit, Frame frame) throws JSONException {
        JSONObject weatherObj = new JSONObject(rawWeatherData.toString());
        JSONObject loc = weatherObj.getJSONObject("location");
        JSONObject temp = weatherObj.getJSONObject("current");
        JSONObject cond = temp.getJSONObject("condition");

        Label locText = new Label(loc.getString("name") + ", " + loc.getString("country"));
        locText.setBounds(20, 30, 140, 30);
        frame.add(locText);

        Label timeText = new Label(loc.getString("localtime"));
        timeText.setBounds(20, 60, 140, 30);
        frame.add(timeText);

        Label weatherText = new Label(cond.getString("text"));
        weatherText.setBounds(20, 90, 140, 30);
        frame.add(weatherText);

        if (unit == "°C") weatherText.setText(cond.getString("text") + ", " + temp.getDouble("temp_c") + "°C");
        if (unit == "°F") weatherText.setText(cond.getString("text") + ", " + temp.getDouble("temp_f") + "°F");

        String iconURL = "https:" + cond.getString("icon");
        try {
            URL url = new URL(iconURL);
            Image image = ImageIO.read(url);
            frame.setIconImage(image);
            frame.setTitle("Weather App - " + Double.toString(unit == "°C"? temp.getDouble("temp_c"): temp.getDouble("temp_f")));
        } catch (Exception e) {
            //Do Nothing if image fails
        }
        
    }

    public static void forecast (StringBuffer rawWeatherData, String unit, Frame frame) throws JSONException {
        JSONObject weatherObj = new JSONObject(rawWeatherData.toString());
        JSONObject fore = weatherObj.getJSONObject("forecast");
        JSONArray day = fore.getJSONArray("forecastday");
        JSONArray hour = day.getJSONObject(0).getJSONArray("hour");

        Label t1 = new Label("FORECAST:");
        t1.setBounds(20, 120, 140, 30);
        frame.add(t1);

        Label t2 = new Label(Utils.getHourly(hour, unit));
        t2.setBounds(20, 150, 600, 30);
        frame.add(t2);
    }

    public static void error (Exception err, Frame f) {
        Label t1 = new Label("ERROR: " + err.getMessage());
        t1.setBounds(20, 100, 600, 60);
        f.add(t1);
    }

    public static void error (String err, Frame f) {
        Label t1 = new Label("ERROR: " + err);
        t1.setBounds(20, 100, 600, 60);
        f.add(t1);
    }
}

class App {    
    App() {  
        Frame frame = new Frame();
        Label l = new Label("Location:");  
        Button b = new Button("Search");  
        TextField t = new TextField();  
        Choice c = new Choice(); 

        l.setBounds(20, 30, 80, 30);  
        t.setBounds(20, 60, 160, 30);  
        b.setBounds(200, 60, 80, 30);  
        c.setBounds(180, 60, 20, 30);
        
        frame.add(b); 
        frame.add(l); 
        frame.add(t);  

        c.add("°C");
        c.add("°F");

        frame.add(c);
   
        frame.setSize(800, 400);  
   
        frame.setTitle("Weather App");   

        b.addActionListener(new ActionListener() {    
            public void actionPerformed(ActionEvent e)  {         
                String unit =  c.getItem(c.getSelectedIndex());    
                String loc = t.getText();
                RequestBuilder req = new RequestBuilder();

                try {
                    StringBuffer dataC = req.getWeatherData(loc, "current");
                    frame.removeAll();
                    Print.current(dataC, unit, frame);
                    StringBuffer dataF = req.getWeatherData(loc, "forecast");
                    Print.forecast(dataF, unit, frame);
                } catch (APIException err) {
                    err.printMessage(frame);
                } catch (Exception err) {
                    Print.error(err, frame);
                }
            }    
        }); 

        frame.setLayout(null);   
        frame.setVisible(true);  
    }
}    

class RequestBuilder {
    public StringBuffer getWeatherData(String location, String type) throws APIException, IOException, JSONException {
        URL url = new URL("http://api.weatherapi.com/v1/" + type + ".json?key=" + ClientSecrets.apiKey + "&q=" + location + "&aqi=no");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int status = con.getResponseCode();

        if (status >= 400) throw new APIException("Error", status);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content;
    }
}

class Main {
    public static void main (String args[]) {
        App ui = new App();   
    }
}