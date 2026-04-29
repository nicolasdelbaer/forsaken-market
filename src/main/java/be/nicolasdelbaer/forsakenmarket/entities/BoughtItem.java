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
    @Getter
    private Long id;

    //No need for fk here
    @Getter @Setter
    private Long marketItemId;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    @Getter @Setter
    @ManyToOne
    private Player player;

    @Getter @Setter
    private Integer boughtPrice;

    @Getter @Setter
    private Integer decayNbRounds;

    @Getter @Setter
    private Long boughtRoundId;

    @Getter @Setter
    private Long soldRoundId;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private MarketItemStatus status;


    @Getter @Setter @Column(nullable = false)
    private LocalDateTime boughtAt;
    @Getter @Setter
    private LocalDateTime soldAt;
    @Getter @Setter
    private LocalDateTime decayAt;

    //TODO meh ? relicat? voir statut ?
    @Getter @Setter
    private boolean expired;

    public void updateExpiration(Long currentRound) {
        if(!(status == MarketItemStatus.BOUGHT)) {
            //should not go here
            System.out.println("---SHOULD NOT update a non BOUGHT status from BoughtItem");
            return;
        }
        if(currentRound > (boughtRoundId + decayNbRounds)) {
            status = MarketItemStatus.DECAYED;
            expired = true;
        }
    }
}
