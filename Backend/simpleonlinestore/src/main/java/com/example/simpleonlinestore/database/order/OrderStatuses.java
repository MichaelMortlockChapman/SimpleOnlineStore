package com.example.simpleonlinestore.database.order;

public class OrderStatuses {

  public static enum statuses {
    ORDERED,
    SHIPPED,
    DELIVERY,
    COMPLETE,
    CANCELED
  }
  
  public static final String ORDERED = "ORDERED";
  public static final String SHIPPED = "SHIPPED";
  public static final String DELIVERY = "DELIVERY";
  public static final String COMPLETE = "COMPLETE";
  public static final String CANCELED = "CANCELED";
}
