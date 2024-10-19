package net.toshimichi.company.command;

import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.CompanyRepository;

import java.util.List;

public class CompanyTabCompleter {

    public static List<String> onTabComplete(CompanyRepository companyRepository, String[] args) {
        List<String> names = companyRepository.findAll()
                .stream()
                .map(Company::getName)
                .toList();

        if (args.length < 1) return names;

        return names.stream()
                .filter(it -> it.startsWith(String.join(" ", args)))
                .toList();
    }
}
