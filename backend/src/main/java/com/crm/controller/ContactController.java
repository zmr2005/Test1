package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.common.Result;
import com.crm.dto.ContactCreateDTO;
import com.crm.entity.Contact;
import com.crm.service.ContactService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers/{customerId}/contacts")
public class ContactController {

    @Resource
    private ContactService contactService;

    @GetMapping
    public Result<List<Contact>> list(@PathVariable Long customerId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getCustomerId, customerId).orderByAsc(Contact::getId);
        return Result.ok(contactService.list(wrapper));
    }

    @PostMapping
    public Result<Contact> create(@PathVariable Long customerId,
                                  @Valid @RequestBody ContactCreateDTO dto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(dto, contact);
        contact.setCustomerId(customerId);
        contactService.save(contact);
        return Result.ok(contact);
    }
}
