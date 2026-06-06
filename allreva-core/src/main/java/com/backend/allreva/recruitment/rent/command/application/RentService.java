package com.backend.allreva.recruitment.rent.command.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.storage.StorageWriter;
import com.backend.allreva.recruitment.rent.command.event.RentJoinedEvent;
import com.backend.allreva.recruitment.rent.command.event.RentRegisteredEvent;
import com.backend.allreva.recruitment.rent.command.implementation.RentJoiner;
import com.backend.allreva.recruitment.rent.command.implementation.RentParticipantCanceller;
import com.backend.allreva.recruitment.rent.command.implementation.RentParticipantReader;
import com.backend.allreva.recruitment.rent.command.implementation.RentReader;
import com.backend.allreva.recruitment.rent.command.implementation.RentRegister;
import com.backend.allreva.recruitment.rent.command.implementation.RentUpdater;
import com.backend.allreva.recruitment.rent.command.implementation.RentWriter;
import com.backend.allreva.recruitment.rent.command.input.RentIdCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinIdCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinUpdateCommand;
import com.backend.allreva.recruitment.rent.command.input.RentRegisterCommand;
import com.backend.allreva.recruitment.rent.command.input.RentUpdateCommand;
import com.backend.allreva.recruitment.rent.domain.Depositor;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRegister rentRegister;
    private final RentUpdater rentUpdater;
    private final RentReader rentReader;
    private final RentParticipantReader rentParticipantReader;
    private final RentWriter rentWriter;
    private final RentJoiner rentJoiner;
    private final RentParticipantCanceller rentParticipantCanceller;
    private final StorageWriter storageWriter;

    @Transactional
    public Long register(final RentRegisterCommand command, final Long memberId) {
        Rent rent = rentRegister.register(command, memberId);
        Rent savedRent = rentWriter.save(rent);

        Events.raise(RentRegisteredEvent.from(savedRent));

        return savedRent.getId();
    }

    @Transactional
    public void update(final RentUpdateCommand command, final Long memberId) {
        Rent rent = rentReader.get(command.rentId());

        rent.validateMine(memberId);
        rentUpdater.update(rent, command);
        rentWriter.save(rent);
    }

    @Transactional
    public void close(final RentIdCommand request, final Long memberId) {
        Rent rent = rentReader.get(request.rentId());

        rent.validateMine(memberId);
        rent.close();
        rentWriter.save(rent);
    }

    @Transactional
    public void delete(final RentIdCommand request, final Long memberId) {
        Rent rent = rentReader.get(request.rentId());

        rent.validateMine(memberId);

        storageWriter.delete(rent.getImage().getUrl());
        rentWriter.delete(rent);
    }

    @Transactional
    public Long join(final RentJoinCommand command, final Long memberId) {
        Rent rent = rentReader.get(command.rentId());

        RentParticipant participant = rentJoiner.join(rent, command, memberId);
        RentParticipant saved = rentWriter.save(participant);

        Events.raise(RentJoinedEvent.from(rent, saved));
        return saved.getId();
    }

    @Transactional
    public void updateJoin(final RentJoinUpdateCommand request, final Long memberId) {
        RentParticipant participant = rentParticipantReader.get(request.rentParticipantId());

        participant.validateMine(memberId);

        participant.update(
                Depositor.builder()
                        .depositorName(request.depositorName())
                        .depositorTime(request.depositorTime())
                        .phone(request.phone())
                        .build(),
                request.passengerNum(),
                request.refundType(),
                request.refundAccount(),
                request.boardingDate());
        rentWriter.save(participant);
    }

    @Transactional
    public void cancelJoin(final RentJoinIdCommand request, final Long memberId) {
        RentParticipant participant = rentParticipantReader.get(request.rentParticipantId());

        rentParticipantCanceller.cancel(participant, memberId);
        rentWriter.delete(participant);
    }
}
