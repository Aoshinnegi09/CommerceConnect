package com.commerceconnect.service;

import com.commerceconnect.dto.CampaignRequest;
import com.commerceconnect.entity.Campaign;
import com.commerceconnect.entity.User;
import com.commerceconnect.exception.ResourceNotFoundException;
import com.commerceconnect.repository.CampaignRepository;
import com.commerceconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    public CampaignService(CampaignRepository campaignRepository, UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
    }

    public Campaign createCampaign(Long userId, CampaignRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Campaign campaign = new Campaign();
        campaign.setName(request.name());
        campaign.setDescription(request.description());
        campaign.setCampaignType(request.campaignType());
        campaign.setStatus(request.status());
        campaign.setCreatedBy(user);
        campaign.setStartDate(request.startDate() == null || request.startDate().isBlank() ? null : LocalDateTime.parse(request.startDate()));
        campaign.setEndDate(request.endDate() == null || request.endDate().isBlank() ? null : LocalDateTime.parse(request.endDate()));
        return campaignRepository.save(campaign);
    }

    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }
}
