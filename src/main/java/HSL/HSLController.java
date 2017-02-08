package HSL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller

public class HSLController {


    private String json;
    private JSONArray vehicleActivityArray;
    private List<String> results;

    @RequestMapping(value = "/line", method = RequestMethod.GET)
    public
    @ResponseBody
    String findWithLineRef(@RequestParam(value = "LineRef", required = true) String name) throws IOException, JSONException {
        getJson();
        getVehicleActivityArray();
        this.results = new ArrayList<>();

        for (int i = 0; i < this.vehicleActivityArray.length(); i++) {

            JSONObject vehicleActivity = this.vehicleActivityArray.getJSONObject(i);

            StringBuilder result = new StringBuilder();

            if (vehicleActivity
                    .getJSONObject("MonitoredVehicleJourney")
                    .getJSONObject("LineRef")
                    .getString("value")
                    .equals(name)) {
                result.append("RecordedAtTime: ");
                result.append(vehicleActivity.getLong("RecordedAtTime"));
                result.append(vehicleActivity.getJSONObject("MonitoredVehicleJourney").getJSONObject("LineRef"));
                result.append(vehicleActivity.getJSONObject("MonitoredVehicleJourney").getJSONObject("VehicleLocation"));
                results.add(result.toString());


            }

        } if (this.results.isEmpty()) {
            return "Linjaa ei löytynyt";
        }

        return results.toString();
    }

    @RequestMapping(value ="/vehicle", method = RequestMethod.GET)
    public
    @ResponseBody
    String findWithVehicleRef(@RequestParam(value = "VehicleRef", required = true) String name) throws IOException, JSONException {
        getJson();
        getVehicleActivityArray();
        this.results = new ArrayList<>();


        for (int i = 0; i < this.vehicleActivityArray.length(); i++) {

            JSONObject vehicleActivity = this.vehicleActivityArray.getJSONObject(i);
            StringBuilder result = new StringBuilder();



            if (vehicleActivity
                    .getJSONObject("MonitoredVehicleJourney")
                    .getJSONObject("VehicleRef")
                    .getString("value")
                    .equals(name)) {
                result.append("RecordedAtTime: ");
                result.append(vehicleActivity.getLong("RecordedAtTime"));
                result.append(vehicleActivity.getJSONObject("MonitoredVehicleJourney").getJSONObject("VehicleRef"));
                result.append(vehicleActivity.getJSONObject("MonitoredVehicleJourney").getJSONObject("VehicleLocation"));
                this.results.add(result.toString());

            }

        }
        if (this.results.isEmpty()) {
            return "Ajoneuvoa ei löytynyt";
        }
        return this.results.toString();
    }

    public String getJson() {
        this.json = ClientBuilder.newClient()
                .target("http://dev.hsl.fi/siriaccess/vm/json")
                .queryParam("operatorRef", "HSL")
                .request(MediaType.APPLICATION_JSON).get(String.class);
        return this.json;

    }

    public JSONArray getVehicleActivityArray() throws JSONException {
        this.vehicleActivityArray = (new JSONObject(this.json))
                .getJSONObject("Siri")
                .getJSONObject("ServiceDelivery")
                .getJSONArray("VehicleMonitoringDelivery")
                .getJSONObject(0)
                .getJSONArray("VehicleActivity");
        return this.vehicleActivityArray;
    }
}


