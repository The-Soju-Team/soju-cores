/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.web;

import java.util.HashMap;

/**
 *
 * @author HienDM
 */
public class PageFactory {
    private String template;
    private HashMap<String, String> childPages;

    public PageFactory(String template, HashMap childPages) {
        this.template = template;
        this.childPages = childPages;
    }
    
    public PageFactory(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public HashMap getChildPages() {
        return childPages;
    }

    public void setChildPages(HashMap childPages) {
        this.childPages = childPages;
    }
    
    public void addChildPage(String position, String path) {
        if(childPages == null) childPages = new HashMap();
        childPages.put(path, position);
    }
}
