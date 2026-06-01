package com.Vendor.service;

import com.Vendor.repository.InterviewRepository;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final Calendar calendar;

    private final InterviewRepository interviewRepository;

    public List<String> getFreeSlots(
            String panelEmail,
            String date
    ) {

        try {

            String start = date + "T00:00:00Z";
            String end = date + "T23:59:59Z";

            FreeBusyRequest request = new FreeBusyRequest();

            request.setTimeMin(new DateTime(start));
            request.setTimeMax(new DateTime(end));

            request.setItems(List.of(
                    new FreeBusyRequestItem().setId(panelEmail)
            ));

            FreeBusyResponse response =
                    calendar.freebusy()
                            .query(request)
                            .execute();

            List<TimePeriod> busySlots =
                    response.getCalendars()
                            .get(panelEmail)
                            .getBusy();

            List<String> freeSlots = new ArrayList<>();

            LocalDate selectedDate =
                    LocalDate.parse(date);

            LocalTime slotStart =
                    LocalTime.of(9, 0);

            LocalTime slotEndLimit =
                    LocalTime.of(18, 0);

            while (slotStart.isBefore(slotEndLimit)) {

                LocalTime slotEnd =
                        slotStart.plusHours(1);

                ZonedDateTime slotStartDateTime =
                        ZonedDateTime.of(
                                selectedDate,
                                slotStart,
                                ZoneOffset.UTC
                        );

                ZonedDateTime slotEndDateTime =
                        ZonedDateTime.of(
                                selectedDate,
                                slotEnd,
                                ZoneOffset.UTC
                        );

                boolean isBusy = false;

                for (TimePeriod busy : busySlots) {

                    Instant busyStart =
                            Instant.parse(
                                    busy.getStart()
                                            .toStringRfc3339()
                            );

                    Instant busyEnd =
                            Instant.parse(
                                    busy.getEnd()
                                            .toStringRfc3339()
                            );

                    Instant currentSlotStart =
                            slotStartDateTime.toInstant();

                    Instant currentSlotEnd =
                            slotEndDateTime.toInstant();

                    boolean overlap =

                            currentSlotStart.isBefore(busyEnd)
                                    &&
                                    currentSlotEnd.isAfter(busyStart);

                    if (overlap) {

                        isBusy = true;

                        break;
                    }
                }

                String currentSlot =
                        slotStart.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                        );

                Date startTime = Date.from(
                        slotStartDateTime.toInstant()
                );

                boolean alreadyBooked =
                        interviewRepository
                                .existsByInterviewerEmailAndStartTime(
                                        panelEmail,
                                        startTime
                                );

                if (!isBusy && !alreadyBooked) {

                    freeSlots.add(
                            selectedDate + "T" + currentSlot
                    );
                }

                slotStart = slotStart.plusHours(1);
            }

            return freeSlots;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "❌ Error fetching free slots: "
                            + e.getMessage()
            );
        }
    }
}