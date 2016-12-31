package com.saivision.collection.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by checkapp on 23/10/16.
 */

public class GroupPOJO {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("group_name")
    @Expose
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
