package com.olenanoskova.task_and_time_tracker.service;

import com.olenanoskova.task_and_time_tracker.repository.entity.InviteEntity;

import java.util.UUID;

public interface InviteService {

    InviteEntity generateInvite(UUID companyId);

    UUID resolveCompany(String inviteCode);
}
