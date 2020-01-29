package com.wtra.client.controller;

import com.wtra.client.entity.Sign;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.wtra.client.controller.HomeController.API_INSTANCE;
import static com.wtra.client.controller.LoginController.TOKEN;
import static com.wtra.client.entity.Sign.*;
import static com.wtra.client.service.AuthenticationService.checkToken;

@Controller
public class SignsController {

    private static final String signsEndpoint = "signs";
    private static final HttpClient client = HttpClientBuilder.create().build();
    private static final String owlIRI = "https://github.com/danCazacu/WTra#";

    @RequestMapping("/signs")
    public String signs(Model model, @CookieValue(value = TOKEN, defaultValue = "") String token, HttpServletResponse httpServletResponse) {
        if (!token.isEmpty()) {
            String retur = checkToken(token, httpServletResponse, "signs");
            if (!retur.equals("signs"))
                return retur;
        }
        return enrichModel(model, token, httpServletResponse);
    }

    private static String enrichModel(Model model, String session, HttpServletResponse httpServletResponse) {

        List<Sign> signs = new ArrayList<>();

        HttpGet getSignsRequest = new HttpGet(API_INSTANCE + signsEndpoint);
        getSignsRequest.setHeader("Accept", "application/json");

        try {

            HttpResponse response = client.execute(getSignsRequest);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {

                model.addAttribute("signsError", "Something went wrong. Please try again latter...");
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);

                JSONObject signPropertiesJsonObject = new JSONObject(result);
                JSONArray signPropertiesBinding = signPropertiesJsonObject.getJSONObject("results").getJSONArray("bindings");

                for (int i = 0; i < signPropertiesBinding.length(); i++) {

                    JSONObject jsonobject = signPropertiesBinding.getJSONObject(i);
                    JSONObject sign = jsonobject.getJSONObject("sign");
                    String signName = sign.getString("value").replace(owlIRI, "");

                    Sign signObj = handleSign(model, signName);
                    if (signObj != null) {
                        signs.add(signObj);
                    }
                }

                model.addAttribute("signs", signs);
            }

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("signsError", "Sorry :( ... Something went wrong... " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            model.addAttribute("signsError", "Sorry :( ... Something went wrong... " + e.getMessage());
        }

        return "signs";
    }

    private static Sign handleSign(Model model, String signName) throws IOException {

        String commentURL = "http://www.w3.org/2000/01/rdf-schema#comment";
        String typeURL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        String individualURL = "http://www.w3.org/2002/07/owl#NamedIndividual";

        try {
            HttpGet getSignRequest = new HttpGet(API_INSTANCE + signsEndpoint + "/" + signName);
            getSignRequest.setHeader("Accept", "application/json");

            HttpResponse response = client.execute(getSignRequest);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                model.addAttribute("signsError", "Something went wrong. Please try again latter...");
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {

                String result = EntityUtils.toString(entity);
                JSONObject signPropertiesJsonObject = new JSONObject(result);
                JSONArray signPropertiesBinding = signPropertiesJsonObject.getJSONObject("results").getJSONArray("bindings");
                Map<String, Set<String>> signProperties = new HashMap<>();

                signProperties.put(hasBackgroundColor, new HashSet<>());
                signProperties.put(hasBorderColor, new HashSet<>());
                signProperties.put(hasForm, new HashSet<>());
                signProperties.put(hasLegalRegulations, new HashSet<>());
                signProperties.put(applicableTo, new HashSet<>());
                signProperties.put(hasImageLink, new HashSet<>());
                signProperties.put(description, new HashSet<>());
                signProperties.put(country, new HashSet<>());
                signProperties.put(type, new HashSet<>());

                for (int i = 0; i < signPropertiesBinding.length(); i++) {

                    JSONObject jsonobject = signPropertiesBinding.getJSONObject(i);

                    signProperties.get(country).add(jsonobject.getJSONObject(country).getString("value").replace(owlIRI, "").trim());

                    JSONObject jsonSignProperty = jsonobject.getJSONObject("signProperty");
                    String signProperty = jsonSignProperty.getString("value").trim();

                    JSONObject jsonSignPropertyValue = jsonobject.getJSONObject("signPropertyValue");
                    String propertyValue = jsonSignPropertyValue.getString("value").replace(owlIRI, "").trim();

                    if (signProperty.contains(owlIRI)) {

                        signProperty = signProperty.replace(owlIRI, "").trim();
                    }

                    if (signProperty.equals(commentURL)) {

                        signProperty = description;
                    }

                    if (signProperty.equals(typeURL)) {

                        signProperty = type;
                    }

                    if (propertyValue.contains(owlIRI)) {

                        propertyValue = propertyValue.replace(owlIRI, "").trim();
                    }

                    if (!propertyValue.equals(individualURL)) {

                        if (signProperties.get(signProperty) != null) {
                            signProperties.get(signProperty).add(propertyValue);
                        }
                    }
                }

                if(signProperties.get(type).size() > 0) {
                    return Sign.createSign(signName, signProperties);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("signsError", "Sorry :( ... Something went wrong... " + e.getMessage());
        }

        return null;
    }
}
