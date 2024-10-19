package net.toshimichi.company.command;

import net.toshimichi.company.data.Company;
import net.toshimichi.company.data.Member;

import java.util.List;

public class MemberTabCompleter {

    public static List<String> onTabComplete(Company company, String[] args) {
        List<String> names = company.getMembers()
                .stream()
                .map(Member::getName)
                .toList();

        if (args.length < 1) return names;

        return names.stream()
                .filter(it -> it.startsWith(args[0]))
                .toList();
    }
}
