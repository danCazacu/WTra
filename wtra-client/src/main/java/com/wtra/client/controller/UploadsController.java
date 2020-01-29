package com.wtra.client.controller;

import com.google.gson.*;
import com.wtra.client.entity.Location;
import com.wtra.client.entity.Sign;
import com.wtra.client.entity.SignFromVideo;
import com.wtra.client.entity.Video;
import com.wtra.client.service.DatabaseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Array;
import java.util.*;

import static com.wtra.client.controller.LoginController.TOKEN;
import static com.wtra.client.entity.Sign.description;
import static com.wtra.client.entity.Sign.type;
import static com.wtra.client.service.AuthenticationService.checkToken;
import static com.wtra.client.service.AuthenticationService.invalidateSession;

@Controller
public class UploadsController {

    private static final String owlIRI = "https://github.com/danCazacu/WTra#";
    private static String commentURL = "http://www.w3.org/2000/01/rdf-schema#comment";
    private static String typeURL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    @RequestMapping("/uploads")
    public String uploads(Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse){
        if (!token.isEmpty()) {
            String retur = checkToken(token, httpServletResponse,"uploads");
            if(!retur.equals("uploads"))
                return retur;
        }
        return enrichModel(model,token,httpServletResponse);

    }

    public static String enrichModel(Model model, String session, HttpServletResponse httpServletResponse) {
        String email;
        try {
            email = DatabaseService.getEmailByToken(session);
        } catch (Exception e) {
            invalidateSession(httpServletResponse);
            e.printStackTrace();
            return "index";
        }
        if (email == null) {
            invalidateSession(httpServletResponse);
            return "index";
        }
        try {
            Map<String,Object> videosFromDB = DatabaseService.getVideos(email);
            List<Video> videos  = new ArrayList<Video>();

            for (String videoName : videosFromDB.keySet()) {

                Video video = new Video();

                JSONObject jObject = new JSONObject(videosFromDB.get(videoName).toString());

                video.setDuration(Double.valueOf(jObject.get("duration").toString()));
                video.setName(jObject.get("name").toString());
                video.setId(jObject.get("id").toString());

                String country = jObject.get("country").toString().replace(owlIRI, "");
                country = country.substring(0, 1).toUpperCase() + country.substring(1);

                video.setCountry(country);

                JSONArray signsJsonObject = jObject.getJSONArray("signs");

                for (Object signObj : signsJsonObject) {

                    JSONObject signJsonObject = new JSONObject(signObj.toString());

                    SignFromVideo signFromVideo = new SignFromVideo();
                    signFromVideo.setVideoStamp(Integer.valueOf(signJsonObject.get("video_timestamp").toString()));

                    JSONObject locationJsonObject = new JSONObject(signJsonObject.get("location").toString());
                    String coord_long = locationJsonObject.get("coord_long").toString();
                    String coord_lat = locationJsonObject.get("coord_lat").toString();
                    signFromVideo.setLocation(new Location(coord_long, coord_lat));

                    JSONObject jsonSign = new JSONObject(signJsonObject.get("sign").toString());
                    signFromVideo.setMoreAt(jsonSign.get("moreAt").toString());

                    String signName = jsonSign.getString("name").toString().replace(owlIRI, "").replace("_", " ");

                    JSONArray jsonSignProperties = jsonSign.getJSONArray("properties");
                    Map<String, Set<String>> signProperties = new HashMap<>();

                    for (Object jsonSignProperty : jsonSignProperties) {

                        JSONObject jsonProperty = new JSONObject(jsonSignProperty.toString());
                        String property = jsonProperty.get("property").toString();
                        String propertyValue = jsonProperty.get("value").toString();

                        if (property.contains(owlIRI)) {

                            property = property.replace(owlIRI, "").trim();
                        }

                        if (property.equals(commentURL)) {

                            property = description;
                        }

                        if (property.equals(typeURL)) {

                            property = type;
                        }

                        if (propertyValue.contains(owlIRI)) {

                            propertyValue = propertyValue.replace(owlIRI, "").trim();
                        }

                        if (signProperties.get(property) == null) {

                            signProperties.put(property, new HashSet<>());
                        }

                        signProperties.get(property).add(propertyValue);
                    }

                    signFromVideo.setSign(Sign.createSign(signName, signProperties));


                    video.getSigns().add(signFromVideo);
                }

                videos.add(video);
            }

            model.addAttribute("videos", videos);
        } catch (Exception e) {
            invalidateSession(httpServletResponse);
            e.printStackTrace();
            return "index";
        }

        return "uploads";
    }
}
