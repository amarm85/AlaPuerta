package com.attebion.api.alapuerta.models;



/**
 * Created by amar-pc on 5/29/2016.
 */
public class LoginJsonResponse {

    private Status status;
    private Data data;
    private Link[] links;

    // delete this it is only for testing
    public String getMessage() {
        return message;
    }
    private String message;




    public Status getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public Link[] getLinks() {
        return links;
    }





    public class Status{
        private boolean success;
        private String message;
    }

    public class Data{
        private String apiKey;
        private String email;
    }

    public class Link{

        private String rel;
        private String href;
    }
}
