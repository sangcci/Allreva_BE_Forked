package com.backend.allreva.concert.place.kopis;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import com.backend.allreva.concert.place.domain.Location;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class KopisHallMapper {

    public List<ConcertHall> toHalls(final KopisHallDetailResponse response) {
        return response.getDb().getMt13s().getMt13List().stream()
                .map(hall -> toHall(response.getDb(), hall))
                .toList();
    }

    private ConcertHall toHall(final KopisHallDetailResponse.Db db, final KopisHallDetailResponse.Mt13 hall) {
        return ConcertHall.builder()
                .hallCode(hall.getMt13id())
                .name(toHallName(db.getFcltynm(), hall.getPrfplcnm()))
                .seatScale(Integer.parseInt(hall.getSeatscale().replace(",", "")))
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasStore(toBoolean(db.getStore()))
                        .hasCafe(toBoolean(db.getCafe()))
                        .hasParkingLot(toBoolean(db.getParkinglot()))
                        .hasRestaurant(toBoolean(db.getRestaurant()))
                        .hasDisabledParking(toBoolean(db.getParkbarrier()))
                        .hasElevator(toBoolean(db.getElevbarrier()))
                        .hasDisabledToilet(toBoolean(db.getRestbarrier()))
                        .hasRunway(toBoolean(db.getRunwbarrier()))
                        .build())
                .location(Location.builder()
                        .longitude(Double.parseDouble(db.getLo()))
                        .latitude(Double.parseDouble(db.getLa()))
                        .address(db.getAdres())
                        .build())
                .build();
    }

    private String toHallName(final String facilityName, final String hallName) {
        if (facilityName.equals(hallName)) {
            return facilityName;
        }
        return facilityName + " " + hallName;
    }

    private boolean toBoolean(final String yesNo) {
        return "Y".equals(yesNo);
    }
}
