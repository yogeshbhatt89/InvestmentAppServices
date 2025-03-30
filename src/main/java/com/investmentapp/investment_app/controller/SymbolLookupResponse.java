package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.DTO.SymbolDTO;

import java.util.List;

public class SymbolLookupResponse {
    private int count;
    private List<SymbolDTO> result;


    public SymbolLookupResponse() {
        // No-arg constructor
    }
    public SymbolLookupResponse(int count, List<SymbolDTO> result) {
        this.count = count;
        this.result = result;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<SymbolDTO> getResult() {
        return result;
    }

    public void setResult(List<SymbolDTO> result) {
        this.result = result;
    }
}