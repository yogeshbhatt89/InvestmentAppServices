package com.investmentapp.investment_app.controller;

import com.investmentapp.investment_app.model.Country;
import com.investmentapp.investment_app.model.Language;
import com.investmentapp.investment_app.repository.CountryRepository;
import com.investmentapp.investment_app.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookups")
@RequiredArgsConstructor
public class LookUpController {
    private final CountryRepository countryRepo;
    private final LanguageRepository languageRepo;

    @GetMapping("/countries")
    public List<Country> getCountries() {
        return countryRepo.findAll(Sort.by("name"));
    }

    @GetMapping("/languages")
    public List<Language> getLanguages() {
        return languageRepo.findAll(Sort.by("name"));
    }
}
