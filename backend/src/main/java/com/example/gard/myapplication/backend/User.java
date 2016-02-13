package com.example.gard.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
//import com.google.appengine

import org.w3c.dom.Text;

import java.lang.annotation.Annotation;

@Entity
public class User {

    @Id
    String id;

    @Index
    private String regId;

    String name;
    //private String name;
    private String messagingId;

    public User(){}

    public void setId(String id){this.id = id;}

    public void setRegId(String regId){this.regId = regId;}

    public void setName(String name){this.name = name;}

    public void setMessagingId(String gcmId){messagingId = gcmId;}

    public String getId(){return id;}
    public String getRegId(){return regId;}
    public String getName(){return name;}
    public String getMessagingId(){return messagingId;}
}