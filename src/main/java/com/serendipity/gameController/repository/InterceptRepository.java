package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Intercept;
import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterceptRepository extends CrudRepository<Intercept, Long> {

    Optional<Intercept> findByIntercepter(Player player);

}
