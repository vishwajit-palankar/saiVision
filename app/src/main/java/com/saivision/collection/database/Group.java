package com.saivision.collection.database;

import io.realm.RealmObject;

/**
 * Created by checkapp on 23/10/16.
 */

public class Group extends RealmObject{
    private String id;
    private String groupName;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName The group_name
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
