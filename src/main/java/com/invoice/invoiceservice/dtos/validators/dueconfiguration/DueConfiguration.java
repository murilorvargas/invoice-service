package com.invoice.invoiceservice.dtos.validators.dueconfiguration;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DueConfigurationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DueConfiguration {

    String message() default "Invalid due configuration";

     Class<?>[] groups() default {};

     Class<? extends Payload>[] payload() default {};
}
