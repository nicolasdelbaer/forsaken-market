package be.nicolasdelbaer.forsakenmarket.entities;

import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Player {
    private static final int[] LEVEL_THRESHOLDS = {0, 100, 250, 500, 1000};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Column(nullable = false)
    @Getter @Setter
    private String name;

    @Column(nullable = false, unique = true)
    @Getter @Setter
    private String email;

    @Column(nullable = false)
    @Getter @Setter
    private String password;

    /*
     * Current money that player can use to buy new item
     * User debit & credit for managing the wallet
     */
    @Getter
    private Integer wallet;

    /*
    * Current level of the player
    * It'll grow as the player wins experience by selling items
    * and interacting with the game. It'll eventually unlock new
    * items, skills, events, ...
    */
    @Getter @Setter
    private Integer level;

    /*
     * Reputation represents exp
     * All won reputation points from the account creation
     */
    @Getter @Setter
    private Integer totReput;

    /*
    * Reputation represents exp
    * current reputation is the won points from the current level
    * used to persist the progression
    */
    @Getter @Setter
    private Integer currentReput;

    @Getter @Setter
    @ManyToMany
    private List<PlayerRole> playerRoles;


    public Player(String name, String email, String password, int startingWallet, List<PlayerRole> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.wallet = startingWallet;
        this.level = 1;
        this.totReput = 0;
        this.currentReput = 0;
        this.playerRoles = roles;
    }

    public void debit(Integer amount) throws PlayerInsufficientFundsException {
        if(!canAfford(amount))
            throw new PlayerInsufficientFundsException("cannot afford item");
        wallet -= amount;
    }
    public void credit(Integer amount){
        wallet += amount;
    }

    public boolean canAfford(Integer cost){
        return cost <= wallet;
    }

    public void addReputation(Integer reputationScore) {
        currentReput += reputationScore;
        totReput += reputationScore;

        //update level from thresholds table
        while (level < LEVEL_THRESHOLDS.length && currentReput >= LEVEL_THRESHOLDS[level]) {
            currentReput -= LEVEL_THRESHOLDS[level];
            level ++;
        }
    }

    public List<String> getRoles() {
        return playerRoles.stream().map(PlayerRole::getName).toList();
    }
}
