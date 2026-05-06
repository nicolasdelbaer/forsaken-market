package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
    @Min(0)
    @Max(1_000_000)
    private Integer open;

    @Getter @Setter
    @Min(0)
    @Max(1_000_000)
    private Integer close;

    @Getter @Setter
    @Min(0)
    @Max(1_000_000)
    private Integer high;

    @Getter @Setter
    @Min(0)
    @Max(1_000_000)
    private Integer low;


    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    @Getter @Setter
    private Long startRoundId;
    @Getter @Setter
    private Long endRoundId;

    @Getter @CreationTimestamp
    private LocalDateTime createdAt;
}
