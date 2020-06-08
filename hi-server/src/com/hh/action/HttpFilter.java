/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.action;

import com.hh.web.HttpUtils;

/**
 *
 * @author vtsoft
 */
public interface HttpFilter {
    public boolean execute(HttpUtils hu);
}
