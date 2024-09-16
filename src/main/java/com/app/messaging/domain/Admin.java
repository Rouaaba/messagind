package com.app.messaging.domain;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Entity
@Getter
@DiscriminatorValue("Admin")
public class Admin extends User {
    
}
