package com.example.chiang.busstop.Model;

import org.simpleframework.xml.Default;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by chiang on 12/24/2014.
 */
public class BusParser extends DefaultHandler{

    public ArrayList<Bus> buslist;
    public Bus bus;
    private String TAG = "BusParser";
    private String origin =
            "http://api.translink.ca/rttiapi/v1/buses?apikey=faYApdPzIJThbIF16yCP&routeNo=";
    private String data = "";
    private Boolean dataTag = false;
    private Set<Integer> blackList = new HashSet<Integer>();

    public BusParser(int busRoute){
        origin = origin + busRoute;
        get();
    }

    private void get(){
        try {
            buslist = new ArrayList<Bus>();
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.parse(new InputSource(new URL(origin).openStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        dataTag = true;
        data = "";
        if(localName.equals("Bus"))
            bus = new Bus();

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        dataTag = false;

        if(localName.equalsIgnoreCase("VehicleNo")) {
            bus.setVehicleNo(Integer.parseInt(data));
        }else if(localName.equalsIgnoreCase("RouteNo")) {
            bus.setRouteNo(Integer.parseInt(data));
        }else if(localName.equalsIgnoreCase("RecordedTime")) {
            bus.setRecordedTime(data);
        }else if(localName.equalsIgnoreCase("Latitude")) {
            bus.setLatitude(Double.parseDouble(data));
        }else if(localName.equalsIgnoreCase("Longitude")) {
            bus.setLongitude(Double.parseDouble(data));
        }else if(localName.equalsIgnoreCase("Direction")) {
            bus.setDirection(data);
        } else if (localName.equals("Bus")){
            if(!blackList.contains(bus.getVehicleNo())) {
                buslist.add(bus);
            }
        }


    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(dataTag) {
            data = new String(ch, start, length);
            dataTag = false;
        }
    }

    public void selectBus(int selection){
        for(Bus bus: buslist){
            if(bus.getVehicleNo() != selection){
                blackList.add(bus.getVehicleNo());
            }
        }

    }

    public void resetBus(){
        blackList.clear();
    }

}
