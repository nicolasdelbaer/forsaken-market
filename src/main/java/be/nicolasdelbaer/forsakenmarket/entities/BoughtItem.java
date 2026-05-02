package be.nicolasdelbaer.forsakenmarket.entities;

import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
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
public class BoughtItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter private Long id;

    //No need for fk here
    @Getter @Setter private Long marketItemId;

    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    @Getter @Setter private ItemBlueprint itemBlueprint;

    @ManyToOne
    @Getter @Setter private Player player;

    @Getter @Setter private Integer boughtPrice;

    @Getter @Setter private Integer decayNbRounds;

    @Getter @Setter
    @Enumerated(EnumType.STRING) private MarketItemStatus status;


    //TODO use table for action history avoiding redundance
    @Column(nullable = false)
    @Getter @Setter private Long boughtRoundId;
    @Getter @Setter private Long soldRoundId;
    @Getter @Setter private Long decayedRoundId;
    @Getter @Setter private Long discardedRoundId;

    @Column(nullable = false)
    @Getter @Setter private LocalDateTime boughtAt;
    @Getter @Setter private LocalDateTime soldAt;
    @Getter @Setter private LocalDateTime decayedAt;
    @Getter @Setter private LocalDateTime discardedAt;


    //BoughtRound: 5 - Decay: 3
    //Round 5: 3 left -> Round 6: 2 left -> Round 7: 1 left -> Round 8: 0 left -> Round 9: Decayed
    public void updateExpiration(Long currentRound) {
        if(currentRound > boughtRoundId + decayNbRounds) {
            status = MarketItemStatus.DECAYED;
            decayedAt = LocalDateTime.now();
            decayedRoundId = currentRound;
        }
    }

    public boolean isDecayed(){
        return status.equals(MarketItemStatus.DECAYED);
    }
}
