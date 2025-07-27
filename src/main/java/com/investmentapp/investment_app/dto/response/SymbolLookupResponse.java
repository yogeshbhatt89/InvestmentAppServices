package com.investmentapp.investment_app.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
