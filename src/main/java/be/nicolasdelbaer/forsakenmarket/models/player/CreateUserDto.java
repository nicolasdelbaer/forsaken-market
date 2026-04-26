package be.nicolasdelbaer.forsakenmarket.models.player;

public record CreateUserDto(String email, String pwd, String userName, int userWallet) {
}
