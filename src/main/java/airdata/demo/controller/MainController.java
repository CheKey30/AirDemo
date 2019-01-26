package airdata.demo.controller;

import airdata.demo.model.AirCondition;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
public class MainController {
    @Autowired
    AirCondition airCondition;
    @RequestMapping("/")
    public String toIndex(){
        return "index";
    }
    @RequestMapping("/air")
    public String toAir(){
        airCondition.setData(airCondition.transfer(airCondition.readCSV()));
        if (!airCondition.insertData(airCondition.getData())){
            return "error";
        }
        return "air";
    }

    @RequestMapping("/db")

    public String toDB(ModelMap map){
        QueryResult result = airCondition.query("select * from air");
        if (result.getResults().get(0).getSeries()==null){
            return "error";
        }
        List head = result.getResults().get(0).getSeries().get(0).getColumns();
        System.out.println(head.size());
        List value = result.getResults().get(0).getSeries().get(0).getValues();
        map.addAttribute("head",head);
        map.addAttribute("value",value);
        return "db";
    }
}
