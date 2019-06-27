package com.strumski.reactivegot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "No such house exists in game of thrones")
public class HouseNotFoundException extends RuntimeException {
}
