package com.commerceconnect.service;

import com.commerceconnect.dto.CampaignRequest;
import com.commerceconnect.dto.CampaignResponse;
import com.commerceconnect.entity.Campaign;
import com.commerceconnect.entity.CampaignStatus;
import com.commerceconnect.entity.User;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.CampaignRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public CampaignService(CampaignRepository campaignRepository, UserRepository userRepository, AuditService auditService) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public CampaignResponse createCampaign(Long userId, CampaignRequest request, String actorEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Campaign campaign = mapRequest(new Campaign(), request);
        campaign.setCreatedBy(user);
        Campaign saved = campaignRepository.save(campaign);
        auditService.log("Campaign", saved.getId(), "CREATE", actorEmail, "Created campaign " + saved.getName());
        return DtoMapper.toCampaignResponse(saved);
    }

    @Transactional
    public CampaignResponse updateCampaign(Long id, CampaignRequest request, String actorEmail) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        Campaign saved = campaignRepository.save(mapRequest(campaign, request));
        auditService.log("Campaign", saved.getId(), "UPDATE", actorEmail, "Updated campaign " + saved.getName());
        return DtoMapper.toCampaignResponse(saved);
    }

    public CampaignResponse getCampaignById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        return DtoMapper.toCampaignResponse(campaign);
    }

    public Page<CampaignResponse> getCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable).map(DtoMapper::toCampaignResponse);
    }

    @Transactional
    public void deleteCampaign(Long id, String actorEmail) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        campaign.setStatus(CampaignStatus.CANCELLED);
        campaignRepository.save(campaign);
        auditService.log("Campaign", id, "CANCEL", actorEmail, "Cancelled campaign " + campaign.getName());
    }

    private Campaign mapRequest(Campaign campaign, CampaignRequest request) {
        CampaignStatus status;
        try {
            status = CampaignStatus.valueOf(request.status().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid campaign status.");
        }

        LocalDateTime startDate = request.startDate() == null || request.startDate().isBlank() ? null : LocalDateTime.parse(request.startDate());
        LocalDateTime endDate = request.endDate() == null || request.endDate().isBlank() ? null : LocalDateTime.parse(request.endDate());
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate.");
        }

        campaign.setName(request.name());
        campaign.setDescription(request.description());
        campaign.setCampaignType(request.campaignType());
        campaign.setStatus(status);
        campaign.setStartDate(startDate);
        campaign.setEndDate(endDate);
        return campaign;
    }
}
