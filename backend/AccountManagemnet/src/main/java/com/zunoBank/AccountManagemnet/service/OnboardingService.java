package com.zunoBank.AccountManagemnet.service;

import com.zunoBank.AccountManagemnet.client.AuthServiceClient;
import com.zunoBank.AccountManagemnet.dto.*;
import com.zunoBank.AccountManagemnet.entity.CurrentAccount;
import com.zunoBank.AccountManagemnet.entity.Customer;
import com.zunoBank.AccountManagemnet.entity.SavingAccount;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.entity.type.AccountType;
import com.zunoBank.AccountManagemnet.entity.type.CustomerStatus;
import com.zunoBank.AccountManagemnet.error.AccountException;
import com.zunoBank.AccountManagemnet.repository.CurrentAccountRepository;
import com.zunoBank.AccountManagemnet.repository.CustomerRepository;
import com.zunoBank.AccountManagemnet.repository.SavingAccountRepository;
import com.zunoBank.AccountManagemnet.service.helper.CurrentAccountBuilder;
import com.zunoBank.AccountManagemnet.service.helper.CustomerBuilder;
import com.zunoBank.AccountManagemnet.service.helper.SavingAccountBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final CustomerRepository customerRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final AuthServiceClient authServiceClient;
    private final CustomerBuilder customerBuilder;
    private final SavingAccountBuilder savingAccountBuilder;
    private final CurrentAccountBuilder currentAccountBuilder;

    // ─────────────────────────────────────────────────────────────────────
    // STEP 1: RO submits ONE form
    // ─────────────────────────────────────────────────────────────────────

    @Transactional
    public OnboardingResponseDTO createApplication(
            OnboardingRequestDTO request, String employeeId) {

        StaffResponseDto staff = authServiceClient
                .getStaffByEmployeeId(employeeId);

        Customer customer;

        if (request.getExistingCif() != null
                && !request.getExistingCif().isBlank()) {

            // ── EXISTING CUSTOMER ─────────────────────────────────────
            customer = customerRepository
                    .findByCif(request.getExistingCif())
                    .orElseThrow(() -> new AccountException(
                            "Customer not found with CIF: "
                                    + request.getExistingCif()));

            if (customer.getStatus() != CustomerStatus.ACTIVE)
                throw new AccountException(
                        "Customer is not active. Status: "
                                + customer.getStatus());

            // check duplicate account
            if (request.getAccountType() == AccountType.SAVINGS) {
                if (savingAccountRepository
                        .existsByCustomer(customer))
                    throw new AccountException(
                            "Customer already has a Saving Account");
            } else {
                if (currentAccountRepository
                        .existsByCustomer(customer))
                    throw new AccountException(
                            "Customer already has a Current Account");
            }

        } else {

            // ── NEW CUSTOMER ──────────────────────────────────────────

            // duplicate checks
            if (customerRepository.existsByPhone(request.getPhone()))
                throw new AccountException(
                        "Phone already registered: "
                                + request.getPhone());

            if (customerRepository.existsByEmail(request.getEmail()))
                throw new AccountException(
                        "Email already registered: "
                                + request.getEmail());

            if (request.getAadhaarNumber() != null &&
                    customerRepository.existsByAadhaarNumber(
                            request.getAadhaarNumber()))
                throw new AccountException(
                        "Aadhaar already registered");

            if (request.getPanNumber() != null &&
                    customerRepository.existsByPanNumber(
                            request.getPanNumber()))
                throw new AccountException(
                        "PAN already registered");

            // save new customer
            customer = customerBuilder.buildCustomer(request, staff);
            customer = customerRepository.save(customer);
        }

        // ── min deposit validation ────────────────────────────────────
        BigDecimal minDeposit = request.getAccountType()
                == AccountType.SAVINGS
                ? new BigDecimal("500")
                : new BigDecimal("5000");

        if (request.getInitialDeposit().compareTo(minDeposit) < 0)
            throw new AccountException(
                    "Minimum deposit for "
                            + request.getAccountType()
                            + " is ₹" + minDeposit);

        // ── Save Account ──────────────────────────────────────────────
        if (request.getAccountType() == AccountType.SAVINGS) {
            SavingAccount sa = savingAccountBuilder.buildSavingAccount(request, customer, staff);
            savingAccountRepository.save(sa);
        } else {
            CurrentAccount ca = currentAccountBuilder.buildCurrentAccount(request, customer, staff);
            currentAccountRepository.save(ca);
        }

        // ── Build Response ────────────────────────────────────────────
        return OnboardingResponseDTO.builder()
                .customerId(customer.getId())
                .cif(customer.getCif())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .city(customer.getCity())
                .state(customer.getState())
                .accountType(request.getAccountType())
                .initialDeposit(request.getInitialDeposit())
                .branchName(request.getBranchName())
                .branchCode(staff.getBranchCode())      // ← add this
                .createdByRoId(staff.getEmployeeId())   // ← add this
                .roName(staff.getFullName())            // ← add this
                .submittedAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────
    // STEP 2: Manager approves or rejects ONCE
    // ─────────────────────────────────────────────────────────────────────

//    @Transactional
//    public OnboardingResponseDTO processApproval(
//            OnboardingApprovalDTO approval, String employeeId) {
//
//        StaffResponseDto manager = authServiceClient
//                .getStaffByEmployeeId(employeeId);
//
//        Customer customer = customerRepository
//                .findById(approval.getCustomerId())
//                .orElseThrow(() -> new AccountException(
//                        "Customer not found: #"
//                                + approval.getCustomerId()));
//
//        // find pending account
//        Optional<SavingAccount> pendingSaving =
//                savingAccountRepository
//                        .findByCustomerAndStatus(
//                                customer,
//                                AccountStatus.PENDING_APPROVAL);
//
//        Optional<CurrentAccount> pendingCurrent =
//                currentAccountRepository
//                        .findByCustomerAndStatus(
//                                customer,
//                                AccountStatus.PENDING_APPROVAL);
//
//        if (pendingSaving.isEmpty() && pendingCurrent.isEmpty())
//            throw new AccountException(
//                    "No pending account found for customer: #"
//                            + approval.getCustomerId());
//
//        if (approval.isApproved()) {
//
//            // ── generate CIF only if new customer ─────────────────────
//            if (customer.getCif() == null) {
//                customer.generateCif();
//            }
//
//            customer.setApprovedByManagerId(manager.getEmployeeId());
//            customer.setApprovedByManagerName(
//                    manager.getFullName());
//            customer.setActionTakenAt(LocalDateTime.now());
//            customerRepository.save(customer);
//
//            // ── approve saving account ────────────────────────────────
//            if (pendingSaving.isPresent()) {
//                SavingAccount sa = pendingSaving.get();
//                sa.setCif(customer.getCif());
//                sa.setApprovedByManagerId(manager.getEmployeeId());
//                sa.setApprovedByManagerName(manager.getFullName());
//                sa.setActionTakenAt(LocalDateTime.now());
//                sa.generateAccountNumber();
//                savingAccountRepository.save(sa);
//
//                return buildSavingResponse(customer, sa);
//            }
//
//            // ── approve current account ───────────────────────────────
//            if (pendingCurrent.isPresent()) {
//                CurrentAccount ca = pendingCurrent.get();
//                ca.setCif(customer.getCif());
//                ca.setApprovedByManagerId(manager.getEmployeeId());
//                ca.setApprovedByManagerName(manager.getFullName());
//                ca.setActionTakenAt(LocalDateTime.now());
//                ca.generateAccountNumber();
//                currentAccountRepository.save(ca);
//
//                return buildCurrentResponse(customer, ca);
//            }
//
//        } else {
//
//            // ── REJECT ────────────────────────────────────────────────
//            if (approval.getRejectionReason() == null
//                    || approval.getRejectionReason().isBlank())
//                throw new AccountException(
//                        "Rejection reason is mandatory");
//
//            // reject customer only if new (no CIF)
//            if (customer.getCif() == null) {
//                customer.setStatus(CustomerStatus.REJECTED);
//                customer.setRejectionReason(
//                        approval.getRejectionReason());
//                customer.setApprovedByManagerId(manager.getEmployeeId());
//                customer.setApprovedByManagerName(manager.getFullName());
//                customer.setActionTakenAt(LocalDateTime.now());
//                customerRepository.save(customer);
//            }
//
//            // reject saving account
//            pendingSaving.ifPresent(sa -> {
//                sa.setStatus(AccountStatus.REJECTED);
//                sa.setRejectionReason(
//                        approval.getRejectionReason());
//                sa.setApprovedByManagerId(manager.getEmployeeId());
//                sa.setActionTakenAt(LocalDateTime.now());
//                savingAccountRepository.save(sa);
//            });
//
//            // reject current account
//            pendingCurrent.ifPresent(ca -> {
//                ca.setStatus(AccountStatus.REJECTED);
//                ca.setRejectionReason(
//                        approval.getRejectionReason());
//                ca.setApprovedByManagerId(manager.getEmployeeId());
//                ca.setActionTakenAt(LocalDateTime.now());
//                currentAccountRepository.save(ca);
//            });
//        }
//
//        return OnboardingResponseDTO.builder()
//                .customerId(customer.getId())
//                .cif(customer.getCif())
//                .status(customer.getStatus())
//                .fullName(customer.getFullName())
//                .rejectionReason(customer.getRejectionReason())
//                .build();
//    }

    // ─────────────────────────────────────────────────────────────────────
    // Manager views pending queue
    // ─────────────────────────────────────────────────────────────────────

//    @Transactional
//    public List<PendingOnboardingDTO> getPendingApplications(
//            String branchCode, StaffResponseDto staff) {
//
//        List<PendingOnboardingDTO> result = savingAccountRepository
//                .findByStatusAndBranchCode(
//                        AccountStatus.PENDING_APPROVAL, branchCode)
//                .stream()
//                .map(sa -> PendingOnboardingDTO.builder()
//                        .customerId(sa.getCustomer().getId())
//                        .fullName(sa.getCustomer().getFullName())
//                        .cif(sa.getCustomer().getCif())
//                        .status(sa.getCustomer().getStatus())
//                        .phone(sa.getCustomer().getPhone())
//                        .email(sa.getCustomer().getEmail())
//                        .dateOfBirth(sa.getCustomer().getDateOfBirth())
//                        .gender(sa.getCustomer().getGender() != null
//                                ? sa.getCustomer().getGender().name()
//                                : null)
//                        .addressLine1(
//                                sa.getCustomer().getAddressLine1())
//                        .city(sa.getCustomer().getCity())
//                        .state(sa.getCustomer().getState())
//                        .pincode(sa.getCustomer().getPincode())
//                        .aadhaarNumber(
//                                sa.getCustomer().getAadhaarNumber())
//                        .panNumber(sa.getCustomer().getPanNumber())
//                        .occupationType(
//                                sa.getCustomer().getOccupationType() != null
//                                        ? sa.getCustomer().getOccupationType().name()
//                                        : null)
//                        .employerName(sa.getCustomer().getEmployerName())
//                        .annualIncome(sa.getCustomer().getAnnualIncome())
//                        .accountType(AccountType.SAVINGS)
//                        .initialDeposit(sa.getInitialDeposit())
//                        .ifscCode(sa.getIfscCode())
//                        .branchCode(staff.getBranchCode())
//                        .branchName(sa.getBranchName())
//                        .createdByRoId(staff.getEmployeeId())
//                        .roName(staff.getFullName())
//                        .submittedAt(sa.getSubmittedAt())
//                        .build())
//                .collect(Collectors.toList());
//
//        currentAccountRepository
//                .findByStatusAndBranchCode(
//                        AccountStatus.PENDING_APPROVAL, branchCode)
//                .stream()
//                .map(ca -> PendingOnboardingDTO.builder()
//                        .customerId(ca.getCustomer().getId())
//                        .fullName(ca.getCustomer().getFullName())
//                        .cif(ca.getCustomer().getCif())
//                        .status(ca.getCustomer().getStatus())
//                        .phone(ca.getCustomer().getPhone())
//                        .email(ca.getCustomer().getEmail())
//                        .dateOfBirth(ca.getCustomer().getDateOfBirth())
//                        .gender(ca.getCustomer().getGender() != null
//                                ? ca.getCustomer().getGender().name()
//                                : null)
//                        .addressLine1(
//                                ca.getCustomer().getAddressLine1())
//                        .city(ca.getCustomer().getCity())
//                        .state(ca.getCustomer().getState())
//                        .pincode(ca.getCustomer().getPincode())
//                        .aadhaarNumber(
//                                ca.getCustomer().getAadhaarNumber())
//                        .panNumber(ca.getCustomer().getPanNumber())
//                        .occupationType(
//                                ca.getCustomer().getOccupationType() != null
//                                        ? ca.getCustomer().getOccupationType().name()
//                                        : null)
//                        .employerName(ca.getCustomer().getEmployerName())
//                        .annualIncome(ca.getCustomer().getAnnualIncome())
//                        .accountType(AccountType.CURRENT)
//                        .initialDeposit(ca.getInitialDeposit())
//                        .ifscCode(ca.getIfscCode())
//                        .branchCode(ca.getBranchCode())
//                        .branchName(ca.getBranchName())
//                        .createdByRoId(ca.getCreatedByRoId())
//                        .roName(ca.getRoName())
//                        .submittedAt(ca.getSubmittedAt())
//                        .build())
//                .forEach(result::add);
//
//        return result;
//    }

    // ─────────────────────────────────────────────────────────────────────
    // Get by CIF
    // ─────────────────────────────────────────────────────────────────────

//    public OnboardingResponseDTO getByCif(String cif) {
//
//        Customer customer = customerRepository
//                .findByCif(cif)
//                .orElseThrow(() -> new AccountException(
//                        "Customer not found with CIF: " + cif));
//
//        Optional<SavingAccount> sa =
//                savingAccountRepository.findByCif(cif);
//
//        Optional<CurrentAccount> ca =
//                currentAccountRepository.findByCif(cif);
//
//        return OnboardingResponseDTO.builder()
//                .customerId(customer.getId())
//                .cif(customer.getCif())
//                .status(customer.getStatus())
//                .fullName(customer.getFullName())
//                .phone(customer.getPhone())
//                .email(customer.getEmail())
//                .city(customer.getCity())
//                .state(customer.getState())
//                .accountId(sa.map(SavingAccount::getId).orElse(
//                        ca.map(CurrentAccount::getId).orElse(null)))
//                .accountNumber(
//                        sa.map(SavingAccount::getAccountNumber).orElse(
//                                ca.map(CurrentAccount::getAccountNumber)
//                                        .orElse(null)))
//                .accountType(sa.isPresent()
//                        ? AccountType.SAVINGS
//                        : ca.isPresent()
//                        ? AccountType.CURRENT : null)
//                .balance(sa.map(SavingAccount::getBalance).orElse(
//                        ca.map(CurrentAccount::getBalance).orElse(null)))
//                .initialDeposit(
//                        sa.map(SavingAccount::getInitialDeposit).orElse(
//                                ca.map(CurrentAccount::getInitialDeposit)
//                                        .orElse(null)))
//                .interestRate(
//                        sa.map(SavingAccount::getInterestRate)
//                                .orElse(null))
//                .overdraftLimit(
//                        ca.map(CurrentAccount::getOverdraftLimit)
//                                .orElse(null))
//                .minimumBalance(
//                        sa.map(SavingAccount::getMinimumBalance).orElse(
//                                ca.map(CurrentAccount::getMinimumBalance)
//                                        .orElse(null)))
//                .branchCode(
//                        sa.map(SavingAccount::getBranchCode).orElse(
//                                ca.map(CurrentAccount::getBranchCode)
//                                        .orElse(null)))
//                .branchName(
//                        sa.map(SavingAccount::getBranchName).orElse(
//                                ca.map(CurrentAccount::getBranchName)
//                                        .orElse(null)))
//                .approvedByManagerId(customer.getApprovedByManagerId())
//                .approvedByManagerName(
//                        customer.getApprovedByManagerName())
//                .actionTakenAt(customer.getActionTakenAt())
//                .createdByRoId(customer.getCreatedByRoId())
//                .roName(customer.getRoName())
//                .submittedAt(customer.getSubmittedAt())
//                .build();
//    }

    // ─────────────────────────────────────────────────────────────────────
    // Get Saving Account
    // ─────────────────────────────────────────────────────────────────────

//    public OnboardingResponseDTO getSavingAccount(
//            String accountNumber) {
//
//        SavingAccount sa = savingAccountRepository
//                .findByAccountNumber(accountNumber)
//                .orElseThrow(() -> new AccountException(
//                        "Saving account not found: " + accountNumber));
//
//        Customer customer = sa.getCustomer();
//
//        return OnboardingResponseDTO.builder()
//                .customerId(customer.getId())
//                .cif(customer.getCif())
//                .status(customer.getStatus())
//                .fullName(customer.getFullName())
//                .phone(customer.getPhone())
//                .email(customer.getEmail())
//                .city(customer.getCity())
//                .state(customer.getState())
//                .accountId(sa.getId())
//                .accountNumber(sa.getAccountNumber())
//                .accountType(AccountType.SAVINGS)
//                .balance(sa.getBalance())
//                .initialDeposit(sa.getInitialDeposit())
//                .interestRate(sa.getInterestRate())
//                .minimumBalance(sa.getMinimumBalance())
//                .ifscCode(sa.getIfscCode())
//                .branchCode(sa.getBranchCode())
//                .branchName(sa.getBranchName())
//                .createdByRoId(sa.getCreatedByRoId())
//                .roName(sa.getRoName())
//                .submittedAt(sa.getSubmittedAt())
//                .approvedByManagerId(sa.getApprovedByManagerId())
//                .approvedByManagerName(sa.getApprovedByManagerName())
//                .actionTakenAt(sa.getActionTakenAt())
//                .build();
//    }

    // ─────────────────────────────────────────────────────────────────────
    // Get Current Account
    // ─────────────────────────────────────────────────────────────────────

//    public OnboardingResponseDTO getCurrentAccount(
//            String accountNumber) {
//
//        CurrentAccount ca = currentAccountRepository
//                .findByAccountNumber(accountNumber)
//                .orElseThrow(() -> new AccountException(
//                        "Current account not found: " + accountNumber));
//
//        Customer customer = ca.getCustomer();
//
//        return OnboardingResponseDTO.builder()
//                .customerId(customer.getId())
//                .cif(customer.getCif())
//                .status(customer.getStatus())
//                .fullName(customer.getFullName())
//                .phone(customer.getPhone())
//                .email(customer.getEmail())
//                .city(customer.getCity())
//                .state(customer.getState())
//                .accountId(ca.getId())
//                .accountNumber(ca.getAccountNumber())
//                .accountType(AccountType.CURRENT)
//                .balance(ca.getBalance())
//                .initialDeposit(ca.getInitialDeposit())
//                .overdraftLimit(ca.getOverdraftLimit())
//                .minimumBalance(ca.getMinimumBalance())
//                .ifscCode(ca.getIfscCode())
//                .branchCode(ca.getBranchCode())
//                .branchName(ca.getBranchName())
//                .createdByRoId(ca.getCreatedByRoId())
//                .roName(ca.getRoName())
//                .submittedAt(ca.getSubmittedAt())
//                .approvedByManagerId(ca.getApprovedByManagerId())
//                .approvedByManagerName(ca.getApprovedByManagerName())
//                .actionTakenAt(ca.getActionTakenAt())
//                .build();
//    }

    // ─────────────────────────────────────────────────────────────────────
    // Get All Accounts by CIF
    // ─────────────────────────────────────────────────────────────────────

//    public CustomerAccountsDTO getAllAccountsByCif(String cif) {
//
//        Customer customer = customerRepository
//                .findByCif(cif)
//                .orElseThrow(() -> new AccountException(
//                        "Customer not found with CIF: " + cif));
//
//        Optional<SavingAccount> sa =
//                savingAccountRepository.findByCif(cif);
//
//        Optional<CurrentAccount> ca =
//                currentAccountRepository.findByCif(cif);
//
//        return CustomerAccountsDTO.builder()
//                .customerId(customer.getId())
//                .cif(customer.getCif())
//                .status(customer.getStatus())
//                .fullName(customer.getFullName())
//                .phone(customer.getPhone())
//                .email(customer.getEmail())
//                .city(customer.getCity())
//                .state(customer.getState())
//                .pincode(customer.getPincode())
//                .savingAccount(sa.map(s -> SavingAccountDTO.builder()
//                        .id(s.getId())
//                        .accountNumber(s.getAccountNumber())
//                        .status(s.getStatus())
//                        .balance(s.getBalance())
//                        .initialDeposit(s.getInitialDeposit())
//                        .interestRate(s.getInterestRate())
//                        .minimumBalance(s.getMinimumBalance())
//                        .ifscCode(s.getIfscCode())
//                        .branchCode(s.getBranchCode())
//                        .branchName(s.getBranchName())
//                        .createdAt(s.getCreatedAt())
//                        .lastTransactionAt(s.getLastTransactionAt())
//                        .build()).orElse(null))
//                .currentAccount(ca.map(c -> CurrentAccountDTO.builder()
//                        .id(c.getId())
//                        .accountNumber(c.getAccountNumber())
//                        .status(c.getStatus())
//                        .balance(c.getBalance())
//                        .initialDeposit(c.getInitialDeposit())
//                        .overdraftLimit(c.getOverdraftLimit())
//                        .minimumBalance(c.getMinimumBalance())
//                        .ifscCode(c.getIfscCode())
//                        .branchCode(c.getBranchCode())
//                        .branchName(c.getBranchName())
//                        .createdAt(c.getCreatedAt())
//                        .lastTransactionAt(c.getLastTransactionAt())
//                        .build()).orElse(null))
//                .build();
//    }

    // ─────────────────────────────────────────────────────────────────────
    // Private Builders
    // ─────────────────────────────────────────────────────────────────────

//    private Customer buildCustomer(OnboardingRequestDTO r, StaffResponseDto staff) {
//        Customer c = new Customer();
//        c.setFirstName(r.getFirstName());
//        c.setMiddleName(r.getMiddleName());
//        c.setLastName(r.getLastName());
//        c.setDateOfBirth(r.getDateOfBirth());
//        c.setGender(r.getGender());
//        c.setMaritalStatus(r.getMaritalStatus());
//        c.setPhone(r.getPhone());
//        c.setEmail(r.getEmail());
//        c.setAddressLine1(r.getAddressLine1());
//        c.setAddressLine2(r.getAddressLine2());
//        c.setCity(r.getCity());
//        c.setState(r.getState());
//        c.setPincode(r.getPincode());
//        c.setAadhaarNumber(r.getAadhaarNumber());
//        c.setPanNumber(r.getPanNumber());
//        c.setOccupationType(r.getOccupationType());
//        c.setEmployerName(r.getEmployerName());
//        c.setAnnualIncome(r.getAnnualIncome());
//        c.setBranchCode(staff.getBranchCode());      // ← was request.getBranchCode()
//        c.setBranchName(r.getBranchName());
//        c.setCreatedByRoId(staff.getEmployeeId());   // ← was request.getCreatedByRoId()
//        c.setRoName(staff.getFullName());            // ← was request.getRoName()
//        return c;
//    }

//    private SavingAccount buildSavingAccount(
//            OnboardingRequestDTO r, Customer customer,StaffResponseDto staff) {
//        SavingAccount sa = new SavingAccount();
//        sa.setCustomer(customer);
//        sa.setCif(customer.getCif());
//        sa.setInitialDeposit(r.getInitialDeposit());
//        sa.setIfscCode(r.getIfscCode());
//        sa.setBranchCode(staff.getBranchCode());     // ← was request.getBranchCode()
//        sa.setBranchName(r.getBranchName());
//        sa.setCreatedByRoId(staff.getEmployeeId());  // ← was request.getCreatedByRoId()
//        sa.setRoName(staff.getFullName());           // ← was request.getRoName()
//        return sa;
//    }

//    private CurrentAccount buildCurrentAccount(
//            OnboardingRequestDTO r, Customer customer, StaffResponseDto staff) {
//        CurrentAccount ca = new CurrentAccount();
//        ca.setCustomer(customer);
//        ca.setCif(customer.getCif());
//        ca.setInitialDeposit(r.getInitialDeposit());
//        ca.setIfscCode(r.getIfscCode());
//        ca.setBranchCode(staff.getBranchCode());
//        ca.setBranchName(r.getBranchName());
//        ca.setCreatedByRoId(staff.getEmployeeId());
//        ca.setRoName(staff.getFullName());
//        return ca;
//    }

//    private OnboardingResponseDTO buildSavingResponse(
//            Customer c, SavingAccount sa) {
//        return OnboardingResponseDTO.builder()
//                .customerId(c.getId())
//                .cif(c.getCif())
//                .status(c.getStatus())
//                .fullName(c.getFullName())
//                .phone(c.getPhone())
//                .email(c.getEmail())
//                .city(c.getCity())
//                .state(c.getState())
//                .accountId(sa.getId())
//                .accountNumber(sa.getAccountNumber())
//                .accountType(AccountType.SAVINGS)
//                .balance(sa.getBalance())
//                .initialDeposit(sa.getInitialDeposit())
//                .interestRate(sa.getInterestRate())
//                .minimumBalance(sa.getMinimumBalance())
//                .ifscCode(sa.getIfscCode())
//                .branchCode(sa.getBranchCode())
//                .branchName(sa.getBranchName())
//                .createdByRoId(sa.getCreatedByRoId())
//                .roName(sa.getRoName())
//                .submittedAt(sa.getSubmittedAt())
//                .approvedByManagerId(c.getApprovedByManagerId())
//                .approvedByManagerName(c.getApprovedByManagerName())
//                .actionTakenAt(c.getActionTakenAt())
//                .build();
//    }

//    private OnboardingResponseDTO buildCurrentResponse(
//            Customer c, CurrentAccount ca) {
//        return OnboardingResponseDTO.builder()
//                .customerId(c.getId())
//                .cif(c.getCif())
//                .status(c.getStatus())
//                .fullName(c.getFullName())
//                .phone(c.getPhone())
//                .email(c.getEmail())
//                .city(c.getCity())
//                .state(c.getState())
//                .accountId(ca.getId())
//                .accountNumber(ca.getAccountNumber())
//                .accountType(AccountType.CURRENT)
//                .balance(ca.getBalance())
//                .initialDeposit(ca.getInitialDeposit())
//                .overdraftLimit(ca.getOverdraftLimit())
//                .minimumBalance(ca.getMinimumBalance())
//                .ifscCode(ca.getIfscCode())
//                .branchCode(ca.getBranchCode())
//                .branchName(ca.getBranchName())
//                .createdByRoId(ca.getCreatedByRoId())
//                .roName(ca.getRoName())
//                .submittedAt(ca.getSubmittedAt())
//                .approvedByManagerId(c.getApprovedByManagerId())
//                .approvedByManagerName(c.getApprovedByManagerName())
//                .actionTakenAt(c.getActionTakenAt())
//                .build();
//    }
}