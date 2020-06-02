package com.htp.dao;

import com.htp.domain.Location;

import java.util.List;
import java.util.Optional;

public interface LocationDao {

    List<Location> findAll();

    Optional<Location> findById(Long locationId);

    Location findOne(Long locationId);

    Location save(Location location);

    Location update(Location location);

    int delete(Location location);
}
