package com.investmentapp.investment_app.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

public class SymbolLookupResponse {
  private int count;
  private List<SymbolResponse> result;

  public SymbolLookupResponse() {
    // No-arg constructor
  }

  public SymbolLookupResponse(int count, List<SymbolResponse> result) {
    this.count = count;
    this.result = result;
  }

}
