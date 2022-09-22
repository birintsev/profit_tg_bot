package org.profit.telegrambot.model;

import lombok.*;
import org.profit.telegrambot.enums.CustomerStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Customer {

    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private CustomerStatus status;

}
