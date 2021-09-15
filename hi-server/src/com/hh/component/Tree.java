/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.component;

import java.util.*;

/**
 * @author hiendm1
 */
public class Tree {

    public List getTreeList(List<Map> lstData, String idColumn, String titleColumn, String parentColumn, String sortColumn) {
        return getTreeList(lstData, idColumn, titleColumn, parentColumn, sortColumn, "");
    }

    public List getTreeList(List<Map> lstData, String idColumn, String titleColumn, String parentColumn, String sortColumn, String parentRootValue) {
        lstData = sortList(lstData, sortColumn);
        if (parentRootValue == null) parentRootValue = "";
        if (lstData == null || lstData.isEmpty()) return new ArrayList();
        HashMap<String, TreeBO> dataRef = new HashMap();
        for (int i = 0; i < lstData.size(); i++) {
            Map row = lstData.get(i);
            TreeBO data = new TreeBO();
            data.children = new ArrayList();
            data.key = row.get(idColumn).toString();
            data.title = (String) row.get(titleColumn);
            dataRef.put(row.get(idColumn).toString(), data);
        }
        List lstResult = new ArrayList();
        for (int i = 0; i < lstData.size(); i++) {
            Map row = lstData.get(i);
            String parentId = "";
            if (row.get(parentColumn) != null) parentId = row.get(parentColumn).toString();
            if (parentId.equals(parentRootValue)) {
                TreeBO data = dataRef.get(row.get(idColumn).toString());
                data.expand = true;
                lstResult.add(data);
            } else {
                TreeBO parent = dataRef.get(parentId);
                if (parent != null) {
                    parent.children.add(dataRef.get(row.get(idColumn).toString()));
                    parent.isFolder = true;
                }
            }
        }
        return lstResult;
    }

    public List<Map> sortList(List<Map> lstData, final String colCompare) {
        Collections.sort(lstData, new Comparator<Map>() {
            @Override
            public int compare(Map user1, Map user2) {
                if (Integer.parseInt(user1.get(colCompare).toString()) > Integer.parseInt(user2.get(colCompare).toString()))
                    return 1;
                else if (Integer.parseInt(user1.get(colCompare).toString()) > Integer.parseInt(user2.get(colCompare).toString()))
                    return 0;
                else return -1;
            }
        });
        return lstData;
    }
}
