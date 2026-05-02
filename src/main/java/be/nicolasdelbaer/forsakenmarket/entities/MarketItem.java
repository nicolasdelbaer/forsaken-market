package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class MarketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter private Long id;

    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    @Getter @Setter private ItemBlueprint itemBlueprint;

    /*
     * Initial time of market availability in round nb
     */
    @Getter @Setter private Integer timeToLive;

    /*
     * The round where the item has been added to the market
     */
    @Getter @Setter private Long createdRoundId;


    @Getter @Setter private LocalDateTime createdAt;
    @Getter @Setter private LocalDateTime expiredAt;

    @Getter @Setter private boolean expired;


    //CreationRound: 5 - Expire in: 3
    //Round 5: 3 left -> Round 6: 2 left -> Round 7: 1 left -> Round 8: Out of market
    public void updateExpiration(Long currentRoundId) {
        boolean shouldExpire = currentRoundId > (createdRoundId +timeToLive-1);
        if(shouldExpire){
            expired = true;
            expiredAt = LocalDateTime.now();
        }
    }
}
