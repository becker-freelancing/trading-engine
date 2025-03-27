package com.becker.freelance.capital.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateSessionRequest {
    private final String identifier;
    private final Boolean encryptPassword;
    private String password;

    public CreateSessionRequest(String identifier, String password, Boolean encryptPassword) {
        this.identifier = identifier;
        this.password = password;
        this.encryptPassword = encryptPassword;
    }

    public String identifier() {
        return identifier;
    }

    public String password() {
        return password;
    }

    public Boolean encryptPassword() {
        return encryptPassword;
    }

    public void password(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CreateSessionRequest) obj;
        return Objects.equals(this.identifier, that.identifier) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.encryptPassword, that.encryptPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, password, encryptPassword);
    }

    @Override
    public String toString() {
        return "CreateSessionRequest[" +
                "login=" + identifier + ", " +
                "password=" + password + ", " +
                "encryptPassword=" + encryptPassword + ']';
    }

}
