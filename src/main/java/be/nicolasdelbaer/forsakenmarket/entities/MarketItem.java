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
    @Getter
    private Long id;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    /*
     * Initial time of market availability in round nb
     */
    @Getter @Setter
    private Integer timeToLive;

    /*
     * The round where the item has been added to the market
     */
    @Getter @Setter
    private Long roundId;


    @Getter @Setter
    private LocalDateTime createdAt;
    @Getter @Setter
    private LocalDateTime expiredAt;

    @Getter @Setter
    private boolean expired;


    public void updateExpiration(Long currentRoundId) {
        boolean shouldExpire = currentRoundId > (roundId+timeToLive);
        if(shouldExpire){
            expired = true;
            expiredAt = LocalDateTime.now();
        }
    }
}
