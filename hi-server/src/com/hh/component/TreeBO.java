/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.component;

import java.util.ArrayList;

/**
 * @author vtsoft
 */
public class TreeBO {
    public String key;
    public String title;
    public String tooltip;
    public boolean select;
    public boolean isFolder;
    public boolean expand;
    public boolean hideCheckbox;
    public boolean unselectable;
    public ArrayList<TreeBO> children;
}
