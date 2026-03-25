package com.zunoBank.AccountManagemnet.service.helper;

import com.zunoBank.AccountManagemnet.dto.OnboardingRequestDTO;
import com.zunoBank.AccountManagemnet.dto.StaffResponseDto;
import com.zunoBank.AccountManagemnet.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerBuilder {

    public Customer buildCustomer(OnboardingRequestDTO r, StaffResponseDto staff) {
        Customer c = new Customer();
        c.setFirstName(r.getFirstName());
        c.setMiddleName(r.getMiddleName());
        c.setLastName(r.getLastName());
        c.setDateOfBirth(r.getDateOfBirth());
        c.setGender(r.getGender());
        c.setMaritalStatus(r.getMaritalStatus());
        c.setPhone(r.getPhone());
        c.setEmail(r.getEmail());
        c.setAddressLine1(r.getAddressLine1());
        c.setAddressLine2(r.getAddressLine2());
        c.setCity(r.getCity());
        c.setState(r.getState());
        c.setPincode(r.getPincode());
        c.setAadhaarNumber(r.getAadhaarNumber());
        c.setPanNumber(r.getPanNumber());
        c.setOccupationType(r.getOccupationType());
        c.setEmployerName(r.getEmployerName());
        c.setAnnualIncome(r.getAnnualIncome());
        c.setBranchCode(staff.getBranchCode());      // ← was request.getBranchCode()
        c.setBranchName(r.getBranchName());
        c.setCreatedByRoId(staff.getEmployeeId());   // ← was request.getCreatedByRoId()
        c.setRoName(staff.getFullName());            // ← was request.getRoName()
        return c;
    }
}
