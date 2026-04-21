package com.tss.aml.controller;


import com.tss.aml.tenant.entity.BankAdmin;
import com.tss.aml.tenant.repository.BankAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank-admin")
@RequiredArgsConstructor
public class BankAdminController {

    private final BankAdminRepository repo;

    @PostMapping
    public BankAdmin create(@RequestBody BankAdmin admin) {
        return repo.save(admin);
    }

    @GetMapping
    public List<BankAdmin> getAll() {
        return repo.findAll();
    }
}
