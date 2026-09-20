package dev.mednikov.accounting.users.dto;

import dev.mednikov.accounting.users.models.User;

import java.util.function.Function;

public final class UserDtoMapper implements Function<User, UserDto> {

    @Override
    public UserDto apply(User user) {
        UserDto result = new UserDto();
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setId(user.getId());
        result.setActive(user.isActive());
        result.setEmailVerified(user.isEmailVerified());
        result.setPremium(user.isPremium());
        return result;
    }
//
//    private String getGravatarUrl(String email){
//        String hash = Hashing.sha256().hashString(email, StandardCharsets.UTF_8).toString();
//        String gravatarUrl = "https://www.gravatar.com/avatar/" + hash + "?d=mp";
//        return gravatarUrl;
//    }

}
