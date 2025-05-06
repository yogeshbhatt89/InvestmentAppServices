package com.investmentapp.investment_app.DTO;

public class MarketStatusDTO {
  private String exchange;
  private String holiday;
  private boolean isOpen;
  private String session;
  private String timezone;
  private long t;
  private String errorMessage;

  public MarketStatusDTO() {}

  public MarketStatusDTO(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  // Getters and Setters
  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getHoliday() {
    return holiday;
  }

  public void setHoliday(String holiday) {
    this.holiday = holiday;
  }

  public String getSession() {
    return session;
  }

  public void setSession(String session) {
    this.session = session;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public long getT() {
    return t;
  }

  public void setT(long timestamp) {
    this.t = timestamp;
  }

  public boolean getIsOpen() {
    return isOpen;
  }

  public void setIsOpen(boolean open) {
    isOpen = open;
  }
}
