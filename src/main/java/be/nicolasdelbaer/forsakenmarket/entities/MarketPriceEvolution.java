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
public class MarketPriceEvolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    private Integer open;

    @Getter @Setter
    private Integer close;

    @Getter @Setter
    private Integer high;

    @Getter @Setter
    private Integer low;


    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    @Getter @Setter
    private Long startRoundId;
    @Getter @Setter
    private Long endRoundId;

    @Getter @Setter
    private LocalDateTime createdAt;
}
