package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.entity.InviteEntity;

import java.util.UUID;

public interface InviteService {

    InviteEntity generateInvite(UUID companyId);

    UUID resolveCompany(String inviteCode);

    /**
     * Joins the user to the invite's company: validates the code and assigns
     * the invite's role. Existing memberships (including the personal
     * workspace) are kept — a user may belong to several companies.
     *
     * @return the joined company id
     */
    UUID joinCompany(String inviteCode, UUID userId);
}
