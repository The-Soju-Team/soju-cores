/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.action;

/**
 * @author HienDM
 */
public class DefaultReturnFilter implements ReturnFilter {
    public String execute(String data, String local) {
        return data;
    }
}
