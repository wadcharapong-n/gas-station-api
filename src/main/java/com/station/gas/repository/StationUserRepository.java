package com.station.gas.repository;

import com.station.gas.model.StationUser;
import com.station.gas.model.User;
import com.station.gas.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StationUserRepository extends JpaRepository<StationUser, UUID> {
    List<StationUser> findByUser(User user);
    List<StationUser> findByStation(Station station);
}
