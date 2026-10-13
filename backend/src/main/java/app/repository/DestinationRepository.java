package app.repository;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import app.entity.Destination;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

}