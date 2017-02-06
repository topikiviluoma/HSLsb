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

@Controller

public class HSLController {

    private String vehicleRef;
    private String lineRef;
    private String json;
    private JSONArray vehicleActivityArray;


    @RequestMapping(value = "/line", method = RequestMethod.GET)
    public
    @ResponseBody
    String findWithLineRef(@RequestParam(value = "LineRef", required = true) String name) throws IOException, JSONException {
        getJson();
        getVehicleActivityArray();

        for (int i = 0; i < this.vehicleActivityArray.length(); i++) {

            JSONObject vehicleActivity = this.vehicleActivityArray.getJSONObject(i);

            if (vehicleActivity
                    .getJSONObject("MonitoredVehicleJourney")
                    .getJSONObject("LineRef")
                    .getString("value")
                    .equals(name)) {
                this.lineRef = vehicleActivity.toString();
                break;
            } else {
                this.lineRef = "Linjaa ei löydy";
            }

        }
        return this.lineRef;
    }

    @RequestMapping(value ="/vehicle", method = RequestMethod.GET)
    public
    @ResponseBody
    String findWithVehicleRef(@RequestParam(value = "VehicleRef", required = true) String name) throws IOException, JSONException {
        getJson();
        getVehicleActivityArray();

        for (int i = 0; i < this.vehicleActivityArray.length(); i++) {

            JSONObject vehicleActivity = this.vehicleActivityArray.getJSONObject(i);

            if (vehicleActivity
                    .getJSONObject("MonitoredVehicleJourney")
                    .getJSONObject("VehicleRef")
                    .getString("value")
                    .equals(name)) {
                this.vehicleRef = vehicleActivity.toString();
                break;

            } else {
                this.vehicleRef = "Ajoneuvoa ei löydy";
            }

        }
        return this.vehicleRef;
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


