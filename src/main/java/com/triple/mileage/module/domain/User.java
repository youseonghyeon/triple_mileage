package com.triple.mileage.module.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID id;

    private int mileage = 0;

    @OneToMany(mappedBy = "receiver")
    private List<PointHistory> pointHistories = new ArrayList<>();

    public User(UUID id) {
        this.id = id;
    }

    public void giveMileage(int mileage) {
        this.mileage += mileage;
    }
}
