package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewm.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("select u from User u " +
            "where (:ids IS NULL OR u.id IN :ids) " +
            "order by u.id asc")
    List<User> getUsers(Pageable pageable,
                        @Param("ids")List<Long> ids);
}