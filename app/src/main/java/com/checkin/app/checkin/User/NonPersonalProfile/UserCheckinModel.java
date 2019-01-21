package com.checkin.app.checkin.User.NonPersonalProfile;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCheckinModel {

    @JsonProperty("shop")
    private
    BriefModel shop;
    @JsonProperty("location")
    private
    String location;
    @JsonProperty("count_visits")
    private
    int countVisits;

    public BriefModel getShop() {
        return shop;
    }

    public String getLocation() {
        return location;
    }

    public int getCountVisits() {
        return countVisits;
    }
}
