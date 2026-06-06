package com.backend.allreva.common.model;

public class Email {

    private final String email;

    public Email(final String email) {
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String email;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Email build() {
            return new Email(email);
        }
    }
}
