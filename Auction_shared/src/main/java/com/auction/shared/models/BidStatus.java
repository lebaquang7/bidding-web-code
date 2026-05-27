package com.auction.shared.models;

import java.io.Serializable;

public class BidStatus {
    public enum bidStatus{SUCCESS, WINNER, INVALID, ALREADY_HIGHEST, EXPIRED}
}
